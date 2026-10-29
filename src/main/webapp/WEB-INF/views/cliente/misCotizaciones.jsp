
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis cotizaciones - CREINFOR</title>

    <style>
        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: 'Segoe UI', Arial, sans-serif;
            background: #eef3f7;
            color: #273b4a;
        }

        header {
            background: linear-gradient(120deg, #188A8D, #4E90D6);
            padding: 25px;
            color: white;
        }

        .header-content {
            max-width: 1100px;
            margin: auto;
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 15px;
        }

        header h1 { margin: 0; font-size: 26px; }
        header p { margin: 5px 0 0; font-size: 12px; }

        .volver {
            color: white;
            text-decoration: none;
            border: 1px solid #ffffff80;
            padding: 10px 15px;
            border-radius: 7px;
        }

        main {
            max-width: 1100px;
            margin: 30px auto;
            padding: 0 20px;
        }

        h2 { color: #173b50; }

        .card {
            background: white;
            border-radius: 14px;
            box-shadow: 0 5px 20px rgba(0,0,0,.06);
            overflow: hidden;
            margin-bottom: 25px;
        }

        .card-title {
            padding: 20px;
            border-bottom: 1px solid #e1e9ee;
            font-size: 17px;
            font-weight: bold;
            color: #173b50;
        }

        .card-body { padding: 20px; }
        .table-wrap { overflow-x: auto; }

        table {
            width: 100%;
            border-collapse: collapse;
            min-width: 650px;
        }

        th {
            background: #f1f6f9;
            padding: 13px;
            text-align: left;
            font-size: 12px;
        }

        td {
            padding: 14px 13px;
            border-bottom: 1px solid #e1e9ee;
            font-size: 13px;
        }

        .badge {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            text-transform: capitalize;
            background: #edf0f4;
            color: #546579;
        }

        .enviada { background: #e0efff; color: #1769aa; }
        .aprobada { background: #e1f5e7; color: #207e46; }
        .rechazada { background: #ffebee; color: #b42332; }
        .borrador, .enviando, .error_envio {
            background: #edf0f4;
            color: #546579;
        }

        .btn {
            display: inline-block;
            text-decoration: none;
            padding: 10px 15px;
            border-radius: 7px;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            border: 0;
        }

        .btn-blue { background: #4E90D6; color: white; }
        .btn-green { background: #18814c; color: white; }
        .btn-red { background: #bb3443; color: white; }

        .total {
            color: #147578;
            font-weight: bold;
            font-size: 17px;
        }

        .resumen {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 15px;
            margin-bottom: 20px;
        }

        .dato {
            background: #f5f9fb;
            padding: 16px;
            border-radius: 9px;
        }

        .dato small {
            display: block;
            color: #81909c;
            margin-bottom: 7px;
        }

        .alerta {
            padding: 16px;
            border-radius: 9px;
            margin-bottom: 18px;
        }

        .exito { background: #e1f5e7; color: #207e46; }
        .error { background: #ffebee; color: #b42332; }

        .acciones {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
            margin-top: 25px;
        }

        @media(max-width:650px) {
            .resumen { grid-template-columns: 1fr; }
            .header-content { flex-wrap: wrap; }
        }
    </style>
</head>

<body>

<header>
    <div class="header-content">
        <div>
            <h1>CREINFOR</h1>
            <p>Portal del cliente · Mis cotizaciones</p>
        </div>

        <a class="volver"
           href="${pageContext.request.contextPath}/clienteDashboard.jsp">
            ← Mi panel
        </a>
    </div>
</header>

<main>
    <h2>Mis cotizaciones</h2>
    <p>Revisa y responde las propuestas económicas de tus solicitudes.</p>

    <c:if test="${not empty sessionScope.mensajeCotizacion}">
        <div class="alerta exito">
            <c:out value="${sessionScope.mensajeCotizacion}"/>
        </div>
        <c:remove var="mensajeCotizacion" scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.errorCotizacion}">
        <div class="alerta error">
            <c:out value="${sessionScope.errorCotizacion}"/>
        </div>
        <c:remove var="errorCotizacion" scope="session"/>
    </c:if>

    <section class="card">
        <div class="card-title">
            Propuestas recibidas
        </div>

        <div class="card-body">
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>Cotización</th>
                            <th>Solicitud</th>
                            <th>Vencimiento</th>
                            <th>Estado</th>
                            <th>Total</th>
                            <th>Acción</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="cot" items="${cotizaciones}">
                            <c:if test="${cot.estado == 'enviada'
                                      || cot.estado == 'aprobada'
                                      || cot.estado == 'rechazada'}">

                                <tr>
                                    <td>#<c:out value="${cot.idCotizacion}"/></td>

                                    <td>
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

                                    <td class="total">
                                        S/
                                        <fmt:formatNumber
                                            value="${cot.total}"
                                            minFractionDigits="2"
                                            maxFractionDigits="2"/>
                                    </td>

                                    <td>
                                        <a class="btn btn-blue"
                                           href="${pageContext.request.contextPath}/cotizaciones/mis?id=${cot.idCotizacion}#detalle">
                                            Ver propuesta
                                        </a>
                                    </td>
                                </tr>

                            </c:if>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <c:if test="${empty cotizaciones}">
                <p>No tienes cotizaciones registradas.</p>
            </c:if>
        </div>
    </section>

    <c:if test="${not empty seleccionada
                   && (seleccionada.estado == 'enviada'
                   || seleccionada.estado == 'aprobada'
                   || seleccionada.estado == 'rechazada')}">

        <section class="card" id="detalle">

            <div class="card-title">
                Cotización #<c:out value="${seleccionada.idCotizacion}"/>
            </div>

            <div class="card-body">

                <div class="resumen">
                    <div class="dato">
                        <small>Solicitud</small>
                        <strong>
                            #<c:out value="${seleccionada.idSolicitud}"/>
                        </strong>
                    </div>

                    <div class="dato">
                        <small>Válida hasta</small>
                        <strong>
                            <c:out value="${seleccionada.fechaVencimiento}"/>
                        </strong>
                    </div>

                    <div class="dato">
                        <small>Estado</small>
                        <span class="badge ${seleccionada.estado}">
                            <c:out value="${seleccionada.estado}"/>
                        </span>
                    </div>
                </div>

                <div class="table-wrap">
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
                                    <td>
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

                <p style="text-align:right;margin-top:22px;">
                    Total cotizado:
                    <strong class="total">
                        S/
                        <fmt:formatNumber
                            value="${seleccionada.total}"
                            minFractionDigits="2"
                            maxFractionDigits="2"/>
                    </strong>
                </p>

                <c:if test="${not empty seleccionada.observaciones}">
                    <div class="dato">
                        <small>Observaciones y condiciones</small>
                        <p style="white-space:pre-wrap;"><c:out
                            value="${seleccionada.observaciones}"/></p>
                    </div>
                </c:if>

                <c:choose>
                    <c:when test="${seleccionada.estado == 'enviada'
                        && not empty seleccionada.fechaVencimiento
                        && seleccionada.fechaVencimiento >= hoy}">

                        <div class="acciones">

                            <form action="${pageContext.request.contextPath}/cotizaciones/mis"
                                  method="post"
                                  class="form-respuesta">

                                <input type="hidden" name="idCotizacion"
                                       value="${seleccionada.idCotizacion}">

                                <input type="hidden" name="respuesta"
                                       value="aprobada">

                                <button type="submit" class="btn btn-green">
                                    ✓ Aceptar cotización
                                </button>
                            </form>

                            <form action="${pageContext.request.contextPath}/cotizaciones/mis"
                                  method="post"
                                  class="form-respuesta">

                                <input type="hidden" name="idCotizacion"
                                       value="${seleccionada.idCotizacion}">

                                <input type="hidden" name="respuesta"
                                       value="rechazada">

                                <button type="submit" class="btn btn-red">
                                    ✕ Rechazar cotización
                                </button>
                            </form>

                        </div>
                    </c:when>

                    <c:when test="${seleccionada.estado == 'enviada'}">
                        <div class="alerta error" style="margin-top:20px;">
                            Esta cotización ha vencido y ya no puede responderse.
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="alerta exito" style="margin-top:20px;">
                            Tu respuesta a esta cotización ya fue registrada.
                        </div>
                    </c:otherwise>
                </c:choose>

            </div>
        </section>

    </c:if>
</main>

<script>
document.querySelectorAll(".form-respuesta").forEach(form => {
    form.addEventListener("submit", function(event) {
        const respuesta = form.querySelector(
            'input[name="respuesta"]').value;

        const pregunta = respuesta === "aprobada"
            ? "¿Confirmas que aceptas esta cotización?"
            : "¿Confirmas que rechazas esta cotización?";

        if (!confirm(pregunta)) {
            event.preventDefault();
            return;
        }

        document.querySelectorAll(".form-respuesta button")
            .forEach(boton => {
                boton.disabled = true;
            });
    });
});
</script>

</body>
</html>
