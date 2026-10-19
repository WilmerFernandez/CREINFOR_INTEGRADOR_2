package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DAO.TokenActivacionDAO;
import utp_integrador_2.DAO.UsuarioDAO;
import utp_integrador_2_model.TokenActivacion;
import utp_integrador_2.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;

@WebServlet("/ActivarCuentaServlet")
public class ActivarCuentaServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");

        if (token == null || token.trim().isEmpty()) {
            request.setAttribute(
                    "error",
                    "El enlace de activación no es válido."
            );

            request.getRequestDispatcher(
                    "activarCuenta.jsp"
            ).forward(request, response);

            return;
        }

        try (Connection connection =
                     BDConnection.getConnection()) {

            TokenActivacionDAO tokenDAO =
                    new TokenActivacionDAO(connection);

            TokenActivacion tokenActivacion =
                    tokenDAO.buscarPorToken(token);

            if (tokenActivacion == null) {

                request.setAttribute(
                        "error",
                        "El enlace de activación no existe."
                );

            } else if (tokenActivacion.isUsado()) {

                request.setAttribute(
                        "error",
                        "Este enlace de activación ya fue utilizado."
                );

            } else if (LocalDateTime.now().isAfter(
                    tokenActivacion.getFechaExpiracion())) {

                request.setAttribute(
                        "error",
                        "El enlace de activación ha expirado."
                );

            } else {

                request.setAttribute(
                        "token",
                        token
                );
            }

            request.getRequestDispatcher(
                    "activarCuenta.jsp"
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

            request.setAttribute("token", token);

            request.getRequestDispatcher(
                    "activarCuenta.jsp"
            ).forward(request, response);

            return;
        }

        if (!password.equals(confirmarPassword)) {

            request.setAttribute(
                    "error",
                    "Las contraseñas no coinciden."
            );

            request.setAttribute("token", token);

            request.getRequestDispatcher(
                    "activarCuenta.jsp"
            ).forward(request, response);

            return;
        }

        try (Connection connection =
                     BDConnection.getConnection()) {

            TokenActivacionDAO tokenDAO =
                    new TokenActivacionDAO(connection);

            UsuarioDAO usuarioDAO =
                    new UsuarioDAO(connection);

            TokenActivacion tokenActivacion =
                    tokenDAO.buscarPorToken(token);

            if (tokenActivacion == null) {

                request.setAttribute(
                        "error",
                        "El enlace no es válido."
                );

            } else if (tokenActivacion.isUsado()) {

                request.setAttribute(
                        "error",
                        "Este enlace ya fue utilizado."
                );

            } else if (LocalDateTime.now().isAfter(
                    tokenActivacion.getFechaExpiracion())) {

                request.setAttribute(
                        "error",
                        "El enlace de activación ha expirado."
                );

            } else {

                String contrasenaHash =
                        PasswordUtil.encriptar(password);

                usuarioDAO.activarCuenta(
                        tokenActivacion.getIdUsuario(),
                        contrasenaHash
                );

                tokenDAO.marcarComoUsado(
                        tokenActivacion.getIdToken()
                );

                request.setAttribute(
                        "exito",
                        "Cuenta activada correctamente. Ya puede iniciar sesión."
                );
            }

            request.getRequestDispatcher(
                    "activarCuenta.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            throw new ServletException(
                    "Error al activar la cuenta.",
                    e
            );
        }
    }
}