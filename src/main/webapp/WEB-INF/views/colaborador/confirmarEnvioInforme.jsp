
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Enviar informe técnico - CREINFOR</title>

    <style>
        * { box-sizing: border-box; }

        body {
            margin: 0;
            background: #eef3f3;
            font-family: 'Segoe UI', Arial, sans-serif;
            color: #244138;
        }

        header {
            background: #164b3a;
            color: white;
            padding: 24px 8%;
            font-size: 25px;
            font-weight: bold;
        }

        main {
            max-width: 700px;
            margin: 65px auto;
            padding: 0 18px;
        }

        .card {
            background: white;
            border-radius: 15px;
            padding: 35px;
            box-shadow: 0 7px 25px #00000012;
        }

        h2 { margin-top: 0; color: #164b3a; }

        p { line-height: 1.7; }

        .detalle {
            background: #f1f7f4;
            padding: 19px;
            border-radius: 10px;
            margin: 22px 0;
        }

        .acciones {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
        }

        .btn {
            padding: 13px 18px;
            border-radius: 8px;
            border: none;
            font: 600 14px 'Segoe UI', Arial, sans-serif;
            cursor: pointer;
            text-decoration: none;
        }

        .enviar { background: #188A8D; color: white; }
        .volver { background: #e8efec; color: #355a49; }
        .btn:disabled { opacity: .6; cursor: wait; }
    </style>
</head>

<body>
<header>CREINFOR</header>

<main>
    <div class="card">
        <h2>Enviar informe técnico final</h2>

        <p>
            El servicio ya fue finalizado y el informe técnico
            está guardado. Confirma su envío al cliente.
        </p>

        <div class="detalle">
            <strong>
                Solicitud #<c:out value="${solicitud.idSolicitud}"/>
            </strong>

            <p>
                <c:out value="${solicitud.motivo}"/>
            </p>

            <p>
                El PDF incluirá el informe técnico,
                los colaboradores y las actividades registradas.
            </p>
        </div>

        <form id="formEnviar"
              action="${pageContext.request.contextPath}/informes/enviar"
              method="post">

            <input type="hidden"
                   name="idSolicitud"
                   value="${solicitud.idSolicitud}">

            <input type="hidden"
                   name="tokenEnvioInforme"
                   value="${tokenEnvioInforme}">

            <div class="acciones">
                <a class="btn volver"
                   href="${pageContext.request.contextPath}/colaboradorDashboard">
                    Cancelar
                </a>

                <button type="submit"
                        class="btn enviar"
                        id="btnEnviar">
                    Enviar PDF al cliente
                </button>
            </div>
        </form>
    </div>
</main>

<script>
    const form = document.getElementById("formEnviar");
    const boton = document.getElementById("btnEnviar");
    let enviando = false;

    form.addEventListener("submit", function(event) {
        if (enviando) {
            event.preventDefault();
            return;
        }

        if (!confirm("¿Confirmas el envío del informe técnico al cliente?")) {
            event.preventDefault();
            return;
        }

        enviando = true;
        boton.disabled = true;
        boton.textContent = "Generando PDF y enviando...";
    });
</script>
</body>
</html>
