<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="utp_integrador_2_model.Usuario" %>
<%@ page import="utp_integrador_2_model.Solicitud" %>
<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");

    if (usuario == null
            || !"colaborador".equalsIgnoreCase(usuario.getTipoUsuario())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String rolColaborador
            = (String) request.getAttribute("rolColaborador");

    if (rolColaborador == null) {
        rolColaborador = "No especificado";
    }

    List<Solicitud> solicitudes
            = (List<Solicitud>) request.getAttribute("solicitudes");

    if (solicitudes == null) {
        solicitudes = new ArrayList<>();
    }

    String mensajeExito
            = (String) request.getAttribute("mensajeExito");

    String mensajeError
            = (String) request.getAttribute("mensajeError");

    Integer idColaboradorLogeado
            = (Integer) request.getAttribute("idColaboradorLogeado");

    if (idColaboradorLogeado == null) {
        idColaboradorLogeado = -1;
    }

    int pendientes = 0;
    int enProceso = 0;
    int finalizadas = 0;

    for (Solicitud s : solicitudes) {
        if ("pendiente".equalsIgnoreCase(s.getEstado())) {
            pendientes++;
        } else if ("en proceso".equalsIgnoreCase(s.getEstado())) {
            enProceso++;
        } else if ("finalizada".equalsIgnoreCase(s.getEstado())) {
            finalizadas++;
        }
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Panel del Colaborador - CREINFOR</title>

        <style>
            :root {
                --navy: #173b50;
                --navy-dark: #102d3e;
                --teal: #168c91;
                --teal-dark: #107177;
                --bg: #eef3f6;
                --text: #253746;
                --muted: #718596;
                --line: #e3ebef;
            }

            * {
                box-sizing: border-box;
            }

            body {
                margin: 0;
                background: var(--bg);
                color: var(--text);
                font-family: 'Segoe UI', Arial, sans-serif;
                font-size: 14px;
            }

            /* CABECERA */
            .header {
                background: linear-gradient(135deg, #127174, #3d78b8);
                color: white;
                padding: 22px 30px;
            }

            .header-inner {
                max-width: 1250px;
                margin: auto;
                display: flex;
                align-items: center;
                justify-content: space-between;
                gap: 20px;
            }

            .header h1 {
                margin: 0;
                font-size: 26px;
                letter-spacing: .5px;
            }

            .header p {
                margin: 5px 0 0;
                font-size: 12px;
                color: #d6e5ed;
            }

            .header-logout {
                color: white;
                border: 1px solid #68899b;
                border-radius: 7px;
                padding: 10px 16px;
                text-decoration: none;
                font-weight: 600;
                white-space: nowrap;
            }

            .header-logout:hover {
                background: rgba(255,255,255,.12);
            }

            .main {
                max-width: 1250px;
                margin: 30px auto;
                padding: 0 20px;
            }

            /* BIENVENIDA */
            .welcome {
                margin-bottom: 24px;
            }

            .welcome h2 {
                margin: 0 0 7px;
                font-size: 25px;
                color: var(--navy);
            }

            .welcome p {
                margin: 0;
                font-size: 13px;
                color: var(--muted);
            }

            .role-badge {
                display: inline-block;
                background: #dcecf1;
                color: #176077;
                border-radius: 16px;
                padding: 5px 11px;
                margin-left: 5px;
                font-weight: 700;
                text-transform: capitalize;
            }

            /* TARJETAS DE RESUMEN */
            .stats {
                display: grid;
                grid-template-columns: repeat(4, 1fr);
                gap: 16px;
                margin-bottom: 24px;
            }

            .stat-card {
                background: white;
                padding: 20px;
                border-radius: 12px;
                border-left: 4px solid var(--teal);
                box-shadow: 0 5px 18px rgba(0,0,0,.05);
            }

            .stat-card p {
                margin: 0 0 8px;
                color: var(--muted);
                font-size: 13px;
            }

            .stat-card strong {
                font-size: 26px;
                color: var(--navy);
            }

            .stat-card.warning {
                border-left-color: #e5a23a;
            }

            .stat-card.success {
                border-left-color: #31a26b;
            }

            .stat-card.info {
                border-left-color: #388ed1;
            }

            /* TARJETAS GENERALES */
            .card {
                background: #ffffff;
                border-radius: 14px;
                box-shadow: 0 5px 20px rgba(0,0,0,.055);
                overflow: hidden;
                margin-bottom: 24px;
            }

            .card-header {
                padding: 20px 24px;
                border-bottom: 1px solid var(--line);
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 12px;
            }

            .card-header h3 {
                margin: 0;
                font-size: 18px;
                color: var(--navy);
            }

            .card-header p {
                margin: 6px 0 0;
                color: var(--muted);
                font-size: 12px;
            }

            .card-body {
                padding: 23px 24px;
            }

            /* TABLA */
            .table-responsive {
                width: 100%;
                overflow-x: auto;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                min-width: 850px;
            }

            th {
                background: #f2f6f9;
                color: #3e5667;
                text-align: left;
                padding: 13px;
                font-size: 12px;
                font-weight: 700;
                border-bottom: 2px solid var(--line);
            }

            td {
                padding: 14px 13px;
                border-bottom: 1px solid var(--line);
                vertical-align: middle;
                font-size: 13px;
            }

            tbody tr:hover {
                background: #f7fbfc;
            }

            tbody tr.fila-seleccionada {
                background: #e3f5f4 !important;
            }

            .motivo-columna {
                max-width: 290px;
                min-width: 200px;
                overflow-wrap: anywhere;
            }

            /* ESTADOS */
            .estado-badge {
                display: inline-block;
                padding: 6px 11px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 700;
                white-space: nowrap;
            }

            .estado-pendiente {
                background: #fff0d5;
                color: #a3680a;
            }

            .estado-en-proceso {
                background: #e0f0ff;
                color: #1769aa;
            }

            .estado-finalizado {
                background: #e0f5e9;
                color: #21834f;
            }

            /* BOTONES */
            .btn {
                display: inline-block;
                border: 0;
                padding: 9px 13px;
                border-radius: 7px;
                color: white;
                font-size: 12px;
                font-weight: 650;
                cursor: pointer;
                text-decoration: none;
                white-space: nowrap;
                transition: .2s;
            }

            .btn-select {
                background: #168c91;
            }

            .btn-select:hover {
                background: #107177;
            }

            .btn-detail {
                background: #388ed1;
            }

            .btn-detail:hover {
                background: #2674b1;
            }

            .btn-culminar {
                background: #eca33c;
                color: #432b06;
            }

            .btn-culminar:hover {
                background: #da912b;
            }

            .btn:disabled {
                opacity: .45;
                cursor: not-allowed;
            }

            .acciones-tabla {
                display: flex;
                flex-wrap: wrap;
                gap: 7px;
                align-items: center;
            }

            .acciones-tabla form {
                margin: 0;
            }

            /* FORMULARIO */
            .form-layout-container {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 25px;
                align-items: stretch;
            }

            .form-group {
                margin-bottom: 17px;
            }

            .form-group label {
                display: block;
                font-weight: 650;
                font-size: 13px;
                margin-bottom: 8px;
                color: #3c5364;
            }

            .form-group input,
            .form-group textarea {
                display: block;
                width: 100%;
                padding: 12px 13px;
                border: 1px solid #d0dce4;
                border-radius: 8px;
                background: #fff;
                color: var(--text);
                font: inherit;
                outline: none;
            }

            .form-group textarea {
                min-height: 115px;
                resize: vertical;
            }

            .form-group input:focus,
            .form-group textarea:focus {
                border-color: var(--teal);
                box-shadow: 0 0 0 3px rgba(22,140,145,.10);
            }

            .form-group input:disabled,
            .form-group textarea:disabled {
                background: #f1f4f6;
            }

            .selected-box {
                background: #edf8f7;
                border: 1px solid #c9e9e6;
                border-radius: 8px;
                padding: 12px 14px;
                font-weight: 650;
                color: var(--navy);
            }

            #detalleSolicitud {
                background: #f7fafc;
                border: 1px solid #dce6ed;
                border-radius: 10px;
                padding: 20px;
                min-height: 260px;
                line-height: 1.8;
                overflow-wrap: anywhere;
            }

            .form-action-buttons {
                display: flex;
                flex-wrap: wrap;
                gap: 12px;
                justify-content: flex-end;
                margin-top: 20px;
            }

            .btn-register {
                background: var(--teal);
                padding: 12px 22px;
                font-size: 14px;
            }

            .btn-register:hover {
                background: var(--teal-dark);
            }

            /* MENSAJES */
            .message-success,
            .message-error {
                padding: 14px 17px;
                border-radius: 9px;
                margin-bottom: 22px;
                font-size: 14px;
            }

            .message-success {
                background: #e7f6eb;
                border: 1px solid #b8e4c4;
                color: #236c39;
            }

            .message-error {
                background: #ffebee;
                border: 1px solid #f4bfc7;
                color: #ac293b;
            }

            .alerta-exito {
                background: #e5f5ea;
                color: #207743;
                padding: 15px;
                border-radius: 9px;
                margin: 15px 0;
            }

            .empty {
                text-align: center;
                padding: 35px;
                color: var(--muted);
            }

            .footer {
                text-align: center;
                padding: 10px 20px 30px;
                color: #93a4b0;
                font-size: 12px;
            }

            @media (max-width: 900px) {
                .stats {
                    grid-template-columns: repeat(2, 1fr);
                }

                .form-layout-container {
                    grid-template-columns: 1fr;
                }
            }

            @media (max-width: 550px) {
                .header-inner {
                    align-items: flex-start;
                    flex-direction: column;
                }

                .stats {
                    grid-template-columns: 1fr 1fr;
                }

                .main {
                    padding: 0 12px;
                }

                .card-body {
                    padding: 16px;
                }

                .card-header {
                    padding: 17px;
                }
            }

            .btn-informe {
                display: inline-block;
                background: #188A8D;
                color: white;
                padding: 10px 13px;
                border-radius: 7px;
                text-decoration: none;
                font-size: 12px;
                font-weight: 600;
            }

            .btn-informe:hover {
                background: #116e72;
                color: white;
            }

            .estado-informe {
                display: inline-block;
                padding: 9px 12px;
                border-radius: 7px;
                font-size: 12px;
                font-weight: 600;
                white-space: nowrap;
            }

            .estado-informe.enviado {
                background: #e1f5e7;
                color: #207e46;
            }

            .estado-informe.procesando {
                background: #e0efff;
                color: #1769aa;
            }

            .estado-informe.error-envio {
                background: #fff3cd;
                color: #916200;
            }

            .estado-informe.sin-informe {
                background: #edf0f4;
                color: #718096;
            }


            /* BOTÓN PARA REINTENTAR ENVÍO */

            .btn-reintentar-informe {
                display: inline-block;
                background: #e9a23b;
                color: #3d2908;
                padding: 10px 13px;
                border-radius: 7px;
                text-decoration: none;
                font-size: 12px;
                font-weight: 650;
                transition: background 0.2s ease;
            }

            .btn-reintentar-informe:hover {
                background: #d68a20;
                color: #ffffff;
            }

        </style>
    </head>

    <body>

        <!-- FRANJA SUPERIOR -->
        <header class="header">
            <div class="header-inner">
                <div>
                    <h1>CREINFOR</h1>
                    <p>
                        Sistema de Gestión de Soporte Técnico ·
                        Panel del colaborador
                    </p>
                </div>

                <a href="${pageContext.request.contextPath}/login.jsp"
                   class="header-logout">
                    Salir
                </a>
            </div>
        </header>

        <main class="main">

            <!-- BIENVENIDA -->
            <div class="welcome">
                <h2>
                    Bienvenido,
                    <c:out value="${sessionScope.usuario.nombres}"/>
                    <c:out value="${sessionScope.usuario.apellidos}"/>
                </h2>

                <p>
                    Especialidad:
                    <span class="role-badge">
                        <c:out value="${rolColaborador}"/>
                    </span>
                </p>
            </div>

            <!-- MENSAJES -->

            <c:if test="${not empty mensajeError}">
                <div class="message-error">
                    <c:out value="${mensajeError}"/>
                </div>
            </c:if>

            <c:if test="${not empty sessionScope.mensajeExito}">
                <div class="alerta-exito">
                    <c:out value="${sessionScope.mensajeExito}"/>
                </div>
                <c:remove var="mensajeExito" scope="session"/>
            </c:if>

            <!-- RESUMEN -->
            <div class="stats">

                <div class="stat-card">
                    <p>Total de solicitudes</p>
                    <strong><%= solicitudes.size()%></strong>
                </div>

                <div class="stat-card warning">
                    <p>Pendientes</p>
                    <strong><%= pendientes%></strong>
                </div>

                <div class="stat-card info">
                    <p>En proceso</p>
                    <strong><%= enProceso%></strong>
                </div>

                <div class="stat-card success">
                    <p>Finalizadas</p>
                    <strong><%= finalizadas%></strong>
                </div>

            </div>

            <!-- SOLICITUDES -->
            <section class="card">

                <div class="card-header">
                    <div>
                        <h3>Solicitudes asignadas</h3>
                        <p>Consulta los trabajos que tienes a cargo.</p>
                    </div>
                </div>

                <div class="card-body">

                    <div class="table-responsive">
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Tipo</th>
                                    <th>Motivo</th>
                                    <th>Fecha de registro</th>
                                    <th>Estado</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>

                            <tbody>

                                <% if (solicitudes.isEmpty()) { %>

                                <tr>
                                    <td colspan="6" class="empty">
                                        No tienes solicitudes asignadas.
                                    </td>
                                </tr>

                                <% } else {
                                    for (Solicitud s : solicitudes) {

                                        String estadoTexto = s.getEstado();
                                        String estadoClase = "";

                                        if ("pendiente".equalsIgnoreCase(estadoTexto)) {
                                            estadoClase = "estado-pendiente";
                                        } else if ("en proceso".equalsIgnoreCase(estadoTexto)) {
                                            estadoClase = "estado-en-proceso";
                                        } else if ("finalizada".equalsIgnoreCase(estadoTexto)) {
                                            estadoClase = "estado-finalizado";
                                        }

                                        Boolean esCoordinador
                                                = (Boolean) request.getAttribute(
                                                        "esCoordinador_" + s.getIdSolicitud()
                                                );

                                        if (esCoordinador == null) {
                                            esCoordinador = false;
                                        }
                                %>

                                <tr id="solicitud-<%= s.getIdSolicitud()%>">

                                    <td>
                                        <strong>#<%= s.getIdSolicitud()%></strong>
                                    </td>

                                    <td>
                                        <%= s.getTipoSolicitud()%>
                                    </td>

                                    <td class="motivo-columna">
                                        <c:out value="<%= s.getMotivo()%>"/>
                                    </td>

                                    <td>
                                        <%= s.getFechaRegistro()%>
                                    </td>

                                    <td>
                                        <span class="estado-badge <%= estadoClase%>">
                                            <%= estadoTexto%>
                                        </span>
                                    </td>

                                    <td>
                                        <div class="acciones-tabla">

                                            <button type="button"
                                                    class="btn btn-select seleccionar-solicitud"
                                                    data-id="<%= s.getIdSolicitud()%>"
                                                    data-tipo="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(s.getTipoSolicitud())%>"
                                                    data-motivo="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(s.getMotivo())%>"
                                                    data-estado="<%= org.apache.taglibs.standard.functions.Functions.escapeXml(s.getEstado())%>"
                                                    <% if ("finalizada".equalsIgnoreCase(s.getEstado())) { %>
                                                    disabled
                                                    <% }%>>
                                                Seleccionar
                                            </button>

                                            <a href="${pageContext.request.contextPath}/detalleSolicitud?idSolicitud=<%= s.getIdSolicitud()%>"
                                               target="_blank"
                                               rel="noopener"
                                               class="btn btn-detail">
                                                Ver detalle
                                            </a>



                                            <%
                                                String estadoInforme = (String) request.getAttribute(
                                                        "estadoInforme_" + s.getIdSolicitud()
                                                );
                                            %>

                                            <% if (esCoordinador) { %>

                                            <% if ("en proceso".equalsIgnoreCase(s.getEstado())) {%>

                                            <a href="${pageContext.request.contextPath}/culminarSolicitud?idSolicitudCulminar=<%= s.getIdSolicitud()%>"
                                               class="btn btn-culminar">
                                                Culminar
                                            </a>

                                            <% } else if ("finalizada".equalsIgnoreCase(s.getEstado())) { %>

                                            <% if ("pendiente".equalsIgnoreCase(estadoInforme)) {%>

                                            <a href="${pageContext.request.contextPath}/informes/enviar?idSolicitud=<%= s.getIdSolicitud()%>"
                                               class="btn btn-informe">
                                                Enviar informe
                                            </a>

                                            <% } else if ("enviado".equalsIgnoreCase(estadoInforme)) { %>

                                            <span class="estado-informe enviado">
                                                &#10003; Informe enviado
                                            </span>

                                            <% } else if ("enviando".equalsIgnoreCase(estadoInforme)) { %>

                                            <span class="estado-informe procesando">
                                                Envío en proceso
                                            </span>


                                            <% } else if ("error_envio".equalsIgnoreCase(estadoInforme)) {%>

                                            <a href="${pageContext.request.contextPath}/informes/enviar?idSolicitud=<%= s.getIdSolicitud()%>"
                                               class="btn btn-reintentar-informe"
                                               title="Volver a intentar el envío del informe técnico">
                                                &#8635; Reintentar envío
                                            </a>


                                            <% } else { %>

                                            <span class="estado-informe sin-informe">
                                                Sin informe técnico
                                            </span>

                                            <% } %>

                                            <% } %>

                                            <% } %>



                                        </div>
                                    </td>

                                </tr>

                                <%  }
                                    }%>

                            </tbody>
                        </table>
                    </div>

                </div>
            </section>

            <!-- REGISTRAR ACTIVIDAD -->
            <section class="card">

                <div class="card-header">
                    <div>
                        <h3>Registrar actividad</h3>
                        <p>
                            Selecciona una solicitud y registra
                            el trabajo realizado.
                        </p>
                    </div>
                </div>

                <div class="card-body">

                    <form action="${pageContext.request.contextPath}/colaboradorDashboard"
                          method="post"
                          onsubmit="return validarFormulario()">

                        <div class="form-layout-container">

                            <div class="form-fields-column">

                                <div class="form-group">
                                    <label>Solicitud seleccionada</label>

                                    <div class="selected-box">
                                        ID:
                                        <span id="idSolicitudDisplay">
                                            Ninguna seleccionada
                                        </span>
                                    </div>

                                    <input type="hidden"
                                           id="idSolicitudInput"
                                           name="idSolicitud">
                                </div>

                                <div class="form-group">
                                    <label for="fechaInput">Fecha</label>

                                    <input type="date"
                                           id="fechaInput"
                                           name="fecha"
                                           required>
                                </div>

                                <div class="form-group">
                                    <label for="descripcionInput">
                                        Descripción de la actividad
                                    </label>

                                    <textarea id="descripcionInput"
                                              name="descripcion"
                                              rows="4"
                                              placeholder="Describe el trabajo realizado..."
                                              required></textarea>
                                </div>

                                <div class="form-group">
                                    <label for="tiempoHorasInput">
                                        Tiempo trabajado (horas)
                                    </label>

                                    <input type="number"
                                           id="tiempoHorasInput"
                                           name="tiempoHoras"
                                           min="0.1"
                                           step="0.1"
                                           placeholder="Ejemplo: 2.5"
                                           required>
                                </div>

                                <div class="form-action-buttons">

                                    <button type="submit"
                                            id="btnRegistrarActividad"
                                            class="btn btn-register">
                                        Registrar actividad
                                    </button>

                                </div>

                            </div>

                            <div class="form-details-column">
                                <div id="detalleSolicitud">
                                    <strong>Detalle de la solicitud</strong>
                                    <p>
                                        Selecciona una solicitud de la tabla
                                        para ver sus detalles aquí.
                                    </p>
                                </div>
                            </div>

                        </div>

                    </form>

                </div>
            </section>

        </main>

        <footer class="footer">
            CREINFOR © 2026 · Sistema de Gestión de Soporte Técnico
        </footer>

        <script>
            let filaSeleccionadaAnterior = null;

            document.querySelectorAll(".seleccionar-solicitud")
                    .forEach(function (boton) {

                        boton.addEventListener("click", function () {

                            const fila = this.closest("tr");

                            seleccionarSolicitud(
                                    this.dataset.id,
                                    this.dataset.tipo,
                                    this.dataset.motivo,
                                    this.dataset.estado,
                                    fila
                                    );
                        });
                    });

            function seleccionarSolicitud(id, tipo, motivo, estado, fila) {

                if (filaSeleccionadaAnterior) {
                    filaSeleccionadaAnterior.classList.remove("fila-seleccionada");
                }

                fila.classList.add("fila-seleccionada");
                filaSeleccionadaAnterior = fila;

                document.getElementById("idSolicitudInput").value = id;
                document.getElementById("idSolicitudDisplay").textContent = id;

                const detalle = document.getElementById("detalleSolicitud");
                detalle.textContent = "";

                const titulo = document.createElement("strong");
                titulo.textContent = "Detalle de la solicitud";

                const contenido = document.createElement("p");
                contenido.style.whiteSpace = "pre-wrap";

                contenido.textContent =
                        "Tipo: " + tipo + "\n" +
                        "Motivo: " + motivo + "\n" +
                        "Estado: " + estado;

                detalle.appendChild(titulo);
                detalle.appendChild(contenido);

                const finalizada = estado.toLowerCase() === "finalizada";

                document.getElementById("fechaInput").disabled = finalizada;
                document.getElementById("descripcionInput").disabled = finalizada;
                document.getElementById("tiempoHorasInput").disabled = finalizada;

                document.getElementById("btnRegistrarActividad").disabled = finalizada;

                if (finalizada) {
                    const aviso = document.createElement("p");
                    aviso.textContent =
                            "Esta solicitud está finalizada. No se pueden registrar más actividades.";
                    aviso.style.color = "#b42332";
                    detalle.appendChild(aviso);
                }
            }

            function validarFormulario() {

                const id = document.getElementById("idSolicitudInput").value;
                const fecha = document.getElementById("fechaInput").value;
                const descripcion =
                        document.getElementById("descripcionInput").value;
                const horas = document.getElementById("tiempoHorasInput").value;

                if (!id) {
                    alert("Primero selecciona una solicitud.");
                    return false;
                }

                if (!fecha) {
                    alert("Ingresa la fecha de la actividad.");
                    return false;
                }

                if (!descripcion.trim()) {
                    alert("Ingresa una descripción de la actividad.");
                    return false;
                }

                if (!horas || isNaN(horas) || parseFloat(horas) <= 0) {
                    alert("Ingresa un tiempo válido mayor que cero.");
                    return false;
                }

                return true;
            }
        </script>

    </body>
</html>