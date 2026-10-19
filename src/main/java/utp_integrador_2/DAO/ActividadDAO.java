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
        try (PreparedStatement ps = connection.prepareStatement(INSERT)) {
            ps.setInt(1, actividad.getIdAsignacion());
            ps.setDate(2, new java.sql.Date(actividad.getFecha().getTime()));
            ps.setString(3, actividad.getDescripcion());
            ps.setDouble(4, actividad.getTiempoHoras());
            ps.executeUpdate();
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
