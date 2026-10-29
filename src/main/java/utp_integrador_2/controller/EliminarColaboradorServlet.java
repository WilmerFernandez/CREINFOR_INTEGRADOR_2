package utp_integrador_2.controller;

import utp_integrador_2_model.Usuario;
import utp_integrador_2.DAO.ColaboradorDAO;
import java.sql.Connection;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utp_integrador_2.DAO.BDConnection;

@WebServlet("/eliminarColaborador")
public class EliminarColaboradorServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // 1. Verificar sesión y permisos
        if (session == null
                || !(session.getAttribute("usuario") instanceof Usuario)) {

            response.sendRedirect(
                    request.getContextPath() + "/login.jsp");
            return;
        }

        Usuario usuario
                = (Usuario) session.getAttribute("usuario");

        if (!"admin".equalsIgnoreCase(usuario.getTipoUsuario())) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "No tiene permisos para eliminar colaboradores.");
            return;
        }

        try {

            // 2. Obtener ID del colaborador
            int idColaborador = Integer.parseInt(
                    request.getParameter("idColaborador"));

            if (idColaborador <= 0) {
                session.setAttribute("errorMensaje",
                        "El ID del colaborador no es válido.");
                response.sendRedirect(
                        request.getContextPath() + "/registrarColaborador");
                return;
            }

            Connection connection = BDConnection.getConnection();
            ColaboradorDAO dao = new ColaboradorDAO(connection);

            // 3. Verificar si tiene asignaciones
            if (dao.tieneAsignaciones(idColaborador)) {

                session.setAttribute("errorMensaje",
                        "No se puede eliminar este colaborador "
                        + "porque tiene solicitudes asignadas.");

            } else {

                // 4. Eliminar si no tiene asignaciones
                boolean eliminado
                        = dao.eliminarColaborador(idColaborador);

                if (eliminado) {

                    session.setAttribute("mensaje",
                            "Colaborador eliminado correctamente.");

                } else {

                    session.setAttribute("errorMensaje",
                            "No se pudo eliminar el colaborador. "
                            + "Verifique que exista y que no tenga "
                            + "registros relacionados.");
                }
            }

        } catch (NumberFormatException e) {

            session.setAttribute("errorMensaje",
                    "ID del colaborador no válido.");

        } catch (SQLException e) {

            getServletContext().log(
                    "Error al verificar asignaciones", e);

            session.setAttribute("errorMensaje",
                    "No se pudo verificar si el colaborador "
                    + "tiene solicitudes asignadas.");
        }

        // 5. Regresar al listado
        response.sendRedirect(
                request.getContextPath() + "/registrarColaborador");
    }
}
