package utp_integrador_2.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.facade.ColaboradorFacade;
import utp_integrador_2.facade.SolicitudFacade;
import utp_integrador_2_model.Colaborador;
import utp_integrador_2_model.Solicitud;

@WebServlet("/asignacionForm")
public class AsignacionFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection connection = BDConnection.getConnection()) {
            SolicitudFacade solicitudFacade = new SolicitudFacade(connection);
            ColaboradorFacade colaboradorFacade = new ColaboradorFacade(connection);

            List<Solicitud> solicitudesPendientes = solicitudFacade.listarSolicitudesPendientes();
            List<Colaborador> colaboradores = colaboradorFacade.listarTodos();

            System.out.println("Solicitudes Pendientes: " + solicitudesPendientes.size());
            System.out.println("Colaboradores: " + colaboradores.size());

            request.setAttribute("solicitudesPendientes", solicitudesPendientes);
            request.setAttribute("colaboradores", colaboradores);

            request.getRequestDispatcher("adminAsignacion.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}

