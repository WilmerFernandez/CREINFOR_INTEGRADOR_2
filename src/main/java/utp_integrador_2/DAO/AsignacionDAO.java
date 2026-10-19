package utp_integrador_2.DAO;



import utp_integrador_2_model.Solicitud;
import utp_integrador_2_model.Asignacion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsignacionDAO {

    private Connection connection;

    private static final String INSERT = "INSERT INTO asignaciones (id_solicitud, id_colaborador, es_coordinador) VALUES (?, ?, ?)";

    public AsignacionDAO(Connection connection) {
        this.connection = connection;
    }

    public void insert(Asignacion asignacion) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
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

    public List<Solicitud> listarSolicitudesPendientes() throws SQLException {
        String sql = "SELECT * FROM solicitudes WHERE estado = 'pendiente'";
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

    public int obtenerIdAsignacion(int idSolicitud, int idColaborador) throws SQLException {
        String sql = "SELECT id_asignacion FROM asignaciones WHERE id_solicitud = ? AND id_colaborador = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSolicitud);
            ps.setInt(2, idColaborador);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_asignacion");
                }
            }
        }
        // Si no existe asignación, retorna -1 o lanza excepción según convenga
        return -1;
    }
    
  
    
    

}
