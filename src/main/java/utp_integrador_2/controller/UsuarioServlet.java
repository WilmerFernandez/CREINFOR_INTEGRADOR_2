package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DTO.UsuarioDTO;
import utp_integrador_2.facade.UsuarioFacade;
import utp_integrador_2.util.EmailUtil;
import utp_integrador_2_model.Usuario;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

@WebServlet("/UsuarioServlet")
public class UsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("usuario") instanceof Usuario)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Usuario actual = (Usuario) session.getAttribute("usuario");
        if (!"admin".equalsIgnoreCase(actual.getTipoUsuario())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String modulo = request.getParameter("modulo");
        if (!"clientes".equals(modulo) && !"personal".equals(modulo)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Módulo de registro inválido");
            return;
        }
        request.setAttribute("modulo", modulo);

        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String email = request.getParameter("email");
        String tipoUsuario;

        if ("clientes".equals(modulo)) {
            tipoUsuario = "cliente";
        } else {
            tipoUsuario = request.getParameter("tipoUsuario");
            if (!"colaborador".equals(tipoUsuario) && !"admin".equals(tipoUsuario)) {
                request.setAttribute("mensaje", "Seleccione un tipo de personal válido.");
                mostrarFormulario(request, response);
                return;
            }
        }
        if (nombres == null || nombres.isBlank()
                || apellidos == null || apellidos.isBlank()
                || email == null || email.isBlank()) {
            request.setAttribute("mensaje", "No se pudo registrar el usuario. Complete todos los campos.");
            mostrarFormulario(request, response);
            return;
        }
        nombres = nombres.trim();
        apellidos = apellidos.trim();
        email = email.trim();

        UsuarioDTO dto = UsuarioDTO.builder()
                .nombres(nombres)
                .apellidos(apellidos)
                .email(email)
                .tipoUsuario(tipoUsuario)
                .build();

        String token;
        try (Connection connection = BDConnection.getConnection()) {
            UsuarioFacade facade = new UsuarioFacade(connection);
            token = facade.registrarUsuario(dto);
        } catch (Exception e) {
            log("Error al registrar usuario", e);
            String detalle = e.getMessage();
            if (detalle != null && detalle.contains("Ya existe un usuario registrado con ese correo")) {
                request.setAttribute("mensaje", "Ya existe una cuenta asociada a ese correo electrónico.");
            } else {
                request.setAttribute("mensaje", "No se pudo registrar el usuario. Revise los registros del servidor.");
            }
            mostrarFormulario(request, response);
            return;
        }

        String baseUrl = System.getenv("APP_BASE_URL");
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = request.getScheme() + "://" + request.getServerName();
            int puerto = request.getServerPort();
            if (puerto != 80 && puerto != 443) baseUrl += ":" + puerto;
            baseUrl += request.getContextPath();
        }
        baseUrl = baseUrl.replaceAll("/+$", "");
        String enlaceActivacion = baseUrl + "/ActivarCuentaServlet?token="
                + URLEncoder.encode(token, StandardCharsets.UTF_8);

        try {
            EmailUtil.enviarActivacionCuenta(email, nombres, enlaceActivacion);
            request.setAttribute("mensaje", "Usuario registrado correctamente. Se solicitó el envío del enlace de activación a su correo.");
        } catch (MessagingException e) {
            log("Usuario registrado, pero falló el envío del correo de activación", e);
            request.setAttribute("mensaje", "El usuario fue registrado, pero no se pudo enviar el correo de activación. No vuelva a registrarlo; contacte al administrador.");
        }
        mostrarFormulario(request, response);
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/UsuarioRegistro.jsp").forward(request, response);
    }
}
