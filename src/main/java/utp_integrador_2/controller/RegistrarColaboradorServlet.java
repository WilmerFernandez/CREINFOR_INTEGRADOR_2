package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.facade.ColaboradorFacade;
import utp_integrador_2_model.Colaborador;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/registrarColaborador")
public class RegistrarColaboradorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try (Connection connection = BDConnection.getConnection()) {
            ColaboradorFacade facade = new ColaboradorFacade(connection);

            request.setAttribute("colaboradores", facade.listarTodos());
            request.getRequestDispatcher("colaboradorRegistro.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error al cargar colaboradores.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String nombres = request.getParameter("nombres");
        String apellidos = request.getParameter("apellidos");
        String email = request.getParameter("email");
        String rol = request.getParameter("rol");

        Colaborador colaborador = Colaborador.builder()
                .nombres(nombres)
                .apellidos(apellidos)
                .email(email)
                .rol(rol)
                .build();

        try (Connection connection = BDConnection.getConnection()) {

            ColaboradorFacade facade = new ColaboradorFacade(connection);
            facade.registrarColaborador(colaborador);

            request.setAttribute("mensaje", "Colaborador registrado correctamente.");
            request.setAttribute("colaboradores", facade.listarTodos());

            request.getRequestDispatcher("colaboradorRegistro.jsp").forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute("errorMensaje", e.getMessage());

            try (Connection connection = BDConnection.getConnection()) {
                ColaboradorFacade facade = new ColaboradorFacade(connection);
                request.setAttribute("colaboradores", facade.listarTodos());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            request.getRequestDispatcher("colaboradorRegistro.jsp").forward(request, response);
        }
    }
}