package utp_integrador_2.facade;

import utp_integrador_2.DAO.SolicitudDAO;
import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DTO.SolicitudDTO;
import utp_integrador_2_model.Solicitud;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import utp_integrador_2.DTO.SolicitudDetalleDTO;

public class SolicitudFacade {

    private SolicitudDAO solicitudDAO;

    public SolicitudFacade(Connection connection) {
        this.solicitudDAO = new SolicitudDAO(connection);
    }

    public void registrarSolicitud(SolicitudDTO dto) throws SQLException {
        Solicitud solicitud = Solicitud.builder()
                .idUsuario(dto.getIdUsuario())
                .tipoSolicitud(dto.getTipoSolicitud())
                .motivo(dto.getMotivo())
                .estado("pendiente")
                .build();

        solicitudDAO.insert(solicitud);
    }

    public List<Solicitud> obtenerSolicitudesPorUsuario(int idUsuario) throws SQLException {
        return solicitudDAO.listarPorUsuario(idUsuario);
    }

    public List<Solicitud> obtenerSolicitudesPorColaborador(int idColaborador) throws SQLException {
        return solicitudDAO.listarPorColaborador(idColaborador);
    }

    public void cambiarEstado(int idSolicitud, String estado) throws SQLException {
        solicitudDAO.actualizarEstado(idSolicitud, estado);
    }

    public List<Solicitud> listarSolicitudesPendientes() throws SQLException {
        return solicitudDAO.listarSolicitudesPendientes();
    }

    public void actualizarEstadoSolicitud(int idSolicitud, String nuevoEstado) throws SQLException {
        try (Connection connection = BDConnection.getConnection()) {
            SolicitudDAO solicitudDAO = new SolicitudDAO(connection);
            solicitudDAO.actualizarEstado(idSolicitud, nuevoEstado);
        }

    }

    // --- Método para Administrador  ---
    public List<SolicitudDetalleDTO> obtenerTodasLasSolicitudesParaAdmin(String motivo, String colaborador, String rol) {
        try {
            // Pasa los parámetros al DAO
            return solicitudDAO.listarTodasLasSolicitudesConDetalles(motivo, colaborador, rol);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudieron cargar las solicitudes. Por favor, inténtelo de nuevo más tarde.", e);
        }

    }

    // Nuevo método para obtener una solicitud por ID
    public Solicitud obtenerSolicitudPorId(int idSolicitud) {
        try {
            return solicitudDAO.obtenerSolicitudPorId(idSolicitud);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener la solicitud. " + e.getMessage(), e);
        }
    }

    // Nuevo método para actualizar una solicitud
    public boolean actualizarSolicitud(Solicitud solicitud) {
        try {
            // Lógica de negocio adicional si es necesario antes de llamar al DAO
            // Por ejemplo, verificar que el usuario actual tenga permisos para editar esta solicitud
            return solicitudDAO.actualizarSolicitud(solicitud);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar la solicitud. " + e.getMessage(), e);
        }
    }

    // Nuevo método para eliminar una solicitud
    public boolean eliminarSolicitud(int idSolicitud) {
        try {
            // Lógica de negocio adicional si es necesario (ej. verificar permisos)
            return solicitudDAO.eliminarSolicitud(idSolicitud);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar la solicitud. " + e.getMessage(), e);
        }
    }
    
    // NUEVO MÉTODO: Verificar si una solicitud tiene asignaciones
    public boolean verificarSiTieneAsignaciones(int idSolicitud) {
        try {
            return solicitudDAO.tieneAsignaciones(idSolicitud);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al verificar asignaciones de la solicitud. " + e.getMessage(), e);
        }
    }

}