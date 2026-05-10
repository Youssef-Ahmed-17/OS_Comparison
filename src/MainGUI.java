import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainGUI extends Application {
    ObservableList<Process> processes = FXCollections.observableArrayList();
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
        HBox input = new HBox(10, pid, at, bt, pr, add, clear, run);
        VBox root = new VBox(15, title, input, table);
        root.setPadding(new Insets(10));
        stage.setScene(new Scene(root, 800, 500));
        stage.setTitle("OS Project");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

