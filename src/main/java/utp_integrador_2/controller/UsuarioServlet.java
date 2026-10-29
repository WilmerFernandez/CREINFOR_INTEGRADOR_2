
package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DTO.UsuarioDTO;
import utp_integrador_2.facade.UsuarioFacade;
import utp_integrador_2.util.EmailUtil;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

@WebServlet("/UsuarioServlet")
public class UsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String email = request.getParameter("email");
        String tipoUsuario = request.getParameter("tipoUsuario");

        UsuarioDTO dto = UsuarioDTO.builder()
                .nombres(nombres)
                .apellidos(apellidos)
                .email(email)
                .tipoUsuario(tipoUsuario)
                .build();

        String token;

        // =====================================
        // 1. REGISTRAR USUARIO EN MYSQL
        // =====================================

        try (Connection connection =
                BDConnection.getConnection()) {

            UsuarioFacade facade =
                    new UsuarioFacade(connection);

            token = facade.registrarUsuario(dto);

        } catch (Exception e) {

            log("Error al registrar usuario", e);

            String detalle = e.getMessage();

            if (detalle != null
                    && detalle.contains(
                        "Ya existe un usuario registrado con ese correo")) {

                request.setAttribute("mensaje",
                        "Ya existe una cuenta asociada "
                        + "a ese correo electrónico.");

            } else {

                request.setAttribute("mensaje",
                        "No se pudo registrar el usuario. "
                        + "Revise los registros del servidor.");
            }

            mostrarFormulario(request, response);
            return;
        }

        // =====================================
        // 2. CONSTRUIR ENLACE DE ACTIVACION
        // =====================================

        String baseUrl = System.getenv("APP_BASE_URL");

        if (baseUrl == null || baseUrl.isBlank()) {

            baseUrl = request.getScheme()
                    + "://"
                    + request.getServerName();

            int puerto = request.getServerPort();

            if (puerto != 80 && puerto != 443) {
                baseUrl += ":" + puerto;
            }

            baseUrl += request.getContextPath();
        }

        baseUrl = baseUrl.replaceAll("/+$", "");

        String enlaceActivacion = baseUrl
                + "/ActivarCuentaServlet?token="
                + URLEncoder.encode(
                        token, StandardCharsets.UTF_8);

        // =====================================
        // 3. ENVIAR CORREO DE ACTIVACION
        // =====================================

        try {

            EmailUtil.enviarActivacionCuenta(
                    email,
                    nombres,
                    enlaceActivacion
            );

            request.setAttribute(
                    "mensaje",
                    "Usuario registrado correctamente. "
                    + "Se solicitó el envío del enlace "
                    + "de activación a su correo."
            );

        } catch (MessagingException e) {

            log("Usuario registrado, pero falló "
                    + "el envío del correo de activación", e);

            request.setAttribute(
                    "mensaje",
                    "El usuario fue registrado, pero "
                    + "no se pudo enviar el correo "
                    + "de activación. No vuelva a "
                    + "registrarlo; contacte al administrador."
            );
        }

        mostrarFormulario(request, response);
    }

    private void mostrarFormulario(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "UsuarioRegistro.jsp"
        ).forward(request, response);
    }
}
