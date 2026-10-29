package utp_integrador_2.DAO;

import utp_integrador_2_model.Solicitud;
import utp_integrador_2_model.Asignacion;
import utp_integrador_2.DTO.AsignacionDetalleDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsignacionDAO {

    private final Connection connection;

    private static final String INSERT
            = "INSERT INTO asignaciones "
            + "(id_solicitud, id_colaborador, es_coordinador) "
            + "VALUES (?, ?, ?)";

    public AsignacionDAO(Connection connection) {
        this.connection = connection;
    }

    // Método original: insertar una asignación
    public void insert(Asignacion asignacion) throws SQLException {

        try (PreparedStatement ps = connection.prepareStatement(
                INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, asignacion.getIdSolicitud());
            ps.setInt(2, asignacion.getIdColaborador());
            ps.setBoolean(3, asignacion.isEsCoordinador());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    asignacion.setIdAsignacion(rs.getInt(1));
                }
            }
        }
    }

    public List<Solicitud> listarSolicitudesPendientes()
            throws SQLException {

        String sql = "SELECT s.* FROM solicitudes s "
                + "WHERE s.estado IN ('pendiente', 'en proceso') "
                + "AND ("
                + "EXISTS ("
                + "SELECT 1 FROM cotizaciones c "
                + "WHERE c.id_solicitud = s.id_solicitud "
                + "AND c.estado = 'aprobada'"
                + ") "
                + "OR EXISTS ("
                + "SELECT 1 FROM asignaciones a "
                + "WHERE a.id_solicitud = s.id_solicitud"
                + ")"
                + ") "
                + "ORDER BY s.id_solicitud DESC";

        List<Solicitud> lista = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(Solicitud.builder()
                        .idSolicitud(rs.getInt("id_solicitud"))
                        .tipoSolicitud(rs.getString("tipo_solicitud"))
                        .motivo(rs.getString("motivo"))
                        .build());
            }
        }

        return lista;
    }

    // Método original
    public int obtenerIdAsignacion(
            int idSolicitud, int idColaborador)
            throws SQLException {

        String sql = "SELECT id_asignacion FROM asignaciones "
                + "WHERE id_solicitud = ? AND id_colaborador = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idSolicitud);
            ps.setInt(2, idColaborador);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_asignacion");
                }
            }
        }

        return -1;
    }

    // Verificar si ya existe la asignación
    public boolean existeAsignacion(
            int idSolicitud, int idColaborador)
            throws SQLException {

        return obtenerIdAsignacion(idSolicitud, idColaborador) != -1;
    }

    // Consultar el coordinador actual
    public int obtenerCoordinador(int idSolicitud)
            throws SQLException {

        String sql = "SELECT id_colaborador FROM asignaciones "
                + "WHERE id_solicitud = ? AND es_coordinador = 1";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idSolicitud);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_colaborador");
                }
            }
        }

        return -1;
    }

    public List<AsignacionDetalleDTO> listarAsignaciones()
            throws SQLException {

        List<AsignacionDetalleDTO> lista = new ArrayList<>();

        String sql = "SELECT a.*, s.tipo_solicitud, s.motivo, "
                + "s.estado, c.nombres, c.apellidos, c.rol "
                + "FROM asignaciones a "
                + "JOIN solicitudes s ON a.id_solicitud = s.id_solicitud "
                + "JOIN colaboradores c ON a.id_colaborador = c.id_colaborador "
                + "ORDER BY a.id_solicitud DESC, a.es_coordinador DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(AsignacionDetalleDTO.builder()
                        .idAsignacion(rs.getInt("id_asignacion"))
                        .idSolicitud(rs.getInt("id_solicitud"))
                        .idColaborador(rs.getInt("id_colaborador"))
                        .tipoSolicitud(rs.getString("tipo_solicitud"))
                        .motivo(rs.getString("motivo"))
                        .estado(rs.getString("estado"))
                        .nombres(rs.getString("nombres"))
                        .apellidos(rs.getString("apellidos"))
                        .rol(rs.getString("rol"))
                        .esCoordinador(rs.getBoolean("es_coordinador"))
                        .build());
            }
        }
        return lista;
    }

    // Registrar colaborador o designar coordinador
    // usando una transacción para proteger los cambios
    public void gestionarAsignacion(Asignacion asignacion)
            throws SQLException {

        boolean autoCommitOriginal = connection.getAutoCommit();

        if (!autoCommitOriginal) {
            throw new SQLException(
                    "La conexión ya tiene una transacción activa."
            );
        }

        connection.setAutoCommit(false);

        try {

            int idSolicitud = asignacion.getIdSolicitud();
            int idColaborador = asignacion.getIdColaborador();

            // Bloquear la solicitud mientras se modifica
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
                                "La solicitud no existe."
                        );
                    }

                    estado = rs.getString("estado");
                }
            }

            // Prohibir modificaciones en solicitudes finalizadas
            if (!"pendiente".equalsIgnoreCase(estado)
                    && !"en proceso".equalsIgnoreCase(estado)) {

                throw new SQLException(
                        "No se puede modificar una solicitud finalizada."
                );
            }

