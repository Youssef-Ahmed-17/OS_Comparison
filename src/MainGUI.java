import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MainGUI extends Application {
    ObservableList<Process> processes = FXCollections.observableArrayList();
    TextArea output = new TextArea();
    VBox chartsBox = new VBox(20);

    @Override
    public void start(Stage stage) {
        Label title = new Label("OS Scheduling GUI");
        TextField pid = new TextField();
        pid.setPromptText("PID");
        TextField at = new TextField();
        at.setPromptText("Arrival");
        TextField bt = new TextField();
        bt.setPromptText("Burst");
        TextField pr = new TextField();
        pr.setPromptText("Priority");
        TableView<Process> table = new TableView<>();
        table.setItems(processes);

        TableColumn<Process, String> c1 = new TableColumn<>("PID");
        c1.setCellValueFactory(x -> new javafx.beans.property.SimpleStringProperty(x.getValue().pid));

        TableColumn<Process, Number> c2 = new TableColumn<>("AT");
        c2.setCellValueFactory(x -> new javafx.beans.property.SimpleIntegerProperty(x.getValue().at));

        TableColumn<Process, Number> c3 = new TableColumn<>("BT");
        c3.setCellValueFactory(x -> new javafx.beans.property.SimpleIntegerProperty(x.getValue().bt));

        TableColumn<Process, Number> c4 = new TableColumn<>("Priority");
        c4.setCellValueFactory(x -> new javafx.beans.property.SimpleIntegerProperty(x.getValue().priority));

        table.getColumns().addAll(c1, c2, c3, c4);
        output.setPrefHeight(200);
        Button add = new Button("Add");
        add.setOnAction(e -> {
            try {
                processes.add(new Process(
                        pid.getText(),
                        Integer.parseInt(at.getText()),
                        Integer.parseInt(bt.getText()),
                        Integer.parseInt(pr.getText())
                ));
                pid.clear(); at.clear(); bt.clear(); pr.clear();
            }
            catch (Exception ex){
                System.out.println("Invalid input");
            }
        });

        Button clear = new Button("Clear");
        clear.setOnAction(e -> processes.clear());

        Button run = new Button("Run");
        run.setOnAction(e -> {
            if(processes.isEmpty()) {
                output.setText("No Processes Added");
                return;
            }
            java.util.List<Process> copied =
                    InputHandler.copy(processes);
            java.util.List<GanttEntry> result;
            java.util.List<Process> priorityList =
                    InputHandler.copy(processes);

            java.util.List<Process> srtfList =
                    InputHandler.copy(processes);

            java.util.List<GanttEntry> priorityResult =
                    PriorityScheduler.run(priorityList);

            java.util.List<GanttEntry> srtfResult =
                    SRTFScheduler.run(srtfList);

            MetricsCalculator.calculate(priorityList);

            MetricsCalculator.calculate(srtfList);

            double[] priorityAvg =
                    MetricsCalculator.getAverages(priorityList);

            double[] srtfAvg =
                    MetricsCalculator.getAverages(srtfList);
            MetricsCalculator.calculate(copied);
            double[] avg =
                    MetricsCalculator.getAverages(copied);
            StringBuilder text = new StringBuilder();

            text.append("         PRIORITY          \n\n");

            for(GanttEntry g : priorityResult) {

                text.append(g.pid)
                        .append(" : ")
                        .append(g.start)
                        .append(" -> ")
                        .append(g.end)
                        .append("\n");

            }

            text.append("\nAverage WT: ")
                    .append(priorityAvg[0]);

            text.append("\nAverage TAT: ")
                    .append(priorityAvg[1]);

            text.append("\nAverage RT: ")
                    .append(priorityAvg[2]);

            text.append("\n\n           \n\n");

            text.append("       SRTF       \n\n");

            for(GanttEntry g : srtfResult) {

                text.append(g.pid)
                        .append(" : ")
                        .append(g.start)
                        .append(" -> ")
                        .append(g.end)
                        .append("\n");

            }

            text.append("\nAverage WT: ")
                    .append(srtfAvg[0]);

            text.append("\nAverage TAT: ")
                    .append(srtfAvg[1]);

            text.append("\nAverage RT: ")
                    .append(srtfAvg[2]);

            output.setText(text.toString());

            chartsBox.getChildren().clear();

            Label pTitle = new Label("Priority Gantt Chart");

            HBox pChart = new HBox(2);

            for(GanttEntry g : priorityResult) {

                VBox block = new VBox();

                Rectangle r = new Rectangle();

                r.setWidth((g.end - g.start) * 40);

                r.setHeight(40);

                r.setFill(Color.LIGHTBLUE);

                Label l = new Label(g.pid);

                block.getChildren().addAll(r, l);

                pChart.getChildren().add(block);
            }

            Label sTitle = new Label("SRTF Gantt Chart");

            HBox sChart = new HBox(2);

            for(GanttEntry g : srtfResult) {

                VBox block = new VBox();

                Rectangle r = new Rectangle();

                r.setWidth((g.end - g.start) * 40);

                r.setHeight(40);

                r.setFill(Color.LIGHTGREEN);

                Label l = new Label(g.pid);

                block.getChildren().addAll(r, l);

                sChart.getChildren().add(block);
            }

            chartsBox.getChildren().addAll(
                    pTitle,
                    pChart,
                    sTitle,
                    sChart
            );
        });

        HBox input = new HBox(10, pid, at, bt, pr, add, clear, run);
        VBox root = new VBox(15, title, input, table, output, chartsBox);

                root.setPadding(new Insets(10));
        stage.setScene(new Scene(root, 800, 500));
        stage.setTitle("OS Project");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

