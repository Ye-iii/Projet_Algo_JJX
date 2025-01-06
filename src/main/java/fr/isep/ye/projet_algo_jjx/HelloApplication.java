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

    // 员工管理窗口
    private void showEmployeeManagement() {
        Stage employeeStage = new Stage();
        employeeStage.setTitle("Employee Management");

        VBox employeeBox = new VBox(10);
        employeeBox.setPadding(new Insets(10));
        employeeBox.setAlignment(Pos.CENTER);

        ListView<String> employeeListView = new ListView<>();
        TextField employeeNameField = new TextField();
        employeeNameField.setPromptText("Enter employee name");
        Button addEmployeeButton = new Button("Add Employee");
        Button removeEmployeeButton = new Button("Remove Selected Employee");
        Button viewEmployeeButton = new Button("View Employee Info");
        Button viewHistoryButton = new Button("View Employee History");

        addEmployeeButton.setOnAction(e -> {
            String name = employeeNameField.getText();
            if (!name.isEmpty()) {
                Employee employee = new Employee(name);
                employees.add(employee);
                employeeListView.getItems().add(employee.getName());
                employeeNameField.clear();
            }
        });

        removeEmployeeButton.setOnAction(e -> {
            String selected = employeeListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                employees.removeIf(emp -> emp.getName().equals(selected));
                employeeListView.getItems().remove(selected);
            }
        });

        viewEmployeeButton.setOnAction(e -> {
            String selected = employeeListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Employee employee = employees.stream().filter(emp -> emp.getName().equals(selected)).findFirst().orElse(null);
                if (employee != null) {
                    showEmployeeInfo(employee);
                }
            }
        });

        viewHistoryButton.setOnAction(e -> {
            String selected = employeeListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Employee employee = employees.stream().filter(emp -> emp.getName().equals(selected)).findFirst().orElse(null);
                if (employee != null) {
                    showEmployeeHistory(employee);
                }
            }
        });

        employeeBox.getChildren().addAll(new Label("Employees"), employeeListView, employeeNameField, addEmployeeButton, removeEmployeeButton, viewEmployeeButton, viewHistoryButton);
        Scene employeeScene = new Scene(employeeBox, 300, 400);
        employeeStage.setScene(employeeScene);
        employeeStage.show();
    }

    private void showEmployeeInfo(Employee employee) {
        Stage infoStage = new Stage();
        infoStage.setTitle("Employee Information");

        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(10));
        infoBox.setAlignment(Pos.CENTER);
        Label nameLabel = new Label("Name: " + employee.getName());
        Label idLabel = new Label("ID: " + employee.getId());
        Label positionLabel = new Label("Position: " + employee.getPosition());

        infoBox.getChildren().addAll(nameLabel, idLabel, positionLabel, new Button("Close"));
        Scene infoScene = new Scene(infoBox, 300, 200);
        infoStage.setScene(infoScene);
        infoStage.show();
    }

    private void showEmployeeHistory(Employee employee) {
        Stage historyStage = new Stage();
        historyStage.setTitle("Employee History");

        VBox historyBox = new VBox(10);
        historyBox.setPadding(new Insets(10));
        historyBox.setAlignment(Pos.CENTER);

        ListView<String> historyListView = new ListView<>();
        for (Project project : employee.getProjects()) {
            historyListView.getItems().add(project.getName());
        }

        historyBox.getChildren().addAll(new Label("Projects Completed:"), historyListView, new Button("Close"));
        Scene historyScene = new Scene(historyBox, 300, 300);
        historyStage.setScene(historyScene);
        historyStage.show();
    }

    // 项目管理窗口
    private void showProjectManagement() {
        Stage projectStage = new Stage();
        projectStage.setTitle("Project Management");

        VBox projectBox = new VBox(10);
        projectBox.setPadding(new Insets(10));
        projectBox.setAlignment(Pos.CENTER);

        ListView<String> projectListView = new ListView<>();
        TextField projectNameField = new TextField();
        projectNameField.setPromptText("Enter project name");
        Button addProjectButton = new Button("Add Project");
        Button removeProjectButton = new Button("Remove Selected Project");

        addProjectButton.setOnAction(e -> {
            String name = projectNameField.getText();
            if (!name.isEmpty()) {
                Project project = new Project(name);
                projects.add(project);
                projectListView.getItems().add(project.getName());
                projectNameField.clear();
            }
        });

        removeProjectButton.setOnAction(e -> {
            String selected = projectListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                projects.removeIf(proj -> proj.getName().equals(selected));
                projectListView.getItems().remove(selected);
            }
        });

        projectBox.getChildren().addAll(new Label("Projects"), projectListView, projectNameField, addProjectButton, removeProjectButton);
        Scene projectScene = new Scene(projectBox, 300, 300);
        projectStage.setScene(projectScene);
        projectStage.show();
    }

    // 任务管理窗口
    private void showTaskManagement() {
        Stage taskStage = new Stage();
        taskStage.setTitle("Task Management");

        VBox taskBox = new VBox(10);
        taskBox.setPadding(new Insets(10));
        taskBox.setAlignment(Pos.CENTER);

        ListView<String> projectListView = new ListView<>();
        for (Project project : projects) {
            projectListView.getItems().add(project.getName());
        }

        ListView<String> taskListView = new ListView<>();
        TextField taskNameField = new TextField();
        taskNameField.setPromptText("Enter task name");
        Button addTaskButton = new Button("Add Task");
        addTaskButton.setOnAction(e -> {
            String name = taskNameField.getText();
            if (projectListView.getSelectionModel().getSelectedItem() != null && !name.isEmpty()) {
                Project selectedProject = projects.get(projectListView.getSelectionModel().getSelectedIndex());
                Task task = new Task(name);
                selectedProject.addTask(task);
                taskListView.getItems().add(task.getName());
                taskNameField.clear();
            }
        });

        taskBox.getChildren().addAll(new Label("Select Project"), projectListView, new Label("Tasks"), taskListView, taskNameField, addTaskButton);
        Scene taskScene = new Scene(taskBox, 300, 300);
        taskStage.setScene(taskScene);
        taskStage.show();
    }

    // 项目跟踪窗口（简单示例）
    private void showProjectTracking() {
        Stage trackingStage = new Stage();
        trackingStage.setTitle("Project Tracking");

        VBox trackingBox = new VBox(10);
        trackingBox.setPadding(new Insets(10));
        trackingBox.setAlignment(Pos.CENTER);

        ListView<String> trackingListView = new ListView<>();
        trackingListView.getItems().addAll("Kanban View", "Calendar View");

        trackingBox.getChildren().addAll(new Label("Tracking Options"), trackingListView);
        Scene trackingScene = new Scene(trackingBox, 300, 300);
        trackingStage.setScene(trackingScene);
        trackingStage.show();
    }

    // 员工类
    class Employee {
        private String name;
        private String id;
        private String position;
        private List<Project> projects;

        public Employee(String name) {
            this.name = name;
            this.id = String.valueOf(System.currentTimeMillis()); // 生成一个简单的 ID
            this.position = "Employee"; // 默认职位
            this.projects = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String getPosition() {
            return position;
        }

        public void addProject(Project project) {
            projects.add(project);
        }

        public List<Project> getProjects() {
            return projects;
        }
    }

    // 项目类
    class Project {
        private String name;
        private List<Task> tasks;

        public Project(String name) {
            this.name = name;
            this.tasks = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public void addTask(Task task) {
            tasks.add(task);
        }

        public List<Task> getTasks() {
            return tasks;
        }
    }

    // 任务类
    class Task {
        private String name;

        public Task(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}*/