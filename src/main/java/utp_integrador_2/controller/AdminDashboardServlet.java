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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (!"admin".equalsIgnoreCase(usuario.getTipoUsuario())) {
            response.sendRedirect("login.jsp");
            return;
        }

        try (Connection connection = BDConnection.getConnection()) {
            EstadisticaFacade estadisticaFacade = new EstadisticaFacade(connection);

            // Obtener conteo de solicitudes por estado
            Map<String, Integer> solicitudesPorEstado = estadisticaFacade.obtenerConteoSolicitudesPorEstado();

            request.setAttribute("solicitudesPorEstado", solicitudesPorEstado);

           

            request.getRequestDispatcher("adminDashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
