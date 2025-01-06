package fr.isep.ye.projet_algo_jjx;

import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class employee {
    private final int id;
    private final String name;
    private final String email;

    public employee(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }





//    public void addEmployee(String name, String email) throws SQLException {
//        String sql = "INSERT INTO employee (name, email) VALUES (?, ?)";
//        try (Connection conn = mysql.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, name);
//            pstmt.setString(2, email);
//            pstmt.executeUpdate();
//        }
//    }

    public void updateEmployee(int id, String name, String email) throws SQLException {
        String sql = "UPDATE employee SET name = ?, email = ? WHERE id = ?";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setInt(3, id);
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
}