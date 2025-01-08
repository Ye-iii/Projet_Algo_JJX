package fr.isep.ye.projet_algo_jjx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class mysql {
    private static final String URL = "jdbc:mysql://localhost:3306/jjx"; // 替换为你的数据库名
    private static final String USER = "root"; // 替换为你的数据库用户名
    private static final String PASSWORD = "123456"; // 替换为你的数据库密码

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (connection != null) {
                System.out.println("成功连接到数据库!");
            }
        } catch (SQLException e) {
            System.out.println("连接数据库失败: " + e.getMessage());
            e.printStackTrace(); // 打印异常堆栈跟踪以便调试
        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
// 数据库连接信息
//public class mysql extends Application {
//    // 数据库连接信息
//    private static final String URL = "jdbc:mysql://localhost:3306/jjx";
//    private static final String USERNAME = "root";
//    private static final String PASSWORD = "123456";
//
//    private Connection connection;
//    public mysql() {
//        // 创建数据库连接
//        try {
//            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
//            System.out.println("数据库连接成功！");
//        } catch (SQLException e) {
//            System.err.println("数据库连接失败：" + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    public void start(Stage primaryStage) {
//        // 在这里编写JavaFX界面的代码
//        // 例如：显示主窗口等
//        primaryStage.setTitle("MySQL Database Management");
//        primaryStage.show();
//    }
//
////    public mysql() {
////        try {
////            connection = DriverManager.getConnection(URL, USER, PASSWORD);
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
////    }
//
//    public void addEmployee(String name, String email) {
//        String query = "INSERT INTO employees (name, email) VALUES (?, ?)";
//        try (PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setString(1, name);
//            stmt.setString(2, email);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void deleteEmployee(int id) {
//        String query = "DELETE FROM employees WHERE id = ?";
//        try (PreparedStatement stmt = connection.prepareStatement(query)) {
//            stmt.setInt(1, id);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public List<employee> listEmployees() {
//        List<employee> employees = new ArrayList<>();
//        String query = "SELECT * FROM employees";
//        try (Statement stmt = connection.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//            while (rs.next()) {
//                employees.add(new employee(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return employees;
//    }
//}

