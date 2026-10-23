<%@ page import="java.util.Map,java.util.HashMap" %>
<%@ page import="utp_integrador_2_model.Usuario" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%!
    private String html(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"admin".equalsIgnoreCase(usuario.getTipoUsuario())) {
        response.sendRedirect("login.jsp");
        return;
    }
    Map<String, Integer> estados = (Map<String, Integer>) request.getAttribute("solicitudesPorEstado");
    if (estados == null) {
        estados = new HashMap<>();
    }
    int pendientes = 0, proceso = 0, finalizadas = 0, otros = 0;
    for (Map.Entry<String, Integer> e : estados.entrySet()) {
        String clave = e.getKey() == null ? "" : e.getKey().trim();
        int n = e.getValue() == null ? 0 : Math.max(0, e.getValue());
        if (clave.equalsIgnoreCase("Pendiente")) {
            pendientes += n;
        } else if (clave.equalsIgnoreCase("En proceso")) {
            proceso += n;
        } else if (clave.equalsIgnoreCase("Finalizada")) {
            finalizadas += n;
        } else {
            otros += n;
        }
    }
    int total = pendientes + proceso + finalizadas + otros;
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Administración | CREINFOR</title>
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <style>
            :root {
                --azul:#3b5998;
                --azul2:#294778;
                --fondo:#f4f7fc;
                --texto:#25354b;
                --muted:#728096;
                --borde:#e4eaf2;
            }
            * {
                box-sizing:border-box;
            }
            body {
                margin:0;
                font-family:'Segoe UI',Arial,sans-serif;
                background:var(--fondo);
                color:var(--texto);
            }
            .topbar {
                background:#fff;
                border-bottom:1px solid var(--borde);
                padding:15px max(24px,calc((100% - 1200px)/2));
                display:flex;
                justify-content:space-between;
                align-items:center;
                gap:15px;
            }
            .brand {
                display:flex;
                align-items:center;
                gap:12px;
            }
            .brand-icon {
                background:var(--azul);
                color:#fff;
                padding:13px;
                border-radius:12px;
            }
            .brand strong {
                display:block;
                font-size:21px;
                color:var(--azul);
                letter-spacing:.6px;
            }
            .brand small {
                color:var(--muted);
            }
            .top-right {
                display:flex;
                align-items:center;
                gap:16px;
            }
            .role {
                background:#edf3ff;
                color:var(--azul);
                padding:9px 13px;
                border-radius:25px;
                font-size:13px;
                font-weight:600;
            }
            .exit {
                text-decoration:none;
                color:#d7444a;
                font-weight:600;
                font-size:13px;
            }
            .container {
                max-width:1240px;
                margin:auto;
                padding:28px 20px 35px;
            }
            .hero {
                background:linear-gradient(115deg,#3b5998,#547cc0);
                color:#fff;
                border-radius:18px;
                padding:30px 35px;
                display:flex;
                justify-content:space-between;
                align-items:center;
                margin-bottom:25px;
                box-shadow:0 12px 28px rgba(59,89,152,.14);
            }
            .hero h1 {
                margin:0 0 9px;
                font-size:27px;
            }
            .hero p {
                margin:0;
                color:#e5edff;
            }
            .hero i {
                font-size:58px;
                opacity:.5;
            }
            .section-heading {
                display:flex;
                justify-content:space-between;
                align-items:center;
                gap:15px;
                margin:22px 0 15px;
            }
            .section-heading h2 {
                margin:0;
                font-size:20px;
            }
            .section-heading p {
                margin:5px 0 0;
                font-size:13px;
                color:var(--muted);
            }
            .stats {
                display:grid;
                grid-template-columns:repeat(4,minmax(0,1fr));
                gap:16px;
            }
            .card {
                background:#fff;
                border:1px solid var(--borde);
                border-radius:15px;
                padding:23px;
                box-shadow:0 5px 20px rgba(34,58,92,.035);
            }
            .stat-top {
                display:flex;
                align-items:center;
                justify-content:space-between;
                gap:10px;
            }
            .stat-title {
                font-size:13px;
                color:var(--muted);
                font-weight:600;
            }
            .stat-icon {
                width:39px;
                height:39px;
                border-radius:11px;
                display:grid;
                place-items:center;
            }
            .stat-value {
                margin-top:10px;
                font-size:31px;
                font-weight:750;
            }
            .stat-sub {
                font-size:12px;
                color:var(--muted);
                margin-top:4px;
            }
            .total .stat-icon {
                background:#edf2ff;
                color:#3b5998;
            }
            .pending .stat-icon {
                background:#fff5df;
                color:#ce8a10;
            }
            .process .stat-icon {
                background:#e8f4ff;
                color:#2196f3;
            }
            .done .stat-icon {
                background:#e7f6eb;
                color:#31934c;
            }
            .filters {
                display:flex;
                flex-wrap:wrap;
                gap:14px;
                align-items:end;
            }
            .field {
                flex:1;
                min-width:180px;
            }
            label {
                display:block;
                margin-bottom:8px;
                font-size:13px;
                font-weight:600;
            }
            select {
                width:100%;
                border:1px solid #d9e2ee;
                border-radius:9px;
                padding:12px;
                background:#fff;
                color:var(--texto);
                font:inherit;
            }
            select:focus {
                outline:2px solid #b3c7ee;
            }
            .button {
                background:var(--azul);
                color:#fff;
                border:0;
                border-radius:9px;
                padding:12px 18px;
                text-decoration:none;
                font:inherit;
                font-size:13px;
                font-weight:600;
                cursor:pointer;
            }
            .button:hover {
                background:var(--azul2);
            }
            .button.secondary {
                background:#edf2fa;
                color:var(--azul);
            }
            .filtered-summary {
                margin-top:13px;
                color:var(--muted);
                font-size:13px;
            }
            .charts {
                display:grid;
                grid-template-columns:1fr 1fr;
                gap:18px;
            }
            .chart-head {
                display:flex;
                justify-content:space-between;
                gap:12px;
                align-items:center;
                margin-bottom:15px;
            }
            .chart-head h3 {
                margin:0;
                font-size:17px;
            }
            .chart-wrap {
                height:290px;
                position:relative;
            }
            .actions {
                display:grid;
                grid-template-columns:repeat(4,minmax(0,1fr));
                gap:14px;
            }
            .action {
                display:flex;
                gap:12px;
                align-items:center;
                text-decoration:none;
                color:var(--texto);
                padding:18px;
                border:1px solid var(--borde);
                border-radius:12px;
                background:#fff;
                font-weight:600;
                font-size:13px;
                transition:.2s;
            }
            .action i {
                font-size:20px;
                color:var(--azul);
            }
            .action:hover {
                border-color:#9cb6e7;
                transform:translateY(-2px);
                box-shadow:0 5px 15px rgba(0,0,0,.06);
            }
            .foot {
                text-align:center;
                color:#95a0b0;
                font-size:12px;
                margin-top:30px;
            }
            @media(max-width:850px) {
                .stats,.actions {
                    grid-template-columns:repeat(2,1fr);
                }
                .charts {
                    grid-template-columns:1fr;
                }
            }
            @media(max-width:520px) {
                .topbar {
                    padding:14px;
                }
                .role {
                    display:none;
                }
                .hero {
                    padding:25px;
                }
                .hero h1 {
                    font-size:21px;
                }
                .hero i {
                    display:none;
                }
                .stats {
                    gap:10px;
                }
                .card {
                    padding:16px;
                }
                .actions {
                    grid-template-columns:1fr;
                }
            }
        </style>
    </head>
    <body>
        <header class="topbar">
            <div class="brand"><span class="brand-icon"><i class="fas fa-desktop"></i></span><div><strong>CREINFOR</strong><small>Centro de administración</small></div></div>
            <div class="top-right"><span class="role"><i class="fas fa-user-shield"></i> Administrador</span><a class="exit" href="login.jsp"><i class="fas fa-sign-out-alt"></i> Salir</a></div>
        </header>
        <main class="container">
            <section class="hero"><div><h1>¡Bienvenido, <%= html(usuario.getNombres())%>!</h1><p>Supervisa las solicitudes y administra el soporte técnico.</p></div><i class="fas fa-chart-line"></i></section>
            <div class="section-heading"><div><h2>Resumen general</h2><p>Información actual proporcionada por el sistema.</p></div></div>
            <section class="stats" aria-label="Indicadores de solicitudes">
                <div class="card total"><div class="stat-top"><span class="stat-title">Total de solicitudes</span><span class="stat-icon"><i class="fas fa-clipboard-list"></i></span></div><div class="stat-value"><%= total%></div><div class="stat-sub">Todos los estados</div></div>
                <div class="card pending"><div class="stat-top"><span class="stat-title">Pendientes</span><span class="stat-icon"><i class="fas fa-clock"></i></span></div><div class="stat-value"><%= pendientes%></div><div class="stat-sub">Por atender</div></div>
                <div class="card process"><div class="stat-top"><span class="stat-title">En proceso</span><span class="stat-icon"><i class="fas fa-cogs"></i></span></div><div class="stat-value"><%= proceso%></div><div class="stat-sub">En atención</div></div>
                <div class="card done"><div class="stat-top"><span class="stat-title">Finalizadas</span><span class="stat-icon"><i class="fas fa-check-circle"></i></span></div><div class="stat-value"><%= finalizadas%></div><div class="stat-sub">Atención completada</div></div>
            </section>
            <div class="section-heading"><div><h2>Analizar solicitudes</h2><p>Filtra los gráficos por estado sin recargar la página.</p></div></div>
            <section class="card">
                <div class="filters"><div class="field"><label for="estadoFiltro">Estado de solicitud</label><select id="estadoFiltro"><option value="todos">Todos los estados</option><option value="Pendiente">Pendientes</option><option value="En proceso">En proceso</option><option value="Finalizada">Finalizadas</option><option value="Otros">Otros estados</option></select></div><button type="button" class="button secondary" id="limpiarFiltro"><i class="fas fa-undo"></i> Limpiar filtro</button></div>
                <div class="filtered-summary" id="resumenFiltro" aria-live="polite">Mostrando todas las solicitudes.</div>
            </section>
            <div class="section-heading"><div><h2>Estadísticas visuales</h2><p>Distribución real de solicitudes según su estado.</p></div></div>
            <section class="charts">
                <div class="card"><div class="chart-head"><h3>Distribución de solicitudes</h3><i class="fas fa-chart-pie"></i></div><div class="chart-wrap"><canvas id="graficoDona"></canvas></div></div>
                <div class="card"><div class="chart-head"><h3>Solicitudes por estado</h3><i class="fas fa-chart-bar"></i></div><div class="chart-wrap"><canvas id="graficoBarras"></canvas></div></div>
            </section>
            <div class="section-heading"><div><h2>Accesos rápidos</h2><p>Funciones principales del administrador.</p></div></div>
            <nav class="actions" aria-label="Acciones administrativas">
                <a class="action" href="asignacionForm"><i class="fas fa-tasks"></i> Asignar solicitudes</a>
                <a class="action" href="listaUsuarios"><i class="fas fa-users-cog"></i>Gestión de Usuarios</a>
                <a class="action" href="registrarColaborador"><i class="fas fa-users"></i> Registrar colaborador</a>
                <a class="action" href="solicitudes"><i class="fas fa-list"></i> Lista de solicitudes</a>
            </nav>
            <footer class="foot">© 2026 CREINFOR S.A.C. · Sistema de Gestión de Soporte Técnico</footer>
        </main>
        <script>
            (function () {
                const original = [
                    {estado: 'Pendiente', cantidad:<%= pendientes%>, color: '#e6a23c'},
                    {estado: 'En proceso', cantidad:<%= proceso%>, color: '#2196f3'},
                    {estado: 'Finalizada', cantidad:<%= finalizadas%>, color: '#4caf50'},
                    {estado: 'Otros', cantidad:<%= otros%>, color: '#9aa7bd'}
                ];
                const filtro = document.getElementById('estadoFiltro');
                const resumen = document.getElementById('resumenFiltro');
                const chartDisponible = typeof Chart !== 'undefined';
                if (!chartDisponible) {
                    resumen.textContent = 'No se pudo cargar Chart.js. Revisa la conexión a Internet.';
                    return;
                }
                const base = {responsive: true, maintainAspectRatio: false, plugins: {legend: {display: false}}};
                const dona = new Chart(document.getElementById('graficoDona'), {
                    type: 'doughnut', data: {labels: [], datasets: [{data: [], backgroundColor: [], borderWidth: 3, borderColor: '#fff'}]},
                    options: {responsive: true, maintainAspectRatio: false, cutout: '68%', plugins: {legend: {position: 'bottom'}}}
                });
                const barras = new Chart(document.getElementById('graficoBarras'), {
                    type: 'bar', data: {labels: [], datasets: [{label: 'Solicitudes', data: [], backgroundColor: [], borderRadius: 7, maxBarThickness: 65}]},
                    options: {...base, scales: {y: {beginAtZero: true, ticks: {precision: 0}}, x: {grid: {display: false}}}}
                });
                function actualizar() {
                    const valor = filtro.value;
                    const datos = valor === 'todos' ? original : original.filter(x => x.estado === valor);
                    const labels = datos.map(x => x.estado);
                    const cantidades = datos.map(x => x.cantidad);
                    const colores = datos.map(x => x.color);
                    dona.data.labels = labels;
                    dona.data.datasets[0].data = cantidades;
                    dona.data.datasets[0].backgroundColor = colores;
                    barras.data.labels = labels;
                    barras.data.datasets[0].data = cantidades;
                    barras.data.datasets[0].backgroundColor = colores;
                    dona.update();
                    barras.update();
                    const total = cantidades.reduce((a, b) => a + b, 0);
                    resumen.textContent = valor === 'todos' ? 'Mostrando todas las solicitudes: ' + total : 'Estado: ' + valor + ' · Solicitudes: ' + total;
                }
                filtro.addEventListener('change', actualizar);
                document.getElementById('limpiarFiltro').addEventListener('click', () => {
                    filtro.value = 'todos';
                    actualizar();
                });
                actualizar();
            })();
        </script>
    </body>
</html>