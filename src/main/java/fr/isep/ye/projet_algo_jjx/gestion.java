package fr.isep.ye.projet_algo_jjx;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.ListView;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class gestion extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 创建菜单
        MenuBar menuBar = new MenuBar();
        Label bienvenue = new Label("Bonjour!");

        // 创建 "员工" 菜单
        Menu employeeMenu = new Menu("员工");
        MenuItem addEmployeeItem = new MenuItem("添加员工");
        MenuItem deleteEmployeeItem = new MenuItem("删除员工");
        MenuItem updateEmployeeItem = new MenuItem("修改员工信息");
        MenuItem viewEmployeesItem = new MenuItem("查看员工");
        employeeMenu.getItems().addAll(addEmployeeItem, deleteEmployeeItem, updateEmployeeItem, viewEmployeesItem);

        // 创建 "项目" 菜单
        Menu projetMenu = new Menu("项目");
        MenuItem addProjetItem = new MenuItem("添加项目");
        MenuItem deleteProjetItem = new MenuItem("删除项目");
        MenuItem updateProjetItem = new MenuItem("修改项目信息");
        MenuItem viewProjetsItem = new MenuItem("查看项目");
        MenuItem kanbanMenuItem = new MenuItem("看板视图");
        projetMenu.getItems().addAll(addProjetItem, deleteProjetItem, updateProjetItem, viewProjetsItem, kanbanMenuItem);

        // 创建 "任务" 菜单
        Menu tacheMenu = new Menu("任务");
        MenuItem addTacheItem = new MenuItem("添加任务");
        MenuItem deleteTacheItem = new MenuItem("删除任务");
        MenuItem updateTacheItem = new MenuItem("修改任务信息");
        MenuItem viewTacheItem = new MenuItem("查看任务");
        tacheMenu.getItems().addAll(addTacheItem, deleteTacheItem, updateTacheItem, viewTacheItem);

        Menu viewMenu = new Menu("视图");
        MenuItem viewCalendarItem = new MenuItem("日历视图");
        viewMenu.getItems().addAll(viewCalendarItem);
        viewCalendarItem.setOnAction(e -> {
            try {
                viewCalendar(mysql.getConnection()); // 调用日历视图方法，并传入数据库连接
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        // 将菜单添加到菜单栏
        menuBar.getMenus().addAll(employeeMenu, projetMenu, tacheMenu, viewMenu);

        // 主布局
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(bienvenue);

        // 添加员工功能
        VBox addEmployeeLayout = addEmployeeModule();
        VBox addProjetLayout = addProjetModule();
        VBox addTacheLayout = addTacheModule();
        // 事件处理
        addEmployeeItem.setOnAction(e -> root.setCenter(addEmployeeLayout));
        addProjetItem.setOnAction(e -> root.setCenter(addProjetLayout));
        addTacheItem.setOnAction(e -> root.setCenter(addTacheLayout));

        VBox deleteEmployeeLayout = deleteEmployeeModule();
        VBox deleteProjetLayout = deleteProjetModule();
        VBox deleteTacheLayout = deleteTacheModule();

        deleteEmployeeItem.setOnAction(e -> root.setCenter(deleteEmployeeLayout));
        deleteProjetItem.setOnAction(e -> root.setCenter(deleteProjetLayout));
        deleteTacheItem.setOnAction(e -> root.setCenter(deleteTacheLayout));

        VBox updateEmployeeLayout = updateEmployeeModule();
        VBox updateProjetLayout = updateProjetModule();
        VBox updateTacheLayout = updateTacheModule();

        updateEmployeeItem.setOnAction(e -> root.setCenter(updateEmployeeLayout));
        updateProjetItem.setOnAction(e -> root.setCenter(updateProjetLayout));
        updateTacheItem.setOnAction(e -> root.setCenter(updateTacheLayout));

        viewEmployeesItem.setOnAction(e -> root.setCenter(viewEmployeesModule()));
        viewProjetsItem.setOnAction(e -> root.setCenter(viewProjetModule()));
        viewTacheItem.setOnAction(e -> root.setCenter(viewTacheModule()));
        kanbanMenuItem.setOnAction(e -> root.setCenter(viewKanbanModule()));


        Scene scene = new Scene(root, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Management System");
        primaryStage.show();
    }


    private GridPane calendarGrid; // 日历网格
    private DatePicker datePicker;

    private mysql db = new mysql();

    private static TextField createTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setPrefWidth(200);
        textField.setMaxWidth(200);
        textField.setMinWidth(200);
        return textField;
    }

    //创建添加员工的布局
    private VBox addEmployeeModule() {

        TextField employeeIdField = createTextField("员工ID");
        TextField employeeNameField = createTextField("姓名");
        TextField employeeSexField = createTextField("性别(H/F)");
        TextField employeeAgeField = createTextField("年龄");
        TextField employeeEmailField = createTextField("电子邮件");

        Button addEmployeeButton = new Button("添加员工");
        addEmployeeButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = employeeIdField.getText();
                if (idText == null || idText.trim().isEmpty()) {
                    throw new IllegalArgumentException("ID cannot be null or empty.");
                }

                String ageText = employeeAgeField.getText();
                int id = Integer.parseInt(idText);
                String nom = employeeNameField.getText();
                String sexe = employeeSexField.getText();
                int age = Integer.parseInt(ageText);
                String email = employeeEmailField.getText();

                gestionEmplo gestionEmplo = new gestionEmplo(db);
                gestionEmplo.addEmployee(id, nom, sexe, age, email);
                // 显示成功提示
                alertMessage = "员工已成功添加！";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);

                // 清空输入框
                employeeIdField.clear();
                employeeNameField.clear();
                employeeSexField.clear();
                employeeAgeField.clear();
                employeeEmailField.clear();

            } catch (NumberFormatException ex) {
                alertMessage = "ID 格式无效，请输入数字。";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning！", alertMessage);
            } catch (Exception ex) {
                alertMessage = "添加员工时发生错误: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }
        });

        VBox layout = new VBox(10, employeeIdField, employeeNameField, employeeSexField, employeeAgeField, employeeEmailField, addEmployeeButton);
        layout.setPadding(new Insets(20)); // 设置内边距
        layout.setAlignment(Pos.CENTER); // 居中对齐

        return layout;
    }

    private VBox deleteEmployeeModule() {
        Label employeeIdLabel = new Label("员工id:");
        TextField employeeIdField = createTextField("");

        Button deleteEmployeeButton = new Button("删除员工");

        deleteEmployeeButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = employeeIdField.getText();
                if (idText == null || idText.trim().isEmpty()) {
                    throw new IllegalArgumentException("ID cannot be null or empty.");
                }

                int id = Integer.parseInt(idText);

                // 调用 GestionEmplo 的 addEmployee 方法
                gestionEmplo gestionEmplo = new gestionEmplo(db);
                gestionEmplo.deleteEmployee(id);

                alertMessage = "已成功删除！";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);

                employeeIdField.clear();

            } catch (NumberFormatException ex) {
                alertMessage = "ID 格式无效，请输入数字。";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning!", alertMessage);
            } catch (Exception ex) {
                alertMessage = "删除员工时发生错误: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }
        });
        VBox layout = new VBox(20, employeeIdLabel, employeeIdField, deleteEmployeeButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    private VBox updateEmployeeModule() {
        Label employeeIdLabel = new Label("PS: 员工ID不能修改: ");
        TextField employeeIdField = createTextField("员工ID");
        TextField employeeNameField = createTextField("修改姓名");
        TextField employeeSexField = createTextField("修改性别(H/F)");
        TextField employeeAgeField = createTextField("修改年龄");
        TextField employeeEmailField = createTextField("修改邮箱");

        Button updateEmployeeButton = new Button("修改信息");
        updateEmployeeButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = employeeIdField.getText();
                if (idText == null || idText.trim().isEmpty()) {
                    throw new IllegalArgumentException("ID cannot be null or empty.");
                }
                int id = Integer.parseInt(idText);
                String newName = employeeNameField.getText();
                String newSex = employeeSexField.getText();
                String newAgetext = employeeAgeField.getText();
                String newEmail = employeeEmailField.getText();
                int newAge = Integer.parseInt(newAgetext);

                // 调用 GestionEmplo 的 addEmployee 方法
                gestionEmplo gestionEmplo = new gestionEmplo(db);
                gestionEmplo.updateEmployee(id, newName, newSex, newAge, newEmail);

                alertMessage = "员工信息已成功修改！";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);

                employeeNameField.clear();
                employeeSexField.clear();
                employeeAgeField.clear();
                employeeEmailField.clear();

            } catch (NumberFormatException ex) {
                alertMessage = "ID 格式无效，请输入数字。";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning！", alertMessage);
            } catch (Exception ex) {
                alertMessage = "修改员工信息时发生错误: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }

        });
        VBox layout = new VBox(10, employeeIdField, employeeIdLabel, employeeNameField, employeeSexField, employeeAgeField, employeeEmailField, updateEmployeeButton);
        layout.setPadding(new Insets(20)); // 设置内边距
        layout.setAlignment(Pos.CENTER); // 居中对齐
        return layout;
    }


    private VBox viewEmployeesModule() {
        TableView<employee> employeeTableView = new TableView<>();
        TableColumn<employee, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<employee, String> nameColumn = new TableColumn<>("Nom");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<employee, String> sexColumn = new TableColumn<>("Sexe");
        sexColumn.setCellValueFactory(new PropertyValueFactory<>("sex"));
        TableColumn<employee, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        TableColumn<employee, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<employee, String> projetColumn = new TableColumn<>("已完成项目");
        projetColumn.setCellValueFactory(data -> {
            employee emp = data.getValue();
            try {
                gestionEmplo gestionEmplo = new gestionEmplo(db);
                String projet = gestionEmplo.employee_projet(emp.getId());
                return new SimpleStringProperty(projet);
            } catch (SQLException ex) {
                ex.printStackTrace();
                return new SimpleStringProperty("加载失败");
            }
        });

        // 将列添加到表格中
        employeeTableView.getColumns().addAll(idColumn, nameColumn, sexColumn, ageColumn, emailColumn, projetColumn);

        gestionEmplo employeeManager = new gestionEmplo(db);
        try {
            employeeManager.listEmployee(employeeTableView);
        } catch (SQLException ex) {
            ex.printStackTrace();
            employeeTableView.getItems().add(new employee(-1, "无法加载员工列表", "", -1, ""));
        }

        return new VBox(employeeTableView);
    }

    // 创建添加项目的布局
    private VBox addProjetModule() {
        // 创建 gestionProjet 的实例
        gestionProjet projetManager = new gestionProjet(db);

        // 创建文本框
        TextField projetIdField = createTextField("项目id");
        TextField projetNameField = createTextField("项目名称");
        TextField projetGroupField = createTextField("项目小组");
        TextField projetDdlField = createTextField("项目截止时间 (YYYY-MM-DD)");
        ComboBox<String> projetStatusComboBox = new ComboBox<>();
        projetStatusComboBox.getItems().addAll("待办", "进行中", "已完成");
        projetStatusComboBox.setPromptText("选择项目状态");

        Label instructions = new Label("按住 Ctrl/Cmd 或 Shift 键以多选员工");
        // 创建员工选择 ListView
        ListView<String> employeeListView = new ListView<>();
        employeeListView.setPrefWidth(300);
        employeeListView.setMaxWidth(300);
        employeeListView.setMinWidth(300);
        // 假设从数据库获取员工名称
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT nom FROM employee");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                employeeListView.getItems().add(rs.getString("nom"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        employeeListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 创建添加项目按钮
        Button addProjetButton = new Button("添加项目");
        addProjetButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                int id = Integer.parseInt(projetIdField.getText());
                String name = projetNameField.getText();
                String group = projetGroupField.getText();
                String ddl = projetDdlField.getText();
                String status = projetStatusComboBox.getValue();

                // 获取选中的员工ID
                List<Integer> selectedMemberIds = new ArrayList<>();
                for (String employeeName : employeeListView.getSelectionModel().getSelectedItems()) {
                    // 通过员工姓名获取员工ID
                    int selectedEmployeeId = projetManager.getEmployeeId(employeeName); // 获取员工ID
                    if (selectedEmployeeId != -1) {
                        selectedMemberIds.add(selectedEmployeeId);
                    }
                }

                // 调用添加项目的方法
                projetManager.addProjet(id, name, group, ddl, status, selectedMemberIds);

                alertMessage = "项目已成功添加！";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);
                // 清空输入框
                projetIdField.clear();
                projetNameField.clear();
                projetGroupField.clear();
                projetDdlField.clear();
                projetStatusComboBox.setValue(null);
                employeeListView.getSelectionModel().clearSelection();// 清空选中的员工

            } catch (NumberFormatException ex) {
                alertMessage = "ID 格式无效，请输入数字。";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning！", alertMessage);
            } catch (Exception ex) {
                alertMessage = "添加项目时发生错误: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }
        });

        // 创建布局并返回
        VBox layout = new VBox(10, projetIdField, projetNameField, projetGroupField, projetDdlField, projetStatusComboBox, instructions, new Label("项目成员:"), employeeListView, addProjetButton);
        layout.setPadding(new Insets(20)); // 设置内边距
        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    private VBox deleteProjetModule() {
        Label projetIdLabel = new Label("项目id:");
        TextField projetIdField = createTextField("");

        Button deleteProjetButton = new Button("删除项目");
        //Label messageLabel = new Label();弹出窗口显示执行结果
        deleteProjetButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = projetIdField.getText(); // projetIdField 是输入项目 ID 的 TextField
                if (idText == null || idText.trim().isEmpty()) {
                    throw new IllegalArgumentException("项目ID不能为空。");
                }

                int projetId = Integer.parseInt(idText);

                // 调用 deleteProjet 方法
                gestionProjet gestionProjet = new gestionProjet(db);
                gestionProjet.deleteProjet(projetId);

                // 显示成功提示
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                alertMessage = "项目已成功删除！";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);
                // 清空输入框
                projetIdField.clear();

            } catch (NumberFormatException ex) {
                alertMessage = "ID 格式无效，请输入数字。";
                showAlert(Alert.AlertType.ERROR, "错误", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "警告", alertMessage);
            } catch (Exception ex) {
                alertMessage = "删除项目时发生错误: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "错误", alertMessage);
            }

        });
        VBox layout = new VBox(20, projetIdLabel, projetIdField, deleteProjetButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    private VBox updateProjetModule() {
        gestionProjet projetManager = new gestionProjet(db);

        Label projetIdLabel = new Label("PS: 项目ID不能修改: ");
        TextField projetIdField = createTextField("项目ID");
        TextField projetNameField = createTextField("修改项目名称");
        TextField projetGroupField = createTextField("项目小组");
        TextField projetDdlField = createTextField("修改截止日期 (YYYY-MM-DD)");
        ComboBox<String> projetStatusComboBox = new ComboBox<>();
        projetStatusComboBox.getItems().addAll("待办", "进行中", "已完成");
        projetStatusComboBox.setPromptText("选择项目状态");

        ListView<String> employeeListView = new ListView<>();
        employeeListView.setPrefWidth(300);
        employeeListView.setMaxWidth(300);
        employeeListView.setMinWidth(300);
        // 假设从数据库获取员工名称
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT nom FROM employee");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                employeeListView.getItems().add(rs.getString("nom"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        employeeListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button updateProjetButton = new Button("修改项目信息");
        //Label messageLabel = new Label();弹出窗口显示执行结果
        updateProjetButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = projetIdField.getText();
                if (idText == null || idText.trim().isEmpty()) {
                    throw new IllegalArgumentException("ID cannot be null or empty.");
                }
                int id = Integer.parseInt(idText);
                String newName = projetNameField.getText();
                String newGroup = projetGroupField.getText();
                String newDdltext = projetDdlField.getText();
                String newStatus = projetStatusComboBox.getValue();

                List<Integer> memberIds = new ArrayList<>();
                for (String employeeName : employeeListView.getSelectionModel().getSelectedItems()) {
                    int selectedEmployeeId = projetManager.getEmployeeId(employeeName);
                    if (selectedEmployeeId != -1) {
                        memberIds.add(selectedEmployeeId);
                    }
                }
                projetManager.updateProjet(id, newName, newGroup, newDdltext, newStatus, memberIds);

                alertMessage = "项目信息已成功修改！";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);

                projetNameField.clear();
                projetGroupField.clear();
                projetDdlField.clear();
                projetStatusComboBox.getSelectionModel().clearSelection();

            } catch (NumberFormatException ex) {
                alertMessage = "ID 格式无效，请输入数字。";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning！", alertMessage);
            } catch (Exception ex) {
                alertMessage = "修改项目信息时发生错误: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }
        });
        VBox layout = new VBox(10, projetIdField, projetIdLabel, projetNameField, projetGroupField, projetDdlField, projetStatusComboBox, employeeListView, updateProjetButton);
        layout.setPadding(new Insets(20)); // 设置内边距
        layout.setAlignment(Pos.CENTER); // 居中对齐
        return layout;
    }

    private VBox viewProjetModule() {
        TableView<projet> projetTableView = new TableView<>();

        // 定义各个列
        TableColumn<projet, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<projet, String> nameColumn = new TableColumn<>("Nom");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<projet, String> groupColumn = new TableColumn<>("Groupe");
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("group"));

        TableColumn<projet, String> ddlColumn = new TableColumn<>("Deadline");
        ddlColumn.setCellValueFactory(data -> {
            LocalDate deadline = data.getValue().getDdl(); // 假设 getDdl() 返回 LocalDate
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 你可以选择其他格式
            return new SimpleStringProperty(deadline != null ? deadline.format(dateFormat) : "无截止日期");
        });

        TableColumn<projet, String> statusColumn = new TableColumn<>("Statut");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 定义成员列
        TableColumn<projet, String> memberColumn = new TableColumn<>("Membre");
        memberColumn.setCellValueFactory(data -> {
            projet project = data.getValue();
            List<String> members = project.getMembers(); // 假设已经加载成员数据
            if (members == null || members.isEmpty()) {
                return new SimpleStringProperty("无成员");
            }
            // 将成员姓名拼接成一个字符串
            String memberNames = String.join(", ", members);
            return new SimpleStringProperty(memberNames);
        });


        // 将所有列添加到表格中
        projetTableView.getColumns().addAll(idColumn, nameColumn, groupColumn, ddlColumn, statusColumn, memberColumn);

        // 获取项目管理类的实例
        gestionProjet projetManager = new gestionProjet(db);
        try {
            projetManager.listProjet(projetTableView);
        } catch (SQLException ex) {
            ex.printStackTrace();
            projetTableView.getItems().add(new projet(-1, "无法加载成员列表", "", null, "", new ArrayList<>()));
        }

        return new VBox(projetTableView);
    }

    // 创建添加任务的布局
    private VBox addTacheModule() {

        gestionTache gestionTache = new gestionTache(db);

        // 创建文本框
        TextField tacheIdField = createTextField("任务id");
        TextField tacheNameField = createTextField("任务名称");
        TextField tacheDdlField = createTextField("截止日期 (YYYY-MM-DD)");
        TextField tacheCategField = createTextField("任务类别");
        TextArea tacheDescriptionArea = new TextArea();
        tacheDescriptionArea.setPromptText("任务注释（详细描述）");
        tacheDescriptionArea.setPrefHeight(350);
        tacheDescriptionArea.setPrefWidth(200);
        tacheDescriptionArea.setMaxWidth(200);
        tacheDescriptionArea.setMinWidth(200);
        Label ps = new Label("按住 Ctrl/Cmd 或 Shift 键进行多选");

        // 创建员工选择 ListView
        ListView<String> employeeListView = new ListView<>();
        employeeListView.setPrefWidth(300);
        employeeListView.setMaxWidth(300);
        employeeListView.setMinWidth(300);
        // 假设从数据库获取员工名称
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT nom FROM employee");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                employeeListView.getItems().add(rs.getString("nom"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        employeeListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 创建项目选择 ListView
        ListView<String> projetListView = new ListView<>();
        projetListView.setPrefWidth(300);
        projetListView.setMaxWidth(300);
        projetListView.setMinWidth(300);
        // 假设从数据库获取项目名称
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT nom FROM projet");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                projetListView.getItems().add(rs.getString("nom"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        projetListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 创建添加任务按钮
        Button addTacheButton = new Button("添加任务");
        addTacheButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = tacheIdField.getText();
                int id = Integer.parseInt(idText);
                String name = tacheNameField.getText();
                String deadline = tacheDdlField.getText();
                String category = tacheCategField.getText();
                String description = tacheDescriptionArea.getText();

                // 获取选中的员工ID
                List<Integer> selectedEmployeeIds = new ArrayList<>();
                for (String employeeName : employeeListView.getSelectionModel().getSelectedItems()) {
                    int employeeId = gestionTache.getEmployeeId(employeeName); // 假设有这个方法获取员工ID
                    selectedEmployeeIds.add(employeeId);
                }

                // 获取选中的项目ID
                List<Integer> selectedProjetIds = new ArrayList<>();
                for (String projetName : projetListView.getSelectionModel().getSelectedItems()) {
                    int projetId = gestionTache.getProjetId(projetName); // 假设有这个方法获取项目ID
                    selectedProjetIds.add(projetId);
                }

                // 调用添加任务的方法
                gestionTache.addTache(id, name, deadline, category, description, selectedEmployeeIds, selectedProjetIds);

                alertMessage = "任务已成功添加！";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);

                // 清空输入框
                tacheIdField.clear();
                tacheNameField.clear();
                tacheDdlField.clear();
                tacheCategField.clear();
                tacheDescriptionArea.clear();
                employeeListView.getSelectionModel().clearSelection();
                projetListView.getSelectionModel().clearSelection();

            } catch (NumberFormatException ex) {
                alertMessage = "ID 格式无效，请输入数字。";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning！", alertMessage);
            } catch (Exception ex) {
                alertMessage = "添加任务时发生错误: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }
        });

        VBox layout = new VBox(10, tacheIdField, tacheNameField, tacheDdlField, tacheCategField, tacheDescriptionArea, ps,
                new Label("分配员工:"), employeeListView,
                new Label("属于项目:"), projetListView,
                addTacheButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    private VBox viewTacheModule() {
        TableView<tache> tacheTableView = new TableView<>();
        // 定义各个列
        TableColumn<tache, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<tache, Integer> nameColumn = new TableColumn<>("Nom");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<tache, String> ddlColumn = new TableColumn<>("Deadline");
        ddlColumn.setCellValueFactory(data -> {
            LocalDate deadline = data.getValue().getDdl(); // 假设 getDdl() 返回 LocalDate
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 你可以选择其他格式
            return new SimpleStringProperty(deadline != null ? deadline.format(dateFormat) : "无截止日期");
        });
        TableColumn<tache, Integer> categColumn = new TableColumn<>("Categorie");
        categColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<tache, Integer> descripColumn = new TableColumn<>("Description");
        descripColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        // 设置列的属性
        TableColumn<tache, String> employeeNameColumn = new TableColumn<>("分配的员工");
        employeeNameColumn.setCellValueFactory(data -> {
            List<String> employees = data.getValue().getEmployeeName();
            return new SimpleStringProperty(employees != null ? String.join(", ", employees) : "无分配员工");
        });
        TableColumn<tache, String> projetNameColumn = new TableColumn<>("所属项目");
        projetNameColumn.setCellValueFactory(data -> {
            List<String> projets = data.getValue().getProjetName();
            return new SimpleStringProperty(projets != null ? String.join(", ", projets) : "错误");
        });

        tacheTableView.getColumns().addAll(idColumn, nameColumn, ddlColumn, categColumn, descripColumn, employeeNameColumn, projetNameColumn);

        // 加载数据
        gestionTache gestionTache = new gestionTache(db);

        loadData(tacheTableView, gestionTache, "id");

        // 创建排序按钮
        Button sortByIdButton = new Button("按 ID 排序");
        Button sortByDeadlineButton = new Button("按 Deadline 排序");

        // 按钮事件绑定
        sortByIdButton.setOnAction(event -> loadData(tacheTableView, gestionTache, "id"));
        sortByDeadlineButton.setOnAction(event -> loadData(tacheTableView, gestionTache, "deadline"));

        // 布局设置
        HBox buttonBox = new HBox(10, sortByIdButton, sortByDeadlineButton);
        VBox layout = new VBox(10, buttonBox, tacheTableView);
        layout.setPadding(new Insets(10));

        return layout;
    }
    private void loadData(TableView<tache> tacheTableView, gestionTache gestionTache, String order) {
        tacheTableView.getItems().clear();
        try {
            List<tache> taches = gestionTache.listTache(order);
            tacheTableView.getItems().addAll(taches);
        } catch (SQLException ex) {
            ex.printStackTrace();
            tacheTableView.getItems().add(new tache(-1, "无法加载任务信息", null, "", "", new ArrayList<>(), new ArrayList<>()));
        }
    }

    private VBox updateTacheModule() {
        gestionTache tacheManager = new gestionTache(db);
        // 标签和输入框
        Label tacheIdLabel = new Label("PS: 任务ID不能修改");
        Label tacheEmployeeLabel = new Label("修改分配员工：");
        Label tacheProjetLabel = new Label("修改所属项目：");
        Label ps = new Label("按住 Ctrl/Cmd 或 Shift 键进行多选");
        TextField tacheIdField = createTextField("任务ID");

        TextField tacheNameField = createTextField("修改任务名称");
        TextField tacheCategoryField = createTextField("修改任务类别");
        TextField tacheDdlField = createTextField("修改截止日期 (YYYY-MM-DD)");
        TextArea tacheDescriptionArea = new TextArea();
        tacheDescriptionArea.setPromptText("修改任务描述");
        tacheDescriptionArea.setPrefHeight(350);
        tacheDescriptionArea.setPrefWidth(200);
        tacheDescriptionArea.setMaxWidth(200);
        tacheDescriptionArea.setMinWidth(200);// 设置多行文本区域高度

        ListView<String> employeeListView = new ListView<>();
        employeeListView.setPrefWidth(300);
        employeeListView.setMaxWidth(300);
        employeeListView.setMinWidth(300);
        // 假设从数据库获取员工名称
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT nom FROM employee");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                employeeListView.getItems().add(rs.getString("nom"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        employeeListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ListView<String> projectListView = new ListView<>();
        projectListView.setPrefWidth(300);
        projectListView.setMaxWidth(300);
        projectListView.setMinWidth(300);
        // 假设从数据库获取项目名称
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT nom FROM projet");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                projectListView.getItems().add(rs.getString("nom"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        projectListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // 按钮
        Button updateTacheButton = new Button("修改任务信息");
        updateTacheButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = tacheIdField.getText();
                if (idText == null || idText.trim().isEmpty()) {
                    throw new IllegalArgumentException("ID不能为空。");
                }
                int id = Integer.parseInt(idText);

                String newName = tacheNameField.getText();
                String newCategory = tacheCategoryField.getText();
                String newDdltext = tacheDdlField.getText();
                String newDescription = tacheDescriptionArea.getText();

                List<Integer> employeeIds = new ArrayList<>();
                for (String employeeName : employeeListView.getSelectionModel().getSelectedItems()) {
                    int employeeId = tacheManager.getEmployeeId(employeeName);
                    if (employeeId != -1) {
                        employeeIds.add(employeeId);
                    }
                }

                List<Integer> projectIds = new ArrayList<>();
                for (String projectName : projectListView.getSelectionModel().getSelectedItems()) {
                    int projectId = tacheManager.getProjetId(projectName);
                    if (projectId != -1) {
                        projectIds.add(projectId);
                    }
                }

                tacheManager.updateTache(id, newName, newDdltext, newCategory, newDescription, employeeIds, projectIds);

                alertMessage = "任务信息已成功修改！";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);

                // 清空输入框
                tacheNameField.clear();
                tacheCategoryField.clear();
                tacheDdlField.clear();
                tacheDescriptionArea.clear();
                employeeListView.getSelectionModel().clearSelection();
                projectListView.getSelectionModel().clearSelection();

            } catch (NumberFormatException ex) {
                alertMessage = "ID 格式无效，请输入数字。";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning！", alertMessage);
            } catch (Exception ex) {
                alertMessage = "修改任务信息时发生错误: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }
        });

        // 布局
        VBox layout = new VBox(10, tacheIdField, tacheIdLabel, tacheNameField, tacheDdlField, tacheCategoryField, tacheDescriptionArea, ps, tacheEmployeeLabel, employeeListView, tacheProjetLabel, projectListView, updateTacheButton);
        layout.setPadding(new Insets(20)); // 设置内边距
        layout.setAlignment(Pos.CENTER); // 居中对齐
        return layout;
    }

    private VBox deleteTacheModule() {
        gestionTache tacheManager = new gestionTache(db);

        // 输入任务 ID 的文本框
        Label tacheIdLabel = new Label("任务id:");
        TextField tacheIdField = createTextField("");
        Button deleteTacheButton = new Button("删除任务");

        // 按钮点击事件

        deleteTacheButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = tacheIdField.getText(); // tacheIdField 是输入任务 ID 的 TextField
                if (idText == null || idText.trim().isEmpty()) {
                    throw new IllegalArgumentException("任务ID不能为空。");
                }

                int tacheId = Integer.parseInt(idText);

                // 调用 deleteTache 方法
                gestionTache gestionTache = new gestionTache(db);
                gestionTache.deleteTache(tacheId);

                // 显示成功提示
                alertMessage = "已成功删除！";
                showAlert(Alert.AlertType.INFORMATION, "成功", alertMessage);

                // 清空输入框
                tacheIdField.clear();

            } catch (NumberFormatException ex) {
                alertMessage = "ID 格式无效，请输入数字。";
                showAlert(Alert.AlertType.ERROR, "错误", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "警告", alertMessage);
            } catch (Exception ex) {
                alertMessage = "删除任务时发生错误: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "错误", alertMessage);
            }
        });

        // 布局
        VBox layout = new VBox(10, tacheIdLabel, tacheIdField, deleteTacheButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private BorderPane viewKanbanModule() {
        BorderPane kanbanLayout = new BorderPane();
        HBox kanbanColumns = new HBox(10); // 看板列容器

        String[] statuses = {"待办", "进行中", "已完成"};
        gestionProjet projetManager = new gestionProjet(db);

        for (String status : statuses) {
            VBox column = new VBox(10);
            column.setPadding(new Insets(10));
            column.setStyle("-fx-background-color: lightgrey; -fx-border-color: black;");
            column.setPrefWidth(200);

            Label columnHeader = new Label(status);
            columnHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            column.getChildren().add(columnHeader);

            try {
                // 获取数据库中的项目列表
                List<projet> projets = projetManager.getProjets(status);
                for (projet proj : projets) {
                    Button projectButton = new Button(proj.getName());
                    projectButton.setMaxWidth(Double.MAX_VALUE);

                    // 点击事件，显示表格
                    projectButton.setOnAction(e -> showProjetDetails(proj));

                    column.getChildren().add(projectButton);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            kanbanColumns.getChildren().add(column);
        }

        kanbanLayout.setLeft(kanbanColumns);
        return kanbanLayout;
    }

    private void showProjetDetails(projet proj) {
        TableView<projet> projetTableView = new TableView<>();

        TableColumn<projet, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<projet, String> nameColumn = new TableColumn<>("Nom");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<projet, String> groupColumn = new TableColumn<>("Groupe");
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("group"));

        TableColumn<projet, String> ddlColumn = new TableColumn<>("Deadline");
        ddlColumn.setCellValueFactory(data -> {
            LocalDate deadline = data.getValue().getDdl(); // 假设 getDdl() 返回 LocalDate
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 你可以选择其他格式
            return new SimpleStringProperty(deadline != null ? deadline.format(dateFormat) : "无截止日期");
        });

        TableColumn<projet, String> membersColumn = new TableColumn<>("成员");
        membersColumn.setCellValueFactory(data -> {
            List<String> members = proj.getMembers();
            return new SimpleStringProperty(members.isEmpty() ? "无成员" : String.join(", ", members));
        });

        projetTableView.getColumns().addAll(idColumn, nameColumn, groupColumn, ddlColumn, membersColumn);
        projetTableView.getItems().add(proj);

        Stage detailStage = new Stage();
        detailStage.setTitle("项目详情");
        detailStage.setScene(new Scene(new VBox(projetTableView), 600, 400));
        detailStage.show();
    }
    private void viewCalendar(Connection connection) {
        Stage calendarStage = new Stage(); // 创建新窗口
        calendarStage.setTitle("项目截止日期日历");

        // 创建 DatePicker，用于选择日期
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setOnAction(e -> updateCalendar(connection, datePicker.getValue()));

        // 创建日历视图
        calendarGrid = new GridPane();
        calendarGrid.setVgap(5); // 设置行间距
        calendarGrid.setHgap(5); // 设置列间距

        // 设置每列和每行的大小
        for (int i = 0; i < 7; i++) { // 7列（星期日到星期六）
            calendarGrid.getColumnConstraints().add(new ColumnConstraints(80)); // 每列宽度为80像素
        }

        for (int i = 0; i < 6; i++) { // 6行（最多需要6行来显示所有日期）
            calendarGrid.getRowConstraints().add(new RowConstraints(50)); // 每行高度为50像素
        }

        // 创建布局
        VBox vbox = new VBox(datePicker, calendarGrid);
        Scene calendarScene = new Scene(vbox, 600, 400); // 修改窗口大小
        calendarStage.setScene(calendarScene);
        calendarStage.show();

        // 初始更新日历
        updateCalendar(connection, datePicker.getValue());
    }

    private void updateCalendar(Connection connection, LocalDate selectedDate) {
        // 清空之前的日历内容
        calendarGrid.getChildren().clear();

        // 获取当前月份和年份
        YearMonth yearMonth = YearMonth.from(selectedDate);
        int dayOfWeek = yearMonth.atDay(1).getDayOfWeek().getValue(); // 获取每月第一天的星期几
        int daysInMonth = yearMonth.lengthOfMonth();

        // 添加日历标题
        String[] headers = {"日", "一", "二", "三", "四", "五", "六"};
        for (int i = 0; i < headers.length; i++) {
            calendarGrid.add(new Label(headers[i]), i, 0); // 添加星期标题
        }

        // 存储截止日期和项目名称
        Map<LocalDate, String> projectDeadlines = new HashMap<>();

        // 从数据库获取项目数据并填充截止日期映射
        String sql = "SELECT id, nom, deadline FROM projet"; // 假设这是你的项目表的SQL查询

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("nom");
                LocalDate deadline = rs.getDate("deadline").toLocalDate(); // 将 java.sql.Date 转换为 LocalDate
                projectDeadlines.put(deadline, name);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 处理异常
        }

        // 填充日历日期
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), day);
            String projectInfo = projectDeadlines.get(currentDate); // 获取对应的项目名称

            Label dateLabel = new Label(day + (projectInfo != null ? "\n" + projectInfo : "")); // 显示日期和项目名称
            calendarGrid.add(dateLabel, (dayOfWeek - 1 + day) % 7, (day + dayOfWeek - 1) / 7 + 1); // 计算行和列
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
