package fr.isep.ye.projet_algo_jjx;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ListView;
import org.w3c.dom.Text;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        Menu projectMenu = new Menu("项目");
        MenuItem addProjetItem = new MenuItem("添加项目");
        MenuItem deleteProjetItem=new MenuItem("删除项目");
        MenuItem updateProjetItem = new MenuItem("修改项目信息");
        MenuItem viewProjectsItem = new MenuItem("查看项目");
        projectMenu.getItems().addAll(addProjetItem, deleteProjetItem,updateProjetItem,viewProjectsItem);

        // 创建 "任务" 菜单
        Menu taskMenu = new Menu("任务");
        MenuItem addTacheItem = new MenuItem("添加任务");
        MenuItem deleteTacheItem=new MenuItem("删除项目");
        MenuItem updateTacheItem = new MenuItem("修改任务信息");
        MenuItem viewTacheItem = new MenuItem("查看任务");
        taskMenu.getItems().addAll(addTacheItem, deleteTacheItem,updateTacheItem,viewTacheItem);

        // 将菜单添加到菜单栏
        menuBar.getMenus().addAll(employeeMenu, projectMenu, taskMenu);

        // 主布局
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(bienvenue);

        // 添加员工功能
        VBox addEmployeeLayout = createAddEmployeeLayout();
        VBox addProjetLayout = createAddProjetLayout();
        VBox addTacheLayout = createAddTacheLayout();
        // 事件处理
        addEmployeeItem.setOnAction(e -> root.setCenter(addEmployeeLayout));
        addProjetItem.setOnAction(e -> root.setCenter(addProjetLayout));
        addTacheItem.setOnAction(e -> root.setCenter(addTacheLayout));

        VBox deleteEmployeeLayout = createdeleteEmployeeLayout();
        VBox deleteProjetLayout = createdeleteProjetLayout();
        VBox deleteTacheLayout =createDeleteTacheLayout();

        deleteEmployeeItem.setOnAction(e -> root.setCenter(deleteEmployeeLayout));
        deleteProjetItem.setOnAction(e->root.setCenter(deleteProjetLayout));
        deleteTacheItem.setOnAction(e->root.setCenter(deleteTacheLayout));

        VBox updateEmployeeLayout = createupdateEmployeeLayout();
        VBox updateProjetLayout = createupdateProjetLayout();
        VBox updateTacheLayout =createUpdateTacheLayout();

        updateEmployeeItem.setOnAction(e -> root.setCenter(updateEmployeeLayout));
        updateProjetItem.setOnAction(e -> root.setCenter(updateProjetLayout));
        updateTacheItem.setOnAction(e->root.setCenter(updateTacheLayout));

        viewEmployeesItem.setOnAction(e -> root.setCenter(createViewEmployeesLayout()));
        viewProjectsItem.setOnAction(e -> root.setCenter(createViewProjetLayout()));
        viewTacheItem.setOnAction(e -> root.setCenter(createViewTacheLayout()));

        Scene scene = new Scene(root, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Management System");
        primaryStage.show();
    }

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
    private VBox createAddEmployeeLayout() {

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

    private VBox createdeleteEmployeeLayout() {
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

    private VBox createupdateEmployeeLayout() {
        Label employeeIdLabel = new Label("PS: 员工ID不能修改: ");
        TextField employeeIdField = createTextField("员工ID");
        TextField employeeNameField = createTextField("修改姓名");
        TextField employeeSexField = createTextField("修改性别(H/F)");
        TextField employeeAgeField = createTextField("修改年龄");
        TextField employeeEmailField = createTextField("修改邮箱");

        Button updateEmployeeButton = new Button("修改信息");
        //Label messageLabel = new Label();弹出窗口显示执行结果
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
                int newAge=Integer.parseInt(newAgetext);

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
        VBox layout = new VBox(10, employeeIdField,employeeIdLabel, employeeNameField, employeeSexField, employeeAgeField, employeeEmailField, updateEmployeeButton);
        layout.setPadding(new Insets(20)); // 设置内边距
        layout.setAlignment(Pos.CENTER); // 居中对齐
        return layout;
    }


    private VBox createViewEmployeesLayout() {
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
    private VBox createAddProjetLayout() {
        // 创建 gestionProjet 的实例
        gestionProjet projetManager = new gestionProjet(db);

        // 创建文本框
        TextField projetIdField = createTextField("项目id");
        TextField projetNameField = createTextField("项目名称");
        TextField projetGroupField = createTextField("项目小组");
        TextField projetDdlField = createTextField("项目截止时间 (YYYY-MM-DD)");
        ComboBox<String> projetStatusComboBox = new ComboBox<>();
        projetStatusComboBox.getItems().addAll("未开始", "进行中", "已完成", "已取消");
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
                    int selectedEmployeeId = projetManager.getEmployeeIdByName(employeeName); // 获取员工ID
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
        VBox layout = new VBox(10, projetIdField, projetNameField, projetGroupField, projetDdlField, projetStatusComboBox, instructions, new Label("项目成员:"),employeeListView, addProjetButton);
        layout.setPadding(new Insets(20)); // 设置内边距
        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    private VBox createdeleteProjetLayout() {
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

    private VBox createupdateProjetLayout() {
        gestionProjet projetManager = new gestionProjet(db);

        Label projetIdLabel = new Label("PS: 项目ID不能修改: ");
        TextField projetIdField = createTextField("项目ID");
        TextField projetNameField = createTextField("修改项目名称");
        TextField projetGroupField = createTextField("项目小组");
        TextField projetDdlField = createTextField("修改截止日期 (YYYY-MM-DD)");
        ComboBox<String> projetStatusComboBox = new ComboBox<>();
        projetStatusComboBox.getItems().addAll("未开始","进行中", "已完成", "已取消");
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
                    int selectedEmployeeId = projetManager.getEmployeeIdByName(employeeName);
                    if (selectedEmployeeId != -1) {
                        memberIds.add(selectedEmployeeId);
                    }
                }
                projetManager.updateProjet(id,newName,newGroup,newDdltext,newStatus, memberIds);

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
        VBox layout = new VBox(10, projetIdField,projetIdLabel, projetNameField, projetGroupField, projetDdlField, projetStatusComboBox, employeeListView, updateProjetButton);
        layout.setPadding(new Insets(20)); // 设置内边距
        layout.setAlignment(Pos.CENTER); // 居中对齐
        return layout;
    }

    private VBox createViewProjetLayout() {
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
                    Date deadline = data.getValue().getDdl();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // 你可以选择其他格式
                    return new SimpleStringProperty(deadline != null ? dateFormat.format(deadline) : "无截止日期");
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
    private VBox createAddTacheLayout() {

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
                    int employeeId = gestionTache.getEmployeeIdByName(employeeName); // 假设有这个方法获取员工ID
                    selectedEmployeeIds.add(employeeId);
                }

                // 获取选中的项目ID
                List<Integer> selectedProjetIds = new ArrayList<>();
                for (String projetName : projetListView.getSelectionModel().getSelectedItems()) {
                    int projetId = gestionTache.getProjetIdByName(projetName); // 假设有这个方法获取项目ID
                    selectedProjetIds.add(projetId);
                }

                // 调用添加任务的方法
                gestionTache.addTache(id, name, deadline, category, description,selectedEmployeeIds, selectedProjetIds);

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

        VBox layout = new VBox(10, tacheIdField, tacheNameField, tacheDdlField, tacheCategField, tacheDescriptionArea,ps,
                new Label("分配员工:"), employeeListView,
                new Label("属于项目:"), projetListView,
                addTacheButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    private VBox createViewTacheLayout() {

        TableView<tache> tacheTableView = new TableView<>();
        // 定义各个列
        TableColumn<tache, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<tache, Integer> nameColumn = new TableColumn<>("Nom");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<tache, String> ddlColumn = new TableColumn<>("Deadline");
        ddlColumn.setCellValueFactory(data -> {
            Date deadline = data.getValue().getDdl();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // 你可以选择其他格式
            return new SimpleStringProperty(deadline != null ? dateFormat.format(deadline) : "无截止日期");
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

        tacheTableView.getColumns().addAll(idColumn, nameColumn, ddlColumn, categColumn, descripColumn,employeeNameColumn,projetNameColumn);

        // 加载数据
        gestionTache gestionTache = new gestionTache(db);
        try {
            List<tache> taches = gestionTache.listTache();
            System.out.println("Loaded tasks: " + taches.size());
            tacheTableView.getItems().addAll(taches);
        } catch (SQLException ex) {
            ex.printStackTrace();
            tacheTableView.getItems().add(new tache(-1, "无法加载任务信息", null, "", "", new ArrayList<>(), new ArrayList<>()));
        }

       return new VBox(tacheTableView);
    }

    private VBox createUpdateTacheLayout() {
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
                    int employeeId = tacheManager.getEmployeeIdByName(employeeName);
                    if (employeeId != -1) {
                        employeeIds.add(employeeId);
                    }
                }

                List<Integer> projectIds = new ArrayList<>();
                for (String projectName : projectListView.getSelectionModel().getSelectedItems()) {
                    int projectId = tacheManager.getProjetIdByName(projectName);
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

            }catch (NumberFormatException ex) {
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
        VBox layout = new VBox(10, tacheIdField,tacheIdLabel,tacheNameField,tacheDdlField,tacheCategoryField,tacheDescriptionArea,ps,tacheEmployeeLabel,employeeListView,tacheProjetLabel,projectListView, updateTacheButton);
        layout.setPadding(new Insets(20)); // 设置内边距
        layout.setAlignment(Pos.CENTER); // 居中对齐
        return layout;
    }

    private VBox createDeleteTacheLayout() {
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
        VBox layout = new VBox(10, tacheIdLabel,tacheIdField, deleteTacheButton);
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

    public static void main(String[] args) {
        launch(args);
    }
}