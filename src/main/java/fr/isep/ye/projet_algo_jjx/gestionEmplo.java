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

    public void listEmployees(TableView<employee> tableView) throws SQLException {
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
