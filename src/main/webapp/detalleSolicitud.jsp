<%@ page import="java.util.List" %>
<%@ page import="utp_integrador_2_model.Actividad" %>
<%@ page session="true" %>

<%
    List<Actividad> actividades = (List<Actividad>) request.getAttribute("actividades");
    Integer idSolicitud = (Integer) request.getAttribute("idSolicitud");
    if (actividades == null) {
        actividades = new java.util.ArrayList<>();
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Detalle de Solicitud - Actividades</title>
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                line-height: 1.6;
                margin: 0;
                padding: 20px;
                background-color: #f5f5f5;
                color: #333;
            }

            .container {
                max-width: 1000px;
                margin: 0 auto;
                background: white;
                padding: 25px;
                border-radius: 8px;
                box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
            }

            h2 {
                color: #2c3e50;
                border-bottom: 2px solid #3498db;
                padding-bottom: 10px;
                margin-top: 0;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                margin: 20px 0;
                box-shadow: 0 2px 3px rgba(0,0,0,0.1);
            }

            th {
                background-color: #3498db;
                color: white;
                text-align: left;
                padding: 12px;
            }

            td {
                padding: 10px 12px;
                border-bottom: 1px solid #ddd;
            }

            tr:nth-child(even) {
                background-color: #f2f2f2;
            }

            tr:hover {
                background-color: #e3f2fd;
            }

            .empty-message {
                text-align: center;
                padding: 20px;
                color: #7f8c8d;
                font-style: italic;
            }

            .btn {
                display: inline-block;
                background: #3498db;
                color: white;
                padding: 10px 20px;
                text-decoration: none;
                border-radius: 5px;
                transition: background 0.3s;
                border: none;
                cursor: pointer;
                font-size: 16px;
            }

            .btn-orange {
                display: inline-block;
                background: #e67e22; /* naranja fuerte */
                color: white;
                padding: 10px 20px;
                text-decoration: none;
                border-radius: 5px;
                transition: background 0.3s;
                border: none;
                cursor: pointer;
                font-size: 16px;
            }


            .btn:hover {
                background: #2980b9;
            }

            .header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 20px;
            }

            .badge {
                background: #e74c3c;
                color: white;
                padding: 5px 10px;
                border-radius: 20px;
                font-size: 14px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h2>Historial de Actividades</h2>
                <span class="badge">Solicitud #<%= idSolicitud%></span>
            </div>

            <table>
                <thead>
                    <tr>
                        <th>Fecha</th>
                        <th>Descripción</th>
                        <th>Tiempo (horas)</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (actividades.isEmpty()) { %>
                    <tr>
                        <td colspan="3" class="empty-message">No hay actividades registradas para esta solicitud</td>
                    </tr>
                    <% } else {
                    for (Actividad a : actividades) {%>
                    <tr>
                        <td><%= a.getFecha()%></td>
                        <td><%= a.getDescripcion()%></td>
                        <td><%= a.getTiempoHoras()%></td>
                    </tr>
                    <%  }
                    }%>
                </tbody>
            </table>

            <a href="colaboradorDashboard" class="btn">Volver al Dashboard</a>
            <a href="solicitudes" class="btn-orange">Volver a la lista</a>
        </div>
    </body>
</html>