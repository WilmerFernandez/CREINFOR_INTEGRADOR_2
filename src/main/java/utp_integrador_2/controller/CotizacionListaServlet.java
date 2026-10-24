
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

@WebServlet("/cotizaciones/lista")
public class CotizacionListaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        Usuario usuario = session == null ? null
                : (Usuario) session.getAttribute("usuario");

        if (usuario == null ||
                !"admin".equalsIgnoreCase(usuario.getTipoUsuario())) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try (Connection connection = BDConnection.getConnection()) {

            CotizacionFacade facade =
                    new CotizacionFacade(connection);

            List<Cotizacion> cotizaciones = facade.listarTodas();

            request.setAttribute("cotizaciones", cotizaciones);

            // Mostrar el detalle cuando se selecciona una cotización
            String id = request.getParameter("id");

            if (id != null && !id.isBlank()) {

                int idCotizacion = Integer.parseInt(id);

                if (idCotizacion <= 0) {
                    response.sendError(
                            HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                Cotizacion cotizacion = facade.buscarPorId(idCotizacion);

                if (cotizacion == null) {
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                List<DetalleCotizacion> detalles =
                        facade.listarDetalles(idCotizacion);

                request.setAttribute("cotizacionSeleccionada", cotizacion);
                request.setAttribute("detalles", detalles);
            }

            request.getRequestDispatcher("/cotizacionLista.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ID de cotización inválido.");

        } catch (Exception e) {
            throw new ServletException(
                    "Error al cargar las cotizaciones.", e);
        }
    }
}
