
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Nueva Cotización - CREINFOR</title>

        <style>
            :root {
                --navy: #173b50;
                --teal: #188A8D;
                --blue: #4E90D6;
                --bg: #eef3f7;
                --text: #293e4c;
                --line: #e1e9ed;
            }

            * {
                box-sizing: border-box;
            }

            body {
                margin: 0;
                background: var(--bg);
                color: var(--text);
                font-family: 'Segoe UI', Arial, sans-serif;
            }

            .header {
                background: linear-gradient(120deg, #188A8D, #4E90D6);
                color: white;
                padding: 22px 30px;
            }

            .header-inner {
                max-width: 1150px;
                margin: auto;
            }

            .header h1 {
                font-size: 26px;
                margin: 0;
            }

            .header p {
                margin: 5px 0 0;
                font-size: 13px;
            }

            .container {
                max-width: 1150px;
                margin: 30px auto;
                padding: 0 20px;
            }

            .page-title {
                margin-bottom: 22px;
            }

            .page-title h2 {
                margin: 0 0 6px;
                color: var(--navy);
            }

            .page-title p {
                margin: 0;
                color: #758795;
                font-size: 13px;
            }

            .card {
                background: white;
                border-radius: 14px;
                box-shadow: 0 5px 20px rgba(0,0,0,.06);
                margin-bottom: 22px;
                overflow: hidden;
            }

            .card-header {
                border-bottom: 1px solid var(--line);
                padding: 18px 24px;
                font-size: 17px;
                font-weight: 700;
                color: var(--navy);
            }

            .card-body {
                padding: 24px;
            }

            .row {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 20px;
            }

            .field {
                margin-bottom: 18px;
            }

            label {
                display: block;
                font-size: 13px;
                font-weight: 650;
                margin-bottom: 8px;
            }

            input, select, textarea {
                width: 100%;
                padding: 11px 12px;
                border: 1px solid #cedae2;
                border-radius: 8px;
                font: inherit;
                font-size: 14px;
                background: white;
            }

            input:focus, select:focus, textarea:focus {
                outline: none;
                border-color: var(--teal);
                box-shadow: 0 0 0 3px #188a8d19;
            }

            textarea {
                resize: vertical;
                min-height: 90px;
            }

            .table-wrap {
                overflow-x: auto;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                min-width: 600px;
            }

            th {
                padding: 13px;
                text-align: left;
                background: #f1f6f9;
                font-size: 13px;
                color: #466071;
            }

            td {
                padding: 11px;
                border-bottom: 1px solid var(--line);
            }

            .importe {
                font-weight: 700;
                color: var(--navy);
                white-space: nowrap;
            }

            .btn {
                display: inline-block;
                padding: 11px 17px;
                border-radius: 8px;
                border: none;
                font-weight: 650;
                cursor: pointer;
                font-size: 13px;
                text-decoration: none;
            }

            .btn-primary {
                background: var(--teal);
                color: white;
            }

            .btn-primary:hover {
                background: #126e71;
            }

            .btn-secondary {
                background: #eaf0f4;
                color: #385366;
            }

            .btn-danger {
                background: #fee6e6;
                color: #b62e35;
            }

            .btn:disabled {
                opacity: .6;
                cursor: not-allowed;
            }

            .totales {
                display: flex;
                justify-content: flex-end;
                padding: 24px;
                border-top: 1px solid var(--line);
            }

            .total-box {
                min-width: 260px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 15px;
            }

            .total-box strong {
                color: #116c70;
                font-size: 28px;
            }

            .actions {
                display: flex;
                justify-content: flex-end;
                gap: 12px;
                flex-wrap: wrap;
                margin-bottom: 35px;
            }

            .mensaje {
                border-radius: 8px;
                padding: 14px;
                margin-bottom: 20px;
                font-size: 14px;
            }

            .error {
                background: #ffebee;
                color: #b42332;
            }

            .success {
                background: #e5f5e9;
                color: #207743;
            }

            .footer {
                text-align: center;
                color: #91a2b0;
                font-size: 12px;
                padding: 25px;
            }

            @media (max-width: 650px) {
                .row {
                    grid-template-columns: 1fr;
                }
                .card-body {
                    padding: 16px;
                }
            }
        </style>

        <link rel="stylesheet"
              href="https://cdn.jsdelivr.net/npm/tom-select@2.6.2/dist/css/tom-select.css">
    </head>

    <body>

        <header class="header">
            <div class="header-inner">
                <h1>CREINFOR</h1>
                <p>Sistema de Gestión de Soporte Técnico · Cotizaciones</p>
            </div>
        </header>

        <main class="container">

            <div class="page-title">
                <h2>Nueva cotización</h2>
                <p>Elabora una propuesta económica para una solicitud.</p>
            </div>

            <c:if test="${not empty mensajeError}">
                <div class="mensaje error">
                    <c:out value="${mensajeError}"/>
                </div>
            </c:if>

            <c:if test="${not empty param.creada}">
                <div class="mensaje success">
                    Cotización #<c:out value="${param.creada}"/>
                    guardada correctamente como borrador.
                </div>
            </c:if>

            <form id="formCotizacion"
                  action="${pageContext.request.contextPath}/cotizaciones/nueva"
                  method="post">

                <section class="card">
                    <div class="card-header">Datos generales</div>

                    <div class="card-body">
                        <div class="row">

                            <div class="field">
                                <label for="idSolicitud">Solicitud</label>

                                <select id="solicitudBuscador" name="idSolicitud" required>
                                    <option value="">Seleccione una solicitud</option>

                                    <c:forEach var="s" items="${solicitudes}">
                                        <option value="${s.idSolicitud}">
                                            #<c:out value="${s.idSolicitud}"/>
                                            -
                                            <c:out value="${s.nombreCliente}"/>
                                            -
                                            <c:out value="${s.tipoSolicitud}"/>
                                            -
                                            <c:out value="${s.motivo}"/>
                                        </option>
                                    </c:forEach>
                                </select>

                            </div>

                            <div class="field">
                                <label for="fechaVencimiento">
                                    Válida hasta
                                </label>

                                <input type="date"
                                       id="fechaVencimiento"
                                       name="fechaVencimiento"
                                       required>
                            </div>

                        </div>

                        <div class="field">
                            <label for="observaciones">
                                Observaciones de la cotización
                            </label>

                            <textarea id="observaciones"
                                      name="observaciones"
                                      placeholder="Indica el alcance y las condiciones del servicio..."></textarea>
                        </div>

                    </div>
                </section>

                <section class="card">

                    <div class="card-header">
                        Conceptos de la cotización
                    </div>

                    <div class="card-body">
                        <div class="table-wrap">

                            <table>
                                <thead>
                                    <tr>
                                        <th>Descripción</th>
                                        <th>Cantidad</th>
                                        <th>Precio unitario (S/)</th>
                                        <th>Subtotal</th>
                                        <th>Acción</th>
                                    </tr>
                                </thead>

                                <tbody id="detalleBody">

                                    <tr>
                                        <td>
                                            <input type="text"
                                                   name="descripcion"
                                                   placeholder="Ej. Desarrollo de software"
                                                   maxlength="255"
                                                   required>
                                        </td>

                                        <td>
                                            <input type="number"
                                                   name="cantidad"
                                                   value="1"
                                                   min="0.01"
                                                   step="0.01"
                                                   required>
                                        </td>

                                        <td>
                                            <input type="number"
                                                   name="precioUnitario"
                                                   value="0"
                                                   min="0"
                                                   step="0.01"
                                                   required>
                                        </td>

                                        <td class="importe">S/ 0.00</td>

                                        <td>
                                            <button type="button"
                                                    class="btn btn-danger"
                                                    onclick="eliminarFila(this)">
                                                Quitar
                                            </button>
                                        </td>
                                    </tr>

                                </tbody>
                            </table>

                        </div>

                        <div style="margin-top:20px;">
                            <button type="button"
                                    class="btn btn-secondary"
                                    onclick="agregarFila()">
                                + Agregar concepto
                            </button>
                        </div>

                    </div>

                    <div class="totales">
                        <div class="total-box">
                            <span>Total estimado</span>
                            <strong id="total">S/ 0.00</strong>
                        </div>
                    </div>

                </section>

                <div class="actions">
                    <a href="${pageContext.request.contextPath}/adminDashboard"
                       class="btn btn-secondary">
                        Volver al panel
                    </a>

                    <button type="submit"
                            class="btn btn-primary"
                            id="btnGuardar">
                        Guardar cotización
                    </button>
                </div>

            </form>

        </main>

        <footer class="footer">
            CREINFOR © 2026 · Sistema de Gestión de Soporte Técnico
        </footer>

        <script>
            const detalleBody = document.getElementById("detalleBody");
            const formatoMoneda = new Intl.NumberFormat("es-PE", {
                style: "currency",
                currency: "PEN"
            });

            function agregarFila() {
                if (detalleBody.rows.length >= 50) {
                    alert("Máximo 50 conceptos por cotización.");
                    return;
                }

                const fila = detalleBody.rows[0].cloneNode(true);

                fila.querySelector('[name="descripcion"]').value = "";
                fila.querySelector('[name="cantidad"]').value = "1";
                fila.querySelector('[name="precioUnitario"]').value = "0";
                fila.querySelector(".importe").textContent = "S/ 0.00";

                detalleBody.appendChild(fila);
                calcularTotal();
            }

            function eliminarFila(boton) {
                if (detalleBody.rows.length === 1) {
                    alert("La cotización necesita al menos un concepto.");
                    return;
                }

                boton.closest("tr").remove();
                calcularTotal();
            }

            function calcularTotal() {
                let total = 0;

                detalleBody.querySelectorAll("tr").forEach(fila => {

                    const cantidad = Number(
                            fila.querySelector('[name="cantidad"]').value) || 0;

                    const precio = Number(
                            fila.querySelector('[name="precioUnitario"]').value) || 0;

                    const importe = Math.round(
                            (cantidad * precio + Number.EPSILON) * 100) / 100;

                    fila.querySelector(".importe").textContent =
                            formatoMoneda.format(importe);

                    total += importe;
                });

                document.getElementById("total").textContent =
                        formatoMoneda.format(total);
            }

            detalleBody.addEventListener("input", calcularTotal);

            const formulario = document.getElementById("formCotizacion");
            const botonGuardar = document.getElementById("btnGuardar");

            let enviando = false;

            formulario.addEventListener("submit", function (event) {

                if (enviando) {
                    event.preventDefault();
                    return;
                }

                enviando = true;
                botonGuardar.disabled = true;
                botonGuardar.textContent = "Guardando cotización...";
            });

            window.addEventListener("pageshow", function (event) {
                if (event.persisted) {
                    enviando = false;
                    botonGuardar.disabled = false;
                    botonGuardar.textContent = "Guardar cotización";
                }
            });

            calcularTotal();
        </script>



        <script src="https://cdn.jsdelivr.net/npm/tom-select@2.6.2/dist/js/tom-select.complete.min.js"></script>

        <script>
            document.addEventListener("DOMContentLoaded", function () {

                new TomSelect("#solicitudBuscador", {
                    create: false,
                    maxItems: 1,
                    searchField: ["text"],
                    placeholder: "Escribe el ID, cliente o tipo...",
                    allowEmptyOption: false
                });

            });
        </script>


    </body>
</html>
