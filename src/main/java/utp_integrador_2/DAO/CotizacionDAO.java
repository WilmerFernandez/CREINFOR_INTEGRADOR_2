package utp_integrador_2.DAO;

import utp_integrador_2_model.Cotizacion;
import utp_integrador_2_model.DetalleCotizacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utp_integrador_2_model.Solicitud;



public class CotizacionDAO {

    private final Connection connection;

    public CotizacionDAO(Connection connection) {
        this.connection = connection;
    }

    // Guardar cotización y recuperar el ID generado
    public void insertar(Cotizacion cotizacion) throws SQLException {

        String sql = "INSERT INTO cotizaciones "
                + "(id_solicitud, fecha_vencimiento, estado, moneda, "
                + "subtotal, impuesto, total, observaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, cotizacion.getIdSolicitud());

            if (cotizacion.getFechaVencimiento() != null) {
                ps.setDate(2, Date.valueOf(
                        cotizacion.getFechaVencimiento()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            ps.setString(3, cotizacion.getEstado());
            ps.setString(4, cotizacion.getMoneda());
            ps.setBigDecimal(5, cotizacion.getSubtotal());
            ps.setBigDecimal(6, cotizacion.getImpuesto());
            ps.setBigDecimal(7, cotizacion.getTotal());
            ps.setString(8, cotizacion.getObservaciones());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException(
                            "No se pudo obtener el ID de la cotización.");
                }

                cotizacion.setIdCotizacion(rs.getInt(1));
            }
        }
    }

    // Registrar un concepto de cotización
    public void insertarDetalle(DetalleCotizacion detalle)
            throws SQLException {

        String sql = "INSERT INTO detalle_cotizacion "
                + "(id_cotizacion, descripcion, cantidad, "
                + "precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, detalle.getIdCotizacion());
            ps.setString(2, detalle.getDescripcion());
            ps.setBigDecimal(3, detalle.getCantidad());
            ps.setBigDecimal(4, detalle.getPrecioUnitario());
            ps.setBigDecimal(5, detalle.getSubtotal());

            ps.executeUpdate();
        }
    }

    // Consultar cotizaciones de una solicitud
    public List<Cotizacion> listarPorSolicitud(int idSolicitud)
            throws SQLException {

        List<Cotizacion> lista = new ArrayList<>();

        String sql = "SELECT * FROM cotizaciones "
                + "WHERE id_solicitud = ? "
                + "ORDER BY id_cotizacion DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idSolicitud);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCotizacion(rs));
                }
            }
        }

        return lista;
    }

    // Consultar una cotización por ID
    public Cotizacion buscarPorId(int idCotizacion)
            throws SQLException {

        String sql = "SELECT * FROM cotizaciones "
                + "WHERE id_cotizacion = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idCotizacion);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearCotizacion(rs) : null;
            }
        }
    }

    // Consultar conceptos de una cotización
    public List<DetalleCotizacion> listarDetalles(int idCotizacion)
            throws SQLException {

        List<DetalleCotizacion> lista = new ArrayList<>();

        String sql = "SELECT * FROM detalle_cotizacion "
                + "WHERE id_cotizacion = ? ORDER BY id_detalle";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idCotizacion);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(DetalleCotizacion.builder()
                            .idDetalle(rs.getInt("id_detalle"))
                            .idCotizacion(rs.getInt("id_cotizacion"))
                            .descripcion(rs.getString("descripcion"))
                            .cantidad(rs.getBigDecimal("cantidad"))
                            .precioUnitario(rs.getBigDecimal("precio_unitario"))
                            .subtotal(rs.getBigDecimal("subtotal"))
                            .build());
                }
            }
        }

        return lista;
    }

    // Cambiar estado de la cotización
    public boolean actualizarEstado(int idCotizacion, String estado)
            throws SQLException {

        String sql = "UPDATE cotizaciones SET estado = ?, "
                + "fecha_respuesta = CASE "
                + "WHEN ? IN ('aprobada', 'rechazada') "
                + "THEN CURRENT_TIMESTAMP ELSE NULL END "
                + "WHERE id_cotizacion = ? "
                + "AND estado = 'enviada'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setString(2, estado);
            ps.setInt(3, idCotizacion);

            return ps.executeUpdate() == 1;
        }
    }

    // Convertir fila SQL en objeto Java
    private Cotizacion mapearCotizacion(ResultSet rs)
            throws SQLException {

        Timestamp emision = rs.getTimestamp("fecha_emision");
        Date vencimiento = rs.getDate("fecha_vencimiento");
        Timestamp respuesta = rs.getTimestamp("fecha_respuesta");

        return Cotizacion.builder()
                .idCotizacion(rs.getInt("id_cotizacion"))
                .idSolicitud(rs.getInt("id_solicitud"))
                .fechaEmision(emision == null ? null
                        : emision.toLocalDateTime())
                .fechaVencimiento(vencimiento == null ? null
                        : vencimiento.toLocalDate())
                .estado(rs.getString("estado"))
                .moneda(rs.getString("moneda"))
                .subtotal(rs.getBigDecimal("subtotal"))
                .impuesto(rs.getBigDecimal("impuesto"))
                .total(rs.getBigDecimal("total"))
                .observaciones(rs.getString("observaciones"))
                .fechaRespuesta(respuesta == null ? null
                        : respuesta.toLocalDateTime())
                .build();
    }

    // Listar todas las cotizaciones
    public List<Cotizacion> listarTodas() throws SQLException {

        List<Cotizacion> lista = new ArrayList<>();

        String sql = "SELECT * FROM cotizaciones "
                + "ORDER BY id_cotizacion DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCotizacion(rs));
            }
        }

        return lista;
    }

    public String obtenerEmailCliente(int idCotizacion)
            throws SQLException {

        String sql = "SELECT u.email FROM cotizaciones c "
                + "JOIN solicitudes s ON c.id_solicitud = s.id_solicitud "
                + "JOIN usuarios u ON s.id_usuario = u.id_usuario "
                + "WHERE c.id_cotizacion = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idCotizacion);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("email") : null;
            }
        }
    }

