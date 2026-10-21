package utp_integrador_2.facade;

import utp_integrador_2.DAO.AsignacionDAO;
import utp_integrador_2.DTO.AsignacionDTO;
import utp_integrador_2_model.Asignacion;
import java.util.List;
import utp_integrador_2_model.Solicitud;

import java.sql.Connection;
import java.sql.SQLException;

public class AsignacionFacade {
    private AsignacionDAO asignacionDAO;

    public AsignacionFacade(Connection connection) {
        this.asignacionDAO = new AsignacionDAO(connection);
    }

    public void registrarAsignacion(AsignacionDTO dto) throws SQLException {
        Asignacion asignacion = Asignacion.builder()
                .idSolicitud(dto.getIdSolicitud())
                .idColaborador(dto.getIdColaborador())
                .esCoordinador(dto.isEsCoordinador())
                .build();

        asignacionDAO.gestionarAsignacion(asignacion);
    }
    
    public List<Solicitud> listarSolicitudesDisponibles()
        throws SQLException {

    return asignacionDAO.listarSolicitudesPendientes();
}
    
}
