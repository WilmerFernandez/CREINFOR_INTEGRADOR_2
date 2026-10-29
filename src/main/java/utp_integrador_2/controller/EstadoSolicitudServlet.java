package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.facade.SolicitudFacade;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/cambiarEstado")
public class EstadoSolicitudServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));
            String nuevoEstado = request.getParameter("estado"); // ej. "finalizada"

            try (Connection connection = BDConnection.getConnection()) {
                SolicitudFacade facade = new SolicitudFacade(connection);
                facade.cambiarEstado(idSolicitud, nuevoEstado);
            }

            response.sendRedirect("colaboradorSolicitudes.jsp"); // o donde corresponda
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}