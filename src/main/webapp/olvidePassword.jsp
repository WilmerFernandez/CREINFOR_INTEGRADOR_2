<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Recuperar contraseña - CREINFOR</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: Arial, Helvetica, sans-serif;
            background: linear-gradient(135deg, #eef4f8, #dce8f0);
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .container {
            width: 100%;
            max-width: 430px;
            background: white;
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 12px 35px rgba(0,0,0,0.12);
        }

        .header {
            background: #153b5b;
            color: white;
            text-align: center;
            padding: 28px;
        }

        .header h1 {
            margin: 0;
            font-size: 27px;
        }

        .header p {
            margin: 8px 0 0;
            font-size: 14px;
        }

        .content {
            padding: 30px;
        }

        h2 {
            text-align: center;
            margin-top: 0;
            color: #263238;
        }

        .description {
            text-align: center;
            color: #607d8b;
            font-size: 14px;
            line-height: 1.5;
            margin-bottom: 25px;
        }

        label {
            display: block;
            margin-bottom: 7px;
            font-weight: 600;
            color: #37474f;
        }

        input[type="email"] {
            width: 100%;
            padding: 12px;
            border: 1px solid #cfd8dc;
            border-radius: 8px;
            font-size: 15px;
            margin-bottom: 18px;
        }

        button {
            width: 100%;
            padding: 13px;
            border: none;
            border-radius: 8px;
            background: #1976d2;
            color: white;
            font-weight: 600;
            font-size: 16px;
            cursor: pointer;
        }

        .message {
            margin-top: 18px;
            padding: 13px;
            background: #e8f5e9;
            color: #2e7d32;
            border-radius: 8px;
            text-align: center;
        }

        .link-box {
            margin-top: 15px;
            padding: 13px;
            background: #fff8e1;
            border-radius: 8px;
            word-break: break-all;
            font-size: 13px;
        }

        .volver {
            display: block;
            text-align: center;
            margin-top: 18px;
            color: #1565c0;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="header">
        <h1>CREINFOR</h1>
        <p>Recuperación de contraseña</p>
    </div>

    <div class="content">

        <h2>¿Olvidaste tu contraseña?</h2>

        <p class="description">
            Ingresa tu correo electrónico para generar
            un enlace de recuperación.
        </p>

        <form action="OlvidePasswordServlet" method="post">

            <label for="email">Correo electrónico</label>

            <input
                type="email"
                id="email"
                name="email"
                placeholder="usuario@correo.com"
                required
            >

            <button type="submit">
                Recuperar contraseña
            </button>

        </form>

        <%
            String mensaje =
                    (String) request.getAttribute("mensaje");

            String enlace =
                    (String) request.getAttribute("enlaceRecuperacion");

            if (mensaje != null) {
        %>

            <div class="message">
                <%= mensaje %>
            </div>

        <%
            }

            if (enlace != null) {
        %>

            <div class="link-box">
                <strong>Enlace temporal:</strong><br><br>

                <a href="<%= enlace %>">
                    <%= enlace %>
                </a>
            </div>

        <%
            }
        %>

        <a href="login.jsp" class="volver">
            Volver al inicio de sesión
        </a>

    </div>
</div>

</body>
</html>