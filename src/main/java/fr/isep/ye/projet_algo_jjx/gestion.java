package fr.isep.ye.projet_algo_jjx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ListView;
import java.sql.SQLException;

public class gestion extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 创建菜单
        MenuBar menuBar = new MenuBar();

        // 创建 "员工" 菜单
        Menu employeeMenu = new Menu("员工");
        MenuItem addEmployeeItem = new MenuItem("添加员工");
        MenuItem viewEmployeesItem = new MenuItem("查看员工");
        employeeMenu.getItems().addAll(addEmployeeItem, viewEmployeesItem);

        // 创建 "项目" 菜单
        Menu projectMenu = new Menu("项目");
        MenuItem addProjectItem = new MenuItem("添加项目");
        MenuItem viewProjectsItem = new MenuItem("查看项目");
        projectMenu.getItems().addAll(addProjectItem, viewProjectsItem);

        // 创建 "任务" 菜单
        Menu taskMenu = new Menu("任务");
        MenuItem addTaskItem = new MenuItem("添加任务");
        MenuItem viewTasksItem = new MenuItem("查看任务");
        taskMenu.getItems().addAll(addTaskItem, viewTasksItem);

        // 将菜单添加到菜单栏
        menuBar.getMenus().addAll(employeeMenu, projectMenu, taskMenu);

        // 主布局
        BorderPane root = new BorderPane();
        root.setTop(menuBar);

        // 添加员工功能
    //    VBox addEmployeeLayout = createAddEmployeeLayout();
        VBox addProjectLayout = createAddProjectLayout();
        VBox addTaskLayout = createAddTaskLayout();

        // 事件处理
    //    addEmployeeItem.setOnAction(e -> root.setCenter(addEmployeeLayout));
        viewEmployeesItem.setOnAction(e -> root.setCenter(createViewEmployeesLayout()));
        addProjectItem.setOnAction(e -> root.setCenter(addProjectLayout));
        viewProjectsItem.setOnAction(e -> root.setCenter(createViewProjectsLayout()));
        addTaskItem.setOnAction(e -> root.setCenter(addTaskLayout));
        viewTasksItem.setOnAction(e -> root.setCenter(createViewTasksLayout()));

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Management System");
        primaryStage.show();
    }

     //创建添加员工的布局
//    private VBox createAddEmployeeLayout() {
//        TextField employeeNameField = new TextField();
//        employeeNameField.setPromptText("员工姓名");
//        TextField employeeEmailField = new TextField();
//        employeeEmailField.setPromptText("员工电子邮件");
//        Button addEmployeeButton = new Button("添加员工");
//        addEmployeeButton.setOnAction(e -> {
//            String name = employeeNameField.getText();
//            String email = employeeEmailField.getText();
//            employee employeeManager = new employee();
//            try {
//                employeeManager.addEmployee(name, email);
//                employeeNameField.clear();
//                employeeEmailField.clear();
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        });
//
//        return new VBox(employeeNameField, employeeEmailField, addEmployeeButton);
//    }

    private mysql db = new mysql();
    private VBox createViewEmployeesLayout() {
        TableView<employee> employeeTableView = new TableView<>();

        // 创建 ID 列
        TableColumn<employee, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // 创建 Name 列
        TableColumn<employee, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // 创建 Email 列
        TableColumn<employee, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        // 将列添加到表格中
        employeeTableView.getColumns().addAll(idColumn, nameColumn, emailColumn);

        gestionEmplo employeeManager = new gestionEmplo(db);
        try {
            employeeManager.listEmployees(employeeTableView);
        } catch (SQLException ex) {
            ex.printStackTrace();
            employeeTableView.getItems().add(new employee(-1, "无法加载员工列表", ""));
        }

        return new VBox(employeeTableView);
    }

    // 创建查看员工的布局
