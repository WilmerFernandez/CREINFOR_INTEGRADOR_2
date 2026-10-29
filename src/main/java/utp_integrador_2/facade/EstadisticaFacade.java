package utp_integrador_2.facade;

import utp_integrador_2.DAO.EstadisticaDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;



public class EstadisticaFacade {
    private EstadisticaDAO estadisticaDAO;

    public EstadisticaFacade(Connection connection) {
        this.estadisticaDAO = new EstadisticaDAO(connection);
    }

    public Map<String, Integer> obtenerConteoSolicitudesPorEstado() throws SQLException {
        return estadisticaDAO.conteoSolicitudesPorEstado();
    }
}
