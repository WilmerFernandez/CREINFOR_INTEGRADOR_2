<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Registrar Usuario - CREINFOR</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: Arial, Helvetica, sans-serif;
            background: linear-gradient(135deg, #eef4f8, #dfeaf1);
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 25px;
        }

        .register-container {
            width: 100%;
            max-width: 470px;
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 12px 35px rgba(0, 0, 0, 0.12);
            overflow: hidden;
        }

        .header {
            background: #153b5b;
            padding: 28px 30px 24px;
            text-align: center;
            color: white;
        }

        .logo {
            width: 90px;
            height: 90px;
            object-fit: contain;
            margin-bottom: 10px;
        }

        .header h1 {
            margin: 0;
            font-size: 26px;
            letter-spacing: 1px;
        }

        .header p {
            margin: 8px 0 0;
            font-size: 14px;
            opacity: 0.9;
        }

        .form-content {
            padding: 30px;
        }

        .form-title {
            margin: 0 0 22px;
            text-align: center;
            color: #263238;
            font-size: 21px;
        }

        .form-group {
            margin-bottom: 17px;
        }

        label {
            display: block;
            margin-bottom: 7px;
            color: #37474f;
            font-size: 14px;
            font-weight: 600;
        }

        input,
        select {
            width: 100%;
            padding: 12px 13px;
            border: 1px solid #cfd8dc;
            border-radius: 8px;
            font-size: 15px;
            background: #ffffff;
            outline: none;
            transition: 0.2s;
        }

        input:focus,
        select:focus {
            border-color: #1976d2;
            box-shadow: 0 0 0 3px rgba(25, 118, 210, 0.10);
        }

        .info-box {
            background: #f4f8fb;
            border-left: 4px solid #1976d2;
            padding: 12px 14px;
            border-radius: 6px;
            margin-bottom: 20px;
            font-size: 13px;
            color: #455a64;
            line-height: 1.5;
        }

        .btn-register {
            width: 100%;
            border: none;
            border-radius: 8px;
            background: #1976d2;
            color: white;
            padding: 13px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: 0.25s;
        }

        .btn-register:hover {
            background: #125ca4;
        }

        .message {
            margin-top: 20px;
            padding: 12px;
            border-radius: 8px;
            text-align: center;
            background: #e8f5e9;
            color: #2e7d32;
            font-size: 14px;
            border: 1px solid #c8e6c9;
        }

        .activation-box {
            margin-top: 15px;
            background: #fff8e1;
            border: 1px solid #ffe082;
            border-radius: 8px;
            padding: 14px;
        }

        .activation-box strong {
            display: block;
            margin-bottom: 8px;
            color: #795548;
        }

        .activation-link {
            display: block;
            word-break: break-all;
            color: #1565c0;
            text-decoration: none;
            font-size: 13px;
        }

        .activation-link:hover {
            text-decoration: underline;
        }

        .footer {
            text-align: center;
            padding: 0 30px 25px;
            color: #90a4ae;
            font-size: 12px;
        }

        @media (max-width: 520px) {
            .form-content {
                padding: 24px 20px;
            }

            .header {
                padding: 24px 20px;
            }
        }
        
        
        .volver-container {
                text-align: center;
                margin-top: 18px;
            }

            .volver {
                color: #1976d2;
                text-decoration: none;
                font-size: 14px;
                font-weight: 500;
            }

            .volver:hover {
                text-decoration: underline;
            }
    </style>
</head>

<body>

<div class="register-container">

    <div class="header">

        <!--
        Si tienes el logo, descomenta esta línea.
        Cambia la ruta según dónde guardes tu imagen.
        -->

        <!--
        <img
            src="${pageContext.request.contextPath}/img/logo-creinfor.png"
            alt="Logo CREINFOR"
            class="logo"
        >
        -->

        <h1>CREINFOR</h1>
        <p>Sistema de Gestión de Soporte Técnico</p>
    </div>

    <div class="form-content">

        <h2 class="form-title">Registrar nuevo usuario</h2>

        <div class="info-box">
            El administrador crea la cuenta, pero no establece la contraseña.
            El usuario deberá crear su propia contraseña mediante el enlace
            de activación.
        </div>

        <form action="UsuarioServlet" method="post">

            <div class="form-group">
                <label for="nombres">Nombres</label>

                <input
                    type="text"
                    id="nombres"
                    name="nombres"
                    placeholder="Ingrese los nombres"
                    required
                    autofocus
                >
            </div>

            <div class="form-group">
                <label for="apellidos">Apellidos</label>

                <input
                    type="text"
                    id="apellidos"
                    name="apellidos"
                    placeholder="Ingrese los apellidos"
                    required
                >
            </div>

            <div class="form-group">
                <label for="email">Correo electrónico</label>

                <input
                    type="email"
                    id="email"
                    name="email"
                    placeholder="usuario@correo.com"
                    required
                >
            </div>

            <div class="form-group">
                <label for="tipoUsuario">Tipo de usuario</label>

                <select
                    id="tipoUsuario"
                    name="tipoUsuario"
                    required
                >

                    <option value="" disabled selected>
                        Seleccione un tipo de usuario
                    </option>

                    <option value="cliente">
                        Cliente
                    </option>

                    <option value="colaborador">
                        Colaborador
                    </option>

                    <option value="admin">
                        Administrador
                    </option>

                </select>
            </div>

            <button
                type="submit"
                class="btn-register"
            >
                Crear usuario
            </button>
            

        </form>
                    <%
                String mensaje =
                        (String) request.getAttribute("mensaje");

                if (mensaje != null) {
            %>

                <div class="message">
                    <%= mensaje %>
                </div>

            <%
                }
            %>
            
        <div class="volver-container">
        <a href="adminDashboard.jsp" class="volver">
            Volver al panel administrador
        </a>
        </div>
    </div>

    <div class="footer">
        CREINFOR © 2026
    </div>

</div>

</body>
</html>