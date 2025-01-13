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
import java.util.Locale;

public class gestion extends Application {
    @Override
    public void start(Stage primaryStage) {
        Locale.setDefault(Locale.FRANCE);
        MenuBar menuBar = new MenuBar();
        Label bienvenue = new Label("Bonjour!");

        Menu employeeMenu = new Menu("Employé");
        MenuItem addEmployeeItem = new MenuItem("Ajouter un employé");
        MenuItem deleteEmployeeItem = new MenuItem("Supprimer un employé");
        MenuItem updateEmployeeItem = new MenuItem("Modifier les informations sur les employés");
        MenuItem viewEmployeesItem = new MenuItem("Visualisation des employés");
        employeeMenu.getItems().addAll(addEmployeeItem, deleteEmployeeItem, updateEmployeeItem, viewEmployeesItem);

        Menu projetMenu = new Menu("Projet");
        MenuItem addProjetItem = new MenuItem("Ajouter un projet");
        MenuItem deleteProjetItem = new MenuItem("Supprimer un projet");
        MenuItem updateProjetItem = new MenuItem("Modifier les informations du projet");
        MenuItem viewProjetsItem = new MenuItem("Visualisation des projets");
        MenuItem kanbanMenuItem = new MenuItem("Vue Kanban");
        projetMenu.getItems().addAll(addProjetItem, deleteProjetItem, updateProjetItem, viewProjetsItem, kanbanMenuItem);

        Menu tacheMenu = new Menu("Tâches");
        MenuItem addTacheItem = new MenuItem("Ajouter une tâche");
        MenuItem deleteTacheItem = new MenuItem("Supprimer une tâche");
        MenuItem updateTacheItem = new MenuItem("Modifier les informations sur les tâches");
        MenuItem viewTacheItem = new MenuItem("Visualisation des tâches");
        tacheMenu.getItems().addAll(addTacheItem, deleteTacheItem, updateTacheItem, viewTacheItem);

        Menu viewMenu = new Menu("Vue");
        MenuItem viewCalendarItem = new MenuItem("Vue du calendrier");
        viewMenu.getItems().addAll(viewCalendarItem);
        viewCalendarItem.setOnAction(e -> {
            try {
                viewCalendar(mysql.getConnection());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        menuBar.getMenus().addAll(employeeMenu, projetMenu, tacheMenu, viewMenu);


        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(bienvenue);

        VBox addEmployeeLayout = addEmployeeModule();
        VBox addProjetLayout = addProjetModule();
        VBox addTacheLayout = addTacheModule();

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
        primaryStage.setTitle("Système de gestion de projet");
        primaryStage.show();
    }


    private GridPane calendarGrid;
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


    private VBox addEmployeeModule() {
        TextField employeeIdField = createTextField("ID de l'employé");
        TextField employeeNameField = createTextField("Nom");
        TextField employeeSexField = createTextField("Sexe(H/F)");
        TextField employeeAgeField = createTextField("Age");
        TextField employeeEmailField = createTextField("Email");

        Button addEmployeeButton = new Button("Ajouter");
        addEmployeeButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = employeeIdField.getText();
                if (idText == null || idText.trim().isEmpty()) {
                    throw new IllegalArgumentException("L'ID ne peut pas être nul ou vide.");
                }

                String ageText = employeeAgeField.getText();
                int id = Integer.parseInt(idText);
                String nom = employeeNameField.getText();
                String sexe = employeeSexField.getText();
                int age = Integer.parseInt(ageText);
                String email = employeeEmailField.getText();

                gestionEmplo gestionEmplo = new gestionEmplo(db);
                gestionEmplo.addEmployee(id, nom, sexe, age, email);

                alertMessage = "L'employé a été ajouté avec succès ！";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);

                employeeIdField.clear();
                employeeNameField.clear();
                employeeSexField.clear();
                employeeAgeField.clear();
                employeeEmailField.clear();

            } catch (NumberFormatException ex) {
                alertMessage = "Format d'ID non valide, veuillez saisir un numéro.";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning！", alertMessage);
            } catch (Exception ex) {
                alertMessage = "Une erreur s'est produite lors de l'ajout d'un employé: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }
        });

        VBox layout = new VBox(10, employeeIdField, employeeNameField, employeeSexField, employeeAgeField, employeeEmailField, addEmployeeButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }


    private VBox deleteEmployeeModule() {
        Label employeeIdLabel = new Label("ID de l'employé: ");
        TextField employeeIdField = createTextField("");

        Button deleteEmployeeButton = new Button("Suppression");

        deleteEmployeeButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = employeeIdField.getText();
                if (idText == null || idText.trim().isEmpty()) {
                    throw new IllegalArgumentException("L'ID ne peut pas être nul ou vide.");
                }

                int id = Integer.parseInt(idText);

                gestionEmplo gestionEmplo = new gestionEmplo(db);
                gestionEmplo.deleteEmployee(id);

                alertMessage = "Supprimé avec succès！";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);

                employeeIdField.clear();

            } catch (NumberFormatException ex) {
                alertMessage = "Format d'ID non valide, veuillez saisir un nombre.";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning!", alertMessage);
            } catch (Exception ex) {
                alertMessage = "Une erreur s'est produite lors de la suppression d'un employé: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }
        });
        VBox layout = new VBox(20, employeeIdLabel, employeeIdField, deleteEmployeeButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        return layout;
    }


    private VBox updateEmployeeModule() {
        Label employeeIdLabel = new Label("PS: L'ID de l'employé ne peut pas être modifié: ");
        TextField employeeIdField = createTextField("ID de l'employé");
        TextField employeeNameField = createTextField("Modifier le nom");
        TextField employeeSexField = createTextField("Modifier le sexe(H/F)");
        TextField employeeAgeField = createTextField("Modifier l'âge");
        TextField employeeEmailField = createTextField("Modifier l'email");

        Button updateEmployeeButton = new Button("Modifier");
        updateEmployeeButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = employeeIdField.getText();
                if (idText == null || idText.trim().isEmpty()) {
                    throw new IllegalArgumentException("L'ID ne peut pas être nul ou vide.");
                }
                int id = Integer.parseInt(idText);
                String newName = employeeNameField.getText();
                String newSex = employeeSexField.getText();
                String newAgetext = employeeAgeField.getText();
                String newEmail = employeeEmailField.getText();
                int newAge = Integer.parseInt(newAgetext);

                gestionEmplo gestionEmplo = new gestionEmplo(db);
                gestionEmplo.updateEmployee(id, newName, newSex, newAge, newEmail);

                alertMessage = "Les informations sur les employés ont été modifiées avec succès！";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);

                employeeNameField.clear();
                employeeSexField.clear();
                employeeAgeField.clear();
                employeeEmailField.clear();

            } catch (NumberFormatException ex) {
                alertMessage = "Le format de l'ID n'est pas valide, veuillez saisir un numéro.";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning！", alertMessage);
            } catch (Exception ex) {
                alertMessage = "Une erreur s'est produite lors de la modification des informations sur l'employé: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }

        });
        VBox layout = new VBox(10, employeeIdField, employeeIdLabel, employeeNameField, employeeSexField, employeeAgeField, employeeEmailField, updateEmployeeButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
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

        TableColumn<employee, String> projetColumn = new TableColumn<>("Projets terminé");
        projetColumn.setCellValueFactory(data -> {
            employee emp = data.getValue();
            try {
                gestionEmplo gestionEmplo = new gestionEmplo(db);
                String projet = gestionEmplo.employee_projet(emp.getId());
                return new SimpleStringProperty(projet);
            } catch (SQLException ex) {
                ex.printStackTrace();
                return new SimpleStringProperty("Échec du chargement");
            }
        });

        employeeTableView.getColumns().addAll(idColumn, nameColumn, sexColumn, ageColumn, emailColumn, projetColumn);

        gestionEmplo employeeManager = new gestionEmplo(db);
        try {
            employeeManager.listEmployee(employeeTableView);
        } catch (SQLException ex) {
            ex.printStackTrace();
            employeeTableView.getItems().add(new employee(-1, "Impossible de charger la liste du personnel", "", -1, ""));
        }

        return new VBox(employeeTableView);
    }


    private VBox addProjetModule() {
        gestionProjet projetManager = new gestionProjet(db);

        TextField projetIdField = createTextField("ID du projet");
        TextField projetNameField = createTextField("Nom du projet");
        TextField projetGroupField = createTextField("Groupe du projet");
        TextField projetDdlField = createTextField("Deadline(YYYY-MM-DD)");
        ComboBox<String> projetStatusComboBox = new ComboBox<>();
        projetStatusComboBox.getItems().addAll("À faire", "En cours", "Terminé");
        projetStatusComboBox.setPromptText("Sélectionner le statut du projet");

        Label instructions = new Label("Maintenez la touche Ctrl/Cmd ou Shift enfoncée pour effectuer une sélection multiple des employés.");

        ListView<String> employeeListView = new ListView<>();
        employeeListView.setPrefWidth(300);
        employeeListView.setMaxWidth(300);
        employeeListView.setMinWidth(300);

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

        Button addProjetButton = new Button("Ajouter");
        addProjetButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                int id = Integer.parseInt(projetIdField.getText());
                String name = projetNameField.getText();
                String group = projetGroupField.getText();
                String ddl = projetDdlField.getText();
                String status = projetStatusComboBox.getValue();

                List<Integer> selectedMemberIds = new ArrayList<>();
                for (String employeeName : employeeListView.getSelectionModel().getSelectedItems()) {
                    int selectedEmployeeId = projetManager.getEmployeeId(employeeName);
                    if (selectedEmployeeId != -1) {
                        selectedMemberIds.add(selectedEmployeeId);
                    }
                }

                projetManager.addProjet(id, name, group, ddl, status, selectedMemberIds);

                alertMessage = "Le projet a été ajouté avec succès";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);

                projetIdField.clear();
                projetNameField.clear();
                projetGroupField.clear();
                projetDdlField.clear();
                projetStatusComboBox.setValue(null);
                employeeListView.getSelectionModel().clearSelection();

            } catch (NumberFormatException ex) {
                alertMessage = "Format d'ID non valide, veuillez saisir un nombre.";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning！", alertMessage);
            } catch (Exception ex) {
                alertMessage = "Une erreur s'est produite lors de l'ajout d'un élément: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }
        });

        VBox layout = new VBox(10, projetIdField, projetNameField, projetGroupField, projetDdlField, projetStatusComboBox, instructions, new Label("Membres du projet: "), employeeListView, addProjetButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    private VBox deleteProjetModule() {
        Label projetIdLabel = new Label("ID du projet:");
        TextField projetIdField = createTextField("");

        Button deleteProjetButton = new Button("Supprimer");
        deleteProjetButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = projetIdField.getText();
                if (idText == null || idText.trim().isEmpty()) {
                    throw new IllegalArgumentException("L'ID de l'élément ne peut pas être nul.");
                }

                int projetId = Integer.parseInt(idText);

                gestionProjet gestionProjet = new gestionProjet(db);
                gestionProjet.deleteProjet(projetId);

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                alertMessage = "Le project a été supprimé avec succès !";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);

                projetIdField.clear();

            } catch (NumberFormatException ex) {
                alertMessage = "Format d'ID non valide, veuillez saisir un numéro.";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning!", alertMessage);
            } catch (Exception ex) {
                alertMessage = "Une erreur s'est produite lors de la suppression d'un élément: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }

        });
        VBox layout = new VBox(20, projetIdLabel, projetIdField, deleteProjetButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    private VBox updateProjetModule() {
        gestionProjet projetManager = new gestionProjet(db);

        Label projetIdLabel = new Label("PS: L'ID du projet ne peut pas être modifié: ");
        TextField projetIdField = createTextField("ID du projet");
        TextField projetNameField = createTextField("Modifier le nom du projet");
        TextField projetGroupField = createTextField("Groupe du projet");
        TextField projetDdlField = createTextField("Deadline(YYYY-MM-DD)");
        ComboBox<String> projetStatusComboBox = new ComboBox<>();
        projetStatusComboBox.getItems().addAll("À faire", "En cours", "Terminé");
        projetStatusComboBox.setPromptText("Sélection du statut du projet");

        ListView<String> employeeListView = new ListView<>();
        employeeListView.setPrefWidth(300);
        employeeListView.setMaxWidth(300);
        employeeListView.setMinWidth(300);

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

        Button updateProjetButton = new Button("Modifier");
        updateProjetButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = projetIdField.getText();
                if (idText == null || idText.trim().isEmpty()) {
                    throw new IllegalArgumentException("L'ID ne peut pas être nul ou vide.");
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

                alertMessage = "Les informations sur le projet ont été modifiées avec succès！";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);

                projetNameField.clear();
                projetGroupField.clear();
                projetDdlField.clear();
                projetStatusComboBox.getSelectionModel().clearSelection();

            } catch (NumberFormatException ex) {
                alertMessage = "Le format de l'ID n'est pas valide, veuillez saisir un numéro.";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning！", alertMessage);
            } catch (Exception ex) {
                alertMessage = "Une erreur s'est produite lors de la modification des informations du projet: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }
        });
        VBox layout = new VBox(10, projetIdField, projetIdLabel, projetNameField, projetGroupField, projetDdlField, projetStatusComboBox, employeeListView, updateProjetButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    private VBox viewProjetModule() {
        TableView<projet> projetTableView = new TableView<>();

        TableColumn<projet, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<projet, String> nameColumn = new TableColumn<>("Nom");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<projet, String> groupColumn = new TableColumn<>("Groupe");
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("group"));

        TableColumn<projet, String> ddlColumn = new TableColumn<>("Deadline");
        ddlColumn.setCellValueFactory(data -> {
            LocalDate deadline = data.getValue().getDdl();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return new SimpleStringProperty(deadline != null ? deadline.format(dateFormat) : "Pas de date limite");
        });

        TableColumn<projet, String> statusColumn = new TableColumn<>("Statut");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<projet, String> memberColumn = new TableColumn<>("Membre");
        memberColumn.setCellValueFactory(data -> {
            projet project = data.getValue();
            List<String> members = project.getMembers();
            if (members == null || members.isEmpty()) {
                return new SimpleStringProperty("无成员");
            }

            String memberNames = String.join(", ", members);
            return new SimpleStringProperty(memberNames);
        });

        projetTableView.getColumns().addAll(idColumn, nameColumn, groupColumn, ddlColumn, statusColumn, memberColumn);

        gestionProjet projetManager = new gestionProjet(db);
        try {
            projetManager.listProjet(projetTableView);
        } catch (SQLException ex) {
            ex.printStackTrace();
            projetTableView.getItems().add(new projet(-1, "Impossible de charger la liste des membres", "", null, "", new ArrayList<>()));
        }

        return new VBox(projetTableView);
    }

    private VBox addTacheModule() {
        gestionTache gestionTache = new gestionTache(db);

        TextField tacheIdField = createTextField("ID de la tâche");
        TextField tacheNameField = createTextField("Nom de la tâche");
        TextField tacheDdlField = createTextField("Deadline(YYYY-MM-DD)");
        TextField tacheCategField = createTextField("Catégorie de la tâche");
        TextArea tacheDescriptionArea = new TextArea();
        tacheDescriptionArea.setPromptText("Commentaire sur la tâche (description détaillée)");
        tacheDescriptionArea.setPrefHeight(350);
        tacheDescriptionArea.setPrefWidth(200);
        tacheDescriptionArea.setMaxWidth(200);
        tacheDescriptionArea.setMinWidth(200);
        Label ps = new Label("Maintenir la touche Ctrl/Cmd ou Shift enfoncée pour effectuer des sélections multiples");

        ListView<String> employeeListView = new ListView<>();
        employeeListView.setPrefWidth(300);
        employeeListView.setMaxWidth(300);
        employeeListView.setMinWidth(300);

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

        ListView<String> projetListView = new ListView<>();
        projetListView.setPrefWidth(300);
        projetListView.setMaxWidth(300);
        projetListView.setMinWidth(300);

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

        Button addTacheButton = new Button("Ajouter");
        addTacheButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = tacheIdField.getText();
                int id = Integer.parseInt(idText);
                String name = tacheNameField.getText();
                String deadline = tacheDdlField.getText();
                String category = tacheCategField.getText();
                String description = tacheDescriptionArea.getText();

                List<Integer> selectedEmployeeIds = new ArrayList<>();
                for (String employeeName : employeeListView.getSelectionModel().getSelectedItems()) {
                    int employeeId = gestionTache.getEmployeeId(employeeName);
                    selectedEmployeeIds.add(employeeId);
                }

                List<Integer> selectedProjetIds = new ArrayList<>();
                for (String projetName : projetListView.getSelectionModel().getSelectedItems()) {
                    int projetId = gestionTache.getProjetId(projetName);
                    selectedProjetIds.add(projetId);
                }

                gestionTache.addTache(id, name, deadline, category, description, selectedEmployeeIds, selectedProjetIds);

                alertMessage = "La tâche a été ajoutée avec succès！";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);

                tacheIdField.clear();
                tacheNameField.clear();
                tacheDdlField.clear();
                tacheCategField.clear();
                tacheDescriptionArea.clear();
                employeeListView.getSelectionModel().clearSelection();
                projetListView.getSelectionModel().clearSelection();

            } catch (NumberFormatException ex) {
                alertMessage = "Format d'ID non valide, veuillez saisir un nombre.";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning！", alertMessage);
            } catch (Exception ex) {
                alertMessage = "Une erreur s'est produite lors de l'ajout d'une tâche: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }
        });

        VBox layout = new VBox(10, tacheIdField, tacheNameField, tacheDdlField, tacheCategField, tacheDescriptionArea, ps,
                new Label("Affecter un employé:"), employeeListView,
                new Label("Appartient au projet:"), projetListView,
                addTacheButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    private VBox viewTacheModule() {
        TableView<tache> tacheTableView = new TableView<>();

        TableColumn<tache, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<tache, Integer> nameColumn = new TableColumn<>("Nom");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<tache, String> ddlColumn = new TableColumn<>("Deadline");
        ddlColumn.setCellValueFactory(data -> {
            LocalDate deadline = data.getValue().getDdl();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return new SimpleStringProperty(deadline != null ? deadline.format(dateFormat) : "Pas de date limite");
        });

        TableColumn<tache, Integer> categColumn = new TableColumn<>("Categorie");
        categColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<tache, Integer> descripColumn = new TableColumn<>("Description");
        descripColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<tache, String> employeeNameColumn = new TableColumn<>("Employés affectés");
        employeeNameColumn.setCellValueFactory(data -> {
            List<String> employees = data.getValue().getEmployeeName();
            return new SimpleStringProperty(employees != null ? String.join(", ", employees) : "Employés non affectés");
        });
        TableColumn<tache, String> projetNameColumn = new TableColumn<>("Projet affilié");
        projetNameColumn.setCellValueFactory(data -> {
            List<String> projets = data.getValue().getProjetName();
            return new SimpleStringProperty(projets != null ? String.join(", ", projets) : "Error");
        });

        tacheTableView.getColumns().addAll(idColumn, nameColumn, ddlColumn, categColumn, descripColumn, employeeNameColumn, projetNameColumn);

        gestionTache gestionTache = new gestionTache(db);

        loadData(tacheTableView, gestionTache, "id");

        Button sortByIdButton = new Button("Tri par ID");
        Button sortByDeadlineButton = new Button("Tri par date limite");

        sortByIdButton.setOnAction(event -> loadData(tacheTableView, gestionTache, "id"));
        sortByDeadlineButton.setOnAction(event -> loadData(tacheTableView, gestionTache, "deadline"));

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
            tacheTableView.getItems().add(new tache(-1, "Impossible de charger les informations sur les tâches", null, "", "", new ArrayList<>(), new ArrayList<>()));
        }
    }

    private VBox updateTacheModule() {
        gestionTache tacheManager = new gestionTache(db);

        Label tacheIdLabel = new Label("PS: L'ID de tâche ne peut pas être modifié");
        Label tacheEmployeeLabel = new Label("Modifier l'employé assigné：");
        Label tacheProjetLabel = new Label("Modifier le projet auquel elle appartient：");
        Label ps = new Label("Maintenir la touche Ctrl/Cmd ou Shift enfoncée pour effectuer des sélections multiples");
        TextField tacheIdField = createTextField("ID de la tâche");

        TextField tacheNameField = createTextField("Modifier le nom de la tâche");
        TextField tacheCategoryField = createTextField("Modifier la catégorie de la tâche");
        TextField tacheDdlField = createTextField("Modifier deadline(YYYY-MM-DD)");
        TextArea tacheDescriptionArea = new TextArea();
        tacheDescriptionArea.setPromptText("Modifier la description de la tâche");
        tacheDescriptionArea.setPrefHeight(350);
        tacheDescriptionArea.setPrefWidth(200);
        tacheDescriptionArea.setMaxWidth(200);
        tacheDescriptionArea.setMinWidth(200);

        ListView<String> employeeListView = new ListView<>();
        employeeListView.setPrefWidth(300);
        employeeListView.setMaxWidth(300);
        employeeListView.setMinWidth(300);

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

        Button updateTacheButton = new Button("Modifier");
        updateTacheButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = tacheIdField.getText();
                if (idText == null || idText.trim().isEmpty()) {
                    throw new IllegalArgumentException("L'ID ne peut pas être nul.");
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

                alertMessage = "Les informations relatives à la tâche ont été modifiées avec succès！";
                showAlert(Alert.AlertType.INFORMATION, "", alertMessage);

                tacheNameField.clear();
                tacheCategoryField.clear();
                tacheDdlField.clear();
                tacheDescriptionArea.clear();
                employeeListView.getSelectionModel().clearSelection();
                projectListView.getSelectionModel().clearSelection();

            } catch (NumberFormatException ex) {
                alertMessage = "Le format de l'ID n'est pas valide, veuillez saisir un numéro.";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning！", alertMessage);
            } catch (Exception ex) {
                alertMessage = "Une erreur s'est produite lors de la modification des informations de la tâche: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }
        });

        VBox layout = new VBox(10, tacheIdField, tacheIdLabel, tacheNameField, tacheDdlField, tacheCategoryField, tacheDescriptionArea, ps, tacheEmployeeLabel, employeeListView, tacheProjetLabel, projectListView, updateTacheButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        return layout;
    }

    private VBox deleteTacheModule() {
        Label tacheIdLabel = new Label("ID du tâche:");
        TextField tacheIdField = createTextField("");
        Button deleteTacheButton = new Button("Supprimer");

        deleteTacheButton.setOnAction(e -> {
            String alertMessage = null;
            try {
                String idText = tacheIdField.getText();
                if (idText == null || idText.trim().isEmpty()) {
                    throw new IllegalArgumentException("L'identifiant de la tâche ne doit pas être nul.");
                }

                int tacheId = Integer.parseInt(idText);

                gestionTache gestionTache = new gestionTache(db);
                gestionTache.deleteTache(tacheId);

                alertMessage = "La tâche a été supprimée avec succès！";
                showAlert(Alert.AlertType.INFORMATION, "Réussi", alertMessage);

                tacheIdField.clear();

            } catch (NumberFormatException ex) {
                alertMessage = "Format d'identifiant non valide, veuillez saisir un numéro.";
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            } catch (IllegalArgumentException ex) {
                alertMessage = ex.getMessage();
                showAlert(Alert.AlertType.WARNING, "Warning", alertMessage);
            } catch (Exception ex) {
                alertMessage = "Erreur lors de la suppression de la tâche: " + ex.getMessage();
                showAlert(Alert.AlertType.ERROR, "Error", alertMessage);
            }
        });

        VBox layout = new VBox(10, tacheIdLabel, tacheIdField, deleteTacheButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }


    private BorderPane viewKanbanModule() {
        BorderPane kanbanLayout = new BorderPane();
        HBox kanbanColumns = new HBox(10);

        String[] statuses = {"À faire", "En cours", "Terminé"};
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
                List<projet> projets = projetManager.getProjets(status);
                for (projet proj : projets) {
                    Button projectButton = new Button(proj.getName());
                    projectButton.setMaxWidth(Double.MAX_VALUE);

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
            LocalDate deadline = data.getValue().getDdl();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return new SimpleStringProperty(deadline != null ? deadline.format(dateFormat) : "Pas de date de limite");
        });

        TableColumn<projet, String> membersColumn = new TableColumn<>("Members");
        membersColumn.setCellValueFactory(data -> {
            List<String> members = proj.getMembers();
            return new SimpleStringProperty(members.isEmpty() ? "Pas de member" : String.join(", ", members));
        });

        projetTableView.getColumns().addAll(idColumn, nameColumn, groupColumn, ddlColumn, membersColumn);
        projetTableView.getItems().add(proj);

        Stage detailStage = new Stage();
        detailStage.setTitle("Détails du projet");
        detailStage.setScene(new Scene(new VBox(projetTableView), 600, 400));
        detailStage.show();
    }


    private void viewCalendar(Connection connection) {
        Stage calendarStage = new Stage();
        calendarStage.setTitle("Calendrier des dates limites de du projet");

        datePicker = new DatePicker(LocalDate.now());
        datePicker.setOnAction(e -> updateCalendar(connection, datePicker.getValue()));

        calendarGrid = new GridPane();
        calendarGrid.setVgap(5);
        calendarGrid.setHgap(5);

        for (int i = 0; i < 7; i++) {
            calendarGrid.getColumnConstraints().add(new ColumnConstraints(80));
        }

        for (int i = 0; i < 6; i++) {
            calendarGrid.getRowConstraints().add(new RowConstraints(50));
        }

        VBox vbox = new VBox(datePicker, calendarGrid);
        Scene calendarScene = new Scene(vbox, 600, 400);
        calendarStage.setScene(calendarScene);
        calendarStage.show();

        updateCalendar(connection, datePicker.getValue());
    }

    private void updateCalendar(Connection connection, LocalDate selectedDate) {
        calendarGrid.getChildren().clear();

        YearMonth yearMonth = YearMonth.from(selectedDate);
        int dayOfWeek = yearMonth.atDay(1).getDayOfWeek().getValue();
        int daysInMonth = yearMonth.lengthOfMonth();

        String[] headers = {"Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samdi"};
        for (int i = 0; i < headers.length; i++) {
            calendarGrid.add(new Label(headers[i]), i, 0);
        }

        Map<LocalDate, String> projectDeadlines = new HashMap<>();

        String sql = "SELECT id, nom, deadline FROM projet";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("nom");
                LocalDate deadline = rs.getDate("deadline").toLocalDate();
                projectDeadlines.put(deadline, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), day);
            String projectInfo = projectDeadlines.get(currentDate);

            Label dateLabel = new Label(day + (projectInfo != null ? "\n" + projectInfo : ""));
            calendarGrid.add(dateLabel, (dayOfWeek - 1 + day) % 7, (day + dayOfWeek - 1) / 7 + 1);
        }
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
