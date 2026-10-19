<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Activar Cuenta - CREINFOR</title>

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
            padding: 25px;
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
            opacity: 0.9;
        }

        .content {
            padding: 30px;
        }

        h2 {
            text-align: center;
            color: #263238;
            margin-top: 0;
            margin-bottom: 10px;
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
            font-size: 14px;
            color: #37474f;
        }

        input[type="password"] {
            width: 100%;
            padding: 12px;
            margin-bottom: 18px;
            border: 1px solid #cfd8dc;
            border-radius: 8px;
            outline: none;
            font-size: 15px;
        }

        input[type="password"]:focus {
            border-color: #1976d2;
            box-shadow: 0 0 0 3px rgba(25,118,210,0.10);
        }

        button {
            width: 100%;
            padding: 13px;
            border: none;
            border-radius: 8px;
            background: #1976d2;
            color: white;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
        }

        button:hover {
            background: #125ca4;
        }

        .error {
            padding: 13px;
            background: #ffebee;
            color: #c62828;
            border: 1px solid #ffcdd2;
            border-radius: 8px;
            text-align: center;
            margin-bottom: 20px;
        }

        .success {
            padding: 15px;
            background: #e8f5e9;
            color: #2e7d32;
            border: 1px solid #c8e6c9;
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

        .login-link:hover {
            text-decoration: underline;
        }

        .footer {
            text-align: center;
            color: #90a4ae;
            font-size: 12px;
            padding-bottom: 25px;
        }

    </style>

</head>

<body>

<div class="container">

    <div class="header">
        <h1>CREINFOR</h1>
        <p>Activación de cuenta</p>
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

            <h2>Crear contraseña</h2>

            <p class="description">
                Cree su contraseña personal para activar
                su cuenta en CREINFOR.
            </p>

            <form action="ActivarCuentaServlet"
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
                    Activar mi cuenta
                </button>

            </form>

        <% } %>

    </div>

    <div class="footer">
        CREINFOR © 2026
    </div>

</div>

</body>
</html>