//    private VBox createViewEmployeesLayout() {
//        ListView<String> employeeListView = new ListView<>();
//        employee employeeManager = new employee();
//
//        try {
//            employeeManager.listEmployees(employeeListView);
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//            employeeListView.getItems().add("无法加载员工列表。");
//        }
//
//        return new VBox(employeeListView);
//    }

    // 创建添加项目的布局
    private VBox createAddProjectLayout() {
        TextField projectNameField = new TextField();
        projectNameField.setPromptText("项目名称");
        TextField projectGroupField = new TextField();
        projectGroupField.setPromptText("项目小组");
        TextField projectDeadlineField = new TextField();
        projectDeadlineField.setPromptText("项目截止日期 (YYYY-MM-DD)");
        Button addProjectButton = new Button("添加项目");
        addProjectButton.setOnAction(e -> {
            String name = projectNameField.getText();
            String group = projectGroupField.getText();
            String deadline = projectDeadlineField.getText();
            projet projectManager = new projet();
            try {
                projectManager.addProject(name, group, deadline);
                projectNameField.clear();
                projectGroupField.clear();
                projectDeadlineField.clear();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        return new VBox(projectNameField, projectGroupField, projectDeadlineField, addProjectButton);
    }

    // 创建查看项目的布局
    private VBox createViewProjectsLayout() {
        ListView<String> projectListView = new ListView<>();
        projet projectManager = new projet();
        try {
            // 添加员工项
            projectManager.listProjects(projectListView);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new VBox(projectListView);
    }

    // 创建添加任务的布局
    private VBox createAddTaskLayout() {
        TextField taskNameField = new TextField();
        taskNameField.setPromptText("任务名称");
        TextField taskProjectIdField = new TextField();
        taskProjectIdField.setPromptText("项目 ID");
        TextField taskPriorityField = new TextField();
        taskPriorityField.setPromptText("优先级");
        TextField taskDeadlineField = new TextField();
        taskDeadlineField.setPromptText("任务截止日期 (YYYY-MM-DD)");
        TextField taskDescriptionField = new TextField();
        taskDescriptionField.setPromptText("任务描述");
        TextField taskCategoriesField = new TextField();
        taskCategoriesField.setPromptText("任务类别");
        Button addTaskButton = new Button("添加任务");
        addTaskButton.setOnAction(e -> {
            String name = taskNameField.getText();
            int projectId = Integer.parseInt(taskProjectIdField.getText());
            int priority = Integer.parseInt(taskPriorityField.getText());
            String deadline = taskDeadlineField.getText();
            String description = taskDescriptionField.getText();
            String categories = taskCategoriesField.getText();
            tache taskManager = new tache();
            try {
                taskManager.addTask(name, projectId, priority, deadline, description, categories);
                taskNameField.clear();
                taskProjectIdField.clear();
                taskPriorityField.clear();
                taskDeadlineField.clear();
                taskDescriptionField.clear();
                taskCategoriesField.clear();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        return new VBox(taskNameField, taskProjectIdField, taskPriorityField, taskDeadlineField, taskDescriptionField, taskCategoriesField, addTaskButton);
    }

    // 创建查看任务的布局
    private VBox createViewTasksLayout() {
        ListView<String> taskListView = new ListView<>();
        tache taskManager = new tache();
        try {
            taskManager.listTasks(taskListView);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new VBox(taskListView);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

//import javafx.application.Application;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.*;
//import javafx.stage.Stage;
//
//public class gestion extends Application {
//    private mysql db;
//    private ObservableList<employee> employeeList;
//
//    @Override
//    public void start(Stage primaryStage) {
//        db = new mysql();
//        employeeList = FXCollections.observableArrayList(db.listEmployees());
//
//        // TableView 设置
//        TableView<employee> tableView = new TableView<>();
//        TableColumn<employee, Integer> idColumn = new TableColumn<>("ID");
//        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
//
//        TableColumn<employee, String> nameColumn = new TableColumn<>("Name");
//        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
//
//        TableColumn<employee, String> emailColumn = new TableColumn<>("Email");
//        emailColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
//
//        tableView.getColumns().addAll(idColumn, nameColumn, emailColumn);
//        tableView.setItems(employeeList);
//
//        // 输入区
//        TextField nameField = new TextField();
//        nameField.setPromptText("Name");
//        TextField emailField = new TextField();
//        emailField.setPromptText("Email");
//        Button addButton = new Button("Add Employee");
//
//        addButton.setOnAction(e -> {
//            String name = nameField.getText();
//            String email = emailField.getText();
//            if (!name.isEmpty() && !email.isEmpty()) {
//                db.addEmployee(name, email);
//                employeeList.setAll(db.listEmployees());
//                nameField.clear();
//                emailField.clear();
//            }
//        });
//
//        Button deleteButton = new Button("Delete Selected");
//        deleteButton.setOnAction(e -> {
//            employee selected = tableView.getSelectionModel().getSelectedItem();
//            if (selected != null) {
//                db.deleteEmployee(selected.getId());
//                employeeList.setAll(db.listEmployees());
//            }
//        });
//
//        // 布局
//        HBox inputBox = new HBox(10, nameField, emailField, addButton, deleteButton);
//        inputBox.setPadding(new Insets(10));
//
//        VBox root = new VBox(10, tableView, inputBox);
//        root.setPadding(new Insets(10));
//
//        // 场景设置
//        Scene scene = new Scene(root, 600, 400);
//        primaryStage.setTitle("Employee Management");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}