package utp_integrador_2.controller;


import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.facade.SolicitudFacade;
import utp_integrador_2_model.Solicitud;
import utp_integrador_2_model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/listaSolicitudes")
public class ListaSolicitudesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String action = request.getParameter("action"); 

        try (Connection connection = BDConnection.getConnection()) {
            SolicitudFacade facade = new SolicitudFacade(connection);

            if ("edit".equals(action)) {
                int idSolicitud = Integer.parseInt(request.getParameter("id"));
                Solicitud solicitudAEditar = facade.obtenerSolicitudPorId(idSolicitud);

                if (solicitudAEditar != null && "pendiente".equalsIgnoreCase(solicitudAEditar.getEstado()) && solicitudAEditar.getIdUsuario() == usuario.getIdUsuario()) {
                    request.setAttribute("solicitudAEditar", solicitudAEditar);
                    request.getRequestDispatcher("editarSolicitud.jsp").forward(request, response);
                    return; 
                } else {
                    request.setAttribute("errorMessage", "Solicitud no encontrada, no puede ser editada o no es de su propiedad.");
                }
            }
            
            List<Solicitud> lista;
            switch (usuario.getTipoUsuario()) {
                case "cliente":
                    lista = facade.obtenerSolicitudesPorUsuario(usuario.getIdUsuario());
                    break;
                case "colaborador": 
                    lista = facade.obtenerSolicitudesPorColaborador(usuario.getIdUsuario());
                    break;
                default:
                    lista = null;
                    break;
            }

            request.setAttribute("listaSolicitudes", lista);
            String jspDestino = usuario.getTipoUsuario().equals("cliente") ? "clienteSolicitudes.jsp" : "colaboradorSolicitudes.jsp";
            request.getRequestDispatcher(jspDestino).forward(request, response);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "ID de solicitud inválido.");
            request.getRequestDispatcher("/WEB-INF/views/errorPage.jsp").forward(request, response);
        } catch (SQLException e) { 
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error de base de datos: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/errorPage.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error inesperado: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/errorPage.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String action = request.getParameter("action");

        try (Connection connection = BDConnection.getConnection()) {
            SolicitudFacade facade = new SolicitudFacade(connection);

            if ("eliminar".equals(action)) {
                try {
                    int idSolicitud = Integer.parseInt(request.getParameter("id"));
                    Solicitud solicitud = facade.obtenerSolicitudPorId(idSolicitud);

                    if (solicitud != null && solicitud.getIdUsuario() == usuario.getIdUsuario()) {
                        // Primero, verificar si la solicitud ya está asignada
                        if (facade.verificarSiTieneAsignaciones(idSolicitud)) {
                            request.setAttribute("errorMessage", "La solicitud ya ha sido asignada y no puede ser eliminada.");
                        } else if ("pendiente".equalsIgnoreCase(solicitud.getEstado())) { // Solo si está pendiente Y no asignada
                            boolean eliminado = facade.eliminarSolicitud(idSolicitud);
                            if (eliminado) {
                                request.setAttribute("successMessage", "Solicitud eliminada con éxito.");
                            } else {
                                request.setAttribute("errorMessage", "No se pudo eliminar la solicitud. Asegúrese de que esté en estado 'pendiente' y que sea su solicitud.");
                            }
                        } else { // Si no está pendiente pero tampoco asignada (ej. en proceso, finalizada)
                            request.setAttribute("errorMessage", "La solicitud no puede ser eliminada porque su estado no es 'pendiente'.");
                        }
                    } else {
                        request.setAttribute("errorMessage", "No tiene permisos para eliminar esta solicitud o no se encontró.");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "ID de solicitud inválido para eliminar.");
                } catch (RuntimeException e) { 
                    request.setAttribute("errorMessage", "Error al eliminar la solicitud: " + e.getMessage());
                    e.printStackTrace();
                }
            } else if ("actualizar".equals(action)) {
                // ... (tu lógica existente para actualizar, no necesita cambios para este requisito)
                try { 
                    int idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));
                    String tipoSolicitud = request.getParameter("tipoSolicitud");
                    String motivo = request.getParameter("motivo");

                    Solicitud solicitud = facade.obtenerSolicitudPorId(idSolicitud);
                    if (solicitud != null && "pendiente".equalsIgnoreCase(solicitud.getEstado()) && solicitud.getIdUsuario() == usuario.getIdUsuario()) {
                        // Antes de actualizar, podrías considerar si la solicitud fue asignada,
                        // pero el requerimiento es solo para la eliminación.
                        solicitud.setTipoSolicitud(tipoSolicitud);
                        solicitud.setMotivo(motivo);

                        boolean actualizado = facade.actualizarSolicitud(solicitud);
                        if (actualizado) {
                            request.setAttribute("successMessage", "Solicitud actualizada con éxito.");
                        } else {
                            request.setAttribute("errorMessage", "No se pudo actualizar la solicitud. Asegúrese de que esté en estado 'pendiente' y que sea su solicitud.");
                        }
                    } else {
                        request.setAttribute("errorMessage", "No tiene permisos para actualizar esta solicitud o no está en estado 'pendiente'.");
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "ID de solicitud inválido para actualizar.");
                } catch (RuntimeException e) { 
                    request.setAttribute("errorMessage", "Error al actualizar la solicitud: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                request.setAttribute("errorMessage", "Acción no reconocida.");
            }
            
            doGet(request, response);
            
        } catch (SQLException e) { 
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error de conexión a la base de datos: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/errorPage.jsp").forward(request, response);
        } catch (Exception e) { 
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error general al procesar la solicitud POST: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/errorPage.jsp").forward(request, response);
        }
    }
}