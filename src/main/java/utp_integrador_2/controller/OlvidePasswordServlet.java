package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DAO.TokenRecuperacionDAO;
import utp_integrador_2.DAO.UsuarioDAO;
import utp_integrador_2_model.TokenRecuperacion;
import utp_integrador_2_model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
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

        String email =
                request.getParameter("email");

        try (Connection connection =
                     BDConnection.getConnection()) {

            UsuarioDAO usuarioDAO =
                    new UsuarioDAO(connection);

            Usuario usuario =
                    usuarioDAO.buscarPorEmail(email);

            /*
             * Mensaje genérico para evitar revelar
             * si un correo está registrado o no.
             */
            String mensaje =
                    "Si el correo está registrado, "
                    + "se ha generado un enlace de recuperación.";

            request.setAttribute(
                    "mensaje",
                    mensaje
            );

            if (usuario != null
                    && usuario.isCuentaActivada()) {

                String tokenGenerado =
                        UUID.randomUUID().toString();

                TokenRecuperacion token =
                        TokenRecuperacion.builder()
                                .idUsuario(
                                        usuario.getIdUsuario())
                                .token(tokenGenerado)
                                .fechaExpiracion(
                                        LocalDateTime.now()
                                                .plusMinutes(30))
                                .usado(false)
                                .build();

                TokenRecuperacionDAO tokenDAO =
                        new TokenRecuperacionDAO(connection);

                tokenDAO.insertar(token);

                String enlace =
                        request.getScheme()
                        + "://"
                        + request.getServerName()
                        + ":"
                        + request.getServerPort()
                        + request.getContextPath()
                        + "/RestablecerPasswordServlet?token="
                        + tokenGenerado;

                request.setAttribute(
                        "enlaceRecuperacion",
                        enlace
                );
            }

            request.getRequestDispatcher(
                    "olvidePassword.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            throw new ServletException(
                    "Error al recuperar contraseña.",
                    e
            );
        }
    }
}