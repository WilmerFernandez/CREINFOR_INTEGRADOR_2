package utp_integrador_2.controller;


import utp_integrador_2_model.Usuario;
import utp_integrador_2_model.Solicitud;
import utp_integrador_2.DAO.SolicitudDAO;
import utp_integrador_2.DAO.ColaboradorDAO;
import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.util.EmailUtil;
import utp_integrador_2.util.PDFUtil;



import java.io.File;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.mail.MessagingException;


@WebServlet("/culminarSolicitud")
public class CulminarSolicitudServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuarioLogeado = (Usuario) session.getAttribute("usuario");
        if (usuarioLogeado == null || !"colaborador".equalsIgnoreCase(usuarioLogeado.getTipoUsuario())) { // Asegurar que sea al menos un colaborador
            response.sendRedirect("login.jsp");
            return;
        }

        int idSolicitud = -1;
        try {
            idSolicitud = Integer.parseInt(request.getParameter("idSolicitudCulminar"));
        } catch (NumberFormatException e) {
            request.setAttribute("mensajeError", "ID de solicitud inválido.");
            request.getRequestDispatcher("/colaboradorDashboard").forward(request, response); // Redirige al dashboard
            return;
        }

        String mensajeError = null;

        try (Connection connection = BDConnection.getConnection()) {
            SolicitudDAO solicitudDAO = new SolicitudDAO(connection);
            ColaboradorDAO colaboradorDAO = new ColaboradorDAO(connection); // Para obtener idColaborador

            int idColaboradorLogeado = colaboradorDAO.obtenerIdColaboradorPorEmail(usuarioLogeado.getEmail());

            // *** VALIDACIÓN CLAVE: Es el colaborador logeado el coordinador de ESTA solicitud? ***
            if (!solicitudDAO.esColaboradorCoordinadorDeSolicitud(idSolicitud, idColaboradorLogeado)) {
                mensajeError = "No tienes permiso para culminar esta solicitud. Solo el coordinador asignado puede hacerlo.";
                request.setAttribute("mensajeError", mensajeError);
                request.getRequestDispatcher("/colaboradorDashboard").forward(request, response);
                return;
            }

            // Obtener el estado actual de la solicitud antes de intentar culminar
            Solicitud solicitudActual = solicitudDAO.obtenerSolicitudPorId(idSolicitud);
            if (solicitudActual == null) {
                mensajeError = "La solicitud con ID " + idSolicitud + " no fue encontrada.";
                request.setAttribute("mensajeError", mensajeError);
                request.getRequestDispatcher("/colaboradorDashboard").forward(request, response);
                return;
            }

            if ("finalizada".equalsIgnoreCase(solicitudActual.getEstado())) {
                mensajeError = "La solicitud ID " + idSolicitud + " ya se encuentra finalizada.";
                request.setAttribute("mensajeError", mensajeError);
                request.getRequestDispatcher("/colaboradorDashboard").forward(request, response);
                return;
            }

            // *** 1. Culminar la solicitud en la DB (cambiar estado a 'finalizada') ***
            solicitudDAO.culminarSolicitud(idSolicitud);

            // *** 2. Obtener detalles para el email (usuario solicitante) ***
            String emailSolicitante = solicitudDAO.obtenerEmailUsuarioSolicitante(idSolicitud);
            // Volver a obtener la solicitud para asegurar que el estado está actualizado
            Solicitud solicitudFinalizada = solicitudDAO.obtenerSolicitudPorId(idSolicitud); 

            // *** 3. Generar PDF y enviar correo de notificación ***

                if (emailSolicitante != null && solicitudFinalizada != null) {

                    String asunto =
                            "Solicitud #"
                            + idSolicitud
                            + " Culminada - "
                            + solicitudFinalizada.getTipoSolicitud();

                    String mensaje =
                            "Estimado usuario,\n\n"
                            + "Le informamos que su solicitud con ID "
                            + idSolicitud
                            + " ha sido exitosamente culminada.\n\n"
                            + "Adjuntamos a este correo la constancia "
                            + "de atención de su solicitud en formato PDF.\n\n"
                            + "Gracias por su paciencia.\n\n"
                            + "Atentamente,\n"
                            + "El equipo de soporte Creinfor";

                    // Generar PDF
                    File pdf = PDFUtil.generarConstanciaSolicitud(
                            solicitudFinalizada,
                            emailSolicitante
                    );

                    try {

                        // Enviar correo con PDF
                        EmailUtil.sendEmailConAdjunto(
                                emailSolicitante,
                                asunto,
                                mensaje,
                                pdf
                        );

                    } finally {

                        // Eliminar PDF temporal después de enviarlo
                        if (pdf.exists()) {
                            boolean eliminado = pdf.delete();

                            if (!eliminado) {
                                System.err.println(
                                        "No se pudo eliminar el archivo temporal: "
                                        + pdf.getAbsolutePath()
                                );
                            }
                        }
                    }

                } else {

                    System.err.println(
                            "No se pudo generar el PDF o enviar el correo: "
                            + "email solicitante nulo o solicitud finalizada nula."
                    );
                }

          
            request.setAttribute("mensajeExito", "Solicitud culminada y notificación enviada exitosamente.");
            request.getRequestDispatcher("/colaboradorDashboard").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            mensajeError = "Error de base de datos al culminar la solicitud: " + e.getMessage();
            request.setAttribute("mensajeError", mensajeError);
            request.getRequestDispatcher("/colaboradorDashboard").forward(request, response);
        } catch (MessagingException e) {
            e.printStackTrace();
            mensajeError = "Error al enviar el correo de notificación: " + e.getMessage();
            request.setAttribute("mensajeError", mensajeError);
            request.getRequestDispatcher("/colaboradorDashboard").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            mensajeError = "Error inesperado al culminar la solicitud: " + e.getMessage();
            request.setAttribute("mensajeError", mensajeError);
            request.getRequestDispatcher("/colaboradorDashboard").forward(request, response);
        }
    }
}