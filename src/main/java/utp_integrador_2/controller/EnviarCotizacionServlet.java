
package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.facade.CotizacionFacade;
import utp_integrador_2_model.Cotizacion;
import utp_integrador_2_model.DetalleCotizacion;
import utp_integrador_2_model.Usuario;
import utp_integrador_2.util.EmailUtil;
import utp_integrador_2.util.PDFUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/cotizaciones/enviar")
public class EnviarCotizacionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null
                : (Usuario) session.getAttribute("usuario");

        if (usuario == null ||
                !"admin".equalsIgnoreCase(usuario.getTipoUsuario())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        File pdf = null;
        int idCotizacion;

        try {
            idCotizacion = Integer.parseInt(
                    request.getParameter("idCotizacion"));

            if (idCotizacion <= 0) {
                throw new IllegalArgumentException(
                        "ID de cotización inválido.");
            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try (Connection connection = BDConnection.getConnection()) {

            CotizacionFacade facade = new CotizacionFacade(connection);

            Cotizacion cotizacion = facade.buscarPorId(idCotizacion);

            if (cotizacion == null) {
                session.setAttribute("mensajeError",
                        "La cotización no existe.");
                response.sendRedirect(request.getContextPath()
                        + "/cotizaciones/lista");
                return;
            }

            if (!"borrador".equalsIgnoreCase(cotizacion.getEstado())
                    || cotizacion.getFechaVencimiento() == null
                    || cotizacion.getFechaVencimiento()
                            .isBefore(LocalDate.now())) {

                session.setAttribute("mensajeError",
                        "La cotización no está disponible para envío.");
                response.sendRedirect(request.getContextPath()
                        + "/cotizaciones/lista");
                return;
            }

            String emailCliente =
                    facade.obtenerEmailCliente(idCotizacion);

            if (emailCliente == null || emailCliente.isBlank()) {
                throw new IllegalStateException(
                        "El cliente no tiene correo registrado.");
            }

            List<DetalleCotizacion> detalles =
                    facade.listarDetalles(idCotizacion);

            if (detalles.isEmpty()) {
                throw new IllegalStateException(
                        "La cotización no tiene conceptos.");
            }

            // Preparar PDF antes de reservar el envío
            pdf = PDFUtil.generarPDFCotizacion(
                    cotizacion, detalles, emailCliente);

            // Evitar que otra solicitud envíe la misma cotización
            if (!facade.iniciarEnvio(idCotizacion)) {
                session.setAttribute("mensajeError",
                        "La cotización ya está siendo procesada "
                        + "o no puede enviarse.");
                response.sendRedirect(request.getContextPath()
                        + "/cotizaciones/lista");
                return;
            }

            String asunto = "CREINFOR - Cotización #"
                    + idCotizacion;

            String mensaje =
                    "Estimado cliente:\n\n"
                    + "Hemos preparado una propuesta económica "
                    + "para su solicitud #"
                    + cotizacion.getIdSolicitud() + ".\n\n"
                    + "Importe total: S/ "
                    + cotizacion.getTotal() + "\n"
                    + "Válida hasta: "
                    + cotizacion.getFechaVencimiento() + "\n\n"
                    + "Adjuntamos el PDF con los conceptos "
                    + "y condiciones de la cotización.\n\n"
                    + "Esta propuesta está pendiente de su aprobación.\n\n"
                    + "Atentamente,\n"
                    + "Equipo CREINFOR S.A.C.";

            boolean enviado = false;

            try {
                EmailUtil.sendEmailConAdjunto(
                        emailCliente, asunto, mensaje, pdf);
                enviado = true;

            } finally {
                // Si falló o no se confirmó, no reintentar automáticamente
                facade.finalizarEnvio(idCotizacion, enviado);
            }

            session.setAttribute("mensajeExito",
                    "Cotización #" + idCotizacion
                    + " enviada al correo del cliente.");

        } catch (Exception e) {

            getServletContext().log(
                    "Error enviando cotización #" + idCotizacion, e);

            session.setAttribute("mensajeError",
                    "No se pudo confirmar el envío de la cotización. "
                    + "Revisa su estado antes de intentar nuevamente.");

        } finally {

            if (pdf != null && pdf.exists() && !pdf.delete()) {
                getServletContext().log(
                        "No se pudo eliminar el PDF temporal.");
            }
        }

        response.sendRedirect(request.getContextPath()
                + "/cotizaciones/lista");
    }
}
