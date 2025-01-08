package fr.isep.ye.projet_algo_jjx;

import javafx.scene.control.Alert;
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
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,id);
            pstmt.setString(2, name);
            pstmt.setString(3, sexe);
            pstmt.setInt(4, age);
            pstmt.setString(5, email);
            pstmt.executeUpdate();
        }
    }

    public void deleteEmployee(int id) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM employee WHERE id = ?";
        String deleteSql = "DELETE FROM employee WHERE id = ?";
        try (Connection conn = mysql.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {

            // 检查是否存在该 ID
            checkStmt.setInt(1, id);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // 显示提示，无该 ID
                    showAlert(Alert.AlertType.WARNING, "删除失败", "没有找到员工 ID: " + id);
                    return;
                }
            }

            // 执行删除操作
            deleteStmt.setInt(1, id);
            deleteStmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "删除成功", "员工 ID: " + id + " 已删除");
        }
    }

    public void updateEmployee(int id, String name, String sexe, int age, String email) throws SQLException {
        String sql = "UPDATE employee SET nom = ?, sexe = ?, age = ?, email = ? WHERE id = ?";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, sexe);
            pstmt.setInt(3, age);
            pstmt.setString(4, email);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        }
    }

    public void listEmployee(TableView<employee> tableView) throws SQLException {
        String sql = "SELECT * FROM employee"; // 确保表名和字段名正确
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            tableView.getItems().clear(); // 清空当前列表项
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
                "WHERE ep.employee_id = ? AND p.statut = '已完成'"; // 仅选择已完成的项目
        PreparedStatement statement = db.getConnection().prepareStatement(query);
        statement.setInt(1, employeeId);
        ResultSet resultSet = statement.executeQuery();

        StringBuilder projet = new StringBuilder();
        while (resultSet.next()) {
            if (projet.length() > 0) {
                projet.append(", ");
            }
            projet.append(resultSet.getString("nom"));
        }
        return projet.length() > 0 ? projet.toString() : "无已完成项目";
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
