<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <title>Nueva contraseña - CREINFOR</title>

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

        .content {
            padding: 30px;
        }

        h2 {
            text-align: center;
            color: #263238;
            margin-top: 0;
        }

        .description {
            text-align: center;
            color: #607d8b;
            font-size: 14px;
            margin-bottom: 22px;
        }

        label {
            display: block;
            margin-bottom: 7px;
            font-weight: 600;
        }

        input[type="password"] {
            width: 100%;
            padding: 12px;
            border: 1px solid #cfd8dc;
            border-radius: 8px;
            margin-bottom: 18px;
            font-size: 15px;
        }

        button {
            width: 100%;
            padding: 13px;
            border: none;
            background: #1976d2;
            color: white;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
        }

        .error {
            background: #ffebee;
            color: #c62828;
            padding: 13px;
            border-radius: 8px;
            margin-bottom: 18px;
            text-align: center;
        }

        .success {
            background: #e8f5e9;
            color: #2e7d32;
            padding: 14px;
            border-radius: 8px;
            text-align: center;
        }

        .login-link {
            display: block;
            margin-top: 18px;
            text-align: center;
            color: #1565c0;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="header">
        <h1>CREINFOR</h1>
        <p>Restablecer contraseña</p>
    </div>

    <div class="content">

        <%
            String error =
                    (String) request.getAttribute("error");

            String exito =
                    (String) request.getAttribute("exito");

            String token =
                    (String) request.getAttribute("token");
        %>

        <% if (error != null) { %>

            <div class="error">
                <%= error %>
            </div>

        <% } %>

        <% if (exito != null) { %>

            <div class="success">
                <%= exito %>
            </div>

            <a href="login.jsp"
               class="login-link">
                Ir a iniciar sesión
            </a>

        <% } else if (token != null) { %>

            <h2>Nueva contraseña</h2>

            <p class="description">
                Ingresa una nueva contraseña para tu cuenta.
            </p>

            <form
                action="RestablecerPasswordServlet"
                method="post">

                <input
                    type="hidden"
                    name="token"
                    value="<%= token %>"
                >

                <label for="password">
                    Nueva contraseña
                </label>

                <input
                    type="password"
                    id="password"
                    name="password"
                    minlength="8"
                    required
                >

                <label for="confirmarPassword">
                    Confirmar contraseña
                </label>

                <input
                    type="password"
                    id="confirmarPassword"
                    name="confirmarPassword"
                    minlength="8"
                    required
                >

                <button type="submit">
                    Guardar nueva contraseña
                </button>

            </form>

        <% } %>

    </div>

</div>

</body>
</html>