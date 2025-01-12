package fr.isep.ye.projet_algo_jjx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//public class mysql {
//    private static final String URL = "jdbc:mysql://localhost:3306/jjx"; // 替换为你的数据库名
//    private static final String USER = "root"; // 替换为你的数据库用户名
//    private static final String PASSWORD = "123456"; // 替换为你的数据库密码
//
//    public static void main(String[] args) {
//        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
//            if (connection != null) {
//                System.out.println("成功连接到数据库!");
//            }
//        } catch (SQLException e) {
//            System.out.println("连接数据库失败: " + e.getMessage());
//            e.printStackTrace(); // 打印异常堆栈跟踪以便调试
//        }
//    }
//    public static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(URL, USER, PASSWORD);
//    }
//}
public class mysql {
    private static final String URL = "jdbc:mysql://localhost:3306/jjx"; // 替换为你的数据库名
    private static final String USER = "root"; // 替换为你的数据库用户名
    private static final String PASSWORD = "123456"; // 替换为你的数据库密码

    // 静态块，用于测试数据库驱动
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // 加载 MySQL 驱动
            System.out.println("数据库驱动加载成功!");
        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱动加载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     * @return Connection 数据库连接
     * @throws SQLException 如果连接失败则抛出异常
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("获取数据库连接失败: " + e.getMessage());
            throw e; // 重新抛出异常供调用方处理
        }
    }

    public static void main(String[] args) {
        // 测试数据库连接
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