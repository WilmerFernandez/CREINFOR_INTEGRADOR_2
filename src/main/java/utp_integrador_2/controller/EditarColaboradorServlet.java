package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.facade.ColaboradorFacade;
import utp_integrador_2_model.Colaborador;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/editarColaborador")
public class EditarColaboradorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            int id = Integer.parseInt(request.getParameter("id"));

            try (Connection connection = BDConnection.getConnection()) {
                ColaboradorFacade facade = new ColaboradorFacade(connection);

                request.setAttribute("colaboradorEditar", facade.buscarPorId(id));
                request.setAttribute("colaboradores", facade.listarTodos());

                request.getRequestDispatcher("colaboradorRegistro.jsp").forward(request, response);
            }

        } catch (Exception e) {
            throw new ServletException("Error al cargar colaborador.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            int id = Integer.parseInt(request.getParameter("idColaborador"));

            Colaborador colaborador = Colaborador.builder()
                    .idColaborador(id)
                    .nombres(request.getParameter("nombres"))
                    .apellidos(request.getParameter("apellidos"))
                    .email(request.getParameter("email"))
                    .rol(request.getParameter("rol"))
                    .build();

            try (Connection connection = BDConnection.getConnection()) {

                ColaboradorFacade facade = new ColaboradorFacade(connection);
                facade.actualizarColaborador(colaborador);

                request.setAttribute("mensaje", "Colaborador actualizado correctamente.");
                request.setAttribute("colaboradores", facade.listarTodos());

                request.getRequestDispatcher("colaboradorRegistro.jsp").forward(request, response);
            }

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