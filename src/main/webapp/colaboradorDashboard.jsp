<%@ page import="java.util.List" %>
<%@ page import="utp_integrador_2_model.Usuario" %>
<%@ page import="utp_integrador_2_model.Solicitud" %>
<%@ page session="true" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%
    //  Validación de Sesión y Rol
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"colaborador".equalsIgnoreCase(usuario.getTipoUsuario())) {
        response.sendRedirect("login.jsp");
        return;
    }

    // RECUPERAR EL ROL ESPECÍFICO DEL COLABORADOR 
    String rolColaborador = (String) request.getAttribute("rolColaborador");
    if (rolColaborador == null) {
        rolColaborador = "No especificado"; // Valor por defecto si no se pudo obtener
    }

    // Recuperación de Solicitudes y Manejo de Nulos
    List<Solicitud> solicitudes = (List<Solicitud>) request.getAttribute("solicitudes");
    if (solicitudes == null) {
        solicitudes = new java.util.ArrayList<>();
    }

    //  Manejo de Mensajes (éxito y error)
    String mensajeExito = (String) request.getAttribute("mensajeExito");
    String mensajeError = (String) request.getAttribute("mensajeError");

    //  Obtener el ID del colaborador logeado pasado desde el Servlet
    Integer idColaboradorLogeado = (Integer) request.getAttribute("idColaboradorLogeado");
    if (idColaboradorLogeado == null) {
        idColaboradorLogeado = -1; // Valor por defecto si no se pudo obtener
    }


