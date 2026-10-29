package utp_integrador_2.DAO;

import utp_integrador_2_model.Actividad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActividadDAO {

    private Connection connection;

    private static final String INSERT = "INSERT INTO actividades (id_asignacion, fecha, descripcion, tiempo_horas) VALUES (?, ?, ?, ?)";

    public ActividadDAO(Connection connection) {
        this.connection = connection;
    }

    public void insert(Actividad actividad) throws SQLException {

        if (actividad == null
                || actividad.getFecha() == null
                || actividad.getDescripcion() == null
                || actividad.getDescripcion().trim().isEmpty()
                || actividad.getTiempoHoras() <= 0
                || !Double.isFinite(actividad.getTiempoHoras())) {

            throw new SQLException(
                    "Los datos de la actividad no son válidos.");
        }

        boolean autoCommitOriginal = connection.getAutoCommit();

        if (!autoCommitOriginal) {
            throw new SQLException(
                    "La conexión ya tiene una transacción activa.");
        }

        connection.setAutoCommit(false);

        try {
            // Buscar la solicitud relacionada con esta asignación
            int idSolicitud = -1;

            String sqlBuscar
                    = "SELECT id_solicitud FROM asignaciones "
                    + "WHERE id_asignacion = ?";

            try (PreparedStatement ps
                    = connection.prepareStatement(sqlBuscar)) {

                ps.setInt(1, actividad.getIdAsignacion());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idSolicitud = rs.getInt("id_solicitud");
                    }
                }
            }

            if (idSolicitud <= 0) {
                throw new SQLException(
                        "La asignación no existe.");
            }

            // Bloquear la solicitud para evitar que se finalice
            // mientras se registra una actividad
            String sqlEstado
                    = "SELECT estado FROM solicitudes "
                    + "WHERE id_solicitud = ? FOR UPDATE";

            String estado;

            try (PreparedStatement ps
                    = connection.prepareStatement(sqlEstado)) {

                ps.setInt(1, idSolicitud);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        throw new SQLException(
                                "La solicitud no existe.");
                    }

                    estado = rs.getString("estado");
                }
            }

            if (!"en proceso".equalsIgnoreCase(estado)) {
                throw new SQLException(
                        "No se pueden registrar actividades. "
                        + "La solicitud debe estar en proceso.");
            }

            // Insertar actividad únicamente si la solicitud
            // continúa en proceso
            try (PreparedStatement ps
                    = connection.prepareStatement(INSERT)) {

                ps.setInt(1, actividad.getIdAsignacion());
                ps.setDate(2, new java.sql.Date(
                        actividad.getFecha().getTime()));
                ps.setString(3,
                        actividad.getDescripcion().trim());
                ps.setDouble(4, actividad.getTiempoHoras());

                if (ps.executeUpdate() != 1) {
                    throw new SQLException(
                            "No se pudo registrar la actividad.");
                }
            }

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
                    "Error al registrar la actividad.", e);

        } finally {
            connection.setAutoCommit(autoCommitOriginal);
        }
    }

    public List<Actividad> listarPorSolicitud(int idSolicitud) throws SQLException {
        String sql = "SELECT a.* FROM actividades a "
                + "JOIN asignaciones s ON a.id_asignacion = s.id_asignacion "
                + "WHERE s.id_solicitud = ? ORDER BY a.fecha DESC";
        List<Actividad> lista = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Actividad act = Actividad.builder()
                            .idActividad(rs.getInt("id_actividad"))
                            .idAsignacion(rs.getInt("id_asignacion"))
                            .fecha(rs.getDate("fecha"))
                            .descripcion(rs.getString("descripcion"))
                            .tiempoHoras(rs.getDouble("tiempo_horas"))
                            .build();
                    lista.add(act);
                }
            }
        }
        return lista;
    }

}
