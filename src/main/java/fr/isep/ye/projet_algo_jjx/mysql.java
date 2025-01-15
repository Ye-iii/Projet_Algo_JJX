package fr.isep.ye.projet_algo_jjx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class mysql implements connection {
    private static final String URL = "jdbc:mysql://localhost:3306/jjx";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Pilote de base de données chargé avec succès!");
        } catch (ClassNotFoundException e) {
            System.out.println("Pilote de base de données chargé avec erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Pilote de base de données chargé avec erreur: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("La connexion de base de données a déjà fermée!");
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        connection dbConnection = new mysql();
        Connection connection = null;
        try {
            connection = dbConnection.connect();
            if (connection != null) {
                System.out.println("Connexion réussie à la base de données!");
            }
        } finally {
            dbConnection.close(connection);
        }
    }
}