// Reserva el envío para impedir solicitudes simultáneas
    public boolean iniciarEnvio(int idCotizacion)
            throws SQLException {

        String sql = "UPDATE cotizaciones SET estado = 'enviando' "
                + "WHERE id_cotizacion = ? AND estado = 'borrador' "
                + "AND fecha_vencimiento >= CURRENT_DATE()";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idCotizacion);
            return ps.executeUpdate() == 1;
        }
    }

// Confirma el resultado del intento
    public boolean finalizarEnvio(int idCotizacion, boolean enviado)
            throws SQLException {

        String sql = "UPDATE cotizaciones SET estado = ? "
                + "WHERE id_cotizacion = ? AND estado = 'enviando'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, enviado ? "enviada" : "error_envio");
            ps.setInt(2, idCotizacion);
            return ps.executeUpdate() == 1;
        }
    }

    public List<Cotizacion> listarPorCliente(int idUsuario)
            throws SQLException {

        List<Cotizacion> lista = new ArrayList<>();

        String sql = "SELECT c.* FROM cotizaciones c "
                + "INNER JOIN solicitudes s "
                + "ON c.id_solicitud = s.id_solicitud "
                + "WHERE s.id_usuario = ? "
                + "ORDER BY c.id_cotizacion DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCotizacion(rs));
                }
            }
        }

        return lista;
    }

// Responder únicamente a una cotización propia, enviada y vigente
    public boolean responderCotizacion(
            int idCotizacion,
            int idUsuario,
            String respuesta) throws SQLException {

        if (!"aprobada".equals(respuesta)
                && !"rechazada".equals(respuesta)) {
            throw new IllegalArgumentException(
                    "Respuesta de cotización inválida.");
        }

        String sql = "UPDATE cotizaciones c "
                + "INNER JOIN solicitudes s "
                + "ON c.id_solicitud = s.id_solicitud "
                + "SET c.estado = ?, "
                + "c.fecha_respuesta = NOW() "
                + "WHERE c.id_cotizacion = ? "
                + "AND s.id_usuario = ? "
                + "AND c.estado = 'enviada' "
                + "AND c.fecha_vencimiento >= CURRENT_DATE()";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, respuesta);
            ps.setInt(2, idCotizacion);
            ps.setInt(3, idUsuario);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean tieneCotizacionBloqueante(int idSolicitud)
            throws SQLException {

        String sql = "SELECT EXISTS ("
                + "SELECT 1 FROM cotizaciones "
                + "WHERE id_solicitud = ? "
                + "AND estado IN ("
                + "'borrador', 'enviando', 'enviada', "
                + "'error_envio', 'aprobada')"
                + ") AS bloqueada";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idSolicitud);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean("bloqueada");
            }
        }
    }

    public boolean tieneAlgunaCotizacion(int idSolicitud)
            throws SQLException {

        String sql = "SELECT EXISTS ("
                + "SELECT 1 FROM cotizaciones "
                + "WHERE id_solicitud = ?"
                + ") AS existe";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean("existe");
            }
        }
    }

    public boolean tieneCotizacionAprobada(int idSolicitud)
            throws SQLException {

        String sql = "SELECT EXISTS ("
                + "SELECT 1 FROM cotizaciones "
                + "WHERE id_solicitud = ? "
                + "AND estado = 'aprobada'"
                + ") AS aprobada";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idSolicitud);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean("aprobada");
            }
        }
    }

    public List<Solicitud> listarSolicitudesParaCotizar()
            throws SQLException {

        List<Solicitud> lista = new ArrayList<>();

        String sql = "SELECT s.* FROM solicitudes s "
                + "WHERE s.estado = 'pendiente' "
                + "AND NOT EXISTS ("
                + "SELECT 1 FROM cotizaciones c "
                + "WHERE c.id_solicitud = s.id_solicitud "
                + "AND c.estado IN ("
                + "'borrador', 'enviando', 'enviada', "
                + "'error_envio', 'aprobada')"
                + ") "
                + "ORDER BY s.id_solicitud DESC";

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

}
