package utp_integrador_2.DAO;

import utp_integrador_2_model.Colaborador;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorDAO {

    private Connection connection;
    private static final String SELECT_ALL = "SELECT * FROM colaboradores ORDER BY id_colaborador DESC";

    public ColaboradorDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Colaborador> listarTodos() throws SQLException {
        List<Colaborador> lista = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Colaborador c = Colaborador.builder()
                        .idColaborador(rs.getInt("id_colaborador"))
                        .nombres(rs.getString("nombres"))
                        .apellidos(rs.getString("apellidos"))
                        .email(rs.getString("email"))
                        .rol(rs.getString("rol"))
                        .build();

                lista.add(c);
            }
        }
        return lista;
    }

    public Colaborador buscarPorId(int idColaborador) throws SQLException {
        String sql = "SELECT * FROM colaboradores WHERE id_colaborador = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idColaborador);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Colaborador.builder()
                            .idColaborador(rs.getInt("id_colaborador"))
                            .nombres(rs.getString("nombres"))
                            .apellidos(rs.getString("apellidos"))
                            .email(rs.getString("email"))
                            .rol(rs.getString("rol"))
                            .build();
                }
            }
        }
        return null;
    }

    public void insertar(Colaborador colaborador) throws SQLException {
        String sql = "INSERT INTO colaboradores (nombres, apellidos, email, rol) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, colaborador.getNombres());
            ps.setString(2, colaborador.getApellidos());
            ps.setString(3, colaborador.getEmail());
            ps.setString(4, colaborador.getRol());
            ps.executeUpdate();
        }
    }

    public void actualizar(Colaborador colaborador) throws SQLException {
        String sql = "UPDATE colaboradores SET nombres = ?, apellidos = ?, email = ?, rol = ? WHERE id_colaborador = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, colaborador.getNombres());
            ps.setString(2, colaborador.getApellidos());
            ps.setString(3, colaborador.getEmail());
            ps.setString(4, colaborador.getRol());
            ps.setInt(5, colaborador.getIdColaborador());
            ps.executeUpdate();
        }
    }

    public boolean existeEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM colaboradores WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean existeEmailEnOtroColaborador(String email, int idColaborador) throws SQLException {
        String sql = "SELECT COUNT(*) FROM colaboradores WHERE email = ? AND id_colaborador <> ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, idColaborador);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public int obtenerIdColaboradorPorEmail(String email) throws SQLException {
        String sql = "SELECT id_colaborador FROM colaboradores WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_colaborador");
                }
            }
        }
        return -1;
    }

    public String obtenerRolColaborador(int idColaborador) throws SQLException {
        String sql = "SELECT rol FROM colaboradores WHERE id_colaborador = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idColaborador);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("rol");
                }
            }
        }
        return null;
    }

    public boolean eliminarColaborador(int idColaborador) {

        String sql = "DELETE FROM colaboradores WHERE id_colaborador = ?";

        try (Connection conn = BDConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idColaborador);

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean tieneAsignaciones(int idColaborador)
            throws SQLException {

        String sql = "SELECT COUNT(*) FROM asignaciones "
                + "WHERE id_colaborador = ?";

        try (Connection conn = BDConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idColaborador);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }
}
