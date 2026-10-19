package utp_integrador_2.facade;

import utp_integrador_2.DAO.ActividadDAO;
import utp_integrador_2.DTO.ActividadDTO;
import utp_integrador_2_model.Actividad;
import utp_integrador_2.DAO.AsignacionDAO;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;

public class ActividadFacade {

    private ActividadDAO actividadDAO;
    private AsignacionDAO asignacionDAO;

    public ActividadFacade(Connection connection) {
        this.actividadDAO = new ActividadDAO(connection);
        this.asignacionDAO = new AsignacionDAO(connection); // Inicializa el AsignacionDAO también
    }

    public void registrarActividad(ActividadDTO dto) throws SQLException {
        // Convertir ActividadDTO a Actividad
        Actividad actividad = Actividad.builder()
                .idAsignacion(dto.getIdAsignacion())
                .fecha(dto.getFecha())
                .descripcion(dto.getDescripcion())
                .tiempoHoras(dto.getTiempoHoras())
                .build();

        // Llamar al DAO para insertar la actividad
        actividadDAO.insert(actividad);
    }

    public int obtenerIdAsignacion(int idSolicitud, int idColaborador) throws SQLException {
        // Llamar al DAO de asignaciones para obtener el id_asignacion
        return asignacionDAO.obtenerIdAsignacion(idSolicitud, idColaborador);
    }

    public List<Actividad> listarActividadesPorSolicitud(int idSolicitud) throws SQLException {
        return actividadDAO.listarPorSolicitud(idSolicitud);
    }

}
