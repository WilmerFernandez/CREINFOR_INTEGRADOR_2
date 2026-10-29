
package utp_integrador_2.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BDConnection {

    private BDConnection() {
    }

    public static Connection getConnection() throws SQLException {

        String host = obtenerVariable("DB_HOST", "localhost");
        String port = obtenerVariable("DB_PORT", "3306");
        String database = obtenerVariable("DB_NAME", "creinfor");
        String user = obtenerVariable("DB_USER", "root");
        String password = System.getenv("DB_PASSWORD");

        if (password == null || password.isEmpty()) {
            throw new SQLException(
                "Falta configurar la variable DB_PASSWORD"
            );
        }

        String url = "jdbc:mysql://" + host + ":" + port
                + "/" + database
                + "?serverTimezone=America/Lima"
                + "&useUnicode=true"
                + "&characterEncoding=UTF-8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(
                    url, user, password
            );

        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Error al cargar el driver MySQL", e
            );
        }
    }

    private static String obtenerVariable(
            String nombre, String valorDefecto) {

        String valor = System.getenv(nombre);

        return (valor == null || valor.isBlank())
                ? valorDefecto : valor;
    }
}
