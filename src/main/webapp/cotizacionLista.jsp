
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Cotizaciones - CREINFOR</title>

        <style>
            :root {
                --navy: #173b50;
                --teal: #188A8D;
                --blue: #4E90D6;
                --bg: #eef3f7;
                --line: #e1e9ee;
            }

            * {
                box-sizing: border-box;
            }

            body {
                margin: 0;
                font-family: 'Segoe UI', Arial, sans-serif;
                background: var(--bg);
                color: #273b4a;
            }

            .header {
                background: linear-gradient(120deg, var(--teal), var(--blue));
                color: white;
                padding: 23px 30px;
            }

            .header-inner {
                max-width: 1200px;
                margin: auto;
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 15px;
            }

            .header h1 {
                font-size: 26px;
                margin: 0;
            }

            .header p {
                font-size: 12px;
                margin: 5px 0 0;
            }

            .back {
                color: white;
                border: 1px solid #ffffff80;
                border-radius: 7px;
                padding: 9px 13px;
                text-decoration: none;
                font-size: 13px;
            }

            .container {
                max-width: 1200px;
                margin: 30px auto;
                padding: 0 20px;
            }

            .topline {
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 15px;
                flex-wrap: wrap;
                margin-bottom: 22px;
            }

            h2 {
                color: var(--navy);
                margin: 0 0 6px;
                font-size: 24px;
            }

            .subtitle {
                color: #7c8e9b;
                font-size: 13px;
                margin: 0;
            }

            .card {
                background: white;
                border-radius: 14px;
                box-shadow: 0 5px 20px rgba(0,0,0,.06);
                overflow: hidden;
                margin-bottom: 24px;
            }

            .card-header {
                padding: 20px 24px;
                border-bottom: 1px solid var(--line);
                color: var(--navy);
                font-size: 17px;
                font-weight: 700;
            }

            .card-body {
                padding: 22px 24px;
            }

            .tabla-container {
                overflow-x: auto;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                min-width: 700px;
            }

            th {
                background: #f1f6f9;
                color: #456072;
                text-align: left;
                padding: 13px;
                font-size: 12px;
            }

            td {
                padding: 14px 13px;
                border-bottom: 1px solid var(--line);
                font-size: 13px;
            }

            tbody tr:hover {
                background: #f7fbfc;
            }

            .badge {
                display: inline-block;
                padding: 6px 11px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 650;
                text-transform: capitalize;
            }

            .borrador {
                background: #edf0f4;
                color: #546579;
            }

            .enviada {
                background: #e0efff;
                color: #1769aa;
            }

            .aprobada {
                background: #e1f5e7;
                color: #207e46;
            }

            .rechazada {
                background: #ffebee;
                color: #b42332;
            }

            .btn {
                display: inline-block;
                padding: 10px 15px;
                border-radius: 7px;
                font-size: 13px;
                font-weight: 650;
                text-decoration: none;
                border: none;
                cursor: pointer;
            }

            .btn-primary {
                background: var(--teal);
                color: white;
            }

            .btn-primary:hover {
                background: #116e72;
            }

            .btn-detail {
                background: var(--blue);
                color: white;
            }

            .btn-detail:hover {
                background: #3478b8;
            }

            .btn-secondary {
                background: #eef2f5;
                color: #445b6c;
            }

            .total {
                color: #147578;
                font-weight: 750;
                font-size: 15px;
                white-space: nowrap;
            }

            .resumen {
                display: grid;
                grid-template-columns: repeat(3, 1fr);
                gap: 14px;
                margin-bottom: 20px;
            }

            .resumen-item {
                background: #f5f9fb;
                border-radius: 9px;
                padding: 16px;
            }

            .resumen-item small {
                display: block;
                color: #81909c;
                margin-bottom: 7px;
            }

            .resumen-item strong {
                color: var(--navy);
            }

            .empty {
                text-align: center;
                padding: 35px;
                color: #8695a0;
            }

            .footer {
                text-align: center;
                color: #95a3ae;
                font-size: 12px;
                padding: 22px;
            }

            @media(max-width:650px) {
                .resumen {
                    grid-template-columns: 1fr;
                }
                .card-body {
                    padding: 15px;
                }
                .header-inner {
                    flex-wrap: wrap;
                }
            }
        </style>
    </head>

    <body>

        <header class="header">
            <div class="header-inner">
                <div>
                    <h1>CREINFOR</h1>
                    <p>Sistema de Gestión de Soporte Técnico · Cotizaciones</p>
                </div>

                <a href="${pageContext.request.contextPath}/adminDashboard"
                   class="back">← Volver al panel</a>
            </div>
        </header>

        <main class="container">

            <c:if test="${not empty sessionScope.mensajeExito}">
                <div class="card-body" style="background:#e5f5e9;color:#207743;">
                    <c:out value="${sessionScope.mensajeExito}"/>
                </div>
                <c:remove var="mensajeExito" scope="session"/>
            </c:if>

            <c:if test="${not empty sessionScope.mensajeError}">
                <div class="card-body" style="background:#ffebee;color:#b42332;">
                    <c:out value="${sessionScope.mensajeError}"/>
                </div>
                <c:remove var="mensajeError" scope="session"/>
            </c:if>

            <div class="topline">
                <div>
                    <h2>Gestión de cotizaciones</h2>
                    <p class="subtitle">
                        Consulta y administra las propuestas económicas.
                    </p>
                </div>

                <a class="btn btn-primary"
                   href="${pageContext.request.contextPath}/cotizaciones/nueva">
                    + Nueva cotización
                </a>
            </div>

            <!-- LISTADO -->
            <section class="card">
                <div class="card-header">
                    Cotizaciones registradas
                    (<c:out value="${cotizaciones.size()}"/>)
                </div>

                <div class="card-body">
                    <div class="tabla-container">

                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Solicitud</th>
                                    <th>Vencimiento</th>
                                    <th>Estado</th>
                                    <th>Moneda</th>
                                    <th>Importe total</th>
                                    <th>Acción</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="cot" items="${cotizaciones}">
                                    <tr>
                                        <td>#<c:out value="${cot.idCotizacion}"/></td>

                                        <td>
                                            Solicitud
                                            #<c:out value="${cot.idSolicitud}"/>
                                        </td>

                                        <td>
                                            <c:out value="${cot.fechaVencimiento}"/>
                                        </td>

                                        <td>
                                            <span class="badge ${cot.estado}">
                                                <c:out value="${cot.estado}"/>
                                            </span>
                                        </td>

                                        <td><c:out value="${cot.moneda}"/></td>

                                        <td class="total">
                                            S/
                                            <fmt:formatNumber
                                                value="${cot.total}"
                                                minFractionDigits="2"
                                                maxFractionDigits="2"/>
                                        </td>

                                        <td>
                                            <a class="btn btn-detail"
                                               href="${pageContext.request.contextPath}/cotizaciones/lista?id=${cot.idCotizacion}#detalle">
                                                Ver detalle
                                            </a>

                                            <c:if test="${cot.estado == 'borrador'}">
                                                <form action="${pageContext.request.contextPath}/cotizaciones/enviar"
                                                      method="post"
                                                      class="form-envio"
                                                      style="display:inline-block; margin:5px 0;">

                                                    <input type="hidden"
                                                           name="idCotizacion"
                                                           value="${cot.idCotizacion}">

                                                    <button type="submit"
                                                            class="btn btn-primary btn-enviar">
                                                        Enviar al cliente
                                                    </button>
                                                </form>
                                            </c:if>      


                                        </td>




                                    </tr>
                                </c:forEach>

                                <c:if test="${empty cotizaciones}">
                                    <tr>
                                        <td colspan="7" class="empty">
                                            No existen cotizaciones registradas.
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>

                    </div>
                </div>
            </section>

            <!-- DETALLE DE LA COTIZACIÓN -->
            <c:if test="${not empty cotizacionSeleccionada}">

                <section class="card" id="detalle">

                    <div class="card-header">
                        Detalle de cotización
                        #<c:out value="${cotizacionSeleccionada.idCotizacion}"/>
                    </div>

                    <div class="card-body">

                        <div class="resumen">
                            <div class="resumen-item">
                                <small>Solicitud</small>
                                <strong>
                                    #<c:out value="${cotizacionSeleccionada.idSolicitud}"/>
                                </strong>
                            </div>

                            <div class="resumen-item">
                                <small>Estado</small>
                                <span class="badge ${cotizacionSeleccionada.estado}">
                                    <c:out value="${cotizacionSeleccionada.estado}"/>
                                </span>
                            </div>

                            <div class="resumen-item">
                                <small>Válida hasta</small>
                                <strong>
                                    <c:out value="${cotizacionSeleccionada.fechaVencimiento}"/>
                                </strong>
                            </div>
                        </div>

                        <div class="tabla-container">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Descripción</th>
                                        <th>Cantidad</th>
                                        <th>Precio unitario</th>
                                        <th>Subtotal</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    <c:forEach var="d" items="${detalles}">
                                        <tr>
                                            <td><c:out value="${d.descripcion}"/></td>

                                            <td><c:out value="${d.cantidad}"/></td>

                                            <td>
                                                S/
                                                <fmt:formatNumber
                                                    value="${d.precioUnitario}"
                                                    minFractionDigits="2"
                                                    maxFractionDigits="2"/>
                                            </td>

                                            <td class="total">
                                                S/
                                                <fmt:formatNumber
                                                    value="${d.subtotal}"
                                                    minFractionDigits="2"
                                                    maxFractionDigits="2"/>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <div style="text-align:right; margin-top:24px;">
                            <span>Total cotizado:</span>
                            <strong class="total" style="font-size:25px;">
                                S/
                                <fmt:formatNumber
                                    value="${cotizacionSeleccionada.total}"
                                    minFractionDigits="2"
                                    maxFractionDigits="2"/>
                            </strong>
                        </div>

                        <c:if test="${not empty cotizacionSeleccionada.observaciones}">
                            <div class="resumen-item" style="margin-top:22px;">
                                <small>Observaciones</small>
                                <p style="white-space:pre-wrap;">
                                    <c:out value="${cotizacionSeleccionada.observaciones}"/>
                                </p>
                            </div>
                        </c:if>

                        <div style="margin-top:24px;">
                            <a class="btn btn-secondary"
                               href="${pageContext.request.contextPath}/cotizaciones/lista">
                                Cerrar detalle
                            </a>
                        </div>

                    </div>
                </section>

            </c:if>

        </main>

        <footer class="footer">
            CREINFOR © 2026 · Sistema de Gestión de Soporte Técnico
        </footer>


        <script>
            document.querySelectorAll(".form-envio").forEach(form => {
                let enviando = false;

                form.addEventListener("submit", event => {
                    if (enviando) {
                        event.preventDefault();
                        return;
                    }

                    if (!confirm("¿Deseas enviar esta cotización al cliente?")) {
                        event.preventDefault();
                        return;
                    }

                    enviando = true;

                    const boton = form.querySelector(".btn-enviar");
                    boton.disabled = true;
                    boton.textContent = "Generando PDF y enviando...";
                });
            });
        </script>         

    </body>
</html>
