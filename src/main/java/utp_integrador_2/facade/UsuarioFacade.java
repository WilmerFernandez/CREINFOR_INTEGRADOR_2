package utp_integrador_2.facade;

import utp_integrador_2.DAO.TokenActivacionDAO;
import utp_integrador_2.DAO.UsuarioDAO;
import utp_integrador_2.DTO.UsuarioDTO;
import utp_integrador_2_model.TokenActivacion;
import utp_integrador_2_model.Usuario;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class UsuarioFacade {

    private UsuarioDAO usuarioDAO;
    private TokenActivacionDAO tokenDAO;

    public UsuarioFacade(Connection connection) {

        usuarioDAO = new UsuarioDAO(connection);
        tokenDAO = new TokenActivacionDAO(connection);
    }

    public Usuario validarLogin(
            String email,
            String password)
            throws SQLException {

        return usuarioDAO.validarUsuario(email, password);
    }

    public String registrarUsuario(UsuarioDTO dto)
            throws SQLException {
        
         if (usuarioDAO.existeEmail(dto.getEmail())) {
        throw new SQLException(
                "Ya existe un usuario registrado con ese correo."
        );
    }
        
        Usuario usuario = Usuario.builder()
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .email(dto.getEmail())
                .tipoUsuario(dto.getTipoUsuario())
                .cuentaActivada(false)
                .build();

        usuarioDAO.insert(usuario);

        String tokenGenerado =
                UUID.randomUUID().toString();

        TokenActivacion token =
                TokenActivacion.builder()
                        .idUsuario(usuario.getIdUsuario())
                        .token(tokenGenerado)
                        .fechaExpiracion(
                                LocalDateTime.now()
                                        .plusMinutes(30))
                        .usado(false)
                        .build();

        tokenDAO.insertar(token);

        return tokenGenerado;
    }
}