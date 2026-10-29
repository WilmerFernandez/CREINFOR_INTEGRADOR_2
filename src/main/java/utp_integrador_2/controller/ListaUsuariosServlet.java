package utp_integrador_2.controller;

import utp_integrador_2.DAO.UsuarioDAO;
import utp_integrador_2_model.Usuario;
import utp_integrador_2.DAO.BDConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/listaUsuarios")
public class ListaUsuariosServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("usuario") instanceof Usuario)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        Usuario actual = (Usuario) session.getAttribute("usuario");
        if (!"admin".equalsIgnoreCase(actual.getTipoUsuario())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        try (Connection cn = BDConnection.getConnection()) {
            UsuarioDAO dao = new UsuarioDAO(cn);
            request.setAttribute("usuarios", dao.listarTodos());
            request.getRequestDispatcher("/listaUsuarios.jsp").forward(request, response);
        } catch (SQLException e) {
            getServletContext().log("Error al listar usuarios", e);
            throw new ServletException("No se pudo cargar la lista de usuarios.", e);
        }
    }
}