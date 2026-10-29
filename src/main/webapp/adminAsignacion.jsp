<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Asignar solicitudes | CREINFOR</title>
        <style>
            :root {
                --navy:#173b5b;
                --blue:#3b5998;
                --action:#2196f3;
                --bg:#eef3f7;
                --text:#20334a;
                --muted:#70849a;
                --line:#e2eaf1;
            }
            * {
                box-sizing:border-box;
            }
            body {
                margin:0;
                background:var(--bg);
                color:var(--text);
                font:15px 'Segoe UI',Arial,sans-serif;
            }
            header {
                background:var(--navy);
                color:white;
                padding:21px max(24px,calc((100% - 1120px)/2));
            }
            .brand {
                font-size:24px;
                font-weight:800;
                letter-spacing:.5px;
            }
            .brand-sub {
                font-size:12px;
                opacity:.85;
                margin-top:3px;
            }
            main {
                max-width:1120px;
                margin:35px auto;
                padding:0 20px;
            }
            .topline {
                display:flex;
                align-items:center;
                justify-content:space-between;
                gap:16px;
                margin-bottom:24px;
            }
            h1 {
                font-size:27px;
                margin:0 0 7px;
            }
            .subtitle {
                color:var(--muted);
                margin:0;
            }
            .back {
                color:var(--blue);
                text-decoration:none;
                font-weight:650;
                white-space:nowrap;
            }
            .back:hover {
                text-decoration:underline;
            }
            .layout {
                display:grid;
                grid-template-columns:minmax(0,1.7fr) minmax(270px,1fr);
                gap:22px;
                align-items:start;
            }
            .card {
                background:white;
                border:1px solid var(--line);
                border-radius:16px;
                box-shadow:0 6px 24px #193c5b0c;
                overflow:hidden;
            }
            .card-head {
                display:flex;
                align-items:center;
                gap:13px;
                padding:23px 26px;
                border-bottom:1px solid var(--line);
            }
            .icon {
                background:#e8f2ff;
                color:var(--blue);
                border-radius:11px;
                padding:12px;
                font-size:21px;
            }
            h2 {
                margin:0;
                font-size:19px;
            }
            .small {
                font-size:13px;
                color:var(--muted);
                margin:5px 0 0;
                line-height:1.55;
            }
            .content {
                padding:26px;
            }
            .field {
                margin-bottom:22px;
            }
            label {
                display:block;
                font-size:14px;
                font-weight:650;
                margin-bottom:9px;
            }
            select {
                width:100%;
                padding:13px 14px;
                background:#fbfcfe;
                border:1px solid #cedae5;
                border-radius:9px;
                font:inherit;
                color:var(--text);
                outline:none;
            }
            select:focus {
                border-color:var(--action);
                box-shadow:0 0 0 3px #2196f320;
            }
            .hint {
                margin:7px 0 0;
                font-size:12px;
                color:var(--muted);
            }
            .preview {
                display:none;
                background:#f3f8ff;
                border:1px solid #d8e8fa;
                padding:16px;
                border-radius:10px;
                margin-bottom:22px;
                overflow-wrap:anywhere;
            }
            .preview strong {
                color:#1d4c83;
            }
            .preview p {
                margin:8px 0 0;
                line-height:1.6;
                font-size:13px;
            }
            .primary {
                background:var(--action);
                color:white;
                width:100%;
                padding:14px;
                border:0;
                border-radius:10px;
                font:inherit;
                font-weight:700;
                cursor:pointer;
                transition:.2s;
            }
            .primary:hover {
                background:#1976d2;
                transform:translateY(-1px);
            }
            .primary:disabled {
                opacity:.55;
                cursor:not-allowed;
                transform:none;
            }
            .message {
                border-radius:10px;
                padding:14px 16px;
                margin-bottom:20px;
                line-height:1.5;
            }
            .success {
                background:#e9f7ec;
                border:1px solid #bde4c6;
                color:#246a35;
            }
            .error {
                background:#fff0f0;
                border:1px solid #f2c4c4;
                color:#a02d2d;
            }
            .step {
                display:flex;
                gap:13px;
                margin:21px 0;
            }
            .number {
                flex:0 0 32px;
                height:32px;
                display:grid;
                place-items:center;
                border-radius:50%;
                background:#e8f2ff;
                color:var(--blue);
                font-weight:750;
            }
            .step b {
                font-size:14px;
            }
            .step p {
                margin:5px 0 0;
                font-size:13px;
                color:var(--muted);
                line-height:1.6;
            }
            .note {
                padding:22px;
                background:#e4f2ff;
                border:1px solid #cde4fa;
                border-radius:14px;
                margin-top:18px;
            }
            .note h3 {
                color:#195d9b;
                margin:0 0 8px;
                font-size:16px;
            }
            .note p {
                margin:0;
                color:#476b87;
                font-size:13px;
                line-height:1.65;
            }
            footer {
                text-align:center;
                padding:30px 15px;
                font-size:12px;
                color:#8192a2;
            }
            @media(max-width:760px) {
                .layout {
                    grid-template-columns:1fr;
                }
                .topline {
                    align-items:flex-start;
                    flex-direction:column;
                }
                main {
                    margin-top:24px;
                }
                .content,.card-head {
                    padding:20px;
                }
            }


            .tabla-asignaciones {
                width: 100%;
                border-collapse: collapse;
                font-size: 13px;
            }
            .tabla-asignaciones th,
            .tabla-asignaciones td {
                padding: 12px;
                text-align: left;
                border-bottom: 1px solid #e2eaf1;
            }
            .tabla-asignaciones th {
                background: #f3f7fb;
                color: #173b5b;
            }
            .etiqueta {
                display: inline-block;
                padding: 5px 9px;
                border-radius: 15px;
                background: #e3f2fd;
                color: #1565c0;
                font-size: 12px;
                font-weight: 600;
            }
            .etiqueta.coordinador {
                background: #e8f5e9;
                color: #2e7d32;
            }
        </style>
    </head>
    <body>
        <header><div class="brand">CREINFOR</div><div class="brand-sub">Gestión de solicitudes · Administración</div></header>
        <main>
            <div class="topline">
                <div><h1>Asignación de solicitudes</h1><p class="subtitle">Distribuye las solicitudes de soporte entre los colaboradores.</p></div>
                <a class="back" href="adminDashboard">← Volver al dashboard</a>
            </div>
            <div class="layout">
                <section class="card">
                    <div class="card-head"><div class="icon">📋</div><div><h2>Nueva asignación</h2><p class="small">Selecciona una solicitud y el responsable de atenderla.</p></div></div>
                    <div class="content">

                        <c:if test="${not empty sessionScope.mensajeExito}">
                            <div class="message success" role="status">
                                <c:out value="${sessionScope.mensajeExito}"/>
                            </div>
                            <c:remove var="mensajeExito" scope="session"/>
                        </c:if>

                        <c:if test="${not empty sessionScope.mensajeError}">
                            <div class="message error" role="alert">
                                <c:out value="${sessionScope.mensajeError}"/>
                            </div>
                            <c:remove var="mensajeError" scope="session"/>
                        </c:if>


                        <form action="asignacion" method="post" id="asignacionForm">
                            <div class="field"><label for="idSolicitud">Solicitud disponible</label>
                                <select name="idSolicitud" id="idSolicitud" required>
                                    <option value="">Selecciona una solicitud</option>
                                    <c:forEach var="sol" items="${solicitudesPendientes}">
                                        <option value="${sol.idSolicitud}" data-tipo="${fn:escapeXml(sol.tipoSolicitud)}" data-motivo="${fn:escapeXml(sol.motivo)}">ID: <c:out value="${sol.idSolicitud}" /> - <c:out value="${sol.tipoSolicitud}" /> - <c:out value="${sol.motivo}" /></option>
                                    </c:forEach>
                                </select><p class="hint">Solo se muestran las solicitudes enviadas por el controlador.</p>
                            </div>
                            <div id="preview" class="preview" aria-live="polite"><strong>Detalle de la solicitud</strong><p id="previewText"></p></div>
                            <div class="field"><label for="idColaborador">Colaborador responsable</label>
                                <select name="idColaborador" id="idColaborador" required><option value="">Selecciona un colaborador</option>
                                    <c:forEach var="col" items="${colaboradores}"><option value="${col.idColaborador}"><c:out value="${col.nombres}" /> <c:out value="${col.apellidos}" /> (<c:out value="${col.rol}" />)</option></c:forEach>
                                    </select><p class="hint">Selecciona a quien se le asignará esta solicitud.</p>
                                </div>
                                <div class="field"><label for="esCoordinador">¿Será coordinador de esta asignación?</label>
                                    <select name="esCoordinador" id="esCoordinador" required><option value="false" selected>No</option><option value="true">Sí</option></select>
                                    <p class="hint">Indica si el colaborador tendrá el rol de coordinador en esta asignación.</p>
                                </div>
                                <button type="submit" class="primary">✓ Asignar solicitud</button>
                            </form>
                        </div>
                    </section>
                    <aside>
                        <div class="card"><div class="card-head"><div class="icon">⚙️</div><div><h2>¿Cómo funciona?</h2><p class="small">Tres pasos para asignar correctamente.</p></div></div>
                            <div class="content">
                                <div class="step"><div class="number">1</div><div><b>Elige la solicitud</b><p>Selecciona el caso que requiere atención técnica.</p></div></div>
                                <div class="step"><div class="number">2</div><div><b>Designa al colaborador</b><p>Selecciona al integrante del equipo responsable.</p></div></div>
                                <div class="step"><div class="number">3</div><div><b>Confirma la asignación</b><p>El sistema enviará los datos al controlador de asignaciones.</p></div></div>
                            </div>
                        </div>
                        <div class="note"><h3>💡 Recomendación</h3><p>Revisa el tipo y motivo de la solicitud antes de asignarla. Así podrás elegir al colaborador más adecuado.</p></div>
                    </aside>
                </div>



                <section class="card" style="margin-top:25px;">

                    <div class="card-head">
                        <div class="icon">👥</div>
                        <div>
                            <h2>Asignaciones existentes</h2>
                            <p class="small">
                                Consulta los colaboradores y coordinadores de cada solicitud.
                            </p>
                        </div>
                    </div>

                    <div class="content" style="overflow-x:auto;">

                        <table class="tabla-asignaciones">
                            <thead>
                                <tr>
                                    <th>Solicitud</th>
                                    <th>Colaborador</th>
                                    <th>Especialidad</th>
                                    <th>Responsabilidad</th>
                                    <th>Estado</th>
                                </tr>
                            </thead>

                            <tbody>
                            <c:forEach var="a" items="${asignaciones}">
                                <tr>
                                    <td>#<c:out value="${a.idSolicitud}"/></td>
                                    <td>
                                        <c:out value="${a.nombres}"/>
                                        <c:out value="${a.apellidos}"/>
                                    </td>
                                    <td><c:out value="${a.rol}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${a.esCoordinador}">
                                                <span class="etiqueta coordinador">
                                                    ✓ Coordinador
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="etiqueta">
                                                    Colaborador
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><c:out value="${a.estado}"/></td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty asignaciones}">
                                <tr>
                                    <td colspan="5" style="text-align:center;">
                                        No existen asignaciones registradas.
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>

                </div>
            </section>                      


        </main>
        <footer>© 2026 CREINFOR S.A.C. · Sistema de gestión de soporte técnico</footer>
        <script>
            const solicitudSelect = document.getElementById('idSolicitud');
            const preview = document.getElementById('preview');
            const previewText = document.getElementById('previewText');
            solicitudSelect.addEventListener('change', function () {
                const option = this.options[this.selectedIndex];
                if (!this.value) {
                    preview.style.display = 'none';
                    previewText.textContent = '';
                    return;
                }
                previewText.textContent = 'Tipo: ' + (option.dataset.tipo || '') + ' · Motivo: ' + (option.dataset.motivo || '');
                preview.style.display = 'block';
            });
        </script>
    </body>
</html>