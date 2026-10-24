
package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DAO.ColaboradorDAO;
import utp_integrador_2.DAO.InformeTecnicoDAO;
import utp_integrador_2.DAO.SolicitudDAO;
import utp_integrador_2_model.Solicitud;
import utp_integrador_2_model.Usuario;
import utp_integrador_2.util.EmailUtil;
import utp_integrador_2.util.PDFUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.UUID;

@WebServlet("/informes/enviar")
public class EnviarInformeTecnicoServlet extends HttpServlet {

    private Usuario colaborador(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        Usuario usuario =
                (Usuario) session.getAttribute("usuario");

        return usuario != null
                && "colaborador".equalsIgnoreCase(
                        usuario.getTipoUsuario())
                ? usuario : null;
    }

    private int obtenerId(HttpServletRequest request) {
        int id = Integer.parseInt(
                request.getParameter("idSolicitud"));

        if (id <= 0) {
            throw new NumberFormatException();
        }

        return id;
    }

    private Solicitud validarAcceso(
            Connection connection,
            Usuario usuario,
            int idSolicitud) throws Exception {

        SolicitudDAO solicitudDAO =
                new SolicitudDAO(connection);

        ColaboradorDAO colaboradorDAO =
                new ColaboradorDAO(connection);

        int idColaborador =
                colaboradorDAO.obtenerIdColaboradorPorEmail(
                        usuario.getEmail());

        if (idColaborador <= 0
                || !solicitudDAO.esColaboradorCoordinadorDeSolicitud(
                        idSolicitud, idColaborador)) {

            throw new SecurityException(
                    "Solo el coordinador puede enviar el informe.");
        }

        Solicitud solicitud =
                solicitudDAO.obtenerSolicitudPorId(idSolicitud);

        if (solicitud == null
                || !"finalizada".equalsIgnoreCase(
                        solicitud.getEstado())) {

            throw new IllegalStateException(
                    "La solicitud todavía no está finalizada.");
        }

        return solicitud;
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = colaborador(request);

        if (usuario == null) {
            response.sendError(403);
            return;
        }

        response.setHeader("Cache-Control", "no-store");

        try {
            int idSolicitud = obtenerId(request);

            try (Connection connection =
                    BDConnection.getConnection()) {

                Solicitud solicitud = validarAcceso(
                        connection, usuario, idSolicitud);

                InformeTecnicoDAO dao =
                        new InformeTecnicoDAO(connection);

                String estado =
                        dao.obtenerEstadoEnvio(idSolicitud);

                if (estado == null) {
                    response.sendError(404,
                            "Esta solicitud no tiene informe técnico.");
                    return;
                }

                if (!"pendiente".equals(estado)) {
                    response.sendError(409,
                            "El informe no está pendiente de envío. "
                            + "Estado actual: " + estado);
                    return;
                }

                String token = UUID.randomUUID().toString();

                request.getSession().setAttribute(
                        "tokenEnvioInforme", token);

                request.setAttribute("tokenEnvioInforme", token);
                request.setAttribute("solicitud", solicitud);

                request.getRequestDispatcher(
                        "/WEB-INF/views/colaborador/"
                        + "confirmarEnvioInforme.jsp")
                        .forward(request, response);
            }

        } catch (NumberFormatException e) {
            response.sendError(400, "ID inválido.");

        } catch (SecurityException e) {
            response.sendError(403, e.getMessage());

        } catch (IllegalStateException e) {
            response.sendError(409, e.getMessage());

        } catch (Exception e) {
            throw new ServletException(
                    "Error al preparar el envío.", e);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = colaborador(request);

        if (usuario == null) {
            response.sendError(403);
            return;
        }

        HttpSession session = request.getSession(false);

        // Consumir el token para que un mismo formulario
        // no pueda enviarse dos veces.
        String tokenRecibido =
                request.getParameter("tokenEnvioInforme");

        boolean tokenValido;

        synchronized (session) {
            String tokenGuardado = (String)
                    session.getAttribute("tokenEnvioInforme");

            tokenValido = tokenGuardado != null
                    && tokenGuardado.equals(tokenRecibido);

            if (tokenValido) {
                session.removeAttribute("tokenEnvioInforme");
            }
        }

        if (!tokenValido) {
            response.sendError(403,
                    "Formulario inválido o ya utilizado.");
            return;
        }

        int idSolicitud;

        try {
            idSolicitud = obtenerId(request);
        } catch (NumberFormatException e) {
            response.sendError(400, "ID inválido.");
            return;
        }

        File pdf = null;
        boolean reservado = false;
        boolean aceptadoPorSMTP = false;

        try (Connection connection =
                BDConnection.getConnection()) {

            Solicitud solicitud = validarAcceso(
                    connection, usuario, idSolicitud);

            InformeTecnicoDAO informeDAO =
                    new InformeTecnicoDAO(connection);

            SolicitudDAO solicitudDAO =
                    new SolicitudDAO(connection);

            String emailCliente =
                    solicitudDAO.obtenerEmailUsuarioSolicitante(
                            idSolicitud);

            if (emailCliente == null
                    || emailCliente.isBlank()) {

                throw new IllegalStateException(
                        "El solicitante no tiene correo registrado.");
            }

            if (!"pendiente".equals(
                    informeDAO.obtenerEstadoEnvio(idSolicitud))) {

                throw new IllegalStateException(
                        "El informe ya fue enviado "
                        + "o requiere revisión.");
            }

            // Crear PDF utilizando el informe real
            pdf = PDFUtil.generarInformeTecnicoPDF(
                    connection, solicitud, emailCliente);

            // Evita dos envíos simultáneos
            reservado = informeDAO.iniciarEnvio(idSolicitud);

            if (!reservado) {
                throw new IllegalStateException(
                        "Otro proceso ya tomó este informe.");
            }

            String asunto =
                    "CREINFOR - Informe técnico final - Solicitud #"
                    + idSolicitud;

            String mensaje =
                    "Estimado cliente:\n\n"
                    + "Le informamos que la solicitud #"
                    + idSolicitud + " ha sido culminada.\n\n"
                    + "Adjuntamos el informe técnico final, "
                    + "que contiene el diagnóstico, solución, "
                    + "resultados, recomendaciones y actividades "
                    + "realizadas.\n\n"
                    + "Gracias por confiar en CREINFOR.\n\n"
                    + "Atentamente,\n"
                    + "Equipo CREINFOR S.A.C.";

            try {
                EmailUtil.sendEmailConAdjunto(
                        emailCliente,
                        asunto,
                        mensaje,
                        pdf);

                aceptadoPorSMTP = true;

            } finally {
                // El estado se conserva incluso cuando
                // el resultado SMTP es incierto.
                boolean actualizado =
                        informeDAO.finalizarEnvio(
                                idSolicitud, aceptadoPorSMTP);

                if (!actualizado) {
                    throw new IllegalStateException(
                            "No se pudo actualizar el estado de envío.");
                }
            }

            session.setAttribute("mensajeExito",
                    "Informe técnico de la solicitud #"
                    + idSolicitud
                    + " aceptado por el servidor de correo.");

        } catch (Exception e) {

            getServletContext().log(
                    "Error enviando informe técnico de solicitud #"
                    + idSolicitud, e);

            session.setAttribute("mensajeError",
                    "No se pudo confirmar el envío del informe #"
                    + idSolicitud
                    + ". Revisa el estado en MySQL antes de "
                    + "realizar cualquier reintento.");

        } finally {
            if (pdf != null && pdf.exists() && !pdf.delete()) {
                getServletContext().log(
                        "No se pudo eliminar el PDF temporal.");
            }
        }

        response.sendRedirect(
                request.getContextPath()
                + "/colaboradorDashboard");
    }
}
