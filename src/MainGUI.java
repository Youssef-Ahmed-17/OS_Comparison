import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MainGUI extends Application {

    private static final String BG      = "#0f1117";
    private static final String SURFACE = "#1a1d27";
    private static final String SURF2   = "#22263a";
    private static final String ACCENT  = "#4f8ef7";
    private static final String GREEN   = "#4fcf8e";
    private static final String RED     = "#f76b6b";
    private static final String TEXT    = "#e8eaf6";
    private static final String MUTED   = "#6b7280";

    private static final String[] PROC_COLORS = {
            "#4f8ef7","#f7a34f","#4fcf8e","#f76b6b","#b98ef7",
            "#f7e24f","#4ff7e2","#f74fb3","#8ef74f","#f7c34f"
    };

    private static class RawProc {
        final String pid;
        final int at, bt, priority;
        RawProc(String pid, int at, int bt, int priority) {
            this.pid=pid; this.at=at; this.bt=bt; this.priority=priority;
        }
    }

    private final List<RawProc>           rawList  = new ArrayList<>();
    private final ObservableList<RawProc> obsList  = FXCollections.observableArrayList();
    private final Map<String,String>      colorMap = new LinkedHashMap<>();
    private int colorIdx = 0;

    private TextField pidField, atField, btField, priField;
    private Canvas    pCanvas, sCanvas;
    private TableView<Process> pTable, sTable;
    private Label     pAvgLbl, sAvgLbl, compLbl;

    @Override
    public void start(Stage stage) {
        stage.setTitle("CPU Scheduling Simulator  ·  Priority vs SRTF");
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:"+BG);
        root.setTop(buildHeader());
        root.setCenter(buildMain());
        stage.setScene(new Scene(root, 1100, 820));
        stage.setMinWidth(900); stage.setMinHeight(680);
        stage.show();
    }

    private Node buildHeader() {
        Label title = lbl("CPU Scheduling Simulator", ACCENT, 20, true);
        Label sub   = lbl("Preemptive Priority  vs  SRTF   ·   lower priority number = higher urgency", MUTED, 11, false);
        VBox h = new VBox(2, title, sub);
        h.setPadding(new Insets(16,24,12,24));
        h.setStyle("-fx-background-color:"+SURFACE+"; -fx-border-color:"+SURF2+"; -fx-border-width:0 0 1 0;");
        return h;
    }

    private Node buildMain() {
        SplitPane sp = new SplitPane(buildLeft(), buildRight());
        sp.setDividerPositions(0.30);
        sp.setStyle("-fx-background-color:"+BG);
        return sp;
    }

    private Node buildLeft() {
        pidField = field("P1"); atField = field("0"); btField = field("5"); priField = field("1");

        Button addBtn   = btn("＋  Add Process",   ACCENT, this::handleAdd);
        Button runBtn   = btn("▶  Run Simulation", GREEN,  this::handleRun);
        Button clearBtn = btn("✕  Clear All",      RED,    this::handleClear);

        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        grid.add(flbl("PID"),          0,0); grid.add(pidField, 1,0);
        grid.add(flbl("Arrival Time"), 0,1); grid.add(atField,  1,1);
        grid.add(flbl("Burst Time"),   0,2); grid.add(btField,  1,2);
        grid.add(flbl("Priority"),     0,3); grid.add(priField, 1,3);
        grid.getColumnConstraints().addAll(new ColumnConstraints(90), new ColumnConstraints(120));

        // Table shows RawProc (safe — schedulers never touch it)
        TableView<RawProc> inputTable = new TableView<>(obsList);
        inputTable.setStyle(tblStyle());
        inputTable.setPlaceholder(ph("No processes yet"));
        inputTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<RawProc,String> cPid = new TableColumn<>("PID");
        cPid.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().pid));
        cPid.setPrefWidth(70); cPid.setStyle(cs());

        TableColumn<RawProc,Number> cAt = new TableColumn<>("AT");
        cAt.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().at));
        cAt.setPrefWidth(50); cAt.setStyle(cs());

        TableColumn<RawProc,Number> cBt = new TableColumn<>("BT");
        cBt.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().bt));
        cBt.setPrefWidth(50); cBt.setStyle(cs());

        TableColumn<RawProc,Number> cPr = new TableColumn<>("PRI");
        cPr.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().priority));
        cPr.setPrefWidth(50); cPr.setStyle(cs());

        inputTable.getColumns().addAll(cPid, cAt, cBt, cPr);
        inputTable.setOnKeyPressed(e -> {
            RawProc sel = inputTable.getSelectionModel().getSelectedItem();
            String code = e.getCode().toString();
            if (sel != null && (code.equals("DELETE") || code.equals("BACK_SPACE"))) {
                rawList.remove(sel);
                obsList.remove(sel);
            }
        });

        VBox box = new VBox(12,
                slbl("Add Process"), grid,
                new HBox(8, addBtn, clearBtn),
                new Separator(),
                slbl("Process List"), inputTable,
                lbl("Select row + Delete key to remove", MUTED, 10, false),
                runBtn
        );
        box.setPadding(new Insets(16));
        box.setStyle("-fx-background-color:"+SURFACE);
        VBox.setVgrow(inputTable, Priority.ALWAYS);
        return box;
    }

    private Node buildRight() {
        pCanvas = new Canvas(800,60); sCanvas = new Canvas(800,60);
        pTable  = resultsTable();     sTable  = resultsTable();
        pAvgLbl = avgLbl();           sAvgLbl = avgLbl();
        compLbl = lbl("Run the simulation to see the comparison.", TEXT, 13, false);
        compLbl.setWrapText(true);

        TabPane tabs = new TabPane(
                tab("Priority Scheduling", resultsBox(pCanvas, pTable, pAvgLbl)),
                tab("SRTF",                resultsBox(sCanvas, sTable, sAvgLbl)),
                tab("Comparison",          compScroll())
        );
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color:"+BG+"; -fx-tab-min-width:155px;");
        return tabs;
    }

    private VBox resultsBox(Canvas c, TableView<Process> tv, Label avg) {
        VBox box = new VBox(10, slbl("Gantt Chart"), ganttScroll(c), slbl("Metrics"), tv, avg);
        box.setPadding(new Insets(14));
        box.setStyle("-fx-background-color:"+BG);
        VBox.setVgrow(tv, Priority.ALWAYS);
        return box;
    }

    private ScrollPane compScroll() {
        compLbl.setPadding(new Insets(14));
        ScrollPane sp = new ScrollPane(compLbl);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:"+BG+"; -fx-background:"+BG);
        return sp;
    }

    private void handleAdd() {
        String pid = pidField.getText().trim();
        if (pid.isEmpty())                        { alert("PID cannot be empty.");           return; }
        for (RawProc r : rawList)
            if (r.pid.equals(pid))                { alert("PID '"+pid+"' already exists."); return; }
        if (rawList.size() >= 20)                 { alert("Maximum 20 processes.");         return; }

        int at, bt, pri;
        try {
            at  = Integer.parseInt(atField .getText().trim());
            bt  = Integer.parseInt(btField .getText().trim());
            pri = Integer.parseInt(priField.getText().trim());
        } catch (NumberFormatException ex) { alert("AT, BT and Priority must be integers."); return; }

        if (at < 0)          { alert("Arrival Time must be >= 0."); return; }
        if (bt < 1)          { alert("Burst Time must be >= 1.");   return; }
        if (pri<1 || pri>10) { alert("Priority must be 1-10.");     return; }

        RawProc r = new RawProc(pid, at, bt, pri);
        rawList.add(r);
        obsList.add(r);
        assignColor(pid);

        pidField.setText("P"+(rawList.size()+1));
        atField.clear(); btField.clear(); priField.clear();
        pidField.requestFocus();
    }

    private void handleClear() {
        rawList.clear(); obsList.clear(); colorMap.clear(); colorIdx=0;
        clearCanvas(pCanvas); clearCanvas(sCanvas);
        pTable.getItems().clear(); sTable.getItems().clear();
        pAvgLbl.setText(""); sAvgLbl.setText("");
        compLbl.setText("Run the simulation to see the comparison.");
    }

    private void handleRun() {
        if (rawList.isEmpty()) { alert("Add at least one process first."); return; }

        List<Process> pList = buildFreshProcessList();
        List<GanttEntry> pG = PriorityScheduler.run(pList);
        MetricsCalculator.calculate(pList);
        double[] pAvg = MetricsCalculator.getAverages(pList);
        renderGantt(pCanvas, pG);
        fillTable(pTable, pList);
        pAvgLbl.setText(avgText(pAvg));

        List<Process> sList = buildFreshProcessList();
        List<GanttEntry> sG = SRTFScheduler.run(sList);
        MetricsCalculator.calculate(sList);
        double[] sAvg = MetricsCalculator.getAverages(sList);
        renderGantt(sCanvas, sG);
        fillTable(sTable, sList);
        sAvgLbl.setText(avgText(sAvg));

        compLbl.setText(buildComparison(pAvg, sAvg));
    }
    private List<Process> buildFreshProcessList() {
        List<Process> out = new ArrayList<>();
        for (RawProc r : rawList)
            out.add(new Process(r.pid, r.at, r.bt, r.priority));
        return out;
    }

    private void renderGantt(Canvas canvas, List<GanttEntry> gantt) {
        if (gantt.isEmpty()) return;

        final double CELL = 54;
        final double PAD  = 24;
        final double BARH = 34;
        final double BARY = 6;
        final double TICK = BARY + BARH + 14;

        int    maxT = gantt.get(gantt.size()-1).end;
        double need = PAD + maxT * CELL + PAD;
        canvas.setWidth(Math.max(need, 400));

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.web(SURFACE));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int i = 0; i < gantt.size(); i++) {
            GanttEntry g  = gantt.get(i);
            double x0 = PAD + g.start * CELL;
            double x1 = PAD + g.end   * CELL;
            double bw = x1 - x0;

            String clr = g.pid.equals("IDLE") ? SURF2 : getColor(g.pid);
            gc.setFill(Color.web(clr, 0.88));
            gc.fillRoundRect(x0, BARY, bw-1, BARH, 6, 6);

            gc.setFill(Color.web(TEXT));
            gc.setFont(Font.font("Courier New", FontWeight.BOLD, 11));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText(g.pid, x0 + bw/2, BARY + BARH/2, bw - 4);

            gc.setFill(Color.web(MUTED));
            gc.setFont(Font.font("Courier New", 9));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.TOP);
            gc.fillText(String.valueOf(g.start), x0, TICK);

            if (i == gantt.size()-1)
                gc.fillText(String.valueOf(g.end), x1, TICK);
        }
    }

    private ScrollPane ganttScroll(Canvas c) {
        ScrollPane sp = new ScrollPane(c);
        sp.setFitToHeight(true); sp.setPrefHeight(80);
        sp.setStyle("-fx-background-color:"+SURFACE+"; -fx-background:"+SURFACE+"; -fx-border-color:"+SURF2);
        return sp;
    }

    private void clearCanvas(Canvas c) {
        GraphicsContext gc = c.getGraphicsContext2D();
        gc.setFill(Color.web(SURFACE));
        gc.fillRect(0, 0, c.getWidth(), c.getHeight());
    }

    private TableView<Process> resultsTable() {
        TableView<Process> tv = new TableView<>();
        tv.setStyle(tblStyle());
        tv.setPlaceholder(ph("No results yet"));
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.getColumns().addAll(
                pStr("PID", p -> p.pid,      70),
                pInt("AT",  p -> p.at,       50),
                pInt("BT",  p -> p.bt,       50),
                pInt("PRI", p -> p.priority, 50),
                pInt("CT",  p -> p.ct,       50),
                pInt("WT",  p -> p.wt,       50),
                pInt("TAT", p -> p.tat,      55),
                pInt("RT",  p -> p.rt,       50)
        );
        return tv;
    }

    private TableColumn<Process,String> pStr(String h, Function<Process,String> fn, double w) {
        TableColumn<Process,String> c = new TableColumn<>(h);
        c.setCellValueFactory(d -> new SimpleStringProperty(fn.apply(d.getValue())));
        c.setPrefWidth(w); c.setStyle(cs()); return c;
    }

    private TableColumn<Process,Number> pInt(String h, Function<Process,Integer> fn, double w) {
        TableColumn<Process,Number> c = new TableColumn<>(h);
        c.setCellValueFactory(d -> new SimpleIntegerProperty(fn.apply(d.getValue())));
        c.setPrefWidth(w); c.setStyle(cs()); return c;
    }

    private void fillTable(TableView<Process> tv, List<Process> procs) {
        tv.setItems(FXCollections.observableArrayList(
                procs.stream().sorted(Comparator.comparing(p -> p.pid)).collect(Collectors.toList())
        ));
    }

    private String buildComparison(double[] p, double[] s) {
        StringBuilder sb = new StringBuilder();
        sb.append("======COMPARISON=======\n\n");
        sb.append(String.format("%-22s  Priority    SRTF      Winner%n","Metric"));
        sb.append("─".repeat(52)).append("\n");
        sb.append(mRow("Avg Waiting Time",    p[0], s[0]));
        sb.append(mRow("Avg Turnaround Time", p[1], s[1]));
        sb.append(mRow("Avg Response Time",   p[2], s[2]));
        sb.append("\n\nBEHAVIOR\n");
        sb.append("Priority — always runs the most urgent process first.\n");
        sb.append("           Great when some tasks genuinely can't wait.\n");
        sb.append("SRTF     — picks whichever job is closest to finishing.\n");
        sb.append("           Gives optimal avg waiting time for batch work.\n");
        sb.append("\nSTARVATION RISK\n");
        sb.append("Priority — HIGH.     Low-priority jobs may never run.\n");
        sb.append("           Fix: aging (raise priority over time).\n");
        sb.append("SRTF     — MODERATE. Long jobs starve if short ones keep arriving.\n");
        sb.append("           Fix: aging or a maximum wait-time cap.\n");
        sb.append("\nRECOMMENDATION\n").append("─".repeat(44)).append("\n");
        if      (s[0]<=p[0] && s[1]<=p[1])
            sb.append("SRTF performed better — lower WT and TAT.\n" +
                    "Switch to Priority if some tasks need guaranteed urgency.\n");
        else if (p[0]<=s[0] && p[1]<=s[1])
            sb.append("Priority Scheduling performed better — lower WT and TAT.\n" +
                    "Urgent-task ordering suits this workload.\n");
        else
            sb.append("Neither algorithm wins on every metric.\n" +
                    "SRTF suits batch/throughput systems.\n" +
                    "Priority suits real-time systems where urgency comes first.\n");
        sb.append("─".repeat(44));
        return sb.toString();
    }

    private String mRow(String label, double p, double s) {
        String w = p<s ? "Priority" : s<p ? "SRTF" : "Equal";
        return String.format("%-22s  %8.2f  %8.2f  %s%n", label, p, s, w);
    }

    private void assignColor(String pid) {
        colorMap.computeIfAbsent(pid, k -> PROC_COLORS[colorIdx++ % PROC_COLORS.length]);
    }
    private String getColor(String pid) { return colorMap.getOrDefault(pid, ACCENT); }

    private String avgText(double[] a) {
        return String.format("Avg WT: %.2f   ·   Avg TAT: %.2f   ·   Avg RT: %.2f", a[0], a[1], a[2]);
    }

    private Label lbl(String t, String color, int size, boolean bold) {
        Label l = new Label(t);
        l.setStyle(String.format("-fx-font-family:'Courier New'; -fx-font-size:%dpx; -fx-text-fill:%s;%s",
                size, color, bold ? " -fx-font-weight:bold;" : ""));
        return l;
    }
    private Label slbl(String t) { return lbl(t.toUpperCase(), ACCENT, 11, true); }
    private Label flbl(String t) { return lbl(t, MUTED, 12, false); }
    private Label ph(String t)   { return lbl(t, MUTED, 12, false); }

    private Label avgLbl() {
        Label l = new Label("");
        l.setStyle("-fx-font-family:'Courier New'; -fx-font-size:12px; -fx-text-fill:"+GREEN+
                "; -fx-background-color:"+SURF2+"; -fx-padding:8 12; -fx-background-radius:4;");
        return l;
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color:"+SURF2+"; -fx-text-fill:"+TEXT+
                "; -fx-prompt-text-fill:"+MUTED+"; -fx-border-color:"+ACCENT+"33;"+
                " -fx-border-radius:4; -fx-background-radius:4;"+
                " -fx-font-family:'Courier New'; -fx-font-size:13px;");
        return tf;
    }

    private Button btn(String text, String color, Runnable action) {
        Button b = new Button(text);
        String base = "-fx-background-color:%s22; -fx-text-fill:%s; -fx-border-color:%s;"+
                " -fx-border-radius:4; -fx-background-radius:4;"+
                " -fx-font-family:'Courier New'; -fx-font-size:12px; -fx-cursor:hand;";
        b.setStyle(String.format(base, color, color, color));
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnMouseEntered(e -> b.setStyle(String.format(base.replace("22","44"), color, color, color)));
        b.setOnMouseExited (e -> b.setStyle(String.format(base, color, color, color)));
        b.setOnAction(e -> action.run());
        return b;
    }

    private Tab tab(String title, Node content) {
        Tab t = new Tab(title, content);
        t.setStyle("-fx-font-family:'Courier New'; -fx-font-size:12px;");
        return t;
    }

    private String tblStyle() {
        return "-fx-base:"+SURF2+"; -fx-background-color:"+SURFACE+
                "; -fx-table-cell-border-color:"+SURF2+
                "; -fx-font-family:'Courier New'; -fx-font-size:12px;";
    }
    private String cs() {
        return "-fx-alignment:CENTER; -fx-font-family:'Courier New'; -fx-font-size:12px;";
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null); a.setTitle("Input Error"); a.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}