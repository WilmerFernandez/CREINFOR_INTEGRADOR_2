package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DTO.CotizacionDTO;
import utp_integrador_2.DTO.DetalleCotizacionDTO;
import utp_integrador_2.facade.CotizacionFacade;
import utp_integrador_2.facade.AsignacionFacade;
import utp_integrador_2_model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/cotizaciones/nueva")
public class CotizacionServlet extends HttpServlet {

    private boolean esAdministrador(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null
                : (Usuario) session.getAttribute("usuario");

        return usuario != null
                && "admin".equalsIgnoreCase(usuario.getTipoUsuario());
    }

    private void mostrarFormulario(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection connection = BDConnection.getConnection()) {

            CotizacionFacade facade = new CotizacionFacade(connection);

            request.setAttribute(
                    "solicitudes",
                    facade.listarSolicitudesParaCotizar());

            request.getRequestDispatcher(
                    "/cotizacionForm.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(
                    "Error al cargar el formulario de cotizaciones.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!esAdministrador(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        mostrarFormulario(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (!esAdministrador(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        request.setCharacterEncoding("UTF-8");

        try {
            int idSolicitud = Integer.parseInt(
                    request.getParameter("idSolicitud"));

            LocalDate vencimiento = LocalDate.parse(
                    request.getParameter("fechaVencimiento"));

            String observaciones
                    = request.getParameter("observaciones");

            String[] descripciones
                    = request.getParameterValues("descripcion");

            String[] cantidades
                    = request.getParameterValues("cantidad");

            String[] precios
                    = request.getParameterValues("precioUnitario");

            if (descripciones == null
                    || cantidades == null
                    || precios == null
                    || descripciones.length == 0
                    || descripciones.length > 50
                    || descripciones.length != cantidades.length
                    || descripciones.length != precios.length) {

                throw new IllegalArgumentException(
                        "Los conceptos de la cotización son inválidos.");
            }

            List<DetalleCotizacionDTO> detalles = new ArrayList<>();

            for (int i = 0; i < descripciones.length; i++) {

                detalles.add(DetalleCotizacionDTO.builder()
                        .descripcion(descripciones[i])
                        .cantidad(new BigDecimal(cantidades[i]))
                        .precioUnitario(new BigDecimal(precios[i]))
                        .build());
            }

            CotizacionDTO dto = CotizacionDTO.builder()
                    .idSolicitud(idSolicitud)
                    .fechaVencimiento(vencimiento)
                    .observaciones(observaciones)
                    .detalles(detalles)
                    .build();

            int idCotizacion;

            try (Connection connection = BDConnection.getConnection()) {
                CotizacionFacade facade = new CotizacionFacade(connection);
                idCotizacion = facade.registrarCotizacion(dto);
            }

            response.sendRedirect(request.getContextPath()
                    + "/cotizaciones/nueva?creada=" + idCotizacion);

        } catch (Exception e) {

            getServletContext().log(
                    "Error al registrar cotización", e);

            request.setAttribute("mensajeError",
                    "No se pudo guardar la cotización. "
                    + "Verifica los conceptos, importes y fecha.");

            mostrarFormulario(request, response);
        }
    }
}
