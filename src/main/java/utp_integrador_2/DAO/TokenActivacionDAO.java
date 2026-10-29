package utp_integrador_2.DAO;

import utp_integrador_2_model.TokenActivacion;

import java.sql.*;

public class TokenActivacionDAO {

    private final Connection connection;

    public TokenActivacionDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertar(TokenActivacion token) throws SQLException {

       String sql = "INSERT INTO tokens_activacion "
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

    public TokenActivacion buscarPorToken(String token)
            throws SQLException {

        String sql = "SELECT * FROM tokens_activacion WHERE token = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, token);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return TokenActivacion.builder()
                            .idToken(rs.getInt("id_token"))
                            .idUsuario(rs.getInt("id_usuario"))
                            .token(rs.getString("token"))
                            .fechaExpiracion(
                                    rs.getTimestamp("fecha_expiracion")
                                            .toLocalDateTime()
                            )
                            .usado(rs.getBoolean("usado"))
                            .build();
                }
            }
        }

        return null;
    }

    public void marcarComoUsado(int idToken)
            throws SQLException {

         String sql = "UPDATE tokens_activacion "
                + "SET usado = TRUE "
                + "WHERE id_token = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idToken);
            ps.executeUpdate();
        }
    }
}