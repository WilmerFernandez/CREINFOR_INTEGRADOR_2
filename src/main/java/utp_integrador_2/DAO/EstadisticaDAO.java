package utp_integrador_2.DAO;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class EstadisticaDAO {
    private Connection connection;

    public EstadisticaDAO(Connection connection) {
        this.connection = connection;
    }

    public Map<String, Integer> conteoSolicitudesPorEstado() throws SQLException {
        String sql = "SELECT estado, COUNT(*) as total FROM solicitudes GROUP BY estado";
        Map<String, Integer> mapa = new HashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mapa.put(rs.getString("estado"), rs.getInt("total"));
            }
        }
        return mapa;
    }
}
