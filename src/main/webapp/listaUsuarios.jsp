<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Gestión de Usuarios | CREINFOR</title>
        <style>
            * {
                box-sizing: border-box;
            }
            body {
                margin:0;
                background:#eef3f7;
                color:#263238;
                font-family:Arial,Helvetica,sans-serif;
            }
            .header {
                background:#153b5b;
                color:#fff;
                padding:24px 32px;
            }
            .header-inner {
                max-width:1180px;
                margin:auto;
                display:flex;
                justify-content:space-between;
                align-items:center;
                flex-wrap:wrap;
                gap:16px;
            }
            .header h1 {
                margin:0 0 5px;
                font-size:27px;
            }
            .header p {
                margin:0;
                font-size:13px;
                opacity:.9;
            }
            .back {
                color:#fff;
                text-decoration:none;
                font-size:14px;
                border:1px solid #ffffff66;
                padding:10px 14px;
                border-radius:8px;
            }
            .back:hover {
                background:#ffffff16;
            }
            main {
                max-width:1180px;
                margin:30px auto;
                padding:0 20px;
            }
            .page-head {
                display:flex;
                align-items:center;
                justify-content:space-between;
                gap:16px;
                flex-wrap:wrap;
                margin-bottom:18px;
            }
            h2 {
                margin:0 0 6px;
                font-size:23px;
            }
            .muted {
                color:#718697;
                margin:0;
                font-size:14px;
            }
            .btn {
                display:inline-block;
                background:#1976d2;
                color:white;
                border-radius:8px;
                padding:12px 18px;
                text-decoration:none;
                font-size:14px;
                font-weight:bold;
            }
            .btn { border:0; cursor:pointer; font-family:inherit; }
            .btn:hover {
                background:#125fa9;
            }
            .stats {
                display:grid;
                grid-template-columns:repeat(4,minmax(0,1fr));
                gap:14px;
                margin-bottom:20px;
            }
            .stat {
                background:#fff;
                border-radius:12px;
                padding:18px;
                box-shadow:0 5px 18px #132f4512;
                border-left:4px solid #1976d2;
            }
            .stat small {
                color:#748594;
                display:block;
                margin-bottom:8px;
            }
            .stat strong {
                font-size:26px;
                color:#153b5b;
            }
            .card {
                background:#fff;
                border-radius:14px;
                overflow:hidden;
                box-shadow:0 5px 20px #132f4512;
            }
            .card-title {
                padding:20px 22px;
                border-bottom:1px solid #e8edf1;
                font-size:17px;
                font-weight:bold;
                color:#153b5b;
            }
            .filters {
                display:flex;
                flex-wrap:wrap;
                gap:12px;
                padding:20px 22px;
            }
            input,select {
                padding:12px;
                border:1px solid #cfd8dc;
                border-radius:8px;
                font:inherit;
                background:white;
            }
            input:focus,select:focus {
                outline:none;
                border-color:#1976d2;
                box-shadow:0 0 0 3px #1976d218;
            }
            #buscar {
                flex:2;
                min-width:220px;
            }
            #tipoFiltro,#estadoFiltro {
                flex:1;
                min-width:160px;
            }
            .table-wrap {
                overflow-x:auto;
                padding:0 22px 22px;
            }
            table {
                border-collapse:collapse;
                width:100%;
            }
            th {
                background:#f4f7f9;
                color:#475b69;
                text-align:left;
                font-size:13px;
                padding:14px 12px;
            }
            td {
                padding:14px 12px;
                border-bottom:1px solid #e8eef2;
                font-size:14px;
            }
            tr:hover td {
                background:#f9fcfd;
            }
            .name {
                font-weight:bold;
            }
            .badge {
                display:inline-block;
                padding:5px 10px;
                border-radius:25px;
                font-size:12px;
                font-weight:bold;
            }
            .admin {
                background:#e9e4f9;
                color:#6249a5;
            }
            .cliente {
                background:#e3f2fd;
                color:#1565c0;
            }
            .colaborador {
                background:#e8f5e9;
                color:#2e7d32;
            }
            .active {
                background:#e8f5e9;
                color:#2e7d32;
            }
            .pending {
                background:#fff4e3;
                color:#a46108;
            }
            .empty {
                text-align:center;
                color:#78909c;
                padding:30px;
            }
            .counter {
                padding:0 22px 18px;
                color:#718697;
                font-size:13px;
            }
            @media(max-width:800px){
                .stats{
                    grid-template-columns:repeat(2,minmax(0,1fr));
                }
                .header{
                    padding:22px 18px;
                }
            }
            @media(max-width:450px){
                .stats{
                    grid-template-columns:1fr 1fr;
                }
                .stat{
                    padding:12px;
                }
                .page-head{
                    align-items:flex-start;
                }
            }

            /* VENTANA FLOTANTE DE REGISTRO */
            body.modal-abierto { overflow: hidden; }
            .modal-registro[hidden] { display:none; }
            .modal-registro {
                position:fixed; inset:0; z-index:1000;
                display:flex; justify-content:center; align-items:center;
                padding:20px; background:rgba(12,31,48,.65);
            }
            .modal-panel {
                width:min(100%,570px); height:min(90vh,820px);
                display:flex; flex-direction:column;
                background:#fff; border-radius:16px; overflow:hidden;
                box-shadow:0 24px 72px rgba(0,0,0,.30);
            }
            .modal-barra {
                background:#153b5b; color:#fff;
                display:flex; justify-content:space-between;
                align-items:center; padding:13px 18px; gap:12px;
            }
            .modal-barra h3 { font-size:16px; margin:0; }
            .modal-cerrar {
                background:rgba(255,255,255,.13); color:white;
                border:1px solid rgba(255,255,255,.3);
                border-radius:8px; padding:7px 12px;
                font-size:20px; line-height:1; cursor:pointer;
            }
            .modal-cerrar:hover { background:#c62828; }
            .iframe-registro { flex:1; width:100%; border:0; background:#eef3f7; }
            .modal-pie { padding:9px 16px; text-align:center; font-size:12px; color:#718697; }
            @media(max-width:600px) {
                .modal-registro { padding:8px; }
                .modal-panel { height:96vh; }
            }
        </style>
    </head>
    
    
    <body>
        <header class="header">
            <div class="header-inner">
                <div><h1>CREINFOR</h1><p>Sistema de Gestión de Soporte Técnico · Administración de usuarios</p></div>
                <a class="back" href="${pageContext.request.contextPath}/adminDashboard">← Volver al panel</a>
            </div>
        </header>
        <main>
            <div class="page-head">
                <div><h2>Gestión de Usuarios</h2><p class="muted">Consulta las cuentas registradas y su estado de activación.</p></div>
                <button class="btn" type="button" id="abrirRegistro">+ Registrar nuevo usuario</button>
            </div>
            <div class="stats">
                <div class="stat"><small>Total usuarios</small><strong id="total">0</strong></div>
                <div class="stat"><small>Administradores</small><strong id="admins">0</strong></div>
                <div class="stat"><small>Clientes</small><strong id="clientes">0</strong></div>
                <div class="stat"><small>Cuentas activadas</small><strong id="activas">0</strong></div>
            </div>
            <section class="card">
                <div class="card-title">Usuarios registrados</div>
                <div class="filters">
                    <input id="buscar" type="search" placeholder="Buscar por nombre, apellido o correo…" aria-label="Buscar usuario">
                    <select id="tipoFiltro" aria-label="Filtrar por tipo">
                        <option value="">Todos los tipos</option><option value="admin">Administrador</option><option value="cliente">Cliente</option><option value="colaborador">Colaborador</option>
                    </select>
                    <select id="estadoFiltro" aria-label="Filtrar por activación">
                        <option value="">Todos los estados</option><option value="activa">Activada</option><option value="pendiente">Pendiente de activación</option>
                    </select>
                </div>
                <div class="table-wrap">
                    <table>
                        <thead><tr><th>ID</th><th>Usuario</th><th>Correo</th><th>Tipo</th><th>Estado de cuenta</th></tr></thead>
                        <tbody id="tablaUsuarios">
                            <c:forEach var="u" items="${usuarios}">
                                <tr data-tipo="${u.tipoUsuario}" data-estado="${u.cuentaActivada ? 'activa' : 'pendiente'}">
                                    <td><c:out value="${u.idUsuario}"/></td>
                                    <td class="name"><c:out value="${u.nombres}"/> <c:out value="${u.apellidos}"/></td>
                                    <td><c:out value="${u.email}"/></td>
                                    <td><span class="badge ${u.tipoUsuario == 'admin' ? 'admin' : (u.tipoUsuario == 'colaborador' ? 'colaborador' : 'cliente')}"><c:out value="${u.tipoUsuario}"/></span></td>
                                    <td><c:choose><c:when test="${u.cuentaActivada}"><span class="badge active">Activada</span></c:when><c:otherwise><span class="badge pending">Pendiente</span></c:otherwise></c:choose></td>
                                        </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <p id="sinResultados" class="empty" hidden>No se encontraron usuarios con esos filtros.</p>
                </div>
                <div class="counter" id="contador"></div>
            </section>
        </main>

        <!-- Reutiliza el formulario de registro existente sin modificar su servlet -->
        <div id="modalRegistro" class="modal-registro" hidden>
            <section class="modal-panel" role="dialog" aria-modal="true"
                     aria-labelledby="tituloModalRegistro">
                <div class="modal-barra">
                    <h3 id="tituloModalRegistro">Registrar nuevo usuario</h3>
                    <button id="cerrarRegistro" type="button" class="modal-cerrar"
                            aria-label="Cerrar formulario">&times;</button>
                </div>
                <iframe id="iframeRegistro" class="iframe-registro"
                        title="Formulario de registro de usuarios"
                        data-src="${pageContext.request.contextPath}/UsuarioRegistro.jsp"></iframe>
                <div class="modal-pie">Al cerrar se actualizará la lista de usuarios.</div>
            </section>
        </div>
        <script>
            const rows = Array.from(document.querySelectorAll('#tablaUsuarios tr'));
            const buscar = document.getElementById('buscar');
            const tipo = document.getElementById('tipoFiltro');
            const estado = document.getElementById('estadoFiltro');
            const cantidad = (predicado) => rows.filter(predicado).length;
            document.getElementById('total').textContent = rows.length;
            document.getElementById('admins').textContent = cantidad(r => r.dataset.tipo.toLowerCase() === 'admin');
            document.getElementById('clientes').textContent = cantidad(r => r.dataset.tipo.toLowerCase() === 'cliente');
            document.getElementById('activas').textContent = cantidad(r => r.dataset.estado === 'activa');
            function filtrar() {
                const q = buscar.value.trim().toLocaleLowerCase();
                const t = tipo.value;
                const e = estado.value;
                let visibles = 0;
                rows.forEach(row => {
                    const match = row.textContent.toLocaleLowerCase().includes(q)
                            && (!t || row.dataset.tipo.toLowerCase() === t)
                            && (!e || row.dataset.estado === e);
                    row.hidden = !match;
                    if (match)
                        visibles++;
                });
                document.getElementById('sinResultados').hidden = visibles !== 0;
                document.getElementById('contador').textContent = `Mostrando ${visibles} de ${rows.length} usuarios`;
            }
            buscar.addEventListener('input', filtrar);
            tipo.addEventListener('change', filtrar);
            estado.addEventListener('change', filtrar);
            filtrar();

            // Modal con iframe: el registro conserva su lógica JSP/Servlet original.
            const modal = document.getElementById('modalRegistro');
            const marco = document.getElementById('iframeRegistro');
            const abrir = document.getElementById('abrirRegistro');
            const cerrar = document.getElementById('cerrarRegistro');
            let retornoFoco = null;

            function abrirModalRegistro() {
                retornoFoco = document.activeElement;
                if (!marco.getAttribute('src')) {
                    marco.setAttribute('src', marco.dataset.src);
                }
                modal.hidden = false;
                document.body.classList.add('modal-abierto');
                cerrar.focus();
            }
            function cerrarModalRegistro() {
                modal.hidden = true;
                document.body.classList.remove('modal-abierto');
                if (retornoFoco && retornoFoco.focus) retornoFoco.focus();
                // Recarga para mostrar cualquier usuario que se haya registrado.
                window.location.reload();
            }
            abrir.addEventListener('click', abrirModalRegistro);
            cerrar.addEventListener('click', cerrarModalRegistro);
            modal.addEventListener('click', function (event) {
                if (event.target === modal) cerrarModalRegistro();
            });
            document.addEventListener('keydown', function (event) {
                if (event.key === 'Escape' && !modal.hidden) cerrarModalRegistro();
                if (event.key === 'Tab' && !modal.hidden && document.activeElement === cerrar
                    && event.shiftKey) {
                    event.preventDefault();
                    marco.focus();
                }
            });

        </script>
    </body>
</html>