%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Dashboard Colaborador</title>
        <style>
            /* Estilos básicos para mejorar la apariencia */
            body {
                font-family: Arial, sans-serif;
                margin: 20px;
                background-color: #f4f4f4;
                color: #333;
            }
            h2 {
                color: #0056b3;
                margin-bottom: 0px;
            }
            h3 {
                color: #0056b3;
                margin-top: 25px;
                margin-bottom: 15px;
            }
            .user-info {
                margin-bottom: 20px;
                overflow: auto;
            }
            .user-role {
                font-size: 0.9em;
                color: #666;
                margin-top: 5px;
                display: block;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin-bottom: 20px;
                background-color: #fff;
                box-shadow: 0 2px 3px rgba(0,0,0,0.1);
            }
            th, td {
                border: 1px solid #ddd;
                padding: 10px;
                text-align: left;
            }
            th {
                background-color: #007bff;
                color: white;
                text-align: center; /* Centrar texto en los encabezados */
            }
            td {
                vertical-align: top; /* Asegura que el contenido en las celdas se alinee en la parte superior */
            }
            tr:nth-child(even) {
                background-color: #f2f2f2;
            }
            /* Estilo para la fila seleccionada */
            .fila-seleccionada {
                background-color: #e0f7fa !important;
                border: 1px solid #00bcd4;
            }

            .message-success {
                color: green;
                background-color: #d4edda;
                border: 1px solid #28a745;
                padding: 10px;
                margin-bottom: 20px;
                border-radius: 5px;
            }
            .message-error {
                color: red;
                background-color: #f8d7da;
                border: 1px solid #dc3545;
                padding: 10px;
                margin-bottom: 20px;
                border-radius: 5px;
            }
            .form-group {
                margin-bottom: 15px;
            }
            .form-group label {
                display: block;
                margin-bottom: 5px;
                font-weight: bold;
            }
            .form-group input[type="date"],
            .form-group input[type="number"],
            .form-group textarea {
                width: 100%;
                padding: 8px;
                border: 1px solid #ccc;
                border-radius: 4px;
                box-sizing: border-box;
            }
            /* Estilos generales para botones */
            button, .btn-link {
                background-color: #007bff;
                color: white;
                padding: 10px 15px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 16px;
                text-decoration: none;
                display: inline-block;
                text-align: center;
            }
            button:hover, .btn-link:hover {
                background-color: #0056b3;
            }
            .btn-select {
                background-color: #28a745;
                padding: 5px 10px;
                font-size: 0.9em;
            }
            .btn-select:hover {
                background-color: #218838;
            }
            .btn-detail {
                background-color: #3498db;
                color: white;
                padding: 5px 10px;
                text-decoration: none;
                border-radius: 4px;
                margin-left: 10px;
                display: inline-block;
                font-size: 0.9em;
            }
            .btn-detail:hover {
                background-color: #217dbb;
            }
            /* --- Estilos para los estados como botones --- */
            .estado-badge {
                display: inline-block;
                padding: 4px 8px;
                border-radius: 12px;
                font-size: 0.85em;
                font-weight: bold;
                color: white;
                text-align: center;
                min-width: 70px;
            }

            .estado-pendiente {
                background-color: #dc3545; /* Rojo */
            }

            .estado-en-proceso {
                background-color: #ffc107; /* Amarillo */
                color: #333; /* Texto oscuro para contraste */
            }

            .estado-finalizado {
                background-color: #28a745; /* Verde */
            }
            /* --- Fin de estilos para los estados como botones --- */

            /* Estilo para el botón de salir */
            .btn-logout {
                background-color: #dc3545; /* Rojo */
                color: white;
                padding: 10px 15px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 16px;
                text-decoration: none;
                margin-left: 10px; /* Espacio entre los dos botones */
            }
            .btn-logout:hover {
                background-color: #c82333; /* Rojo más oscuro al pasar el mouse */
            }

            /* --- Nuevos estilos para el diseño de 2 columnas del formulario --- */
            .form-layout-container {
                display: flex;
                gap: 20px;
                margin-bottom: 20px;
                flex-wrap: wrap;
                align-items: flex-start;
            }

            .form-fields-column {
                flex: 1; /* Ocupa espacio flexible */
                min-width: 280px; /* Ancho mínimo para la columna del formulario */
                display: flex; /* Habilita flexbox para esta columna */
                flex-direction: column; /* Apila los elementos verticalmente */
                justify-content: space-between; /* Distribuye el espacio entre los elementos */
            }

            .form-details-column {
                flex: 1; /* Ocupa espacio flexible */
                min-width: 280px; /* Ancho mínimo para los detalles */
            }

            #detalleSolicitud {
                padding: 15px;
                border: 1px solid #ccc;
                border-radius: 5px;
                background-color: #f9f9f9;
                height: 100%; /* Asegura que ocupe la altura de la columna */
                min-height: 180px; /* Ajustado para que el cuadro de detalle sea un poco más alto si es necesario */
            }

            .form-action-buttons {
                display: flex;
                justify-content: flex-end; /* Alinea los botones a la derecha */
                gap: 10px; /* Espacio entre los botones */
                margin-top: auto; /* Empuja los botones hacia abajo si hay espacio vertical */
                padding-top: 15px; /* Espacio sobre los botones */
                border-top: 1px solid #eee; /* Separador sutil */
            }
            /* Estilo para el botón Culminar */
            .btn-culminar {
                background-color: #f0ad4e; /* Naranja */
                color: white;
                padding: 5px 10px;
                border: none;
                border-radius: 4px;
                cursor: pointer;
                font-size: 0.9em;
                margin-left: 10px;
            }
            .btn-culminar:hover {
                background-color: #ec971f; /* Naranja más oscuro */
            }
        </style>
        <script>
            let filaSeleccionadaAnterior = null; // Variable global para guardar la fila previamente seleccionada

            function seleccionarSolicitud(id, tipo, motivo, estado, elementoFila) {
                // Eliminar la clase de la fila seleccionada anteriormente
                if (filaSeleccionadaAnterior) {
                    filaSeleccionadaAnterior.classList.remove('fila-seleccionada');
                }

                // Añadir la clase a la fila actual
                elementoFila.classList.add('fila-seleccionada');
                filaSeleccionadaAnterior = elementoFila; // Guardar la fila actual como la seleccionada

                document.getElementById('idSolicitudInput').value = id;
                document.getElementById('idSolicitudDisplay').innerText = id;

                var detalleDiv = document.getElementById('detalleSolicitud');
                detalleDiv.innerHTML = '<b>Detalle solicitud:</b><br/>' +
                        'Tipo: ' + tipo + '<br/>' +
                        'Motivo: ' + motivo + '<br/>' +
                        'Estado: ' + estado;


                // Habilitar/Deshabilitar formulario de actividad
                const fechaInput = document.getElementById('fechaInput');
                const descripcionInput = document.getElementById('descripcionInput');
                const tiempoHorasInput = document.getElementById('tiempoHorasInput');
                
                const btnRegistrarActividad = document.getElementById('btnRegistrarActividad');

                if (estado.toLowerCase() === 'finalizada') {
                    fechaInput.disabled = true;
                    descripcionInput.disabled = true;
                    tiempoHorasInput.disabled = true;
                    // También deshabilitar el botón si la solicitud está finalizada
                    if (btnRegistrarActividad) { 
                       btnRegistrarActividad.disabled = true;
                    }
                    document.getElementById('detalleSolicitud').innerHTML += '<br/><span style="color:red;">Esta solicitud está finalizada. No se pueden registrar más actividades.</span>';
                } else {
                    fechaInput.disabled = false;
                    descripcionInput.disabled = false;
                    tiempoHorasInput.disabled = false;
                    // Habilitar el botón si la solicitud NO está finalizada
                    if (btnRegistrarActividad) { 
                        btnRegistrarActividad.disabled = false;
                    }
                }
            }

            function validarFormulario() {
                var idSolicitud = document.getElementById('idSolicitudInput').value;
                var fecha = document.getElementById('fechaInput').value;
                var descripcion = document.getElementById('descripcionInput').value;
                var tiempoHoras = document.getElementById('tiempoHorasInput').value;

                if (idSolicitud === "") {
                    alert("Por favor, selecciona una solicitud primero.");
                    return false;
                }
                if (fecha === "") {
                    alert("Por favor, ingresa la fecha.");
                    return false;
                }
                if (descripcion.trim() === "") {
                    alert("Por favor, ingresa una descripción para la actividad.");
                    return false;
                }
                if (tiempoHoras === "" || isNaN(tiempoHoras) || parseFloat(tiempoHoras) <= 0) {
                    alert("Por favor, ingresa un tiempo válido en horas (mayor que cero).");
                    return false;
                }
                return true;
            }
        </script>
    </head>
    <body>
        <div class="user-info">
            <h2>Bienvenido, <%= usuario.getNombres() + " " + usuario.getApellidos()%></h2>
            <span class="user-role">(Rol: <%= rolColaborador%>)</span> 
        </div>

        <%-- Mensajes de Éxito y Error --%>
        <% if (mensajeExito != null) {%>
        <div class="message-success">
            <%= mensajeExito%>
        </div>
        <% } %>
        <% if (mensajeError != null) {%>
        <div class="message-error">
            <%= mensajeError%>
        </div>
        <% } %>

        <h3>Solicitudes Asignadas</h3>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Tipo</th>
                    <th>Motivo</th>
                    <th>Fecha Registro</th>
                    <th>Estado</th>
                    <th>Acción</th>
                </tr>
            </thead>
            <tbody>
                <% if (solicitudes.isEmpty()) { %>
                <tr>
                    <td colspan="6" style="text-align:center;">No tienes solicitudes asignadas.</td>
                </tr>
                <% } else { %>
                <% for (Solicitud s : solicitudes) {
                        String estadoClase = "";
                        String estadoTexto = s.getEstado();
                        if ("pendiente".equalsIgnoreCase(estadoTexto)) {
                            estadoClase = "estado-pendiente";
                        } else if ("en proceso".equalsIgnoreCase(estadoTexto)) {
                            estadoClase = "estado-en-proceso";
                        } else if ("finalizada".equalsIgnoreCase(estadoTexto)) {
                            estadoClase = "estado-finalizado";
                        }

                       
                        Boolean esCoordinadorDeEstaSolicitud = (Boolean) request.getAttribute("esCoordinador_" + s.getIdSolicitud());
                        if (esCoordinadorDeEstaSolicitud == null) {
                            esCoordinadorDeEstaSolicitud = false; // Por defecto, si no se pudo determinar
                        }

                %>
                <tr id="solicitud-<%= s.getIdSolicitud()%>">
                    <td><%= s.getIdSolicitud()%></td>
                    <td><%= s.getTipoSolicitud()%></td>
                    <td><%= s.getMotivo()%></td>
                    <td><%= s.getFechaRegistro()%></td>
                    <td>
                        <span class="estado-badge <%= estadoClase%>">
                            <%= estadoTexto%>
                        </span>
                    </td>
                    <td>
                        <button type="button" class="btn-select"
                                onclick="seleccionarSolicitud(
                                <%= s.getIdSolicitud()%>,
                                '<%= s.getTipoSolicitud().replace("'", "\\'")%>',
                                '<%= s.getMotivo().replace("'", "\\'")%>',
                                '<%= s.getEstado().replace("'", "\\'")%>',
                                this.closest('tr')
                                )"
                                <% if ("finalizada".equalsIgnoreCase(s.getEstado())) { %> disabled <% }%> >
                            Seleccionar
                        </button>
                        <a href="detalleSolicitud?idSolicitud=<%= s.getIdSolicitud()%>" target="_blank" class="btn-detail">
                            Ver Detalle
                        </a>
                        
                        <%-- Botón Culminar (Solo si ES COORDINADOR de ESTA solicitud y no está finalizada) --%>
                        <% if (esCoordinadorDeEstaSolicitud && !"finalizada".equalsIgnoreCase(s.getEstado())) {%>
                        <form action="culminarSolicitud" method="post" style="display:inline;">
                            <input type="hidden" name="idSolicitudCulminar" value="<%= s.getIdSolicitud()%>">
                            <button type="submit" class="btn-culminar"
                                    onclick="return confirm('¿Estás seguro de que quieres culminar esta solicitud? Una vez culminada, no se podrán añadir más actividades.');">
                                Culminar
                            </button>    
                        </form> <%-- ¡Etiqueta </form> movida aquí! --%>
                        <% } %> <%-- Cierre del if de esCoordinadorDeEstaSolicitud --%>
                    </td>
                </tr>
                <% } %> <%-- Cierre del for loop --%>
                <% }%> <%-- Cierre del if de solicitudes.isEmpty() --%>
            </tbody>
        </table>

        <h3>Registrar Actividad</h3>
        <form action="colaboradorDashboard" method="post" onsubmit="return validarFormulario()">
            <div class="form-layout-container">
                <div class="form-fields-column">
                    <div class="form-group">
                        <label for="idSolicitudDisplay">Solicitud Seleccionada ID:</label>
                        <span id="idSolicitudDisplay">Ninguna seleccionada</span>
                        <input type="hidden" id="idSolicitudInput" name="idSolicitud"/>
                    </div>
                    <div class="form-group">
                        <label for="fechaInput">Fecha:</label>
                        <input type="date" id="fechaInput" name="fecha" required/>
                    </div>
                    <div class="form-group">
                        <label for="descripcionInput">Descripción:</label>
                        <textarea id="descripcionInput" name="descripcion" rows="4" cols="50" required></textarea>
                    </div>
                    <div class="form-group">
                        <label for="tiempoHorasInput">Tiempo (horas):</label>
                        <input type="number" step="0.1" id="tiempoHorasInput" name="tiempoHoras" min="0.1" required/>
                    </div>
                    <div class="form-action-buttons">
                        <button type="submit" id="btnRegistrarActividad">Registrar Actividad</button>
                        <a href="login.jsp" class="btn-logout">Salir</a>
                    </div>
                </div>
                <div class="form-details-column">
                    <div id="detalleSolicitud">
                        <b>Detalle solicitud:</b><br/>Selecciona una solicitud de la tabla para ver sus detalles aquí.
                    </div>
                </div>
            </div>
        </form>

    </body>
</html>