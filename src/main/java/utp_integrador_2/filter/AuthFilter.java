package utp_integrador_2.filter;

import utp_integrador_2_model.Usuario;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/adminDashboard",
        "/colaboradorDashboard",
        "/clienteDashboard.jsp",
        "/UsuarioRegistro.jsp",
        "/UsuarioServlet"
})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        HttpSession session =
                httpRequest.getSession(false);

        if (session == null
                || session.getAttribute("usuario") == null) {

            httpResponse.sendRedirect(
                    httpRequest.getContextPath()
                    + "/login.jsp"
            );

            return;
        }

        Usuario usuario =
                (Usuario) session.getAttribute("usuario");

        String ruta =
                httpRequest.getRequestURI();

        if ((ruta.endsWith("/UsuarioRegistro.jsp")
                || ruta.endsWith("/UsuarioServlet")
                || ruta.endsWith("/adminDashboard"))
                && !"admin".equals(usuario.getTipoUsuario())) {

            httpResponse.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "No tiene permisos para acceder a esta página."
            );

            return;
        }

        if (ruta.endsWith("/colaboradorDashboard")
                && !"colaborador".equals(
                        usuario.getTipoUsuario())) {

            httpResponse.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "No tiene permisos para acceder a esta página."
            );

            return;
        }

        if (ruta.endsWith("/clienteDashboard.jsp")
                && !"cliente".equals(
                        usuario.getTipoUsuario())) {

            httpResponse.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "No tiene permisos para acceder a esta página."
            );

            return;
        }

        chain.doFilter(request, response);
    }
}