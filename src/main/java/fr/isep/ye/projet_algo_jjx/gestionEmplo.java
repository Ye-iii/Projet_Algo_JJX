package fr.isep.ye.projet_algo_jjx;

import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class gestionEmplo {
    private mysql db;

    public gestionEmplo(mysql db) {
        this.db = db;
    }

    public void addEmployee(int id, String name, String sexe, int age, String email) throws SQLException {
        String sql = "INSERT INTO employee (id, nom, sexe, age, email) VALUES (?, ?, ?, ?, ?)";

        connection dbConnection = new mysql();
        try (Connection conn = dbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, sexe);
            pstmt.setInt(4, age);
            pstmt.setString(5, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteEmployee(int employeeId) throws SQLException {
        String checkEmployeeSQL = "SELECT COUNT(*) FROM employee WHERE id = ?";
        String deleteEmployeeSQL = "DELETE FROM employee WHERE id = ?";
        String deleteProjetEmployeeRelationSQL = "DELETE FROM employee_projet WHERE employee_id = ?";

        connection dbConnection = new mysql();
        try (Connection conn = dbConnection.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement checkStmt = conn.prepareStatement(checkEmployeeSQL)) {
                checkStmt.setInt(1, employeeId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        throw new IllegalArgumentException("L'ID de l'employé spécifié n'a pas été trouvé: " + employeeId);
                    }
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(deleteProjetEmployeeRelationSQL)) {
                pstmt.setInt(1, employeeId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(deleteEmployeeSQL)) {
                pstmt.setInt(1, employeeId);
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    public void updateEmployee(int id, String name, String sexe, int age, String email) throws SQLException {
        String sql = "UPDATE employee SET nom = ?, sexe = ?, age = ?, email = ? WHERE id = ?";

        connection dbConnection = new mysql();
        try (Connection conn = dbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, sexe);
            pstmt.setInt(3, age);
            pstmt.setString(4, email);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void listEmployee(TableView<employee> tableView) throws SQLException {
        String sql = "SELECT * FROM employee";

        connection dbConnection = new mysql();
        try (Connection conn = dbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);


             ResultSet rs = pstmt.executeQuery()) {
            tableView.getItems().clear();
            while (rs.next()) {
                employee employee = new employee(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("sexe"),
                        rs.getInt("age"),
                        rs.getString("email")
                );
                tableView.getItems().add(employee);
            }
        }
    }
    public String employee_projet(int employeeId) throws SQLException {
        String query = "SELECT p.nom FROM projet p " +
                "JOIN employee_projet ep ON p.id = ep.projet_id " +
                "WHERE ep.employee_id = ? AND p.statut = 'Terminé'";

        StringBuilder projet = new StringBuilder();
        connection dbConnection = new mysql();

        try (Connection conn = dbConnection.connect();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, employeeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    if (projet.length() > 0) {
                        projet.append(", ");
                    }
                    projet.append(resultSet.getString("nom"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return projet.length() > 0 ? projet.toString() : "Aucun projet terminé";
    }
}
