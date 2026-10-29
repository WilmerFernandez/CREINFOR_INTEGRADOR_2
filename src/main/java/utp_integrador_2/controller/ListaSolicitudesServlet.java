
package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.facade.SolicitudFacade;
import utp_integrador_2.facade.CotizacionFacade;
import utp_integrador_2_model.Solicitud;
import utp_integrador_2_model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/listaSolicitudes")
public class ListaSolicitudesServlet extends HttpServlet {

    private Usuario usuarioActual(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null
                : (Usuario) session.getAttribute("usuario");
    }

    private boolean esCliente(Usuario usuario) {
        return usuario != null
                && "cliente".equalsIgnoreCase(
                        usuario.getTipoUsuario());
    }

    private void mensaje(HttpServletRequest request,
            String tipo, String texto) {

        request.getSession().setAttribute(tipo, texto);
    }

    private void regresar(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        response.sendRedirect(
                request.getContextPath() + "/listaSolicitudes");
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = usuarioActual(request);

        if (usuario == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        String rol = usuario.getTipoUsuario();

        if (!"cliente".equalsIgnoreCase(rol)
                && !"colaborador".equalsIgnoreCase(rol)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try (Connection connection = BDConnection.getConnection()) {

            SolicitudFacade solicitudFacade =
                    new SolicitudFacade(connection);

            CotizacionFacade cotizacionFacade =
                    new CotizacionFacade(connection);

            String action = request.getParameter("action");

            // ABRIR FORMULARIO DE EDICIÓN
            if ("edit".equals(action)) {

                if (!esCliente(usuario)) {
                    response.sendError(
                            HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                int idSolicitud = Integer.parseInt(
                        request.getParameter("id"));

                Solicitud solicitud =
                        solicitudFacade.obtenerSolicitudPorId(
                                idSolicitud);

                if (solicitud != null
                        && solicitud.getIdUsuario()
                                == usuario.getIdUsuario()
                        && "pendiente".equalsIgnoreCase(
                                solicitud.getEstado())
                        && !cotizacionFacade
                                .tieneCotizacionBloqueante(
                                        idSolicitud)
                        && !solicitudFacade
                                .verificarSiTieneAsignaciones(
                                        idSolicitud)) {

                    request.setAttribute(
                            "solicitudAEditar", solicitud);

                    request.getRequestDispatcher(
                            "/editarSolicitud.jsp")
                            .forward(request, response);
                    return;
                }

                request.setAttribute("errorMessage",
                        "Esta solicitud no se puede editar. "
                        + "Puede tener una cotización activa, "
                        + "estar asignada o no ser de tu propiedad.");
            }

            List<Solicitud> lista;

            if (esCliente(usuario)) {
                lista = solicitudFacade.obtenerSolicitudesPorUsuario(
                        usuario.getIdUsuario());
            } else {
                lista = solicitudFacade.obtenerSolicitudesPorColaborador(
                        usuario.getIdUsuario());
            }

            if (lista == null) {
                lista = new ArrayList<>();
            }

            // Consultar permisos para mostrar botones
            Set<Integer> editables = new HashSet<>();
            Set<Integer> eliminables = new HashSet<>();
            Set<Integer> conCotizacion = new HashSet<>();

            if (esCliente(usuario)) {

                for (Solicitud s : lista) {

                    int id = s.getIdSolicitud();

                    boolean existeCotizacion =
                            cotizacionFacade
                                    .tieneAlgunaCotizacion(id);

                    boolean bloqueante =
                            cotizacionFacade
                                    .tieneCotizacionBloqueante(id);

                    if (existeCotizacion) {
                        conCotizacion.add(id);
                    }

                    if (!"pendiente".equalsIgnoreCase(
                            s.getEstado())) {
                        continue;
                    }

                    // Las solicitudes asignadas no se modifican
                    if (solicitudFacade
                            .verificarSiTieneAsignaciones(id)) {
                        continue;
                    }

                    if (!bloqueante) {
                        editables.add(id);

                        if (!existeCotizacion) {
                            eliminables.add(id);
                        }
                    }
                }
            }

            request.setAttribute("listaSolicitudes", lista);
            request.setAttribute("editables", editables);
            request.setAttribute("eliminables", eliminables);
            request.setAttribute("conCotizacion", conCotizacion);

            String jspDestino = esCliente(usuario)
                    ? "/clienteSolicitudes.jsp"
                    : "/colaboradorSolicitudes.jsp";

            request.getRequestDispatcher(jspDestino)
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ID de solicitud inválido.");

        } catch (Exception e) {
            throw new ServletException(
                    "Error al consultar solicitudes.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Usuario usuario = usuarioActual(request);

        if (!esCliente(usuario)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String action = request.getParameter("action");

        if (!"actualizar".equals(action)
                && !"eliminar".equals(action)) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int idSolicitud;

        try {
            String parametro = "actualizar".equals(action)
                    ? request.getParameter("idSolicitud")
                    : request.getParameter("id");

            idSolicitud = Integer.parseInt(parametro);

            if (idSolicitud <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            mensaje(request, "errorMessage",
                    "El ID de la solicitud no es válido.");
            regresar(request, response);
            return;
        }

        try (Connection connection = BDConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                // Bloquear la solicitud durante la validación
                String sql = "SELECT id_usuario, estado "
                        + "FROM solicitudes "
                        + "WHERE id_solicitud = ? FOR UPDATE";

                int propietario = -1;
                String estado = null;

                try (PreparedStatement ps =
                        connection.prepareStatement(sql)) {

                    ps.setInt(1, idSolicitud);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            propietario = rs.getInt("id_usuario");
                            estado = rs.getString("estado");
                        }
                    }
                }

                if (propietario != usuario.getIdUsuario()
                        || !"pendiente".equalsIgnoreCase(estado)) {

                    throw new IllegalStateException(
                            "La solicitud no te pertenece "
                            + "o ya no está pendiente.");
                }

                SolicitudFacade solicitudFacade =
                        new SolicitudFacade(connection);

                CotizacionFacade cotizacionFacade =
                        new CotizacionFacade(connection);

                if (solicitudFacade
                        .verificarSiTieneAsignaciones(
                                idSolicitud)) {

                    throw new IllegalStateException(
                            "La solicitud ya tiene colaboradores "
                            + "asignados y no puede modificarse.");
                }

                if (cotizacionFacade
                        .tieneCotizacionBloqueante(
                                idSolicitud)) {

                    throw new IllegalStateException(
                            "La solicitud tiene una cotización "
                            + "en trámite y no puede modificarse.");
                }

                boolean resultado;

                if ("eliminar".equals(action)) {

                    // Evitar romper la relación con cotizaciones
                    if (cotizacionFacade
                            .tieneAlgunaCotizacion(idSolicitud)) {

                        throw new IllegalStateException(
                                "No se puede eliminar una solicitud "
                                + "que tiene historial de cotizaciones.");
                    }

                    resultado = solicitudFacade.eliminarSolicitud(
                            idSolicitud);

                } else {

                    String tipoSolicitud =
                            request.getParameter("tipoSolicitud");

                    String motivo = request.getParameter("motivo");

                    if (tipoSolicitud == null
                            || (!"error".equals(tipoSolicitud)
                            && !"capacitación".equals(tipoSolicitud)
                            && !"requerimiento".equals(tipoSolicitud))
                            || motivo == null
                            || motivo.trim().isEmpty()
                            || motivo.length() > 2000) {

                        throw new IllegalArgumentException(
                                "Los datos ingresados no son válidos.");
                    }

                    Solicitud solicitud =
                            solicitudFacade.obtenerSolicitudPorId(
                                    idSolicitud);

                    if (solicitud == null) {
                        throw new IllegalStateException(
                                "La solicitud no existe.");
                    }

                    solicitud.setTipoSolicitud(tipoSolicitud);
                    solicitud.setMotivo(motivo.trim());

                    resultado =
                            solicitudFacade.actualizarSolicitud(
                                    solicitud);
                }

                if (!resultado) {
                    throw new IllegalStateException(
                            "No se pudo completar la operación.");
                }

                connection.commit();

                mensaje(request, "successMessage",
                        "eliminar".equals(action)
                                ? "Solicitud eliminada correctamente."
                                : "Solicitud actualizada correctamente.");

            } catch (Exception e) {

                connection.rollback();

                if (e instanceof IllegalStateException
                        || e instanceof IllegalArgumentException) {

                    mensaje(request, "errorMessage",
                            e.getMessage());

                } else {
                    getServletContext().log(
                            "Error al modificar solicitud", e);

                    mensaje(request, "errorMessage",
                            "No se pudo completar la operación.");
                }

            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {

            getServletContext().log(
                    "Error de base de datos", e);

            mensaje(request, "errorMessage",
                    "No se pudo conectar con la base de datos.");
        }

        regresar(request, response);
    }
}
