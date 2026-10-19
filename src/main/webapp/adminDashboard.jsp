<%@ page import="java.util.Map" %>
<%@ page import="utp_integrador_2_model.Usuario" %>
<%@ page session="true" %>


<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"admin".equalsIgnoreCase(usuario.getTipoUsuario())) {
        response.sendRedirect("login.jsp");
        return;
    }

    Map<String, Integer> solicitudesPorEstado = (Map<String, Integer>) request.getAttribute("solicitudesPorEstado");
    if (solicitudesPorEstado == null) {
        solicitudesPorEstado = new java.util.HashMap<>();
    }

    // Calcular total para estadísticas
    int totalSolicitudes = 0;
    for (Integer cantidad : solicitudesPorEstado.values()) {
        totalSolicitudes += cantidad;
    }
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Dashboard Administrador</title>
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 0;
                padding: 0;
                background-color: #f4f7fc;
            }
            header {
                background-color: #3b5998;
                color: white;
                padding: 20px;
                text-align: center;
            }
            header h2 {
                margin: 0;
            }
            .container {
                display: flex;
                flex-direction: column;
                padding: 20px;
            }

            /* Nuevos estilos para la sección de gráficos */
            .charts-section {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 20px;
                margin-bottom: 20px;
            }

            @media (max-width: 1200px) {
                .charts-section {
                    grid-template-columns: 1fr;
                }
            }

            .chart-card {
                background-color: white;
                border-radius: 10px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                padding: 20px;
                text-align: center;
            }

            .chart-card h3 {
                margin-top: 0;
                color: #3b5998;
            }

            .chart-container {
                width: 100%;
                height: 300px;
                margin-top: 15px;
            }

            /* Estilos para las tarjetas de estadísticas */
            .stats-container {
                display: grid;
                grid-template-columns: repeat(4, 1fr);
                gap: 15px;
                margin-bottom: 20px;
            }

            .stat-card {
                background-color: white;
                border-radius: 10px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                padding: 15px;
                text-align: center;
            }

            .stat-card h4 {
                margin: 0 0 10px 0;
                color: #555;
                font-size: 14px;
            }

            .stat-card .value {
                font-size: 24px;
                font-weight: bold;
                color: #3b5998;
                margin: 5px 0;
            }

            /* Estilos del menú (mantenidos de tu código original) */
            .menu-container {
                background: white;
                border-radius: 12px;
                padding: 25px;
                box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
                margin-bottom: 30px;
            }

            .menu-container h2 {
                margin-top: 0;
                margin-bottom: 20px;
                color: #2c3e50;
                font-size: 1.5rem;
                font-weight: 600;
                padding-bottom: 10px;
                border-bottom: 2px solid #f1f3f6;
            }

            .menu-links {
                list-style: none;
                padding: 0;
                margin: 0;
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                gap: 15px;
            }

            .menu-links li {
                margin: 0;
            }

            .menu-links li a {
                display: flex;
                align-items: center;
                padding: 12px 20px;
                background: #f8f9fa;
                border-radius: 8px;
                text-decoration: none;
                color: #4361ee;
                font-weight: 500;
                transition: all 0.3s ease;
                border: 1px solid #e9ecef;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
            }

            .menu-links li a:hover {
                background: #4361ee;
                color: white;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(67, 97, 238, 0.2);
                border-color: #4361ee;
            }

            .menu-links li a i {
                margin-right: 10px;
                font-size: 1.1rem;
                width: 20px;
                text-align: center;
            }

            .menu-links li a:hover i {
                color: white;
            }

            .menu-links li:last-child a {
                color: #f72585;
                border-color: #f8d7da;
            }

            .menu-links li:last-child a:hover {
                background: #f72585;
                border-color: #f72585;
                box-shadow: 0 4px 8px rgba(247, 37, 133, 0.2);
            }
        </style>
    </head>
    <body>

        <header>
            <h2>Bienvenido, <%= usuario.getNombres() + " " + usuario.getApellidos()%></h2>
            <p>Administrador</p>
        </header>

        <div class="container">
            <!-- Tarjetas de estadísticas rápidas -->
            <div class="stats-container">
                <div class="stat-card">
                    <h4>Solicitudes Totales</h4>
                    <div class="value"><%= totalSolicitudes%></div>
                </div>
                <div class="stat-card">
                    <h4>En Proceso</h4>
                    <div class="value">
                        <%= solicitudesPorEstado.entrySet().stream()
                                .filter(e -> e.getKey().equalsIgnoreCase("En proceso"))
                                .mapToInt(Map.Entry::getValue)
                    .sum()%>
                    </div>
                </div>
                <div class="stat-card">
                    <h4>Pendientes</h4>
                    <div class="value">
                        <%= solicitudesPorEstado.entrySet().stream()
                                .filter(e -> e.getKey().equalsIgnoreCase("Pendiente"))
                                .mapToInt(Map.Entry::getValue)
                    .sum()%>
                    </div>
                </div>
                <div class="stat-card">
                    <h4>Finalizadas</h4>
                    <div class="value">
                        <%= solicitudesPorEstado.entrySet().stream()
                                .filter(e -> e.getKey().equalsIgnoreCase("Finalizada"))
                                .mapToInt(Map.Entry::getValue)
                    .sum()%>
                    </div>
                </div>
            </div>

            <!-- Sección de gráficos -->
            <div class="charts-section">
                <!-- Gráfico 1: Dona (original) -->
                <div class="chart-card">
                    <h3>Distribución de Solicitudes</h3>
                    <div class="chart-container">
                        <canvas id="solicitudesChart"></canvas>
                    </div>
                </div>

                <!-- Gráfico 2: Barras -->
                <div class="chart-card">
                    <h3>Solicitudes por Estado</h3>
                    <div class="chart-container">
                        <canvas id="barrasChart"></canvas>
                    </div>
                </div>

                <!-- Gráfico 3: Líneas -->
                <div class="chart-card">
                    <h3>Tendencia de Solicitudes</h3>
                    <div class="chart-container">
                        <canvas id="lineasChart"></canvas>
                    </div>
                </div>

                <!-- Gráfico 4: Radar -->
                <div class="chart-card">
                    <h3>Comparación de Estados</h3>
                    <div class="chart-container">
                        <canvas id="radarChart"></canvas>
                    </div>
                </div>
            </div>

            <script>
                // Datos comunes para todos los gráficos
                const labels = [
                <% for (String estado : solicitudesPorEstado.keySet()) {%>
                    '<%= estado%>',
                <% } %>
                ];

                const dataValues = [
                <% for (Integer cantidad : solicitudesPorEstado.values()) {%>
                <%= cantidad%>,
                <% }%>
                ];

                const backgroundColors = [
                    'rgba(255, 99, 132, 0.7)',
                    'rgba(54, 162, 235, 0.7)',
                    'rgba(255, 206, 86, 0.7)',
                    'rgba(75, 192, 192, 0.7)'
                ];

                const borderColors = [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)'
                ];

                // 1. Gráfico de Dona (original)
                new Chart(
                        document.getElementById('solicitudesChart').getContext('2d'),
                        {
                            type: 'doughnut',
                            data: {
                                labels: labels,
                                datasets: [{
                                        data: dataValues,
                                        backgroundColor: backgroundColors,
                                        borderColor: borderColors,
                                        borderWidth: 1
                                    }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                plugins: {
                                    legend: {
                                        position: 'bottom'
                                    }
                                }
                            }
                        }
                );

                // 2. Gráfico de Barras
                new Chart(
                        document.getElementById('barrasChart').getContext('2d'),
                        {
                            type: 'bar',
                            data: {
                                labels: labels,
                                datasets: [{
                                        label: 'Cantidad',
                                        data: dataValues,
                                        backgroundColor: backgroundColors,
                                        borderColor: borderColors,
                                        borderWidth: 1
                                    }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                scales: {
                                    y: {
                                        beginAtZero: true
                                    }
                                },
                                plugins: {
                                    legend: {
                                        display: false
                                    }
                                }
                            }
                        }
                );

                // 3. Gráfico de Líneas
                new Chart(
                        document.getElementById('lineasChart').getContext('2d'),
                        {
                            type: 'line',
                            data: {
                                labels: labels,
                                datasets: [{
                                        label: 'Solicitudes',
                                        data: dataValues,
                                        backgroundColor: 'rgba(54, 162, 235, 0.2)',
                                        borderColor: 'rgba(54, 162, 235, 1)',
                                        borderWidth: 2,
                                        tension: 0.4,
                                        fill: true
                                    }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                scales: {
                                    y: {
                                        beginAtZero: true
                                    }
                                },
                                plugins: {
                                    legend: {
                                        display: false
                                    }
                                }
                            }
                        }
                );

                // 4. Gráfico de Radar
                new Chart(
                        document.getElementById('radarChart').getContext('2d'),
                        {
                            type: 'radar',
                            data: {
                                labels: labels,
                                datasets: [{
                                        label: 'Solicitudes',
                                        data: dataValues,
                                        backgroundColor: 'rgba(255, 99, 132, 0.2)',
                                        borderColor: 'rgba(255, 99, 132, 1)',
                                        pointBackgroundColor: 'rgba(255, 99, 132, 1)',
                                        pointBorderColor: '#fff',
                                        pointHoverBackgroundColor: '#fff',
                                        pointHoverBorderColor: 'rgba(255, 99, 132, 1)',
                                        borderWidth: 2
                                    }]
                            },
                            options: {
                                responsive: true,
                                maintainAspectRatio: false,
                                scales: {
                                    r: {
                                        angleLines: {
                                            display: true
                                        },
                                        suggestedMin: 0
                                    }
                                },
                                plugins: {
                                    legend: {
                                        display: false
                                    }
                                }
                            }
                        }
                );
            </script>

            <!-- Menú -->
            <div class="menu-container">
                <h2>Acciones</h2>
                <ul class="menu-links">
                    <li><a href="asignacionForm"><i class="fas fa-tasks"></i> Asignar solicitudes</a></li>
                    <li><a href="UsuarioRegistro.jsp"><i class="fas fa-user-plus"></i> Registrar Usuario</a></li>
                    <li><a href="registrarColaborador"><i class="fas fa-users"></i> Registrar Colaborador</a></li>
                    <li><a href="solicitudes"><i class="fas fa-list"></i> Lista de Solicitudes</a></li>
                    <li><a href="login.jsp"><i class="fas fa-sign-out-alt"></i> Cerrar Sesión</a></li>
                </ul>
            </div>
        </div>
    </body>
</html>