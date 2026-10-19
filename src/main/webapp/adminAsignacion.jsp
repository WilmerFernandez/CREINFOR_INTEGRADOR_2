<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Asignar Solicitud a Colaborador</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f4f4f4;
            color: #333;
        }
        .container {
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            max-width: 500px;
            margin: auto;
        }
        h2 {
            color: #0056b3;
            text-align: center;
            margin-bottom: 25px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
        }
        select, button {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box; /* Para que el padding no aumente el ancho */
        }
        button {
            background-color: #007bff;
            color: white;
            border: none;
            cursor: pointer;
            font-size: 16px;
            transition: background-color 0.3s ease;
        }
        button:hover {
            background-color: #0056b3;
        }
        .message {
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 4px;
            text-align: center;
        }
        .message.success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .message.error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .back-link {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #007bff;
            text-decoration: none;
        }
        .back-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Asignar Solicitud a Colaborador</h2>

        <%-- Mensajes de retroalimentación --%>
        <c:if test="${not empty requestScope.mensajeExito}">
            <div class="message success">
                <p>${requestScope.mensajeExito}</p>
            </div>
        </c:if>
        <c:if test="${not empty requestScope.mensajeError}">
            <div class="message error">
                <p>${requestScope.mensajeError}</p>
            </div>
        </c:if>

        <form action="asignacion" method="post">
            <label for="idSolicitud">Solicitud:</label>
            <select name="idSolicitud" id="idSolicitud" required>
                <option value="">-- Seleccione una solicitud --</option>
                <c:forEach var="sol" items="${solicitudesPendientes}">
                    <option value="${sol.idSolicitud}">
                        ID: ${sol.idSolicitud} - Tipo: ${sol.tipoSolicitud} - Motivo: ${sol.motivo}
                    </option>
                </c:forEach>
            </select>

            <label for="idColaborador">Colaborador:</label>
            <select name="idColaborador" id="idColaborador" required>
                <option value="">-- Seleccione un colaborador --</option>
                <c:forEach var="col" items="${colaboradores}">
                    <option value="${col.idColaborador}">
                        ${col.nombres} ${col.apellidos} (${col.rol})
                    </option>
                </c:forEach>
            </select>

            <label for="esCoordinador">¿Es coordinador de esta asignación?</label>
            <select name="esCoordinador" id="esCoordinador" required>
                <option value="true">Sí</option>
                <option value="false" selected>No</option>
            </select>

            <button type="submit">Asignar Solicitud</button>
        </form>

        <a href="adminDashboard" class="back-link">Volver al dashboard</a>
    </div>
</body>
</html>