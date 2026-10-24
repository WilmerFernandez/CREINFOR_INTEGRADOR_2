package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.facade.CotizacionFacade;
import utp_integrador_2_model.Cotizacion;
import utp_integrador_2_model.DetalleCotizacion;
import utp_integrador_2_model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.time.LocalDate;

@WebServlet("/cotizaciones/mis")
public class MisCotizacionesServlet extends HttpServlet {

    private Usuario obtenerCliente(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        Usuario usuario
                = (Usuario) session.getAttribute("usuario");

        if (usuario == null
                || !"cliente".equalsIgnoreCase(usuario.getTipoUsuario())) {
            return null;
        }

        return usuario;
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Usuario cliente = obtenerCliente(request);

        if (cliente == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        response.setHeader("Cache-Control", "no-store");

        try (Connection connection = BDConnection.getConnection()) {

            CotizacionFacade facade
                    = new CotizacionFacade(connection);

            List<Cotizacion> cotizaciones
                    = facade.listarPorCliente(cliente.getIdUsuario());

            request.setAttribute("cotizaciones", cotizaciones);
            request.setAttribute("hoy", LocalDate.now());

            // Se puede consultar el detalle de una cotización propia
            String parametro = request.getParameter("id");

            if (parametro != null && !parametro.isBlank()) {

                int idCotizacion = Integer.parseInt(parametro);

                if (idCotizacion <= 0) {
                    response.sendError(400);
                    return;
                }

                Cotizacion seleccionada = null;

                for (Cotizacion cot : cotizaciones) {
                    if (cot.getIdCotizacion() == idCotizacion) {
                        seleccionada = cot;
                        break;
                    }
                }

                if (seleccionada == null) {
                    response.sendError(404);
                    return;
                }

                List<DetalleCotizacion> detalles
                        = facade.listarDetalles(idCotizacion);

                request.setAttribute("seleccionada", seleccionada);
                request.setAttribute("detalles", detalles);

                request.setAttribute("puedeResponder",
                        "enviada".equals(seleccionada.getEstado())
                        && seleccionada.getFechaVencimiento() != null
                        && !seleccionada.getFechaVencimiento()
                                .isBefore(LocalDate.now()));
            }

            request.getRequestDispatcher(
                    "/WEB-INF/views/cliente/misCotizaciones.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(400, "Cotización inválida.");

        } catch (Exception e) {
            throw new ServletException(
                    "Error al consultar las cotizaciones.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Usuario cliente = obtenerCliente(request);

        if (cliente == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        HttpSession session = request.getSession(false);

        try {
            int idCotizacion = Integer.parseInt(
                    request.getParameter("idCotizacion"));

            String respuesta = request.getParameter("respuesta");

            if (idCotizacion <= 0
                    || (!"aprobada".equals(respuesta)
                    && !"rechazada".equals(respuesta))) {
                response.sendError(400);
                return;
            }

            try (Connection connection = BDConnection.getConnection()) {

                CotizacionFacade facade
                        = new CotizacionFacade(connection);

                boolean actualizado = facade.responderCotizacion(
                        idCotizacion,
                        cliente.getIdUsuario(),
                        respuesta);

                if (actualizado) {
                    session.setAttribute("mensajeCotizacion",
                            "Cotización #" + idCotizacion
                            + ("aprobada".equals(respuesta)
                            ? " aprobada correctamente."
                            : " rechazada correctamente."));
                } else {
                    session.setAttribute("errorCotizacion",
                            "No se pudo registrar la respuesta. "
                            + "La cotización podría estar vencida, "
                            + "haber sido respondida o no pertenecerte.");
                }
            }

        } catch (NumberFormatException e) {
            response.sendError(400);
            return;

        } catch (Exception e) {
            getServletContext().log(
                    "Error al responder cotización", e);

            session.setAttribute("errorCotizacion",
                    "Ocurrió un error al registrar la respuesta.");
        }

        response.sendRedirect(
                request.getContextPath() + "/cotizaciones/mis");
    }
}
