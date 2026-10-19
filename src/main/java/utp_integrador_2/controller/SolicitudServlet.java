package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DTO.SolicitudDTO;
import utp_integrador_2.facade.SolicitudFacade;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/solicitud")
public class SolicitudServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
        String tipoSolicitud = request.getParameter("tipoSolicitud");
        String motivo = request.getParameter("motivo");

        SolicitudDTO dto = SolicitudDTO.builder()
                .idUsuario(idUsuario)
                .tipoSolicitud(tipoSolicitud)
                .motivo(motivo)
                .build();

        try (Connection connection = BDConnection.getConnection()) {
            SolicitudFacade facade = new SolicitudFacade(connection);
            facade.registrarSolicitud(dto);
            response.sendRedirect("clienteDashboard.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
