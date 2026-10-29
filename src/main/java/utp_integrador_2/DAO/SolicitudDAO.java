package utp_integrador_2.DAO;

import utp_integrador_2_model.Solicitud;
import utp_integrador_2.DTO.SolicitudDetalleDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class SolicitudDAO {

    private Connection connection;

    private static final String INSERT = "INSERT INTO solicitudes (id_usuario, tipo_solicitud, motivo, fecha_registro, estado) VALUES (?, ?, ?, NOW(), 'pendiente')";

    public SolicitudDAO(Connection connection) {
        this.connection = connection;
    }
    
    // SQL para actualizar a estado "finalizada"
    private static final String UPDATE_ESTADO_FINALIZADA = "UPDATE solicitudes SET estado = ? WHERE id_solicitud = ?";
    
     // SQL para obtener solicitudes por colaborador 
    private static final String SELECT_SOLICITUDES_BY_COLABORADOR =
            "SELECT s.id_solicitud, s.id_usuario, s.tipo_solicitud, s.motivo, s.fecha_registro, s.estado " + // SIN fecha_culminacion
            "FROM solicitudes s " +
            "JOIN asignaciones a ON s.id_solicitud = a.id_solicitud " +
            "WHERE a.id_colaborador = ?";
    
    // SQL para obtener el email del usuario solicitante
    private static final String SELECT_EMAIL_USUARIO_SOLICITANTE =
        "SELECT u.email FROM usuarios u JOIN solicitudes s ON u.id_usuario = s.id_usuario WHERE s.id_solicitud = ?";
    
    // SQL para verificar si un colaborador es coordinador de una solicitud
    private static final String CHECK_IS_COORDINATOR =
        "SELECT COUNT(*) FROM asignaciones WHERE id_solicitud = ? AND id_colaborador = ? AND es_coordinador = TRUE";
    
    // *** Método para culminar la solicitud (solo actualiza el estado) ***
    public void culminarSolicitud(int idSolicitud) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(UPDATE_ESTADO_FINALIZADA)) {
            ps.setString(1, "finalizada"); // El estado debe coincidir con el ENUM de tu DB
            ps.setInt(2, idSolicitud);
            int filasActualizadas = ps.executeUpdate();
            if (filasActualizadas == 0) {
                throw new SQLException("No se pudo culminar la solicitud con id: " + idSolicitud);
            }
        }
    }
    
    public List<Solicitud> obtenerSolicitudesPorColaborador(int idColaborador) throws SQLException {
        List<Solicitud> solicitudes = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_SOLICITUDES_BY_COLABORADOR)) {
            ps.setInt(1, idColaborador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Solicitud s = new Solicitud();
                    s.setIdSolicitud(rs.getInt("id_solicitud"));
                    s.setIdUsuario(rs.getInt("id_usuario"));
                    s.setTipoSolicitud(rs.getString("tipo_solicitud"));
                    s.setMotivo(rs.getString("motivo"));
                    s.setFechaRegistro(rs.getTimestamp("fecha_registro")); // Sigue siendo Timestamp para DATETIME
                    s.setEstado(rs.getString("estado"));
                    // No leemos fecha_culminacion
                    solicitudes.add(s);
                }
            }
        }
        return solicitudes;
    }
    
    
    // Método para obtener el email del usuario solicitante (necesario para el email)
    public String obtenerEmailUsuarioSolicitante(int idSolicitud) throws SQLException {
        String email = null;
        try (PreparedStatement ps = connection.prepareStatement(SELECT_EMAIL_USUARIO_SOLICITANTE)) {
            ps.setInt(1, idSolicitud);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    email = rs.getString("email");
                }
            }
        }
        return email;
    }
    
    
    // Método para obtener una solicitud por su ID (sin fecha_culminacion)
    public Solicitud obtenerSolicitudPorId(int idSolicitud) throws SQLException {
        String sql = "SELECT id_solicitud, id_usuario, tipo_solicitud, motivo, fecha_registro, estado FROM solicitudes WHERE id_solicitud = ?"; // SIN fecha_culminacion
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Solicitud s = new Solicitud();
                    s.setIdSolicitud(rs.getInt("id_solicitud"));
                    s.setIdUsuario(rs.getInt("id_usuario"));
                    s.setTipoSolicitud(rs.getString("tipo_solicitud"));
                    s.setMotivo(rs.getString("motivo"));
                    s.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                    s.setEstado(rs.getString("estado"));
                    // No leemos fecha_culminacion
                    return s;
                }
            }
        }
        return null;
    }
    
   
      // Metodo para Verificar si el colaborador es coordinador de la solicitud
    public boolean esColaboradorCoordinadorDeSolicitud(int idSolicitud, int idColaborador) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(CHECK_IS_COORDINATOR)) {
            ps.setInt(1, idSolicitud);
            ps.setInt(2, idColaborador);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Si el conteo es mayor a 0, significa que es coordinador
                }
            }
        }
        return false;
    }
    
    
    
    

    public void insert(Solicitud solicitud) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, solicitud.getIdUsuario());
            ps.setString(2, solicitud.getTipoSolicitud());
            ps.setString(3, solicitud.getMotivo());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    solicitud.setIdSolicitud(rs.getInt(1));
                }
            }
        }

    }

    public List<Solicitud> listarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM solicitudes WHERE id_usuario = ?";
        List<Solicitud> lista = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Solicitud s = Solicitud.builder()
                            .idSolicitud(rs.getInt("id_solicitud"))
                            .idUsuario(rs.getInt("id_usuario"))
                            .tipoSolicitud(rs.getString("tipo_solicitud"))
                            .motivo(rs.getString("motivo"))
                            .fechaRegistro(rs.getTimestamp("fecha_registro"))
                            .estado(rs.getString("estado"))
                            .build();
                    lista.add(s);
                }
            }
        }
        return lista;
    }

    public List<Solicitud> listarPorColaborador(int idColaborador) throws SQLException {
        String sql = "SELECT s.* FROM solicitudes s "
                + "JOIN asignaciones a ON s.id_solicitud = a.id_solicitud "
                + "WHERE a.id_colaborador = ?";
        List<Solicitud> lista = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idColaborador);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Solicitud s = Solicitud.builder()
                            .idSolicitud(rs.getInt("id_solicitud"))
                            .idUsuario(rs.getInt("id_usuario"))
                            .tipoSolicitud(rs.getString("tipo_solicitud"))
                            .motivo(rs.getString("motivo"))
                            .fechaRegistro(rs.getTimestamp("fecha_registro"))
                            .estado(rs.getString("estado"))
                            .build();
                    lista.add(s);
                }
            }
        }
        return lista;
    }


    private static final String UPDATE_ESTADO = "UPDATE solicitudes SET estado = ? WHERE id_solicitud = ?";

    public void actualizarEstado(int idSolicitud, String estado) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(UPDATE_ESTADO)) {
            ps.setString(1, estado);
            ps.setInt(2, idSolicitud);
            int filasActualizadas = ps.executeUpdate();
            if (filasActualizadas == 0) {
                throw new SQLException("No se actualizó ninguna solicitud con id: " + idSolicitud);
            }
        }

    }

    public List<Solicitud> listarSolicitudesPendientes() throws SQLException {
        String sql = "SELECT * FROM solicitudes WHERE estado = 'pendiente'";
        List<Solicitud> lista = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Solicitud s = Solicitud.builder()
                        .idSolicitud(rs.getInt("id_solicitud"))
                        .tipoSolicitud(rs.getString("tipo_solicitud"))
                        .motivo(rs.getString("motivo"))
                        .fechaRegistro(rs.getTimestamp("fecha_registro"))
                        .estado(rs.getString("estado"))
                        .build();
                lista.add(s);
            }
        }
        return lista;
    }
    
    
    public List<SolicitudDetalleDTO> listarTodasLasSolicitudesConDetalles(String tipo_solicitud, String colaborador, String estado) throws SQLException {
        List<SolicitudDetalleDTO> solicitudes = new ArrayList<>();
        StringBuilder sql = new StringBuilder(); // Usar StringBuilder para construir la consulta dinámicamente

        sql.append("SELECT ")
           .append("    s.id_solicitud, ")
           .append("    s.tipo_solicitud, ")
           .append("    s.motivo, ")
           .append("    s.fecha_registro, ")
           .append("    s.estado, ")
           .append("    u.nombres AS nombre_cliente, ")
           .append("    u.apellidos AS apellido_cliente, ")
           .append("    u.email AS email_cliente, ")
           .append("    c.nombres AS nombre_colaborador, ")
           .append("    c.apellidos AS apellido_colaborador, ")
           .append("    c.rol AS rol_colaborador ") // Correcto, es 'rol'
           .append("FROM ")
           .append("    solicitudes s ")
           .append("JOIN ")
           .append("    usuarios u ON s.id_usuario = u.id_usuario ")
           .append("LEFT JOIN ")
           .append("    asignaciones a ON s.id_solicitud = a.id_solicitud ")
           .append("LEFT JOIN ")
           .append("    colaboradores c ON a.id_colaborador = c.id_colaborador ");

        List<String> conditions = new ArrayList<>(); // Para almacenar las condiciones WHERE
        // Usar un List para los parámetros para que el orden sea correcto al setearlos
        List<Object> params = new ArrayList<>();

        if (tipo_solicitud != null && !tipo_solicitud.trim().isEmpty()) {
            conditions.add("s.tipo_solicitud LIKE ?");
            params.add("%" + tipo_solicitud.trim() + "%");
        }
        if (colaborador != null && !colaborador.trim().isEmpty()) {
            // Busca en nombre O apellido del colaborador
            conditions.add("(c.nombres LIKE ? OR c.apellidos LIKE ?)");
            params.add("%" + colaborador.trim() + "%");
            params.add("%" + colaborador.trim() + "%");
        }
        if (estado != null && !estado.trim().isEmpty()) {
            conditions.add("s.estado LIKE ?");
            params.add("%" + estado.trim() + "%");
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join(" AND ", conditions)); // Une las condiciones con " AND "
        }

        sql.append(" ORDER BY s.fecha_registro DESC;");

        System.out.println("SQL Query: " + sql.toString()); // Para depuración, puedes quitarlo después

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            // Setear los parámetros dinámicamente
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SolicitudDetalleDTO dto = new SolicitudDetalleDTO();
                    dto.setIdSolicitud(rs.getInt("id_solicitud"));
                    dto.setTipoSolicitud(rs.getString("tipo_solicitud"));
                    dto.setMotivo(rs.getString("motivo"));

                    // Manejo de LocalDateTime desde Timestamp
                    Timestamp sqlTimestamp = rs.getTimestamp("fecha_registro");
                    if (sqlTimestamp != null) {
                        dto.setFechaRegistro(sqlTimestamp.toLocalDateTime());
                    } else {
                        dto.setFechaRegistro(null);
                    }

                    dto.setEstado(rs.getString("estado"));
                    dto.setNombreCliente(rs.getString("nombre_cliente"));
                    dto.setApellidoCliente(rs.getString("apellido_cliente"));
                    dto.setEmailCliente(rs.getString("email_cliente"));

                    dto.setNombreColaboradorAsignado(rs.getString("nombre_colaborador"));
                    dto.setApellidoColaboradorAsignado(rs.getString("apellido_colaborador"));
                    dto.setRolColaboradorAsignado(rs.getString("rol_colaborador"));

                    solicitudes.add(dto);
                }
            }
        }
        return solicitudes;
    }
    
    // Método para actualizar una solicitud (Editar)
    public boolean actualizarSolicitud(Solicitud solicitud) throws SQLException {
        // Solo permitimos editar motivo y tipo si el estado es 'pendiente'
        // Esto se valida en la base de datos o en la lógica de negocio.
        // Aquí la consulta SQL no lo restringe, la lógica de negocio debe hacerlo antes de llamar a este método.
        String sql = "UPDATE solicitudes SET tipo_solicitud = ?, motivo = ? WHERE id_solicitud = ? AND estado = 'pendiente'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, solicitud.getTipoSolicitud());
            ps.setString(2, solicitud.getMotivo());
            ps.setInt(3, solicitud.getIdSolicitud());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Retorna true si se actualizó al menos una fila
        }
    }

    // Método para eliminar una solicitud
    public boolean eliminarSolicitud(int idSolicitud) throws SQLException {
        // Solo permitimos eliminar si el estado es 'pendiente'
        String sql = "DELETE FROM solicitudes WHERE id_solicitud = ? AND estado = 'pendiente'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Retorna true si se eliminó al menos una fila
        }
    }
    
    
    // NUEVO MÉTODO: Verificar si una solicitud tiene asignaciones
    public boolean tieneAsignaciones(int idSolicitud) throws SQLException {
        String sql = "SELECT COUNT(*) FROM asignaciones WHERE id_solicitud = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Retorna true si hay al menos una asignación
                }
            }
        }
        return false; // Por defecto, si no hay filas o error, no tiene asignaciones
    }
    

}