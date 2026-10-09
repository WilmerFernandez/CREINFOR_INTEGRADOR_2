<%@ page import="utp_integrador_2_model.Usuario" %>
<%@ page session="true" %>

<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"cliente".equals(usuario.getTipoUsuario())) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Mi Panel - Cliente</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .header {
            background-color: #4CAF50;
            color: white;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .form-container {
            background-color: white;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        textarea {
            width: 100%;
            padding: 10px;
            margin: 10px 0;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        select {
            padding: 8px 15px;
            margin: 5px 0;
            width: 100%;
        }
        .btn-group {
            display: flex;
            gap: 10px;
            margin-top: 15px;
        }
        .btn {
            padding: 10px 15px;
            border-radius: 4px;
            text-decoration: none;
            text-align: center;
            flex: 1; /* Hace que ambos botones ocupen el mismo espacio */
            font-size: 14px;
            border: none;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        .btn-primary {
            background-color: #4CAF50;
            color: white;
        }
        .btn-primary:hover {
            background-color: #45a049;
        }
        .btn-info {
            background-color: #2196F3;
            color: white;
        }
        .btn-info:hover {
            background-color: #0b7dda;
        }
        .btn-danger {
            background-color: #f44336;
            color: white;
        }
        .btn-danger:hover {
            background-color: #d32f2f;
        }
        .logout-btn {
            background-color: transparent;
            border: 1px solid white;
            color: white;
            padding: 5px 10px;
            border-radius: 4px;
            cursor: pointer;
        }
        .logout-btn:hover {
            background-color: rgba(255,255,255,0.2);
        }
    </style>
</head>
<body>
    <div class="header">
        <div>
            <h2>¡Hola, <%= usuario.getNombres()%> <%= usuario.getApellidos()%>!</h2>
            <p>Panel de solicitudes de soporte técnico</p>
        </div>
        <form action="login.jsp" method="post">
            <button type="submit" class="logout-btn">Cerrar sesión</button>
        </form>
    </div>

    <div class="form-container">
        <h3>Nueva Solicitud</h3>
        <form action="solicitud" method="post">
            <input type="hidden" name="idUsuario" value="<%= usuario.getIdUsuario()%>"/>
            
            <label for="tipoSolicitud">Tipo de Solicitud:</label><br>
            <select name="tipoSolicitud" id="tipoSolicitud" required>
                <option value="" disabled selected>Selecciona una opción</option>
                <option value="error">Error de software</option>
                <option value="capacitación">Capacitación</option>
                <option value="requerimiento">Nuevo requerimiento</option>
            </select><br/><br>
            
            <label for="motivo">Describe tu solicitud:</label><br>
            <textarea name="motivo" id="motivo" rows="5" placeholder="Explica con detalle tu problema o solicitud..." required></textarea><br/>
            
            <div class="btn-group">
                <button type="submit" class="btn btn-primary">Enviar Solicitud</button>
                <a href="listaSolicitudes" class="btn btn-info">Ver mis solicitudes</a>
            </div>
        </form>
    </div>
</body>
</html>