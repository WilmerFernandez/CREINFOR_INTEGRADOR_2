package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DTO.UsuarioDTO;
import utp_integrador_2.facade.UsuarioFacade;
import utp_integrador_2.util.EmailUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/UsuarioServlet")
public class UsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String nombres =
                request.getParameter("nombres");

        String apellidos =
                request.getParameter("apellidos");

        String email =
                request.getParameter("email");

        String tipoUsuario =
                request.getParameter("tipoUsuario");

        UsuarioDTO dto = UsuarioDTO.builder()
                .nombres(nombres)
                .apellidos(apellidos)
                .email(email)
                .tipoUsuario(tipoUsuario)
                .build();

        try (Connection connection =
                     BDConnection.getConnection()) {

            UsuarioFacade facade =
                    new UsuarioFacade(connection);

            // Registrar usuario y generar token
            String token =
                    facade.registrarUsuario(dto);

            // Construir enlace de activación
            String enlaceActivacion =
                    request.getScheme()
                    + "://"
                    + request.getServerName()
                    + ":"
                    + request.getServerPort()
                    + request.getContextPath()
                    + "/ActivarCuentaServlet?token="
                    + token;

            // Enviar correo de activación
            EmailUtil.enviarActivacionCuenta(
                    email,
                    nombres,
                    enlaceActivacion
            );

            request.setAttribute(
                    "mensaje",
                    "Usuario registrado correctamente. "
                    + "Se envió un enlace de activación "
                    + "a su correo electrónico."
            );

            request.getRequestDispatcher(
                    "UsuarioRegistro.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            String mensajeError =
                    e.getMessage();

            if (mensajeError != null
                    && mensajeError.contains(
                            "Ya existe un usuario registrado con ese correo")) {

                request.setAttribute(
                        "mensaje",
                        "Ya existe una cuenta asociada "
                        + "a ese correo electrónico."
                );

            } else {

                request.setAttribute(
                        "mensaje",
                        "No se pudo registrar el usuario."
                );
            }

            request.getRequestDispatcher(
                    "UsuarioRegistro.jsp"
            ).forward(request, response);
        }
    }
}