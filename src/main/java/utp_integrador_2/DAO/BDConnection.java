package utp_integrador_2.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BDConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/creinfor?serverTimezone=America/Lima";
    private static final String USER = "root";
    private static final String PASSWORD = "admin123";

    private BDConnection() { }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar driver MySQL", e);
        }
    }
}
