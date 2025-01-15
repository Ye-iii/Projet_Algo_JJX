package fr.isep.ye.projet_algo_jjx;

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
        String pro_empDb = "INSERT INTO employee_projet (employee_id, projet_id) VALUES (?, ?)";

        connection dbConnection = new mysql();
        try (Connection conn = dbConnection.connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtProjet = conn.prepareStatement(projetDb);
                 PreparedStatement pstmtProemp = conn.prepareStatement(pro_empDb)) {

                pstmtProjet.setInt(1, id);
                pstmtProjet.setString(2, name);
                pstmtProjet.setString(3, group);
                pstmtProjet.setString(4, deadline);
                pstmtProjet.setString(5, status);
                pstmtProjet.executeUpdate();

                for (Integer member : members) {
                    pstmtProemp.setInt(1, member);
                    pstmtProemp.setInt(2, id);
                    pstmtProemp.addBatch();
                }
                pstmtProemp.executeBatch();
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }


    public void listProjet(TableView<projet> tableView) throws SQLException {
        String query = "SELECT p.id AS projet_id, p.nom, p.groupe, p.deadline, p.statut, " +
                "e.nom AS employee_name " +
                "FROM projet p " +
                "LEFT JOIN employee_projet ep ON p.id = ep.projet_id " +
                "LEFT JOIN employee e ON ep.employee_id = e.id";

        Map<Integer, projet> projetMap = new HashMap<>();

        connection dbConnection = new mysql();
        try (Connection conn = dbConnection.connect();


             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int projetId = rs.getInt("projet_id");

                projet proj = projetMap.computeIfAbsent(projetId, id -> {
                    try {
                        return new projet(
                                id,
                                rs.getString("nom"),
                                rs.getString("groupe"),
                                rs.getDate("deadline").toLocalDate(),
                                rs.getString("statut"),
                                new ArrayList<>()
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                String employeeName = rs.getString("employee_name");
                if (employeeName != null) {
                    proj.getMembers().add(employeeName);
                }
            }

            tableView.getItems().clear();
            tableView.getItems().addAll(projetMap.values());
        }
    }


    public int getEmployeeId(String employeeName) {
        String query = "SELECT id FROM employee WHERE nom = ?";

        connection dbConnection = new mysql();
        try (Connection conn = dbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, employeeName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public void deleteProjet(int projetId) throws SQLException {
        String checkProjetSQL = "SELECT COUNT(*) FROM projet WHERE id = ?";
        String deleteProjetSQL = "DELETE FROM projet WHERE id = ?";
        String deleteProjetEmployeeRelationSQL = "DELETE FROM employee_projet WHERE projet_id = ?";

        connection dbConnection = new mysql();
        try (Connection conn = dbConnection.connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkProjetSQL)) {
                checkStmt.setInt(1, projetId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        throw new IllegalArgumentException("L'ID de projet spécifié n'est pas trouvé: " + projetId);
                    }
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(deleteProjetEmployeeRelationSQL)) {
                pstmt.setInt(1, projetId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(deleteProjetSQL)) {
                pstmt.setInt(1, projetId);
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void updateProjet(int id, String name, String group, String deadline, String status,List<Integer> memberIds) throws SQLException {
        String sql = "UPDATE projet SET nom = ?, groupe = ?, deadline = ?, statut = ? WHERE id = ?";

        connection dbConnection = new mysql();
        try (Connection conn = dbConnection.connect();
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
        connection dbConnection = new mysql();

        String deleteSql = "DELETE FROM employee_projet WHERE projet_id = ?";
        try (Connection conn = dbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setInt(1, projetId);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        String insertSql = "INSERT INTO employee_projet (projet_id, employee_id) VALUES (?, ?)";
        try (Connection conn = dbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (Integer memberId : memberIds) {
                pstmt.setInt(1, projetId);
                pstmt.setInt(2, memberId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<projet> getProjets(String status) throws SQLException {
        String query = "SELECT p.id AS projet_id, p.nom, p.groupe, p.deadline, p.statut, " +
                "e.nom AS employee_name " +
                "FROM projet p " +
                "LEFT JOIN employee_projet ep ON p.id = ep.projet_id " +
                "LEFT JOIN employee e ON ep.employee_id = e.id " +
                "WHERE p.statut = ?";

        Map<Integer, projet> projetMap = new HashMap<>();

        connection dbConnection = new mysql();
        try (Connection conn = dbConnection.connect();

             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int projetId = rs.getInt("projet_id");

                    projet proj = projetMap.computeIfAbsent(projetId, id -> {
                        try {
                            return new projet(
                                    id,
                                    rs.getString("nom"),
                                    rs.getString("groupe"),
                                    rs.getDate("deadline").toLocalDate(),
                                    rs.getString("statut"),
                                    new ArrayList<>()
                            );
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    String employeeName = rs.getString("employee_name");
                    if (employeeName != null) {
                        proj.getMembers().add(employeeName);
                    }
                }
            }
        }
        return new ArrayList<>(projetMap.values());
    }

}

