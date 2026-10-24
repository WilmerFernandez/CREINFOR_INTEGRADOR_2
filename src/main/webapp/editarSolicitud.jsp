
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="utp_integrador_2_model.Solicitud" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    Solicitud solicitudAEditar
            = (Solicitud) request.getAttribute("solicitudAEditar");

    if (solicitudAEditar == null) {
        response.sendRedirect(
                request.getContextPath() + "/listaSolicitudes");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Editar Solicitud - CREINFOR</title>

        <style>
            :root {
                --verde: #188A8D;
                --verde-oscuro: #164b3a;
                --verde-claro: #e8f5ee;
                --fondo: #eef3f3;
                --texto: #263e3c;
                --gris: #718780;
                --borde: #dce8e1;
            }

            * {
                box-sizing: border-box;
            }

            body {
                margin: 0;
                font-family: 'Segoe UI', Arial, sans-serif;
                background: var(--fondo);
                color: var(--texto);
            }

            .header {
                background: linear-gradient(120deg, #164b3a, #24765a);
                padding: 24px 30px;
                color: white;
            }

            .header-content {
                max-width: 1050px;
                margin: auto;
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 20px;
            }

            .brand h1 {
                margin: 0;
                font-size: 27px;
                letter-spacing: .5px;
            }

            .brand p {
                font-size: 12px;
                opacity: .85;
                margin: 5px 0 0;
            }

            .back-header {
                color: white;
                border: 1px solid #ffffff80;
                border-radius: 8px;
                padding: 10px 15px;
                text-decoration: none;
                font-size: 13px;
                transition: .2s;
            }

            .back-header:hover {
                background: #ffffff20;
            }

            /* CONTENEDOR */
            .container {
                max-width: 850px;
                margin: 35px auto;
                padding: 0 20px;
            }

            .page-heading {
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 15px;
                margin-bottom: 23px;
            }

            .page-heading h2 {
                margin: 0 0 7px;
                color: #164b3a;
                font-size: 26px;
            }

            .page-heading p {
                margin: 0;
                font-size: 14px;
                color: var(--gris);
            }

            .badge-id {
                padding: 10px 16px;
                background: #dceee5;
                color: #176044;
                border-radius: 20px;
                font-weight: 700;
                font-size: 13px;
                white-space: nowrap;
            }

            /* TARJETA */
            .card {
                background: white;
                border-radius: 15px;
                box-shadow: 0 6px 25px rgba(0,0,0,.055);
                border: 1px solid var(--borde);
                overflow: hidden;
            }

            .card-header {
                padding: 21px 26px;
                border-bottom: 1px solid var(--borde);
            }

            .card-header h3 {
                margin: 0 0 5px;
                color: #164b3a;
                font-size: 18px;
            }

            .card-header p {
                margin: 0;
                font-size: 12px;
                color: var(--gris);
            }

            .card-body {
                padding: 28px 26px;
            }

            /* FORMULARIO */
            .form-group {
                margin-bottom: 24px;
            }

            .form-group label {
                display: block;
                font-weight: 650;
                font-size: 14px;
                margin-bottom: 10px;
                color: #254b3b;
            }

            .form-control {
                display: block;
                width: 100%;
                padding: 14px;
                background: #fafcfb;
                border: 1px solid #ccdcd3;
                border-radius: 9px;
                font-family: inherit;
                font-size: 14px;
                color: #263e3c;
                outline: none;
                transition: .2s;
            }

            .form-control:focus {
                background: white;
                border-color: #188A8D;
                box-shadow: 0 0 0 3px #188A8D20;
            }

            textarea.form-control {
                min-height: 165px;
                resize: vertical;
                line-height: 1.6;
            }

            .help-text {
                margin-top: 8px;
                color: #82968c;
                font-size: 12px;
            }

            /* INFORMACIÓN */
            .info-box {
                display: flex;
                gap: 12px;
                background: #f0f8f4;
                border: 1px solid #d4eadb;
                padding: 17px;
                border-radius: 10px;
                margin-bottom: 25px;
                color: #34664c;
                font-size: 13px;
                line-height: 1.6;
            }

            .info-icon {
                font-size: 20px;
                flex-shrink: 0;
            }

            /* ACCIONES */
            .form-actions {
                display: flex;
                justify-content: flex-end;
                align-items: center;
                gap: 12px;
                padding-top: 20px;
                border-top: 1px solid var(--borde);
            }

            .btn {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                gap: 8px;
                padding: 13px 20px;
                border-radius: 9px;
                border: 0;
                font-family: inherit;
                font-size: 14px;
                font-weight: 650;
                text-decoration: none;
                cursor: pointer;
                transition: .2s;
            }

            .btn-primary {
                background: #188A8D;
                color: white;
            }

            .btn-primary:hover {
                background: #116e72;
                transform: translateY(-2px);
            }

            .btn-secondary {
                background: #edf2f0;
                color: #455e53;
            }

            .btn-secondary:hover {
                background: #dfe8e3;
            }

            .btn:disabled {
                opacity: .65;
                cursor: wait;
                transform: none;
            }

            /* ERRORES */
            .error-message {
                background: #ffebee;
                color: #b42332;
                padding: 15px;
                border-radius: 9px;
                margin-bottom: 20px;
                font-size: 13px;
            }

            /* RESPONSIVE */
            @media (max-width: 600px) {
                .header-content {
                    flex-wrap: wrap;
                }

                .page-heading {
                    align-items: flex-start;
                }

                .card-body {
                    padding: 20px;
                }

                .form-actions {
                    flex-direction: column-reverse;
                }

                .form-actions .btn {
                    width: 100%;
                }
            }
        </style>
    </head>

    <body>

        <!-- ENCABEZADO -->
        <header class="header">
            <div class="header-content">

                <div class="brand">
                    <h1>CREINFOR</h1>
                    <p>
                        Sistema de Gestión de Soporte Técnico
                        · Solicitudes
                    </p>
                </div>

                <a href="${pageContext.request.contextPath}/listaSolicitudes"
                   class="back-header">
                    ← Volver a mis solicitudes
                </a>

            </div>
        </header>

        <main class="container">

            <!-- TITULO -->
            <div class="page-heading">
                <div>
                    <h2>Editar solicitud</h2>
                    <p>
                        Actualiza la información de tu solicitud
                        antes de que sea atendida.
                    </p>
                </div>

                <span class="badge-id">
                    Solicitud #<c:out value="${solicitudAEditar.idSolicitud}"/>
                </span>
            </div>

            <!-- MENSAJES -->
            <c:if test="${not empty errorMessage}">
                <div class="error-message">
                    <c:out value="${errorMessage}"/>
                </div>
            </c:if>

            <!-- TARJETA -->
            <section class="card">

                <div class="card-header">
                    <h3>Información de la solicitud</h3>
                    <p>
                        Modifica los datos necesarios
                        y guarda los cambios.
                    </p>
                </div>

                <div class="card-body">

                   

                    <form id="formEditar"
                          action="${pageContext.request.contextPath}/listaSolicitudes"
                          method="post"
                          accept-charset="UTF-8">

                        <input type="hidden"
                               name="action"
                               value="actualizar">

                        <input type="hidden"
                               name="idSolicitud"
                               value="${solicitudAEditar.idSolicitud}">

                        <div class="form-group">

                            <label for="tipoSolicitud">
                                Tipo de solicitud
                            </label>

                            <select id="tipoSolicitud"
                                    name="tipoSolicitud"
                                    class="form-control"
                                    required>

                                <option value="error"
                                        ${solicitudAEditar.tipoSolicitud eq 'error' ? 'selected' : ''}>
                                    Error de software
                                </option>

                                <option value="capacitación"
                                        ${solicitudAEditar.tipoSolicitud eq 'capacitación' ? 'selected' : ''}>
                                    Capacitación
                                </option>

                                <option value="requerimiento"
                                        ${solicitudAEditar.tipoSolicitud eq 'requerimiento' ? 'selected' : ''}>
                                    Nuevo requerimiento
                                </option>

                            </select>
                        </div>

                        <div class="form-group">

                            <label for="motivo">
                                Descripción de la solicitud
                            </label>

                            <textarea id="motivo"
                                      name="motivo"
                                      class="form-control"
                                      maxlength="2000"
                                      rows="6"
                                      required><c:out value="${solicitudAEditar.motivo}"/></textarea>

                            <div class="help-text">
                                Describe claramente tu necesidad.
                                Máximo 2000 caracteres.
                            </div>

                        </div>

                        <div class="form-actions">

                            <a href="${pageContext.request.contextPath}/listaSolicitudes"
                               class="btn btn-secondary">
                                ← Cancelar
                            </a>

                            <button type="submit"
                                    class="btn btn-primary"
                                    id="btnGuardar">
                                ✓ Guardar cambios
                            </button>

                        </div>

                    </form>

                </div>
            </section>

        </main>

        <script>
            const formulario = document.getElementById("formEditar");
            const boton = document.getElementById("btnGuardar");

            let enviando = false;

            formulario.addEventListener("submit", function (event) {

                if (enviando) {
                    event.preventDefault();
                    return;
                }

                enviando = true;
                boton.disabled = true;
                boton.textContent = "Guardando cambios...";
            });
        </script>

    </body>
</html>
