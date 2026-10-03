<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>123Digital - Login</title>
    <style>
        /* Estilos consistentes con la página principal */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Arial', sans-serif;
        }
        
        body {
            background: linear-gradient(135deg, #3498db, #2c3e50);
            color: #333;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 20px;
        }
        
        .login-container {
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 5px 30px rgba(0, 0, 0, 0.2);
            width: 100%;
            max-width: 400px;
            animation: fadeIn 0.5s ease-out;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        .logo {
            text-align: center;
            margin-bottom: 30px;
            font-size: 28px;
            font-weight: 700;
            color: #2c3e50;
        }
        
        .logo span {
            color: #3498db;
        }
        
        h2 {
            text-align: center;
            margin-bottom: 30px;
            color: #2c3e50;
            font-size: 24px;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #2c3e50;
            font-size: 14px;
        }
        
        .form-group input {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 16px;
            transition: border-color 0.3s, box-shadow 0.3s;
        }
        
        .form-group input:focus {
            border-color: #3498db;
            box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.2);
            outline: none;
        }
        
        .submit-btn {
            width: 100%;
            padding: 14px;
            background-color: #3498db;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            margin-top: 10px;
        }
        
        .submit-btn:hover {
            background-color: #2980b9;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }
        
        .error-message {
            color: #e74c3c;
            margin-top: 20px;
            text-align: center;
            font-size: 14px;
            padding: 10px;
            background-color: rgba(231, 76, 60, 0.1);
            border-radius: 4px;
        }
        
        .footer-links {
            text-align: center;
            margin-top: 30px;
            font-size: 14px;
        }
        
        .footer-links a {
            color: #7f8c8d;
            text-decoration: none;
            margin: 0 10px;
        }
        
        .footer-links a:hover {
            color: #3498db;
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="logo">CRE<span>INFOR</span></div>
        <h2>Iniciar Sesión</h2>
        <form action="LoginServlet" method="post">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required autofocus
                placeholder="tu@email.com">
            </div>
            
            <div class="form-group">
                <label for="password">Contraseña</label>
                <input type="password" id="password" name="password" required
                placeholder="••••••••">
            </div>
            
            <button type="submit" class="submit-btn">Ingresar</button>
        </form>
        
        <% String error = (String) request.getAttribute("error");
           if (error != null) { %>
            <div class="error-message"><%= error %></div>
        <% } %>
        
        
        <div style="text-align:center; margin-top:15px;">
            <a href="olvidePassword.jsp"
               style="color:#1976d2; text-decoration:none;">
                ¿Olvidaste tu contraseña?
            </a>
        </div>
        
        
        <div class="footer-links">
            <p class="copyright">© 2026 CREINFOR. Todos los derechos reservados.</p>
        </div>
    </div>
</body>
</html>