package fr.isep.ye.projet_algo_jjx;

import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class projet {
    public void addProject(String name, String groupe, String deadline) throws SQLException {
        String sql = "INSERT INTO projects (name, groupe, deadline) VALUES (?, ?, ?)";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, groupe);
            pstmt.setString(3, deadline);
            pstmt.executeUpdate();
        }
    }

    public void updateProject(int id, String name, String groupe, String deadline) throws SQLException {
        String sql = "UPDATE projects SET name = ?, groupe = ?, deadline = ? WHERE id = ?";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, groupe);
            pstmt.setString(3, deadline);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        }
    }

    public void deleteProject(int id) throws SQLException {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
    public void listProjects(ListView<String> listView) throws SQLException {
        String sql = "SELECT * FROM projects";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            listView.getItems().clear();
            while (rs.next()) {
                listView.getItems().add("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Group: " + rs.getString("groupe") + ", Deadline: " + rs.getDate("deadline"));
            }
        }
    }
    public void listTasks(javafx.scene.control.ListView<String> listView) throws SQLException {
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