package fr.isep.ye.projet_algo_jjx;

import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class gestionProjet {
    private mysql db;

    public gestionProjet(mysql db) {
        this.db = db;
    }

    public void addProjet(int id, String name, String group, String deadline, String status, List<Integer> members) throws SQLException {
        String projetDb = "INSERT INTO projet (id, nom, groupe, deadline, statut) VALUES (?, ?, ?, ?, ?)";
        String pro_empDb = "INSERT INTO employee_projet (employee_id, projet_id) VALUES (?, ?)"; // 修改字段名为 projet_id

        try (Connection conn = mysql.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtProjet = conn.prepareStatement(projetDb);
                 PreparedStatement pstmtProemp = conn.prepareStatement(pro_empDb)) {

                // 插入项目数据
                pstmtProjet.setInt(1, id);
                pstmtProjet.setString(2, name);
                pstmtProjet.setString(3, group);
                pstmtProjet.setString(4, deadline);
                pstmtProjet.setString(5, status);
                pstmtProjet.executeUpdate(); // 执行项目插入

                // 插入员工与项目关联数据
                for (Integer member : members) {
                    pstmtProemp.setInt(1, member); // 设置员工ID
                    pstmtProemp.setInt(2, id); // 设置项目ID
                    pstmtProemp.addBatch(); // 添加到批处理
                }
                pstmtProemp.executeBatch(); // 执行批处理插入
                conn.commit(); // 提交事务
            } catch (SQLException ex) {
                conn.rollback(); // 出现异常则回滚
                throw ex; // 抛出异常
            } finally {
                conn.setAutoCommit(true); // 重新设置为自动提交
            }
        }
    }


    public void listProjet(TableView<projet> tableView) throws SQLException {
        String query = "SELECT p.id AS projet_id, p.nom, p.groupe, p.deadline, p.statut, " +
                "e.nom AS employee_name " + // 只选择成员姓名
                "FROM projet p " +
                "LEFT JOIN employee_projet ep ON p.id = ep.projet_id " +
                "LEFT JOIN employee e ON ep.employee_id = e.id";

        Map<Integer, projet> projetMap = new HashMap<>();

        try (Connection conn = db.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int projetId = rs.getInt("projet_id");

                // 如果项目未在 Map 中，创建并添加
                projet proj = projetMap.computeIfAbsent(projetId, id -> {
                    try {
                        return new projet(
                                id,
                                rs.getString("nom"),
                                rs.getString("groupe"),
                                rs.getDate("deadline"),
                                rs.getString("statut"),
                                new ArrayList<>() // 初始化成员列表
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                // 获取成员姓名并添加到项目的成员列表
                String employeeName = rs.getString("employee_name");
                if (employeeName != null) { // 确保有成员数据
                    proj.getMembers().add(employeeName); // 添加成员姓名到项目中
                }
            }

            // 将所有项目添加到 TableView
            tableView.getItems().clear();
            tableView.getItems().addAll(projetMap.values());
        }
    }


    public int getEmployeeIdByName(String employeeName) {
        String query = "SELECT id FROM employee WHERE nom = ?"; // 查询员工 ID 的 SQL 语句
        try (Connection conn = db.getConnection(); // 获取数据库连接
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, employeeName); // 设置查询参数为员工姓名
            ResultSet rs = pstmt.executeQuery(); // 执行查询

            // 如果找到结果，则返回员工 ID
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); // 打印异常信息
        }
        return -1; // 如果没有找到，返回 -1
    }

    public void deleteProjet(int projetId) throws SQLException {
        String checkProjetSQL = "SELECT COUNT(*) FROM projet WHERE id = ?";
        String deleteProjetSQL = "DELETE FROM projet WHERE id = ?";
        String deleteProjetEmployeeRelationSQL = "DELETE FROM employee_projet WHERE projet_id = ?";

        try (Connection conn = mysql.getConnection()) {
            conn.setAutoCommit(false); // 开启事务

            // 检查项目 ID 是否存在
            try (PreparedStatement checkStmt = conn.prepareStatement(checkProjetSQL)) {
                checkStmt.setInt(1, projetId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        throw new IllegalArgumentException("未找到指定的项目ID: " + projetId);
                    }
                }
            }

            // 删除项目与员工的关联
            try (PreparedStatement pstmt = conn.prepareStatement(deleteProjetEmployeeRelationSQL)) {
                pstmt.setInt(1, projetId);
                pstmt.executeUpdate();
            }

            // 删除项目本身
            try (PreparedStatement pstmt = conn.prepareStatement(deleteProjetSQL)) {
                pstmt.setInt(1, projetId);
                pstmt.executeUpdate();
            }

            conn.commit(); // 提交事务
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void updateProjet(int id, String name, String group, String deadline, String status,List<Integer> memberIds) throws SQLException {
        String sql = "UPDATE projet SET nom = ?, groupe = ?, deadline = ?, statut = ? WHERE id = ?";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, group);
            pstmt.setString(3, deadline);
            pstmt.setString(4, status);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        }
        updateProjetMembers(id,memberIds);
    }

    private void updateProjetMembers(int projetId, List<Integer> memberIds)throws SQLException {
// 先删除当前项目的所有成员
        String deleteSql = "DELETE FROM employee_projet WHERE projet_id = ?";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, projetId);
            pstmt.executeUpdate();
        }
        String insertSql = "INSERT INTO employee_projet (projet_id, employee_id) VALUES (?, ?)";
        try (Connection conn = mysql.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (Integer memberId : memberIds) {
                pstmt.setInt(1, projetId);
                pstmt.setInt(2, memberId);
                pstmt.addBatch(); // 使用批处理添加插入命令
            }
            pstmt.executeBatch(); // 执行批处理
        }
    }
}



//    public void updateProject(int id, String name, String groupe, String deadline) throws SQLException {
//        String sql = "UPDATE projects SET name = ?, groupe = ?, deadline = ? WHERE id = ?";
//        try (Connection conn = mysql.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setString(1, name);
//            pstmt.setString(2, groupe);
//            pstmt.setString(3, deadline);
//            pstmt.setInt(4, id);
//            pstmt.executeUpdate();
//        }
//    }
//
//    public void deleteProject(int id) throws SQLException {
//        String sql = "DELETE FROM projects WHERE id = ?";
//        try (Connection conn = mysql.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setInt(1, id);
//            pstmt.executeUpdate();
//        }
//    }
//


//    public void listTasks(javafx.scene.control.ListView<String> listView) throws SQLException {
//        String sql = "SELECT * FROM tasks";
//        try (Connection conn = mysql.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql);
//             ResultSet rs = pstmt.executeQuery()) {
//            listView.getItems().clear();
//            while (rs.next()) {
//                listView.getItems().add("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Project ID: " + rs.getInt("project_id") + ", Priority: " + rs.getInt("priority") + ", Deadline: " + rs.getDate("deadline"));
//            }
//        }
//    }
//}