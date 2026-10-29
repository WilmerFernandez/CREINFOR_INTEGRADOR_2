package utp_integrador_2.controller;


import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2_model.Usuario;
import utp_integrador_2.facade.UsuarioFacade;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection conn = BDConnection.getConnection()) {
            UsuarioFacade facade = new UsuarioFacade(conn);
            Usuario usuario = facade.validarLogin(email, password);

            if (usuario != null) {
                HttpSession session = request.getSession();
                session.setAttribute("usuario", usuario);

                switch (usuario.getTipoUsuario()) {
                    case "cliente":
                        response.sendRedirect("clienteDashboard.jsp");
                        break;
                    case "colaborador":
                        response.sendRedirect("colaboradorDashboard");
                        break;
                    case "admin":
                        response.sendRedirect("adminDashboard");
                        break;
                    default:
                        request.setAttribute("error", "Rol no reconocido");
                        request.getRequestDispatcher("login.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("error", "Credenciales incorrectas");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
