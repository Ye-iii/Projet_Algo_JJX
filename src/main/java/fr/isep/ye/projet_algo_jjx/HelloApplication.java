/*package fr.isep.ye.projet_algo_jjx;





import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {

    private List<Employee> employees = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Task Management System");

        // 创建主界面
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        Button employeeButton = new Button("Manage Employees");
        Button projectButton = new Button("Manage Projects");
        Button taskButton = new Button("Manage Tasks");
        Button trackingButton = new Button("Project Tracking");

        employeeButton.setOnAction(e -> showEmployeeManagement());
        projectButton.setOnAction(e -> showProjectManagement());
        taskButton.setOnAction(e -> showTaskManagement());
        trackingButton.setOnAction(e -> showProjectTracking());

        mainLayout.getChildren().addAll(employeeButton, projectButton, taskButton, trackingButton);
        Scene mainScene = new Scene(mainLayout, 300, 200);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
    }
 */