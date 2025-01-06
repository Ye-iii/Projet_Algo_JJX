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

    public void addEmployee(int id, String name, String email) throws SQLException {
        String sql = "INSERT INTO employee (id, nom, email) VALUES (?, ?, ?)";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,id);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
        }
    }

    public void deleteEmployee(int id) throws SQLException {
        String sql = "DELETE FROM employee WHERE id = ?";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
    public void updateEmployee(int id, String name, String email) throws SQLException {
        String sql = "UPDATE employee SET nom = ?, email = ? WHERE id = ?";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setInt(3, id);
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
                        rs.getString("email")
                );
                tableView.getItems().add(employee);
            }
        }
    }
}
