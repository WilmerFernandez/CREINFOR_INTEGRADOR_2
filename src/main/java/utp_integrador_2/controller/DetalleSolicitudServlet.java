package utp_integrador_2.controller;


import utp_integrador_2.facade.ActividadFacade;
import utp_integrador_2_model.Actividad;
import utp_integrador_2.DAO.BDConnection;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;


@WebServlet("/detalleSolicitud")
public class DetalleSolicitudServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idSolicitudStr = request.getParameter("idSolicitud");
        if (idSolicitudStr == null) {
            response.sendRedirect("colaboradorDashboard");
            return;
        }

        int idSolicitud = Integer.parseInt(idSolicitudStr);

        try (Connection connection = BDConnection.getConnection()) {
            ActividadFacade actividadFacade = new ActividadFacade(connection);
            List<Actividad> actividades = actividadFacade.listarActividadesPorSolicitud(idSolicitud);

            request.setAttribute("actividades", actividades);
            request.setAttribute("idSolicitud", idSolicitud);
            request.getRequestDispatcher("detalleSolicitud.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