// VALIDAR APROBACIÓN DE COTIZACIÓN
            String sqlValidacion
                    = "SELECT "
                    + "EXISTS ("
                    + "SELECT 1 FROM cotizaciones "
                    + "WHERE id_solicitud = ? "
                    + "AND estado = 'aprobada'"
                    + ") AS aprobada, "
                    + "EXISTS ("
                    + "SELECT 1 FROM asignaciones "
                    + "WHERE id_solicitud = ?"
                    + ") AS ya_asignada";

            boolean cotizacionAprobada = false;
            boolean yaAsignada = false;

            try (PreparedStatement ps
                    = connection.prepareStatement(sqlValidacion)) {

                ps.setInt(1, idSolicitud);
                ps.setInt(2, idSolicitud);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        cotizacionAprobada = rs.getBoolean("aprobada");
                        yaAsignada = rs.getBoolean("ya_asignada");
                    }
                }
            }

// No permitir nuevas asignaciones sin aprobación.
// Conservar solicitudes antiguas ya asignadas.
            if (!cotizacionAprobada && !yaAsignada) {

                throw new SQLException(
                        "No se puede asignar esta solicitud porque "
                        + "todavía no tiene una cotización aprobada."
                );
            }

            int idAsignacion = obtenerIdAsignacion(
                    idSolicitud, idColaborador);

            if (asignacion.isEsCoordinador()) {

                // Quitar coordinación al responsable anterior
                String quitarCoordinador
                        = "UPDATE asignaciones "
                        + "SET es_coordinador = 0 "
                        + "WHERE id_solicitud = ? "
                        + "AND es_coordinador = 1";

                try (PreparedStatement ps
                        = connection.prepareStatement(quitarCoordinador)) {

                    ps.setInt(1, idSolicitud);
                    ps.executeUpdate();
                }

                if (idAsignacion != -1) {

                    // Convertir en coordinador al ya asignado
                    String actualizar
                            = "UPDATE asignaciones "
                            + "SET es_coordinador = 1 "
                            + "WHERE id_asignacion = ?";

                    try (PreparedStatement ps
                            = connection.prepareStatement(actualizar)) {

                        ps.setInt(1, idAsignacion);
                        ps.executeUpdate();
                    }

                } else {

                    // Asignar nuevo colaborador como coordinador
                    insert(asignacion);
                }

            } else {

                // Evitar asignaciones duplicadas
                if (idAsignacion != -1) {
                    throw new SQLException(
                            "Este colaborador ya está asignado "
                            + "a esta solicitud."
                    );
                }

                // Nueva asignación sin coordinación
                insert(asignacion);
            }

            // Cambiar automáticamente la solicitud a "en proceso"
            String actualizarEstado
                    = "UPDATE solicitudes "
                    + "SET estado = 'en proceso' "
                    + "WHERE id_solicitud = ? "
                    + "AND estado = 'pendiente'";

            try (PreparedStatement ps
                    = connection.prepareStatement(actualizarEstado)) {

                ps.setInt(1, idSolicitud);
                ps.executeUpdate();
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
                    "Error al gestionar la asignación.", e
            );

        } finally {

            connection.setAutoCommit(autoCommitOriginal);
        }
    }
}
