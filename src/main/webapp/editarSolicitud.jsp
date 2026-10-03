<%-- src/main/webapp/editarSolicitud.jsp --%>
<%@ page import="utp_integrador_2_model.Solicitud" %>
<%@ page import="utp_integrador_2_model.Usuario" %>
<%@ page session="true" %>

<%
    Solicitud solicitudAEditar = (Solicitud) request.getAttribute("solicitudAEditar");
    if (solicitudAEditar == null) {
        // Si no hay solicitud para editar, redirige o muestra un error
        response.sendRedirect("listaSolicitudes?errorMessage=No se pudo cargar la solicitud para edición.");
        return;
    }
    Usuario usuario = (Usuario) session.getAttribute("usuario"); // Necesitas el usuario de sesión para volver
%>
<!DOCTYPE html>
<html>
<head>
    <title>Editar Solicitud - ID: <%= solicitudAEditar.getIdSolicitud() %></title>
    <style>
        body { font-family: 'Arial', sans-serif; max-width: 800px; margin: 0 auto; padding: 20px; background-color: #f8f9fa; }
        h2 { color: #2c3e50; margin-bottom: 20px; text-align: center; }
        .form-container {
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }
        .form-group { margin-bottom: 15px; }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #34495e;
        }
        .form-group input[type="text"],
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box; /* Incluye padding y border en el ancho total */
        }
        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }
        .form-actions {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
            margin-top: 20px;
        }
        .btn {
            padding: 10px 20px;
            border-radius: 4px;
            text-decoration: none;
            font-weight: bold;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        .btn-success {
            background-color: #28a745;
            color: white;
            border: 1px solid #218838;
        }
        .btn-success:hover {
            background-color: #218838;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
            border: 1px solid #5a6268;
        }
        .btn-secondary:hover {
            background-color: #5a6268;
        }
         .error-message { color: red; margin-bottom: 15px; text-align: center; }
    </style>
</head>
<body>
    <div class="form-container">
        <h2>Editar Solicitud</h2>

        <c:if test="${not empty errorMessage}">
            <p class="error-message">${errorMessage}</p>
        </c:if>

        <form action="listaSolicitudes" method="post">
            <input type="hidden" name="action" value="actualizar">
            <input type="hidden" name="idSolicitud" value="<%= solicitudAEditar.getIdSolicitud() %>">
            
            <div class="form-group">
                <label for="idSolicitudDisplay">ID Solicitud:</label>
                <input type="text" id="idSolicitudDisplay" value="<%= solicitudAEditar.getIdSolicitud() %>" disabled>
            </div>

            <div class="form-group">
                <label for="tipoSolicitud">Tipo de Solicitud:</label>
                <select id="tipoSolicitud" name="tipoSolicitud" required>
                    <option value="error" <%= "error".equals(solicitudAEditar.getTipoSolicitud()) ? "selected" : "" %>>Error</option>
                    <option value="capacitación" <%= "capacitación".equals(solicitudAEditar.getTipoSolicitud()) ? "selected" : "" %>>Capacitación</option>
                    <option value="requerimiento" <%= "requerimiento".equals(solicitudAEditar.getTipoSolicitud()) ? "selected" : "" %>>Requerimiento</option>
                </select>
            </div>

            <div class="form-group">
                <label for="motivo">Motivo:</label>
                <textarea id="motivo" name="motivo" rows="5" required><%= solicitudAEditar.getMotivo() %></textarea>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-success">Guardar Cambios</button>
                <a href="listaSolicitudes" class="btn btn-secondary">Cancelar</a>
            </div>
        </form>
    </div>
</body>
</html>