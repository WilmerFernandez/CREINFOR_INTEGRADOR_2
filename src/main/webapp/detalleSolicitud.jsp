
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="utp_integrador_2_model.Actividad" %>
<%@ page import="utp_integrador_2_model.Usuario" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    List<Actividad> actividades
            = (List<Actividad>) request.getAttribute("actividades");

    Integer idSolicitud
            = (Integer) request.getAttribute("idSolicitud");

    if (actividades == null) {
        actividades = new ArrayList<>();
    }

    Usuario usuario = (Usuario) session.getAttribute("usuario");

    String tipoUsuario = usuario != null
            ? usuario.getTipoUsuario() : "";

    boolean esAdmin = "admin".equalsIgnoreCase(tipoUsuario);
    boolean esColaborador = "colaborador".equalsIgnoreCase(tipoUsuario);

    double totalHoras = 0;

    for (Actividad actividad : actividades) {
        totalHoras += actividad.getTiempoHoras();
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Historial de Actividades - CREINFOR</title>

        <style>
            * {
                box-sizing: border-box;
            }

            body {
                margin: 0;
                font-family: 'Segoe UI', Arial, sans-serif;
                background: #eef3f1;
                color: #263238;
            }

            .header {
                background: #164b3a;
                color: white;
                padding: 24px 30px;
            }

            .header-content {
                max-width: 1150px;
                margin: auto;
            }

            .header h1 {
                margin: 0;
                font-size: 26px;
            }

            .header p {
                margin: 5px 0 0;
                font-size: 13px;
                opacity: .85;
            }

            .container {
                max-width: 1150px;
                margin: 30px auto;
                padding: 0 20px;
            }

            .titulo {
                display: flex;
                align-items: center;
                justify-content: space-between;
                gap: 15px;
                margin-bottom: 22px;
            }

            .titulo h2 {
                margin: 0 0 6px;
                font-size: 24px;
                color: #173b30;
            }

            .subtitulo {
                margin: 0;
                font-size: 13px;
                color: #71857c;
            }

            .badge-solicitud {
                background: #dcefe5;
                color: #176448;
                padding: 9px 15px;
                border-radius: 25px;
                font-size: 13px;
                font-weight: 700;
                white-space: nowrap;
            }

            .resumen {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 18px;
                margin-bottom: 22px;
            }

            .resumen-card {
                background: white;
                border-radius: 12px;
                padding: 20px;
                border-left: 4px solid #21845d;
                box-shadow: 0 5px 20px rgba(0,0,0,.05);
            }

            .resumen-card p {
                margin: 0 0 8px;
                color: #70877e;
                font-size: 13px;
            }

            .resumen-card strong {
                font-size: 25px;
                color: #164b3a;
            }

            .card {
                background: white;
                border-radius: 14px;
                box-shadow: 0 5px 22px rgba(0,0,0,.06);
                overflow: hidden;
            }

            .card-header {
                padding: 20px 24px;
                border-bottom: 1px solid #e6ece9;
            }

            .card-header h3 {
                margin: 0;
                font-size: 17px;
                color: #164b3a;
            }

            .card-body {
                padding: 20px 24px;
                overflow-x: auto;
            }

            table {
                width: 100%;
                border-collapse: collapse;
            }

            th {
                background: #f1f6f3;
                color: #315747;
                text-align: left;
                padding: 14px;
                font-size: 13px;
            }

            td {
                padding: 15px 14px;
                border-bottom: 1px solid #edf1ef;
                font-size: 14px;
                vertical-align: top;
            }

            tr:hover td {
                background: #f8fcf9;
            }

            .fecha {
                white-space: nowrap;
                color: #62776b;
            }

            .horas {
                display: inline-block;
                background: #e4f4e9;
                color: #217448;
                padding: 5px 10px;
                border-radius: 15px;
                font-weight: 600;
                white-space: nowrap;
            }

            .empty {
                text-align: center;
                color: #81948a;
                padding: 35px;
            }

            .acciones {
                display: flex;
                flex-wrap: wrap;
                gap: 12px;
                padding: 20px 24px;
                border-top: 1px solid #e6ece9;
            }

            .btn {
                display: inline-block;
                text-decoration: none;
                padding: 11px 18px;
                border-radius: 8px;
                font-size: 14px;
                font-weight: 600;
                transition: .2s;
            }

            .btn-primary {
                background: #21845d;
                color: white;
            }

            .btn-primary:hover {
                background: #176b49;
            }

            .btn-secondary {
                background: #edf3ef;
                color: #315747;
            }

            .btn-secondary:hover {
                background: #dce8e0;
            }

            .footer {
                text-align: center;
                padding: 28px;
                color: #91a29a;
                font-size: 12px;
            }

            @media(max-width: 650px) {
                .resumen {
                    grid-template-columns: 1fr;
                }

                .titulo {
                    align-items: flex-start;
                    flex-direction: column;
                }

                .card-body {
                    padding: 12px;
                }

                th, td {
                    padding: 10px;
                }
            }
        </style>
    </head>

    <body>

        <header class="header">
            <div class="header-content">
                <h1>CREINFOR</h1>
                <p>Sistema de Gestión de Soporte Técnico · Historial de actividades</p>
            </div>
        </header>

        <main class="container">

            <div class="titulo">
                <div>
                    <h2>Historial de Actividades</h2>
                    <p class="subtitulo">
                        Consulta las actividades registradas durante
                        la atención de la solicitud.
                    </p>
                </div>

                <span class="badge-solicitud">
                    Solicitud #<%= idSolicitud%>
                </span>
            </div>

            <!-- RESUMEN -->
            <div class="resumen">

                <div class="resumen-card">
                    <p>Actividades registradas</p>
                    <strong><%= actividades.size()%></strong>
                </div>

                <div class="resumen-card">
                    <p>Total de horas trabajadas</p>
                    <strong><%= totalHoras%> h</strong>
                </div>

            </div>

            <!-- HISTORIAL -->
            <section class="card">

                <div class="card-header">
                    <h3>Detalle de actividades</h3>
                </div>

                <div class="card-body">

                    <table>
                        <thead>
                            <tr>
                                <th>Fecha</th>
                                <th>Descripción de la actividad</th>
                                <th>Tiempo (horas)</th>
                            </tr>
                        </thead>

                        <tbody>

                            <% if (actividades.isEmpty()) { %>

                            <tr>
                                <td colspan="3" class="empty">
                                    No hay actividades registradas
                                    para esta solicitud.
                                </td>
                            </tr>

                            <% } else {
                            for (Actividad a : actividades) {%>

                            <tr>
                                <td class="fecha">
                                    <%= a.getFecha()%>
                                </td>

                                <td>
                                    <c:out value="<%= a.getDescripcion()%>"/>
                                </td>

                                <td>
                                    <span class="horas">
                                        <%= a.getTiempoHoras()%> h
                                    </span>
                                </td>
                            </tr>

                            <%  }
                            } %>

                        </tbody>
                    </table>

                </div>

                <!-- BOTONES SEGÚN EL USUARIO -->
                <div class="acciones">

                    <% if (esAdmin) { %>

                    <a href="${pageContext.request.contextPath}/solicitudes"
                       class="btn btn-primary">
                        ← Volver a solicitudes
                    </a>

                    <a href="${pageContext.request.contextPath}/adminDashboard"
                       class="btn btn-secondary">
                        Volver al panel administrador
                    </a>

                    <% } else if (esColaborador) { %>

                    <a href="${pageContext.request.contextPath}/colaboradorDashboard"
                       class="btn btn-primary">
                        ← Volver al Dashboard
                    </a>

                    <% } else { %>

                    <a href="${pageContext.request.contextPath}/login.jsp"
                       class="btn btn-secondary">
                        Volver
                    </a>

                    <% }%>

                </div>

            </section>

        </main>

        <footer class="footer">
            CREINFOR © 2026 · Sistema de Gestión de Soporte Técnico
        </footer>

    </body>
</html>
