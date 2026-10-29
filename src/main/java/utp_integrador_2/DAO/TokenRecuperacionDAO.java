package utp_integrador_2.DAO;

import utp_integrador_2_model.TokenRecuperacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TokenRecuperacionDAO {

    private final Connection connection;

    public TokenRecuperacionDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertar(TokenRecuperacion token) throws SQLException {

        String sql = "INSERT INTO tokens_recuperacion "
                + "(id_usuario, token, fecha_expiracion, usado) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, token.getIdUsuario());
            ps.setString(2, token.getToken());

            ps.setTimestamp(
                    3,
                    Timestamp.valueOf(token.getFechaExpiracion())
            );

            ps.setBoolean(4, token.isUsado());

            ps.executeUpdate();
        }
    }

    public TokenRecuperacion buscarPorToken(String token)
            throws SQLException {

        String sql = "SELECT * FROM tokens_recuperacion "
                + "WHERE token = ?";

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, token);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return TokenRecuperacion.builder()
                            .idToken(
                                    rs.getInt("id_token"))
                            .idUsuario(
                                    rs.getInt("id_usuario"))
                            .token(
                                    rs.getString("token"))
                            .fechaExpiracion(
                                    rs.getTimestamp(
                                            "fecha_expiracion")
                                            .toLocalDateTime()
                            )
                            .usado(
                                    rs.getBoolean("usado"))
                            .build();
                }
            }
        }

        return null;
    }

    public void marcarComoUsado(int idToken)
            throws SQLException {

        String sql = "UPDATE tokens_recuperacion "
                + "SET usado = TRUE "
                + "WHERE id_token = ?";

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, idToken);

            ps.executeUpdate();
        }
    }
}