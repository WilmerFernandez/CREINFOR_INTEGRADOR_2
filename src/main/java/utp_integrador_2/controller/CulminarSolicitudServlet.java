
package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DAO.ColaboradorDAO;
import utp_integrador_2.DAO.InformeTecnicoDAO;
import utp_integrador_2.DAO.SolicitudDAO;
import utp_integrador_2_model.Solicitud;
import utp_integrador_2_model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/culminarSolicitud")
public class CulminarSolicitudServlet extends HttpServlet {

    private static final String VISTA =
            "/WEB-INF/views/colaborador/informeTecnicoForm.jsp";

    private Usuario obtenerUsuario(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        Usuario usuario =
                (Usuario) session.getAttribute("usuario");

        if (usuario == null ||
                !"colaborador".equalsIgnoreCase(
                        usuario.getTipoUsuario())) {
            return null;
        }

        return usuario;
    }

    private int obtenerId(HttpServletRequest request) {

        String valor = request.getParameter("idSolicitudCulminar");

        if (valor == null) {
            throw new IllegalArgumentException(
                    "Falta el ID de solicitud.");
        }

        int id = Integer.parseInt(valor);

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "ID de solicitud inválido.");
        }

        return id;
    }

    private Solicitud validarSolicitud(
            Connection connection,
            Usuario usuario,
            int idSolicitud) throws SQLException {

        SolicitudDAO solicitudDAO =
                new SolicitudDAO(connection);

        ColaboradorDAO colaboradorDAO =
                new ColaboradorDAO(connection);

        int idColaborador =
                colaboradorDAO.obtenerIdColaboradorPorEmail(
                        usuario.getEmail());

        if (idColaborador <= 0 ||
                !solicitudDAO.esColaboradorCoordinadorDeSolicitud(
                        idSolicitud, idColaborador)) {

            throw new SecurityException(
                    "Solo el coordinador asignado puede "
                    + "culminar esta solicitud.");
        }

        Solicitud solicitud =
                solicitudDAO.obtenerSolicitudPorId(idSolicitud);

        if (solicitud == null) {
            throw new IllegalArgumentException(
                    "La solicitud no existe.");
        }

        if (!"en proceso".equalsIgnoreCase(
                solicitud.getEstado())) {

            throw new IllegalStateException(
                    "Solo se pueden culminar solicitudes en proceso.");
        }

        return solicitud;
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = obtenerUsuario(request);

        if (usuario == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        response.setHeader("Cache-Control", "no-store");

        try {
            int idSolicitud = obtenerId(request);

            try (Connection connection =
                    BDConnection.getConnection()) {

                Solicitud solicitud = validarSolicitud(
                        connection, usuario, idSolicitud);

                request.setAttribute("solicitud", solicitud);

                request.getRequestDispatcher(VISTA)
                        .forward(request, response);
            }

        } catch (SecurityException e) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage());

        } catch (IllegalArgumentException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage());

        } catch (IllegalStateException e) {
            response.sendError(
                    HttpServletResponse.SC_CONFLICT,
                    e.getMessage());

        } catch (SQLException e) {
            throw new ServletException(
                    "Error al abrir el informe técnico.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Usuario usuario = obtenerUsuario(request);

        if (usuario == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        int idSolicitud;

        try {
            idSolicitud = obtenerId(request);
        } catch (IllegalArgumentException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage());
            return;
        }

        String diagnostico = request.getParameter("diagnostico");
        String solucion = request.getParameter("solucion");
        String resultados = request.getParameter("resultados");
        String recomendaciones =
                request.getParameter("recomendaciones");

        try (Connection connection =
                BDConnection.getConnection()) {

            Solicitud solicitud = validarSolicitud(
                    connection, usuario, idSolicitud);

            if (!campoValido(diagnostico)
                    || !campoValido(solucion)
                    || !campoValido(resultados)
                    || !campoValido(recomendaciones)) {

                request.setAttribute("solicitud", solicitud);
                request.setAttribute("mensajeError",
                        "Completa correctamente los cuatro "
                        + "campos del informe.");

                request.getRequestDispatcher(VISTA)
                        .forward(request, response);
                return;
            }

            ColaboradorDAO colaboradorDAO =
                    new ColaboradorDAO(connection);

            int idColaborador =
                    colaboradorDAO.obtenerIdColaboradorPorEmail(
                            usuario.getEmail());

            InformeTecnicoDAO informeDAO =
                    new InformeTecnicoDAO(connection);

            // Guarda el informe y finaliza en una sola transacción
            informeDAO.guardarInformeYCulminar(
                    idSolicitud,
                    idColaborador,
                    diagnostico,
                    solucion,
                    resultados,
                    recomendaciones);

            request.getSession().setAttribute(
                    "mensajeExito",
                    "Solicitud #" + idSolicitud
                    + " finalizada. Informe técnico guardado "
                    + "correctamente. El envío por correo "
                    + "está pendiente.");

            response.sendRedirect(
                    request.getContextPath()
                    + "/colaboradorDashboard");

        } catch (SecurityException e) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage());

        } catch (IllegalStateException e) {
            response.sendError(
                    HttpServletResponse.SC_CONFLICT,
                    e.getMessage());

        } catch (IllegalArgumentException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage());

        } catch (SQLException e) {
            getServletContext().log(
                    "Error guardando informe de solicitud #"
                    + idSolicitud, e);

            response.sendError(
                    HttpServletResponse.SC_CONFLICT,
                    "No se pudo culminar la solicitud. "
                    + "Revisa que siga en proceso "
                    + "y que no tenga un informe final.");
        }
    }

    private boolean campoValido(String valor) {
        return valor != null
                && !valor.trim().isEmpty()
                && valor.length() <= 5000;
    }
}
