package utp_integrador_2.controller;

import utp_integrador_2.DAO.BDConnection;
import utp_integrador_2.DAO.TokenActivacionDAO;
import utp_integrador_2_model.TokenActivacion;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.UUID;

@WebServlet("/PruebaTokenServlet")
public class PruebaTokenServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection connection = BDConnection.getConnection()) {

            TokenActivacionDAO dao =
                    new TokenActivacionDAO(connection);

            String tokenGenerado =
                    UUID.randomUUID().toString();

            TokenActivacion token =
                    TokenActivacion.builder()
                            .idUsuario(1)
                            .token(tokenGenerado)
                            .fechaExpiracion(
                                    LocalDateTime.now().plusMinutes(30)
                            )
                            .usado(false)
                            .build();

            dao.insertar(token);

            response.setContentType("text/html;charset=UTF-8");

            response.getWriter().println(
                    "<h2>Token creado correctamente</h2>"
            );

            response.getWriter().println(
                    "<p>Token: " + tokenGenerado + "</p>"
            );

            response.getWriter().println(
                    "<p>Usuario: " + token.getIdUsuario() + "</p>"
            );

            response.getWriter().println(
                    "<p>Expira: "
                    + token.getFechaExpiracion()
                    + "</p>"
            );

        } catch (Exception e) {

            throw new ServletException(
                    "Error al probar TokenActivacion",
                    e
            );
        }
    }
}