package utp_integrador_2.facade;

import utp_integrador_2.DAO.CotizacionDAO;
import utp_integrador_2.DTO.CotizacionDTO;
import utp_integrador_2.DTO.DetalleCotizacionDTO;
import utp_integrador_2_model.Cotizacion;
import utp_integrador_2_model.DetalleCotizacion;
import utp_integrador_2_model.Solicitud;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CotizacionFacade {

    private final Connection connection;
    private final CotizacionDAO cotizacionDAO;

    public CotizacionFacade(Connection connection) {
        this.connection = connection;
        this.cotizacionDAO = new CotizacionDAO(connection);
    }

    // Registrar una cotización con todos sus conceptos
    public int registrarCotizacion(CotizacionDTO dto)
            throws SQLException {

        // Validar datos generales
        if (dto == null || dto.getIdSolicitud() <= 0) {
            throw new SQLException("Solicitud inválida.");
        }

        if (dto.getFechaVencimiento() == null
                || dto.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new SQLException(
                    "La fecha de vencimiento no puede ser anterior a hoy.");
        }

        List<DetalleCotizacionDTO> detalles = dto.getDetalles();

        if (detalles == null || detalles.isEmpty()) {
            throw new SQLException(
                    "La cotización debe contener al menos un concepto.");
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        // Validar y calcular el costo total
        for (DetalleCotizacionDTO detalle : detalles) {

            if (detalle == null
                    || detalle.getDescripcion() == null
                    || detalle.getDescripcion().trim().isEmpty()
                    || detalle.getCantidad() == null
                    || detalle.getPrecioUnitario() == null
                    || detalle.getCantidad().compareTo(BigDecimal.ZERO) <= 0
                    || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0) {

                throw new SQLException(
                        "Hay conceptos con datos o importes inválidos.");
            }

            BigDecimal subtotalDetalle
                    = detalle.getCantidad()
                            .multiply(detalle.getPrecioUnitario())
                            .setScale(2, RoundingMode.HALF_UP);

            subtotal = subtotal.add(subtotalDetalle);
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        // Por ahora, sin impuesto aplicado automáticamente.
        // La configuración tributaria se definirá posteriormente.
        BigDecimal impuesto = BigDecimal.ZERO.setScale(2);
        BigDecimal total = subtotal.add(impuesto);

        Cotizacion cotizacion = Cotizacion.builder()
                .idSolicitud(dto.getIdSolicitud())
                .fechaVencimiento(dto.getFechaVencimiento())
                .estado("borrador")
                .moneda("PEN")
                .subtotal(subtotal)
                .impuesto(impuesto)
                .total(total)
                .observaciones(dto.getObservaciones())
                .build();

        boolean autoCommitOriginal = connection.getAutoCommit();

        if (!autoCommitOriginal) {
            throw new SQLException(
                    "La conexión ya tiene una transacción activa.");
        }

        connection.setAutoCommit(false);

        try {

            // Verificar que exista la solicitud y bloquearla
            // mientras se crea la cotización
            String sql = "SELECT estado FROM solicitudes "
                    + "WHERE id_solicitud = ? FOR UPDATE";

            try (java.sql.PreparedStatement ps
                    = connection.prepareStatement(sql)) {

                ps.setInt(1, dto.getIdSolicitud());

                try (java.sql.ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        throw new SQLException(
                                "La solicitud no existe.");
                    }

                    if ("finalizada".equalsIgnoreCase(
                            rs.getString("estado"))) {

                        throw new SQLException(
                                "No se puede cotizar una solicitud finalizada.");
                    }
                }
            }

            // Guardar cotización principal
            cotizacionDAO.insertar(cotizacion);

            // Guardar conceptos
            for (DetalleCotizacionDTO detalle : detalles) {

                BigDecimal subtotalDetalle
                        = detalle.getCantidad()
                                .multiply(detalle.getPrecioUnitario())
                                .setScale(2, RoundingMode.HALF_UP);

                DetalleCotizacion entidad
                        = DetalleCotizacion.builder()
                                .idCotizacion(cotizacion.getIdCotizacion())
                                .descripcion(detalle.getDescripcion().trim())
                                .cantidad(detalle.getCantidad())
                                .precioUnitario(detalle.getPrecioUnitario())
                                .subtotal(subtotalDetalle)
                                .build();

                cotizacionDAO.insertarDetalle(entidad);
            }

            connection.commit();

            return cotizacion.getIdCotizacion();

        } catch (Exception e) {

            try {
                connection.rollback();
            } catch (SQLException ex) {
                e.addSuppressed(ex);
            }

            if (e instanceof SQLException) {
                throw (SQLException) e;
            }

            throw new SQLException(
                    "Error al registrar la cotización.", e);

        } finally {
            connection.setAutoCommit(autoCommitOriginal);
        }
    }

    // Consultar cotizaciones por solicitud
    public List<Cotizacion> listarPorSolicitud(int idSolicitud)
            throws SQLException {

        return cotizacionDAO.listarPorSolicitud(idSolicitud);
    }

    // Consultar cotización por ID
    public Cotizacion buscarPorId(int idCotizacion)
            throws SQLException {

        return cotizacionDAO.buscarPorId(idCotizacion);
    }

    // Consultar los conceptos de una cotización
    public List<DetalleCotizacion> listarDetalles(int idCotizacion)
            throws SQLException {

        return cotizacionDAO.listarDetalles(idCotizacion);
    }

    public List<Cotizacion> listarTodas() throws SQLException {
        return cotizacionDAO.listarTodas();
    }

    public String obtenerEmailCliente(int idCotizacion)
            throws SQLException {
        return cotizacionDAO.obtenerEmailCliente(idCotizacion);
    }

    public boolean iniciarEnvio(int idCotizacion)
            throws SQLException {
        return cotizacionDAO.iniciarEnvio(idCotizacion);
    }

    public boolean finalizarEnvio(int idCotizacion, boolean enviado)
            throws SQLException {
        return cotizacionDAO.finalizarEnvio(idCotizacion, enviado);
    }

    public List<Cotizacion> listarPorCliente(int idUsuario)
            throws SQLException {

        return cotizacionDAO.listarPorCliente(idUsuario);
    }

    public boolean responderCotizacion(
            int idCotizacion,
            int idUsuario,
            String respuesta) throws SQLException {

        return cotizacionDAO.responderCotizacion(
                idCotizacion, idUsuario, respuesta);
    }

    public boolean tieneCotizacionBloqueante(int idSolicitud)
            throws SQLException {

        return cotizacionDAO.tieneCotizacionBloqueante(idSolicitud);
    }

    public boolean tieneAlgunaCotizacion(int idSolicitud)
            throws SQLException {
        return cotizacionDAO.tieneAlgunaCotizacion(idSolicitud);
    }

    public boolean tieneCotizacionAprobada(int idSolicitud)
            throws SQLException {

        return cotizacionDAO.tieneCotizacionAprobada(idSolicitud);
    }
    
    public List<Solicitud> listarSolicitudesParaCotizar()
        throws SQLException {

    return cotizacionDAO.listarSolicitudesParaCotizar();
}
}
