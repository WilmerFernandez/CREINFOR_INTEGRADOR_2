package utp_integrador_2.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.facade.ColaboradorFacade;
import utp_integrador_2_model.Colaborador;
import utp_integrador_2_model.Solicitud;
import utp_integrador_2.facade.AsignacionFacade;
import utp_integrador_2.DTO.AsignacionDetalleDTO;

@WebServlet("/asignacionForm")
public class AsignacionFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection connection = BDConnection.getConnection()) {
            AsignacionFacade asignacionFacade = new AsignacionFacade(connection);
            ColaboradorFacade colaboradorFacade = new ColaboradorFacade(connection);

            List<Solicitud> solicitudesPendientes =asignacionFacade.listarSolicitudesDisponibles();
            List<Colaborador> colaboradores = colaboradorFacade.listarTodos();
            
            List<AsignacionDetalleDTO> asignaciones = asignacionFacade.listarAsignaciones();
            

            System.out.println("Solicitudes disponibles: " + solicitudesPendientes.size());
            System.out.println("Colaboradores: " + colaboradores.size());

            request.setAttribute("solicitudesPendientes", solicitudesPendientes);
            request.setAttribute("colaboradores", colaboradores);
            request.setAttribute("asignaciones", asignaciones);

            request.getRequestDispatcher("adminAsignacion.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}

