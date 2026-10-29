package utp_integrador_2.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InformeTecnicoDAO {

    private final Connection connection;

    public InformeTecnicoDAO(Connection connection) {
        this.connection = connection;
    }

    public void guardarInformeYCulminar(
            int idSolicitud,
            int idColaborador,
            String diagnostico,
            String solucion,
            String resultados,
            String recomendaciones) throws SQLException {

        if (diagnostico == null || diagnostico.isBlank()
                || solucion == null || solucion.isBlank()
                || resultados == null || resultados.isBlank()
                || recomendaciones == null || recomendaciones.isBlank()) {
            throw new SQLException(
                    "Todos los campos del informe son obligatorios.");
        }

        boolean autoCommitOriginal = connection.getAutoCommit();

        if (!autoCommitOriginal) {
            throw new SQLException(
                    "La conexión ya tiene una transacción activa.");
        }

        connection.setAutoCommit(false);

        try {

            // 1. Bloquear la solicitud
            String estadoActual = null;

            String sqlSolicitud
                    = "SELECT estado FROM solicitudes "
                    + "WHERE id_solicitud = ? FOR UPDATE";

            try (PreparedStatement ps
                    = connection.prepareStatement(sqlSolicitud)) {

                ps.setInt(1, idSolicitud);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        estadoActual = rs.getString("estado");
                    }
                }
            }

            if (!"en proceso".equalsIgnoreCase(estadoActual)) {
                throw new SQLException(
                        "Solo se pueden culminar solicitudes en proceso.");
            }

            // 2. Validar al coordinador de la solicitud
            String sqlCoordinador
                    = "SELECT 1 FROM asignaciones "
                    + "WHERE id_solicitud = ? "
                    + "AND id_colaborador = ? "
                    + "AND es_coordinador = 1";

            try (PreparedStatement ps
                    = connection.prepareStatement(sqlCoordinador)) {

                ps.setInt(1, idSolicitud);
                ps.setInt(2, idColaborador);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException(
                                "Solo el coordinador asignado "
                                + "puede culminar esta solicitud.");
                    }
                }
            }

            // 3. Guardar el informe técnico
            String sqlInforme
                    = "INSERT INTO informes_tecnicos "
                    + "(id_solicitud, id_colaborador, "
                    + "diagnostico, solucion_aplicada, "
                    + "resultados, recomendaciones) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement ps
                    = connection.prepareStatement(sqlInforme)) {

                ps.setInt(1, idSolicitud);
                ps.setInt(2, idColaborador);
                ps.setString(3, diagnostico.trim());
                ps.setString(4, solucion.trim());
                ps.setString(5, resultados.trim());
                ps.setString(6, recomendaciones.trim());

                ps.executeUpdate();
            }

            // 4. Culminar la solicitud
            String sqlFinalizar
                    = "UPDATE solicitudes SET estado = 'finalizada' "
                    + "WHERE id_solicitud = ? AND estado = 'en proceso'";

            try (PreparedStatement ps
                    = connection.prepareStatement(sqlFinalizar)) {

                ps.setInt(1, idSolicitud);

                if (ps.executeUpdate() != 1) {
                    throw new SQLException(
                            "No se pudo finalizar la solicitud.");
                }
            }

            // Informe y finalización se guardan juntos
            connection.commit();

        } catch (Exception e) {

            try {
                connection.rollback();
            } catch (SQLException rollbackError) {
                e.addSuppressed(rollbackError);
            }

            if (e instanceof SQLException) {
                throw (SQLException) e;
            }

            throw new SQLException(
                    "Error al guardar el informe técnico.", e);

        } finally {
            connection.setAutoCommit(autoCommitOriginal);
        }
    }

    public String obtenerEstadoEnvio(int idSolicitud)
            throws SQLException {

        String sql = "SELECT estado_envio FROM informes_tecnicos "
                + "WHERE id_solicitud = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("estado_envio") : null;
            }
        }
    }

    public boolean iniciarEnvio(int idSolicitud)
            throws SQLException {

        String sql = "UPDATE informes_tecnicos i "
                + "INNER JOIN solicitudes s "
                + "ON s.id_solicitud = i.id_solicitud "
                + "SET i.estado_envio = 'enviando' "
                + "WHERE i.id_solicitud = ? "
                + "AND i.estado_envio IN ('pendiente', 'error_envio') "
                + "AND s.estado = 'finalizada'";

        try (PreparedStatement ps
                = connection.prepareStatement(sql)) {

            ps.setInt(1, idSolicitud);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean finalizarEnvio(
            int idSolicitud, boolean enviado) throws SQLException {

        String sql = "UPDATE informes_tecnicos "
                + "SET estado_envio = ?, "
                + "fecha_envio = CASE WHEN ? THEN NOW() ELSE NULL END "
                + "WHERE id_solicitud = ? AND estado_envio = 'enviando'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, enviado ? "enviado" : "error_envio");
            ps.setBoolean(2, enviado);
            ps.setInt(3, idSolicitud);

            return ps.executeUpdate() == 1;
        }
    }
}
