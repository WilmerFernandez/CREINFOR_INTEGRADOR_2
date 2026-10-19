package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DAO.TokenRecuperacionDAO;
import utp_integrador_2.DAO.UsuarioDAO;
import utp_integrador_2_model.TokenRecuperacion;
import utp_integrador_2.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;

@WebServlet("/RestablecerPasswordServlet")
public class RestablecerPasswordServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String token =
                request.getParameter("token");

        if (token == null || token.trim().isEmpty()) {

            request.setAttribute(
                    "error",
                    "El enlace de recuperación no es válido."
            );

            request.getRequestDispatcher(
                    "restablecerPassword.jsp"
            ).forward(request, response);

            return;
        }

        try (Connection connection =
                     BDConnection.getConnection()) {

            TokenRecuperacionDAO tokenDAO =
                    new TokenRecuperacionDAO(connection);

            TokenRecuperacion recuperacion =
                    tokenDAO.buscarPorToken(token);

            if (recuperacion == null) {

                request.setAttribute(
                        "error",
                        "El enlace de recuperación no existe."
                );

            } else if (recuperacion.isUsado()) {

                request.setAttribute(
                        "error",
                        "Este enlace ya fue utilizado."
                );

            } else if (LocalDateTime.now().isAfter(
                    recuperacion.getFechaExpiracion())) {

                request.setAttribute(
                        "error",
                        "El enlace de recuperación ha expirado."
                );

            } else {

                request.setAttribute(
                        "token",
                        token
                );
            }

            request.getRequestDispatcher(
                    "restablecerPassword.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            throw new ServletException(
                    "Error al validar el enlace.",
                    e
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String token =
                request.getParameter("token");

        String password =
                request.getParameter("password");

        String confirmarPassword =
                request.getParameter("confirmarPassword");

        if (password == null
                || password.length() < 8) {

            request.setAttribute(
                    "error",
                    "La contraseña debe tener como mínimo 8 caracteres."
            );

            request.setAttribute(
                    "token",
                    token
            );

            request.getRequestDispatcher(
                    "restablecerPassword.jsp"
            ).forward(request, response);

            return;
        }

        if (!password.equals(confirmarPassword)) {

            request.setAttribute(
                    "error",
                    "Las contraseñas no coinciden."
            );

            request.setAttribute(
                    "token",
                    token
            );

            request.getRequestDispatcher(
                    "restablecerPassword.jsp"
            ).forward(request, response);

            return;
        }

        try (Connection connection =
                     BDConnection.getConnection()) {

            TokenRecuperacionDAO tokenDAO =
                    new TokenRecuperacionDAO(connection);

            UsuarioDAO usuarioDAO =
                    new UsuarioDAO(connection);

            TokenRecuperacion recuperacion =
                    tokenDAO.buscarPorToken(token);

            if (recuperacion == null) {

                request.setAttribute(
                        "error",
                        "El enlace no es válido."
                );

            } else if (recuperacion.isUsado()) {

                request.setAttribute(
                        "error",
                        "Este enlace ya fue utilizado."
                );

            } else if (LocalDateTime.now().isAfter(
                    recuperacion.getFechaExpiracion())) {

                request.setAttribute(
                        "error",
                        "El enlace ha expirado."
                );

            } else {

                String hash =
                        PasswordUtil.encriptar(password);

                usuarioDAO.actualizarContrasena(
                        recuperacion.getIdUsuario(),
                        hash
                );

                tokenDAO.marcarComoUsado(
                        recuperacion.getIdToken()
                );

                request.setAttribute(
                        "exito",
                        "Contraseña actualizada correctamente."
                );
            }

            request.getRequestDispatcher(
                    "restablecerPassword.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            throw new ServletException(
                    "Error al restablecer la contraseña.",
                    e
            );
        }
    }
}