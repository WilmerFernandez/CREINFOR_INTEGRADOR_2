<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="utp_integrador_2_model.Usuario" %>
<%@ page session="true" %>

<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"cliente".equals(usuario.getTipoUsuario())) {
        response.sendRedirect("login.jsp");
        return;
    }

    String nombres = usuario.getNombres() != null ? usuario.getNombres() : "";
    String apellidos = usuario.getApellidos() != null ? usuario.getApellidos() : "";
    String nombreCompleto = nombres + " " + apellidos;
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel de Cliente | CREINFOR</title>

    <style>
        :root {
            --verde: #4CAF50;
            --verde-oscuro: #388E3C;
            --azul: #2196F3;
            --azul-oscuro: #1976D2;
            --fondo: #f5f5f5;
            --texto: #263238;
            --gris: #718096;
            --borde: #e4e9ee;
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

        /* BARRA SUPERIOR */
        .navbar {
            background: white;
            padding: 17px 7%;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 12px rgba(0,0,0,0.05);
        }

        .logo {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .logo-icono {
            background: var(--verde);
            color: white;
            padding: 11px;
            border-radius: 12px;
            font-size: 23px;
        }

        .logo h2 {
            margin: 0;
            color: var(--verde-oscuro);
            letter-spacing: 1px;
        }

        .logo span {
            font-size: 12px;
            color: var(--gris);
        }

        .logout-btn {
            background: #fff5f5;
            color: #e53935;
            border: 1px solid #ffcdd2;
            padding: 11px 18px;
            border-radius: 9px;
            cursor: pointer;
            font-weight: 600;
            transition: 0.3s;
        }

        .logout-btn:hover {
            background: #f44336;
            color: white;
        }

        /* CONTENEDOR */
        .container {
            max-width: 1120px;
            margin: 35px auto;
            padding: 0 20px;
        }

        /* BIENVENIDA */
        .welcome {
            background: linear-gradient(120deg,
                #4CAF50, #388E3C);
            border-radius: 18px;
            padding: 35px;
            color: white;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 8px 24px rgba(76,175,80,0.18);
        }

        .welcome h1 {
            font-size: 28px;
            margin: 0 0 12px;
        }

        .welcome p {
            margin: 0;
            opacity: 0.92;
            line-height: 1.7;
        }

        .welcome-icon {
            font-size: 75px;
            opacity: 0.85;
        }

        /* TITULOS */
        .section-title {
            margin: 32px 0 20px;
        }

        .section-title h2 {
            font-size: 22px;
            margin-bottom: 7px;
        }

        .section-title p {
            color: var(--gris);
            margin: 0;
            font-size: 14px;
        }

        /* DISTRIBUCION */
        .dashboard-grid {
            display: grid;
            grid-template-columns: 1.8fr 1fr;
            gap: 24px;
            align-items: start;
        }

        .card {
            background: white;
            padding: 30px;
            border-radius: 16px;
            border: 1px solid var(--borde);
            box-shadow: 0 5px 20px rgba(0,0,0,0.035);
        }

        .card-heading {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 25px;
        }

        .card-icon {
            background: #e8f5e9;
            padding: 13px;
            border-radius: 12px;
            font-size: 23px;
        }

        .card-heading h3 {
            margin: 0 0 5px;
            font-size: 19px;
        }

        .card-heading p {
            margin: 0;
            color: var(--gris);
            font-size: 13px;
        }

        /* FORMULARIO */
        .form-group {
            margin-bottom: 24px;
        }

        label {
            display: block;
            font-weight: 600;
            font-size: 14px;
            margin-bottom: 10px;
        }

        select, textarea {
            width: 100%;
            border: 1px solid #dce3e8;
            background: #fafcfd;
            padding: 14px;
            border-radius: 10px;
            font-family: inherit;
            font-size: 14px;
            color: var(--texto);
            outline: none;
            transition: 0.3s;
        }

        select:focus, textarea:focus {
            border-color: var(--verde);
            background: white;
            box-shadow: 0 0 0 3px rgba(76,175,80,0.12);
        }

        textarea {
            resize: vertical;
            min-height: 155px;
            line-height: 1.6;
        }

        .field-help {
            color: var(--gris);
            font-size: 12px;
            margin-top: 8px;
        }

        /* BOTONES */
        .btn-group {
            display: flex;
            gap: 12px;
            margin-top: 20px;
        }

        .btn {
            display: inline-flex;
            justify-content: center;
            align-items: center;
            gap: 8px;
            flex: 1;
            padding: 14px;
            border: none;
            border-radius: 10px;
            font-weight: 600;
            font-size: 14px;
            text-decoration: none;
            cursor: pointer;
            transition: 0.3s;
            font-family: inherit;
        }

        .btn-primary {
            background: var(--verde);
            color: white;
        }

        .btn-primary:hover {
            background: var(--verde-oscuro);
            transform: translateY(-2px);
        }

        .btn-info {
            background: var(--azul);
            color: white;
        }

        .btn-info:hover {
            background: var(--azul-oscuro);
            transform: translateY(-2px);
        }

        /* PANEL LATERAL */
        .side-card {
            margin-bottom: 20px;
        }

        .side-card h3 {
            margin-top: 0;
            font-size: 18px;
        }

        .step {
            display: flex;
            gap: 13px;
            margin: 22px 0;
        }

        .step-number {
            background: #e8f5e9;
            color: var(--verde-oscuro);
            width: 34px;
            height: 34px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            flex-shrink: 0;
        }

        .step strong {
            font-size: 14px;
        }

        .step p {
            font-size: 13px;
            color: var(--gris);
            line-height: 1.6;
            margin: 5px 0 0;
        }

        .help-box {
            background: #e3f2fd;
            border: 1px solid #bbdefb;
            padding: 23px;
            border-radius: 15px;
        }

        .help-box h3 {
            color: #1565C0;
            margin-top: 0;
        }

        .help-box p {
            color: #45627a;
            line-height: 1.7;
            font-size: 13px;
        }

        /* PIE */
        .footer {
            text-align: center;
            padding: 28px 15px;
            color: #90a0ac;
            font-size: 12px;
        }

        /* RESPONSIVE */
        @media (max-width: 800px) {
            .dashboard-grid {
                grid-template-columns: 1fr;
            }

            .welcome-icon {
                display: none;
            }

            .welcome {
                padding: 27px;
            }

            .welcome h1 {
                font-size: 23px;
            }
        }

        @media (max-width: 500px) {
            .navbar {
                padding: 15px;
            }

            .logo h2 {
                font-size: 18px;
            }

            .container {
                margin-top: 20px;
            }

            .card {
                padding: 22px;
            }

            .btn-group {
                flex-direction: column;
            }

            .logout-btn {
                padding: 9px;
                font-size: 12px;
            }
        }
    </style>
</head>

<body>

    <!-- ENCABEZADO -->
    <header class="navbar">
        <div class="logo">
            <div class="logo-icono">🖥️</div>
            <div>
                <h2>CREINFOR</h2>
                <span>Centro de soporte técnico</span>
            </div>
        </div>

        <form action="login.jsp" method="get">
            <button type="submit" class="logout-btn">
                ↪ Salir
            </button>
        </form>
    </header>

    <main class="container">

        <!-- BIENVENIDA -->
        <section class="welcome">
            <div>
                <h1>
                    ¡Hola, <%= nombreCompleto %>! 👋
                </h1>
                <p>
                    Bienvenido a tu centro de soporte técnico.<br>
                    Estamos aquí para ayudarte con tus solicitudes.
                </p>
            </div>
            <div class="welcome-icon">🎧</div>
        </section>

        <div class="section-title">
            <h2>Mi panel de soporte</h2>
            <p>
                Registra y consulta tus solicitudes de
                asistencia técnica.
            </p>
        </div>

        <div class="dashboard-grid">

            <!-- FORMULARIO PRINCIPAL -->
            <section class="card">

                <div class="card-heading">
                    <div class="card-icon">📝</div>
                    <div>
                        <h3>Nueva solicitud</h3>
                        <p>
                            Completa los datos para solicitar ayuda.
                        </p>
                    </div>
                </div>

                <form action="solicitud" method="post">

                    <input type="hidden"
                           name="idUsuario"
                           value="<%= usuario.getIdUsuario() %>">

                    <div class="form-group">
                        <label for="tipoSolicitud">
                            Tipo de solicitud
                        </label>

                        <select name="tipoSolicitud"
                                id="tipoSolicitud"
                                required>

                            <option value="" disabled selected>
                                Selecciona el tipo de atención
                            </option>

                            <option value="error">
                                Error de software
                            </option>

                            <option value="capacitación">
                                Capacitación
                            </option>

                            <option value="requerimiento">
                                Nuevo requerimiento
                            </option>

                        </select>
                    </div>

                    <div class="form-group">
                        <label for="motivo">
                            Descripción de la solicitud
                        </label>

                        <textarea
                            name="motivo"
                            id="motivo"
                            rows="6"
                            maxlength="2000"
                            placeholder="Describe el problema que estás experimentando, cuándo ocurrió y qué necesitas resolver..."
                            required></textarea>

                        <div class="field-help">
                            Proporciona información clara para
                            facilitar la atención de tu solicitud.
                        </div>
                    </div>

                    <div class="btn-group">
                        <button type="submit"
                                class="btn btn-primary">
                            ✓ Enviar solicitud
                        </button>

                        <a href="listaSolicitudes"
                           class="btn btn-info">
                            ☷ Ver mis solicitudes
                        </a>
                    </div>

                </form>
            </section>

            <!-- PANEL LATERAL -->
            <aside>

                <div class="card side-card">
                    <h3>📋 ¿Cómo funciona?</h3>

                    <div class="step">
                        <div class="step-number">1</div>
                        <div>
                            <strong>Registra tu solicitud</strong>
                            <p>
                                Selecciona el tipo de atención
                                y describe tu necesidad.
                            </p>
                        </div>
                    </div>

                    <div class="step">
                        <div class="step-number">2</div>
                        <div>
                            <strong>Revisión del equipo</strong>
                            <p>
                                El personal de soporte revisará
                                la solicitud registrada.
                            </p>
                        </div>
                    </div>

                    <div class="step">
                        <div class="step-number">3</div>
                        <div>
                            <strong>Consulta el seguimiento</strong>
                            <p>
                                Revisa el estado de tus solicitudes
                                desde tu panel.
                            </p>
                        </div>
                    </div>
                </div>

                <div class="help-box">
                    <h3>💡 Consejo útil</h3>
                    <p>
                        Para recibir una mejor atención, indica
                        qué estabas haciendo cuando ocurrió el
                        problema y qué mensaje de error apareció,
                        si corresponde.
                    </p>
                </div>

            </aside>
        </div>

    </main>

    <footer class="footer">
        © 2026 CREINFOR S.A.C. |
        Sistema de Gestión de Soporte Técnico
    </footer>

</body>
</html>
