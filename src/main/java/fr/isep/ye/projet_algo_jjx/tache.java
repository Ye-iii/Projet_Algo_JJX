package fr.isep.ye.projet_algo_jjx;

import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class tache {
    public void addTask(String name, int projectId, int priority, String deadline, String description, String categories) throws SQLException {
        String sql = "INSERT INTO tasks (name, project_id, priority, deadline, description, categories) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, projectId);
            pstmt.setInt(3, priority);
            pstmt.setString(4, deadline);
            pstmt.setString(5, description);
            pstmt.setString(6, categories);
            pstmt.executeUpdate();
        }
    }

    public void updateTask(int id, String name, int projectId, int priority, String deadline, String description, String categories) throws SQLException {
        String sql = "UPDATE tasks SET name = ?, project_id = ?, priority = ?, deadline = ?, description = ?, categories = ? WHERE id = ?";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, projectId);
            pstmt.setInt(3, priority);
            pstmt.setString(4, deadline);
            pstmt.setString(5, description);
            pstmt.setString(6, categories);
            pstmt.setInt(7, id);
            pstmt.executeUpdate();
        }
    }

    public void deleteTask(int id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
    public void listTasks(ListView<String> listView) throws SQLException {
        String sql = "SELECT * FROM tasks";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            listView.getItems().clear();
            while (rs.next()) {
                listView.getItems().add("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Project ID: " + rs.getInt("project_id") + ", Priority: " + rs.getInt("priority") + ", Deadline: " + rs.getDate("deadline"));
            }
        }
    }
}