package fr.isep.ye.projet_algo_jjx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class mysql {
    private static final String URL = "jdbc:mysql://localhost:3306/jjx";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("数据库驱动加载成功!");
        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱动加载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("获取数据库连接失败: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                System.out.println("成功连接到数据库!");
            }
        } catch (SQLException e) {
            System.out.println("连接数据库失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}