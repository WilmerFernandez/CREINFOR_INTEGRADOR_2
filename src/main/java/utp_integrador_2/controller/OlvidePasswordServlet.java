package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DAO.TokenRecuperacionDAO;
import utp_integrador_2.DAO.UsuarioDAO;
import utp_integrador_2_model.TokenRecuperacion;
import utp_integrador_2_model.Usuario;
import utp_integrador_2.util.EmailUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.UUID;

@WebServlet("/OlvidePasswordServlet")
public class OlvidePasswordServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");

        String mensaje
                = "Si el correo está registrado y la cuenta "
                + "está activada, recibirás un enlace "
                + "para restablecer tu contraseña.";

        try (Connection connection
                = BDConnection.getConnection()) {

            if (email != null && !email.isBlank()) {

                UsuarioDAO usuarioDAO
                        = new UsuarioDAO(connection);

                Usuario usuario
                        = usuarioDAO.buscarPorEmail(email.trim());

                if (usuario != null
                        && usuario.isCuentaActivada()) {

                    // Generar token de recuperación
                    String tokenGenerado
                            = UUID.randomUUID().toString();

                    TokenRecuperacion token
                            = TokenRecuperacion.builder()
                                    .idUsuario(usuario.getIdUsuario())
                                    .token(tokenGenerado)
                                    .fechaExpiracion(
                                            LocalDateTime.now()
                                                    .plusMinutes(30))
                                    .usado(false)
                                    .build();

                    TokenRecuperacionDAO tokenDAO
                            = new TokenRecuperacionDAO(connection);

                    tokenDAO.insertar(token);

                    // Construir URL pública de CREINFOR
                    String baseUrl
                            = System.getenv("APP_BASE_URL");

                    if (baseUrl == null
                            || baseUrl.isBlank()) {

                        baseUrl = request.getScheme()
                                + "://"
                                + request.getServerName();

                        int puerto
                                = request.getServerPort();

                        if (puerto != 80 && puerto != 443) {
                            baseUrl += ":" + puerto;
                        }

                        baseUrl += request.getContextPath();
                    }

                    baseUrl = baseUrl.replaceAll("/+$", "");

                    String enlace = baseUrl
                            + "/RestablecerPasswordServlet?token="
                            + URLEncoder.encode(
                                    tokenGenerado,
                                    StandardCharsets.UTF_8);

                    // Enviar recuperación por correo
                    String asunto
                            = "Recuperación de contraseña - CREINFOR";

                    String contenido
                            = "Hola, " + usuario.getNombres()
                            + "\n\n"
                            + "Recibimos una solicitud para "
                            + "restablecer tu contraseña "
                            + "de CREINFOR.\n\n"
                            + "Utiliza el siguiente enlace:\n"
                            + enlace
                            + "\n\n"
                            + "Este enlace vence en 30 minutos "
                            + "y solo puede utilizarse una vez.\n\n"
                            + "Si no solicitaste este cambio, "
                            + "ignora este mensaje.\n\n"
                            + "Equipo CREINFOR";

                    try {
                        EmailUtil.sendEmail(
                                email.trim(),
                                asunto,
                                contenido
                        );

                    } catch (Exception e) {
                        log("Error al enviar correo "
                                + "de recuperación", e);
                    }

                }
            }

        } catch (Exception e) {

            log("Error en la recuperación "
                    + "de contraseña", e);
        }

        request.setAttribute("mensaje", mensaje);

        request.getRequestDispatcher(
                "olvidePassword.jsp"
        ).forward(request, response);
    }
}
