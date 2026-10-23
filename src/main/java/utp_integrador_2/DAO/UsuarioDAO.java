package utp_integrador_2.DAO;

import utp_integrador_2_model.Usuario;
import utp_integrador_2.util.PasswordUtil;
import java.util.List;
import java.util.ArrayList;

import java.sql.*;

public class UsuarioDAO {

    private Connection connection;

    private static final String VALIDAR
            = "SELECT * FROM usuarios WHERE email = ?";

    public UsuarioDAO(Connection connection) {
        this.connection = connection;
    }

    public Usuario validarUsuario(String email, String password)
            throws SQLException {

        try (PreparedStatement ps
                = connection.prepareStatement(VALIDAR)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    boolean cuentaActivada
                            = rs.getBoolean("cuenta_activada");

                    if (!cuentaActivada) {
                        return null;
                    }

                    String hashGuardado
                            = rs.getString("contraseña");

                    if (hashGuardado == null) {
                        return null;
                    }

                    boolean passwordCorrecto
                            = PasswordUtil.verificar(
                                    password,
                                    hashGuardado
                            );

                    if (passwordCorrecto) {

                        return Usuario.builder()
                                .idUsuario(
                                        rs.getInt("id_usuario"))
                                .nombres(
                                        rs.getString("nombres"))
                                .apellidos(
                                        rs.getString("apellidos"))
                                .email(
                                        rs.getString("email"))
                                .contrasena(hashGuardado)
                                .tipoUsuario(
                                        rs.getString("tipo_usuario"))
                                .cuentaActivada(true)
                                .build();
                    }
                }
            }
        }

        return null;
    }

    public void insert(Usuario usuario)
            throws SQLException {

        String sql = "INSERT INTO usuarios "
                + "(nombres, apellidos, email, contraseña, "
                + "tipo_usuario, cuenta_activada) "
                + "VALUES (?, ?, ?, NULL, ?, FALSE)";

        try (PreparedStatement ps
                = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNombres());
            ps.setString(2, usuario.getApellidos());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getTipoUsuario());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                }
            }
        }
    }

    public void activarCuenta(
            int idUsuario,
            String contrasenaHash)
            throws SQLException {

        String sql = "UPDATE usuarios "
                + "SET contraseña = ?, cuenta_activada = TRUE "
                + "WHERE id_usuario = ?";

        try (PreparedStatement ps
                = connection.prepareStatement(sql)) {

            ps.setString(1, contrasenaHash);
            ps.setInt(2, idUsuario);

            ps.executeUpdate();
        }
    }

    public Usuario buscarPorEmail(String email)
            throws SQLException {

        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (PreparedStatement ps
                = connection.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    return Usuario.builder()
                            .idUsuario(
                                    rs.getInt("id_usuario"))
                            .nombres(
                                    rs.getString("nombres"))
                            .apellidos(
                                    rs.getString("apellidos"))
                            .email(
                                    rs.getString("email"))
                            .contrasena(
                                    rs.getString("contraseña"))
                            .tipoUsuario(
                                    rs.getString("tipo_usuario"))
                            .cuentaActivada(
                                    rs.getBoolean("cuenta_activada"))
                            .build();
                }
            }
        }

        return null;
    }

    public void actualizarContrasena(
            int idUsuario,
            String contrasenaHash)
            throws SQLException {

        String sql = "UPDATE usuarios "
                + "SET contraseña = ? "
                + "WHERE id_usuario = ?";

        try (PreparedStatement ps
                = connection.prepareStatement(sql)) {

            ps.setString(1, contrasenaHash);
            ps.setInt(2, idUsuario);

            ps.executeUpdate();
        }
    }

    public boolean existeEmail(String email) throws SQLException {

        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }
    
    public List<Usuario> listarTodos() throws SQLException {
    List<Usuario> usuarios = new ArrayList<>();
    String sql = "SELECT id_usuario, nombres, apellidos, email, tipo_usuario, cuenta_activada "
            + "FROM usuarios ORDER BY id_usuario DESC";

    try (PreparedStatement ps = connection.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Usuario u = Usuario.builder()
                .idUsuario(rs.getInt("id_usuario"))
                .nombres(rs.getString("nombres"))
                .apellidos(rs.getString("apellidos"))
                .email(rs.getString("email"))
                .tipoUsuario(rs.getString("tipo_usuario"))
                .cuentaActivada(rs.getBoolean("cuenta_activada"))
                .build();
            usuarios.add(u);
        }
    }
    return usuarios;
}

}
