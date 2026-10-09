<%@ page import="java.util.List" %>
<%@ page import="utp_integrador_2_model.Solicitud" %>
<%@ page import="utp_integrador_2_model.Usuario" %>
<%@ page session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> <%-- Si tu Solicitud model usa LocalDateTime y necesitas fmt --%>


<%
    List<Solicitud> lista = (List<Solicitud>) request.getAttribute("listaSolicitudes");
    if (lista == null) {
        lista = new java.util.ArrayList<>();
    }
    
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Mis Solicitudes</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f8f9fa;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            padding-bottom: 15px;
            border-bottom: 1px solid #dee2e6;
        }
        h2 {
            color: #2c3e50;
            margin: 0;
        }
        .btn {
            display: inline-block;
            padding: 8px 16px;
            text-decoration: none;
            border-radius: 4px;
            font-weight: 500;
            transition: all 0.3s;
        }
        .btn-primary {
            background-color: #3498db;
            color: white;
            border: 1px solid #2980b9;
        }
        .btn-primary:hover {
            background-color: #2980b9;
        }
        .btn-danger {
            background-color: #e74c3c;
            color: white;
            border: 1px solid #c0392b;
        }
        .btn-danger:hover {
            background-color: #c0392b;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        th, td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #3498db;
            color: white;
            font-weight: 600;
        }
        tr:nth-child(even) {
            background-color: #f2f2f2;
        }
        tr:hover {
            background-color: #e9f7fe;
        }
        .badge {
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
        }
        .badge-pendiente {
            background-color: #e74c3c; /* Rojo */
            color: white;
        }
        .badge-enproceso {
            background-color: #f39c12; /* Amarillo */
            color: white;
        }
        .badge-finalizado {
            background-color: #2ecc71; /* Verde */
            color: white;
        }
        .no-records {
            text-align: center;
            padding: 20px;
            color: #7f8c8d;
            font-style: italic;
        }
        
        .btn-edit {
            background-color: #f0ad4e; /* Naranja */
            color: white;
            border: 1px solid #eea236;
            /* Eliminamos margin-right: 5px; aquí para que los botones estén más juntos si el contenedor es flex */
        }
        .btn-edit:hover {
            background-color: #ec971f;
        }

        /* Nuevo estilo para el contenedor de botones de acción */
        .action-buttons {
            display: flex; /* Usa flexbox para alinear los elementos en fila */
            gap: 5px;      /* Espacio entre los botones */
            align-items: center; /* Alinea verticalmente los botones */
        }
    </style>
</head>
<body>
    <div class="header">
        <h2>Mis Solicitudes - <%= usuario.getNombres() %> <%= usuario.getApellidos() %></h2>
        <div>
            <a href="clienteDashboard.jsp" class="btn btn-primary">Crear Nueva Solicitud</a>
            <a href="clienteDashboard.jsp" class="btn btn-danger">Volver</a>
        </div>
    </div>

    <%-- Mensajes de éxito/error después de operaciones --%>
    <c:if test="${not empty successMessage}">
        <p style="color: green; font-weight: bold;">${successMessage}</p>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <p style="color: red; font-weight: bold;">${errorMessage}</p>
    </c:if>

    <% if (lista.isEmpty()) { %>
        <div class="no-records">
            <p>No hay solicitudes registradas</p>
            <a href="crearSolicitud.jsp" class="btn btn-primary">Crear nueva solicitud</a>
        </div>
    <% } else { %>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Tipo</th>
                    <th>Motivo</th>
                    <th>Fecha Registro</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody>
                <% for(Solicitud s : lista) { %>
                    <tr>
                        <td><%= s.getIdSolicitud() %></td>
                        <td><%= s.getTipoSolicitud() %></td>
                        <td><%= s.getMotivo() %></td>
                        <td><%= s.getFechaRegistro() %></td> <%-- Si getFechaRegistro devuelve LocalDateTime directamente --%>
                        <%-- O usa JSTL fmt si tu DTO tiene un getterAsUtilDate para Solicitud --%>
                        <%-- <td><fmt:formatDate value="<%= s.getFechaRegistroAsUtilDate() %>" pattern="dd/MM/yyyy HH:mm"/></td> --%>
                        <td>
                            <%
                                String estadoClass = "";
                                String estado = s.getEstado().toLowerCase();
                                if(estado.contains("pendiente")) {
                                    estadoClass = "pendiente";
                                } else if(estado.contains("proceso")) {
                                    estadoClass = "enproceso";
                                } else if(estado.contains("finalizado")) {
                                    estadoClass = "finalizado";
                                }
                            %>
                            <span class="badge badge-<%= estadoClass %>">
                                <%= s.getEstado() %>
                            </span>
                        </td>
                        <td>
                            <% if ("pendiente".equalsIgnoreCase(s.getEstado())) { %>
                                <div class="action-buttons"> <%-- Nuevo contenedor para los botones --%>
                                    <%-- Botón Editar --%>
                                    <a href="listaSolicitudes?action=edit&id=<%= s.getIdSolicitud() %>" class="btn btn-edit">Editar</a>
                                    
                                    <%-- Botón Eliminar (con confirmación de JavaScript) --%>
                                    <form action="listaSolicitudes" method="post" style="display:inline-block;">
                                        <input type="hidden" name="action" value="eliminar">
                                        <input type="hidden" name="id" value="<%= s.getIdSolicitud() %>">
                                        <button type="submit" class="btn btn-danger" onclick="return confirm('¿Estás seguro de que quieres eliminar esta solicitud?');">Eliminar</button>
                                    </form>
                                </div>
                            <% } else { %>
                                No disponible
                            <% } %>
                        </td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    <% } %>
</body>
</html>