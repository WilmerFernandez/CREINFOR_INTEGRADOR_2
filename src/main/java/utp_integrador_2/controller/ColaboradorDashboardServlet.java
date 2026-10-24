package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.facade.SolicitudFacade;
import utp_integrador_2_model.Solicitud;
import utp_integrador_2_model.Usuario;
import utp_integrador_2.facade.ActividadFacade;
import utp_integrador_2.DTO.ActividadDTO;
import utp_integrador_2.DAO.ColaboradorDAO;
import utp_integrador_2.DAO.SolicitudDAO;
import utp_integrador_2.DAO.InformeTecnicoDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ArrayList;

@WebServlet("/colaboradorDashboard")
public class ColaboradorDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
// Interpretar correctamente las tildes y la ñ
        request.setCharacterEncoding("UTF-8");

        String idSolicitudParam = request.getParameter("idSolicitud");
        if (idSolicitudParam != null && !idSolicitudParam.isEmpty()) {

            registrarActividad(request, response);
        } else {

            processRequest(request, response);
        }
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"colaborador".equalsIgnoreCase(usuario.getTipoUsuario())) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<Solicitud> solicitudes = new ArrayList<>();
        String rolColaborador = "Colaborador"; // Valor por defecto
        int idColaboradorLogeado = -1;

        try (Connection connection = BDConnection.getConnection()) {
            ColaboradorDAO colaboradorDAO = new ColaboradorDAO(connection);
            SolicitudDAO solicitudDAO = new SolicitudDAO(connection);
            InformeTecnicoDAO informeDAO
                    = new InformeTecnicoDAO(connection);

            idColaboradorLogeado = colaboradorDAO.obtenerIdColaboradorPorEmail(usuario.getEmail());
            if (idColaboradorLogeado == -1) {

                request.setAttribute("mensajeError", "Error: Colaborador no encontrado para el usuario actual.");

            } else {
                request.setAttribute("idColaboradorLogeado", idColaboradorLogeado);

                // Obtener y pasar el rol específico del colaborador
                rolColaborador = colaboradorDAO.obtenerRolColaborador(idColaboradorLogeado);
                request.setAttribute("rolColaborador", rolColaborador);

                SolicitudFacade solicitudFacade = new SolicitudFacade(connection);
                solicitudes = solicitudFacade.obtenerSolicitudesPorColaborador(idColaboradorLogeado);

                // Para cada solicitud, verificar si el colaborador logeado es coordinador de esa solicitud
                for (Solicitud s : solicitudes) {
                    int idSolicitud = s.getIdSolicitud();

                    boolean esCoordinador = solicitudDAO.esColaboradorCoordinadorDeSolicitud(idSolicitud, idColaboradorLogeado);
                    request.setAttribute("esCoordinador_" + idSolicitud, esCoordinador);

                    // Consultar estado del informe técnico
                    String estadoInforme = informeDAO.obtenerEstadoEnvio(idSolicitud);
                    request.setAttribute("estadoInforme_" + idSolicitud, estadoInforme);
                }
            }

            request.setAttribute("solicitudes", solicitudes);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error al cargar el dashboard: " + e.getMessage());

        } finally {

            request.getRequestDispatcher("colaboradorDashboard.jsp").forward(request, response);
        }
    }

    private void registrarActividad(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        try {
            int idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));
            String fechaStr = request.getParameter("fecha");
            String descripcion = request.getParameter("descripcion");
            double tiempoHoras = Double.parseDouble(request.getParameter("tiempoHoras"));

            LocalDate localDate = LocalDate.parse(fechaStr);
            Date fecha = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            try (Connection connection = BDConnection.getConnection()) {
                ColaboradorDAO colaboradorDAO = new ColaboradorDAO(connection);
                int idColaborador = colaboradorDAO.obtenerIdColaboradorPorEmail(usuario.getEmail());
                if (idColaborador == -1) {
                    throw new RuntimeException("Colaborador no encontrado para usuario con email: " + usuario.getEmail());
                }

                ActividadFacade actividadFacade = new ActividadFacade(connection);
                int idAsignacion = actividadFacade.obtenerIdAsignacion(idSolicitud, idColaborador);
                if (idAsignacion == -1) {
                    throw new RuntimeException("Asignación no encontrada para solicitud y colaborador. Puede que no estés asignado a esta solicitud.");
                }

                ActividadDTO dto = ActividadDTO.builder()
                        .idAsignacion(idAsignacion)
                        .fecha(fecha)
                        .descripcion(descripcion)
                        .tiempoHoras(tiempoHoras)
                        .build();

                actividadFacade.registrarActividad(dto);

                request.setAttribute(
                        "mensajeExito",
                        "Actividad registrada exitosamente.");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("mensajeError", "Datos de actividad inválidos (ID de solicitud, tiempo en horas, o fecha).");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error al registrar actividad: " + e.getMessage());
        } finally {
            // Después de registrar la actividad (éxito o error), recargamos el dashboard
            // para que se muestren las solicitudes actualizadas y los mensajes.
            // Llama a processRequest para que se encargue de todo el procesamiento y el forward.
            processRequest(request, response);
        }
    }
}
