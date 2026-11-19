
package utp_integrador_2.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.facade.EstadisticaFacade;
import utp_integrador_2_model.Usuario;

@WebServlet("/adminDashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);

        if (session == null || !(session.getAttribute("usuario") instanceof Usuario)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!"admin".equalsIgnoreCase(usuario.getTipoUsuario())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try (Connection connection = BDConnection.getConnection()) {

            EstadisticaFacade estadisticaFacade = new EstadisticaFacade(connection);

            Map<String, Integer> solicitudesPorEstado =
                    estadisticaFacade.obtenerConteoSolicitudesPorEstado();

            request.setAttribute("solicitudesPorEstado", solicitudesPorEstado);

            request.getRequestDispatcher("/adminDashboard.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            getServletContext().log("Error en dashboard administrativo", e);
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }
}
