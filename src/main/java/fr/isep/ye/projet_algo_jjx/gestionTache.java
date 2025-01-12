package fr.isep.ye.projet_algo_jjx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class gestionTache {
    private mysql db;

    public gestionTache(mysql db) {
        this.db = db;
    }

    public void addTache(int id, String name, String deadline, String category, String description, List<Integer> employeeIds, List<Integer> projetIds) throws SQLException {
        String sql = "INSERT INTO tache (id, nom, deadline, categorie, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, deadline);// 将字符串转换为日期
            pstmt.setString(4, category);
            pstmt.setString(5, description);
            pstmt.executeUpdate();

            // 将任务与员工和项目关联
            for (int employeeId : employeeIds) {
                addTacheEmployee(id, employeeId);
            }
            for (int projetId : projetIds) {
                addTacheProjet(id, projetId);
            }
        }
    }

    private void addTacheEmployee(int tacheId, int employeeId) throws SQLException {
        String sql = "INSERT INTO tache_employee (tache_id, employee_id) VALUES (?, ?)";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tacheId);
            pstmt.setInt(2, employeeId);
            pstmt.executeUpdate();
        }
    }

    private void addTacheProjet(int tacheId, int projetId) throws SQLException {
        String sql = "INSERT INTO tache_projet (tache_id, projet_id) VALUES (?, ?)";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tacheId);
            pstmt.setInt(2, projetId);
            pstmt.executeUpdate();
        }
    }

    public int getEmployeeId(String name) throws SQLException {
        String sql = "SELECT id FROM employee WHERE nom = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new IllegalArgumentException("未找到员工: " + name);
            }
        }
    }

    public int getProjetId(String name) throws SQLException {
        String sql = "SELECT id FROM projet WHERE nom = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new IllegalArgumentException("未找到项目: " + name);
            }
        }
    }

    public void updateTache(int id, String name, String deadline, String category, String description,
                            List<Integer> employeeIds, List<Integer> projetIds) throws SQLException {
        String updateTacheSQL = "UPDATE tache SET nom = ?, deadline = ?, categorie = ?, description = ? WHERE id = ?";
        String deleteEmployeeSQL = "DELETE FROM tache_employee WHERE tache_id = ?";
        String deleteProjetSQL = "DELETE FROM tache_projet WHERE tache_id = ?";
        String insertnewEmployeeSQL = "INSERT INTO tache_employee (tache_id, employee_id) VALUES (?, ?)";
        String insertnewProjetSQL = "INSERT INTO tache_projet (tache_id, projet_id) VALUES (?, ?)";

        try (Connection conn = mysql.getConnection()) {
            conn.setAutoCommit(false); // 开启事务

            try (PreparedStatement updateTacheStmt = conn.prepareStatement(updateTacheSQL)) {
                // 更新 tache 表
                updateTacheStmt.setString(1, name);
                updateTacheStmt.setString(2, deadline);
                updateTacheStmt.setString(3, category);
                updateTacheStmt.setString(4, description);
                updateTacheStmt.setInt(5, id);
                updateTacheStmt.executeUpdate();
            }

            try (PreparedStatement deleteEmployeeStmt = conn.prepareStatement(deleteEmployeeSQL);
                 PreparedStatement deleteProjetStmt = conn.prepareStatement(deleteProjetSQL)) {
                // 删除旧的关系
                deleteEmployeeStmt.setInt(1, id);
                deleteEmployeeStmt.executeUpdate();

                deleteProjetStmt.setInt(1, id);
                deleteProjetStmt.executeUpdate();
            }

            // 插入新的员工关系
            if (employeeIds != null) {
                try (PreparedStatement insertEmployeeStmt = conn.prepareStatement(insertnewEmployeeSQL)) {
                    for (int employeeId : employeeIds) { // 遍历整数 ID 列表
                        insertEmployeeStmt.setInt(1, id); // 任务 ID
                        insertEmployeeStmt.setInt(2, employeeId); // 员工 ID
                        insertEmployeeStmt.addBatch();
                    }
                    insertEmployeeStmt.executeBatch();
                }
            }

// 插入新的项目关系
            if (projetIds != null) {
                try (PreparedStatement insertProjetStmt = conn.prepareStatement(insertnewProjetSQL)) {
                    for (int projetId : projetIds) { // 遍历整数 ID 列表
                        insertProjetStmt.setInt(1, id); // 任务 ID
                        insertProjetStmt.setInt(2, projetId); // 项目 ID
                        insertProjetStmt.addBatch();
                    }
                    insertProjetStmt.executeBatch();
                }
            }

            conn.commit(); // 提交事务
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // 如果失败，抛出异常
        }
    }

    public List<tache> listTache(String order) throws SQLException {
        List<tache> taches = new ArrayList<>();

        String orderByClause = "ORDER BY t.id ASC"; // 默认按 id 排序
        if ("deadline".equalsIgnoreCase(order)) {
            orderByClause = "ORDER BY t.deadline ASC"; // 按 deadline 排序
        }

        String sql = "SELECT t.id AS tache_id, t.nom AS tache_nom, t.deadline, t.categorie, t.description, " +
                "GROUP_CONCAT(DISTINCT e.nom) AS employee_nom, GROUP_CONCAT(DISTINCT p.nom) AS projet_nom " +
                "FROM tache t " +
                "LEFT JOIN tache_employee te ON t.id = te.tache_id " +
                "LEFT JOIN employee e ON te.employee_id = e.id " +
                "LEFT JOIN tache_projet tp ON t.id = tp.tache_id " +
                "LEFT JOIN projet p ON tp.projet_id = p.id " +
                "GROUP BY t.id " + orderByClause;

        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // 直接通过 ResultSet 获取字段值
                int tacheId = rs.getInt("tache_id");
                String tacheName = rs.getString("tache_nom");
                LocalDate deadline = rs.getDate("deadline").toLocalDate();
                String category = rs.getString("categorie");
                String description = rs.getString("description");
                String employeeNameString = rs.getString("employee_nom");
                List<String> employeeName = employeeNameString != null ?
                        Arrays.asList(employeeNameString.split(",")) : new ArrayList<>(); // 转换为列表
                // 以逗号分隔的员工姓名
                String projectNamesString = rs.getString("projet_nom"); // 获取多个项目名称
                List<String> projetName = projectNamesString != null ?
                        Arrays.asList(projectNamesString.split(",")) : new ArrayList<>();

                // 构建任务信息字符串
                tache newTache = new tache(tacheId, tacheName, deadline, category, description, employeeName, projetName);
                taches.add(newTache);
            }
        }
        return taches;
    }

    public void deleteTache(int tacheId) throws SQLException {
        String checkTacheSQL = "SELECT COUNT(*) FROM tache WHERE id = ?";
        String deleteTacheSQL = "DELETE FROM tache WHERE id = ?";
        String deleteTacheEmployeeRelationSQL = "DELETE FROM tache_employee WHERE tache_id = ?";
        String deleteTacheProjetRelationSQL = "DELETE FROM tache_projet WHERE tache_id = ?";

        try (Connection conn = mysql.getConnection()) {
            conn.setAutoCommit(false); // 开启事务

            // 检查任务 ID 是否存在
            try (PreparedStatement checkStmt = conn.prepareStatement(checkTacheSQL)) {
                checkStmt.setInt(1, tacheId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        throw new IllegalArgumentException("未找到指定的任务ID: " + tacheId);
                    }
                }
            }

            // 删除任务与员工的关联
            try (PreparedStatement pstmt = conn.prepareStatement(deleteTacheEmployeeRelationSQL)) {
                pstmt.setInt(1, tacheId);
                pstmt.executeUpdate();
            }

            // 删除任务与项目的关联
            try (PreparedStatement pstmt = conn.prepareStatement(deleteTacheProjetRelationSQL)) {
                pstmt.setInt(1, tacheId);
                pstmt.executeUpdate();
            }

            // 删除任务本身
            try (PreparedStatement pstmt = conn.prepareStatement(deleteTacheSQL)) {
                pstmt.setInt(1, tacheId);
                pstmt.executeUpdate();
            }

            conn.commit(); // 提交事务
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}