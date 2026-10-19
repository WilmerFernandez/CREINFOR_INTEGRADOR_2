package utp_integrador_2.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DTO.AsignacionDTO;
import utp_integrador_2.facade.AsignacionFacade;

@WebServlet("/asignacion")
public class AsignacionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));
            int idColaborador = Integer.parseInt(request.getParameter("idColaborador"));
            boolean esCoordinador = Boolean.parseBoolean(request.getParameter("esCoordinador"));

            AsignacionDTO dto = AsignacionDTO.builder()
                    .idSolicitud(idSolicitud)
                    .idColaborador(idColaborador)
                    .esCoordinador(esCoordinador)
                    .build();

            try (Connection connection = BDConnection.getConnection()) {
                AsignacionFacade facade = new AsignacionFacade(connection);
                facade.registrarAsignacion(dto);
            }

            response.sendRedirect("asignacionForm");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
