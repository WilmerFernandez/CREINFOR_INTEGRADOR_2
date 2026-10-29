
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Informe Técnico - CREINFOR</title>

    <style>
        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: 'Segoe UI', Arial, sans-serif;
            background: #eef3f3;
            color: #263e3c;
        }

        header {
            background: #164b3a;
            color: white;
            padding: 23px 30px;
        }

        .header-inner {
            max-width: 1000px;
            margin: auto;
        }

        header h1 { margin: 0; font-size: 26px; }
        header p { margin: 5px 0 0; font-size: 12px; }

        main {
            max-width: 1000px;
            margin: 30px auto;
            padding: 0 18px;
        }

        h2 { margin-bottom: 6px; }

        .subtitle {
            color: #718780;
            margin-top: 0;
            font-size: 14px;
        }

        .card {
            background: white;
            border-radius: 14px;
            box-shadow: 0 5px 22px #0000000a;
            margin-bottom: 22px;
            overflow: hidden;
        }

        .card-title {
            padding: 20px 24px;
            border-bottom: 1px solid #e4ebe7;
            font-weight: 700;
            color: #164b3a;
        }

        .card-body { padding: 24px; }

        .datos {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 14px;
        }

        .dato {
            background: #f3f8f5;
            border-radius: 9px;
            padding: 15px;
        }

        .dato small {
            color: #748880;
            display: block;
            margin-bottom: 6px;
        }

        .form-group { margin-bottom: 22px; }

        label {
            display: block;
            font-weight: 650;
            margin-bottom: 9px;
            color: #244b3a;
        }

        textarea {
            width: 100%;
            min-height: 110px;
            border: 1px solid #d2dfd8;
            border-radius: 9px;
            padding: 13px;
            font-family: inherit;
            font-size: 14px;
            line-height: 1.6;
            resize: vertical;
        }

        textarea:focus {
            outline: none;
            border-color: #188A8D;
            box-shadow: 0 0 0 3px #188A8D20;
        }

        .help {
            font-size: 12px;
            color: #85958e;
            margin-top: 6px;
        }

        .actions {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            flex-wrap: wrap;
        }

        .btn {
            border: 0;
            padding: 13px 20px;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 650;
            text-decoration: none;
            font-size: 14px;
            font-family: inherit;
        }

        .btn-primary {
            background: #188A8D;
            color: white;
        }

        .btn-secondary {
            background: #eaf0ed;
            color: #365c4b;
        }

        .btn:disabled { opacity: .65; cursor: wait; }

        .alert {
            background: #ffebee;
            color: #a52b3d;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 18px;
        }

        @media(max-width:650px) {
            .datos { grid-template-columns: 1fr; }
            .card-body { padding: 18px; }
        }
    </style>
</head>

<body>
<header>
    <div class="header-inner">
        <h1>CREINFOR</h1>
        <p>Sistema de Gestión de Soporte Técnico · Informe final</p>
    </div>
</header>

<main>
    <h2>Informe técnico de culminación</h2>
    <p class="subtitle">
        Documenta el trabajo realizado antes de finalizar la solicitud.
    </p>

    <c:if test="${not empty mensajeError}">
        <div class="alert">
            <c:out value="${mensajeError}"/>
        </div>
    </c:if>

    <div class="card">
        <div class="card-title">Datos del servicio</div>
        <div class="card-body">
            <div class="datos">
                <div class="dato">
                    <small>Solicitud</small>
                    <strong>#<c:out value="${solicitud.idSolicitud}"/></strong>
                </div>
                <div class="dato">
                    <small>Estado</small>
                    <strong><c:out value="${solicitud.estado}"/></strong>
                </div>
                <div class="dato">
                    <small>Tipo de servicio</small>
                    <strong><c:out value="${solicitud.tipoSolicitud}"/></strong>
                </div>
                <div class="dato">
                    <small>Responsable</small>
                    <strong>Coordinador asignado</strong>
                </div>
            </div>

            <div class="dato" style="margin-top:15px;">
                <small>Motivo de la solicitud</small>
                <strong><c:out value="${solicitud.motivo}"/></strong>
            </div>
        </div>
    </div>

    <div class="card">
        <div class="card-title">Registro del informe técnico</div>

        <div class="card-body">
            <form id="formInforme"
                  action="${pageContext.request.contextPath}/culminarSolicitud"
                  method="post">

                <input type="hidden"
                       name="idSolicitudCulminar"
                       value="${solicitud.idSolicitud}">

                <div class="form-group">
                    <label for="diagnostico">1. Diagnóstico o necesidad identificada</label>
                    <textarea id="diagnostico" name="diagnostico"
                              maxlength="5000" required
                              placeholder="Describe la necesidad o problema identificado..."><c:out value="${param.diagnostico}"/></textarea>
                    <div class="help">Explica qué situación se encontró.</div>
                </div>

                <div class="form-group">
                    <label for="solucion">2. Solución aplicada</label>
                    <textarea id="solucion" name="solucion"
                              maxlength="5000" required
                              placeholder="Describe las acciones realizadas para solucionar el problema..."><c:out value="${param.solucion}"/></textarea>
                </div>

                <div class="form-group">
                    <label for="resultados">3. Resultados obtenidos</label>
                    <textarea id="resultados" name="resultados"
                              maxlength="5000" required
                              placeholder="Indica qué resultados se obtuvieron y cómo se comprobaron..."><c:out value="${param.resultados}"/></textarea>
                </div>

                <div class="form-group">
                    <label for="recomendaciones">4. Recomendaciones al cliente</label>
                    <textarea id="recomendaciones" name="recomendaciones"
                              maxlength="5000" required
                              placeholder="Registra las recomendaciones finales..."><c:out value="${param.recomendaciones}"/></textarea>
                </div>

                <div class="actions">
                    <a href="${pageContext.request.contextPath}/colaboradorDashboard"
                       class="btn btn-secondary">
                        Cancelar
                    </a>

                    <button class="btn btn-primary"
                            type="submit" id="btnGuardar">
                        Finalizar y guardar informe
                    </button>
                </div>
            </form>
        </div>
    </div>
</main>

<script>
    const form = document.getElementById("formInforme");
    const boton = document.getElementById("btnGuardar");
    let procesando = false;

    form.addEventListener("submit", function(event) {
        if (procesando) {
            event.preventDefault();
            return;
        }

        if (!confirm("¿Confirmas que el servicio ha concluido y deseas guardar el informe final?")) {
            event.preventDefault();
            return;
        }

        procesando = true;
        boton.disabled = true;
        boton.textContent = "Guardando informe...";
    });
</script>
</body>
</html>
