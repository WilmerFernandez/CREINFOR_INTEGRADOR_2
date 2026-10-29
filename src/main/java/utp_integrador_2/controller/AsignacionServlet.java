package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DTO.AsignacionDTO;
import utp_integrador_2.facade.AsignacionFacade;
import utp_integrador_2_model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/asignacion")
public class AsignacionServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        Usuario usuario = session == null
                ? null
                : (Usuario) session.getAttribute("usuario");

        if (usuario == null
                || !"admin".equalsIgnoreCase(usuario.getTipoUsuario())) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "No tienes permiso para gestionar asignaciones.");
            return;
        }

        try {

            int idSolicitud = Integer.parseInt(
                    request.getParameter("idSolicitud"));

            int idColaborador = Integer.parseInt(
                    request.getParameter("idColaborador"));

            boolean esCoordinador = Boolean.parseBoolean(
                    request.getParameter("esCoordinador"));

            AsignacionDTO dto = AsignacionDTO.builder()
                    .idSolicitud(idSolicitud)
                    .idColaborador(idColaborador)
                    .esCoordinador(esCoordinador)
                    .build();

            try (Connection connection
                    = BDConnection.getConnection()) {

                AsignacionFacade facade
                        = new AsignacionFacade(connection);

                facade.registrarAsignacion(dto);
            }

            session.setAttribute(
                    "mensajeExito",
                    "La asignación se guardó correctamente.");

        } catch (NumberFormatException e) {

            session.setAttribute(
                    "mensajeError",
                    "Selecciona una solicitud y un colaborador válidos.");

        } catch (SQLException e) {

            e.printStackTrace();

            session.setAttribute(
                    "mensajeError",
                    e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            session.setAttribute(
                    "mensajeError",
                    "Ocurrió un error al guardar la asignación.");
        }

        response.sendRedirect(
                request.getContextPath() + "/asignacionForm");
    }
}
