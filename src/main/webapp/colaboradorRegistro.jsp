<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="es">

<head>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Gestión de Colaboradores - CREINFOR</title>

<style>

* { box-sizing: border-box; }

body {
    margin: 0;
    font-family: Arial, Helvetica, sans-serif;
    background: #eef3f7;
    color: #263238;
}

.header {
    background: #153b5b;
    color: white;
    padding: 24px 40px;
}

.header-content {
    max-width: 1250px;
    margin: auto;
}

.header h1 {
    margin: 0;
    font-size: 27px;
}

.header p {
    margin: 6px 0 0;
    font-size: 14px;
    opacity: .9;
}

.main-container {
    max-width: 1250px;
    margin: 30px auto;
    padding: 0 20px;
}

.titulo {
    margin-bottom: 20px;
}

.titulo h2 {
    margin: 0 0 5px;
}

.titulo p {
    margin: 0;
    color: #78909c;
    font-size: 14px;
}

.layout {
    display: grid;
    grid-template-columns: 380px 1fr;
    gap: 25px;
    align-items: start;
}

.card {
    background: white;
    border-radius: 14px;
    box-shadow: 0 5px 20px rgba(0,0,0,.08);
    overflow: hidden;
}

.card-header {
    padding: 18px 22px;
    border-bottom: 1px solid #eceff1;
}

.card-header h3 {
    margin: 0;
    color: #153b5b;
    font-size: 18px;
}

.card-body {
    padding: 22px;
}

.form-group {
    margin-bottom: 16px;
}

label {
    display: block;
    margin-bottom: 7px;
    font-size: 14px;
    font-weight: 600;
    color: #37474f;
}

input, select {
    width: 100%;
    padding: 11px 12px;
    border: 1px solid #cfd8dc;
    border-radius: 7px;
    font-size: 14px;
    outline: none;
}

input:focus, select:focus {
    border-color: #1976d2;
    box-shadow: 0 0 0 3px rgba(25,118,210,.09);
}

.btn-register {
    width: 100%;
    padding: 12px;
    border: none;
    border-radius: 7px;
    background: #1976d2;
    color: white;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
}

.btn-register:hover {
    background: #125fa9;
}

.volver, .cancelar-edicion {
    display: block;
    text-align: center;
    margin-top: 14px;
    color: #1976d2;
    text-decoration: none;
    font-size: 14px;
}

.volver:hover, .cancelar-edicion:hover {
    text-decoration: underline;
}

.message {
    margin-bottom: 18px;
    padding: 12px 15px;
    border-radius: 7px;
    font-size: 14px;
}

.success {
    background: #e8f5e9;
    color: #2e7d32;
    border: 1px solid #c8e6c9;
}

.error {
    background: #ffebee;
    color: #c62828;
    border: 1px solid #ffcdd2;
}

.table-container {
    overflow-x: auto;
}

table {
    width: 100%;
    border-collapse: collapse;
}

th {
    background: #f4f7f9;
    color: #455a64;
    text-align: left;
    padding: 13px;
    font-size: 13px;
    border-bottom: 2px solid #e0e6ea;
}

td {
    padding: 13px;
    font-size: 14px;
    border-bottom: 1px solid #eceff1;
}

tbody tr:hover {
    background: #f8fbfd;
}

.rol {
    display: inline-block;
    background: #e3f2fd;
    color: #1565c0;
    padding: 5px 9px;
    border-radius: 15px;
    font-size: 12px;
    font-weight: 600;
    text-transform: capitalize;
}

.btn-edit {
    display: inline-block;
    padding: 7px 12px;
    background: #f0ad4e;
    color: white;
    border-radius: 6px;
    text-decoration: none;
    font-size: 13px;
    font-weight: 600;
}

.btn-edit:hover {
    background: #d99225;
}

.sin-registros {
    padding: 35px;
    text-align: center;
    color: #90a4ae;
}

.modo-edicion {
    padding: 10px;
    margin-bottom: 15px;
    border-radius: 7px;
    background: #fff8e1;
    color: #795548;
    font-size: 13px;
}

@media(max-width:900px) {
    .layout { grid-template-columns: 1fr; }
}

</style>

</head>

<body>

<div class="header">
    <div class="header-content">
        <h1>CREINFOR</h1>
        <p>Gestión de colaboradores</p>
    </div>
</div>

<div class="main-container">

    <div class="titulo">
        <h2>Colaboradores</h2>
        <p>Registre, consulte y actualice al personal que trabaja en la empresa.</p>
    </div>

    <c:if test="${not empty mensaje}">
        <div class="message success">${mensaje}</div>
    </c:if>

    <c:if test="${not empty errorMensaje}">
        <div class="message error">${errorMensaje}</div>
    </c:if>

    <div class="layout">

        <!-- FORMULARIO -->

        <div class="card">

            <div class="card-header">
                <h3>${empty colaboradorEditar ? 'Nuevo colaborador' : 'Editar colaborador'}</h3>
            </div>

            <div class="card-body">

                <c:if test="${not empty colaboradorEditar}">
                    <div class="modo-edicion">
                        Está modificando a <strong>${colaboradorEditar.nombres} ${colaboradorEditar.apellidos}</strong>.
                    </div>
                </c:if>

                <form action="${empty colaboradorEditar ? 'registrarColaborador' : 'editarColaborador'}" method="post">

                    <c:if test="${not empty colaboradorEditar}">
                        <input type="hidden" name="idColaborador" value="${colaboradorEditar.idColaborador}">
                    </c:if>

                    <div class="form-group">
                        <label for="nombres">Nombres</label>
                        <input type="text" id="nombres" name="nombres" value="${colaboradorEditar.nombres}" placeholder="Ingrese los nombres" required>
                    </div>

                    <div class="form-group">
                        <label for="apellidos">Apellidos</label>
                        <input type="text" id="apellidos" name="apellidos" value="${colaboradorEditar.apellidos}" placeholder="Ingrese los apellidos" required>
                    </div>

                    <div class="form-group">
                        <label for="email">Correo electrónico</label>
                        <input type="email" id="email" name="email" value="${colaboradorEditar.email}" placeholder="correo@empresa.com" required>
                    </div>

                    <div class="form-group">
                        <label for="rol">Rol</label>

                        <select id="rol" name="rol" required>
                            <option value="">Seleccione un rol</option>
                            <option value="analista" ${colaboradorEditar.rol == 'analista' ? 'selected' : ''}>Analista</option>
                            <option value="diseñador" ${colaboradorEditar.rol == 'diseñador' ? 'selected' : ''}>Diseñador</option>
                            <option value="programador" ${colaboradorEditar.rol == 'programador' ? 'selected' : ''}>Programador</option>
                            <option value="coordinador" ${colaboradorEditar.rol == 'coordinador' ? 'selected' : ''}>Coordinador</option>
                        </select>

                    </div>

                    <button type="submit" class="btn-register">
                        ${empty colaboradorEditar ? 'Registrar colaborador' : 'Guardar cambios'}
                    </button>

                </form>

                <c:if test="${not empty colaboradorEditar}">
                    <a href="registrarColaborador" class="cancelar-edicion">Cancelar edición</a>
                </c:if>

                <a href="adminDashboard" class="volver">Volver al panel administrador</a>

            </div>

        </div>


        <!-- LISTA -->

        <div class="card">

            <div class="card-header">
                <h3>Colaboradores registrados</h3>
            </div>

            <div class="card-body">

                <div class="table-container">

                    <table>

                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Colaborador</th>
                                <th>Correo</th>
                                <th>Rol</th>
                                <th>Acción</th>
                            </tr>
                        </thead>

                        <tbody>

                            <c:choose>

                                <c:when test="${empty colaboradores}">

                                    <tr>
                                        <td colspan="5" class="sin-registros">No existen colaboradores registrados.</td>
                                    </tr>

                                </c:when>

                                <c:otherwise>

                                    <c:forEach var="colaborador" items="${colaboradores}">

                                        <tr>
                                            <td>${colaborador.idColaborador}</td>

                                            <td>
                                                <strong>${colaborador.nombres} ${colaborador.apellidos}</strong>
                                            </td>

                                            <td>${colaborador.email}</td>

                                            <td>
                                                <span class="rol">${colaborador.rol}</span>
                                            </td>

                                            <td>
                                                <a href="editarColaborador?id=${colaborador.idColaborador}" class="btn-edit">Editar</a>
                                            </td>
                                        </tr>

                                    </c:forEach>

                                </c:otherwise>

                            </c:choose>

                        </tbody>

                    </table>

                </div>

            </div>

        </div>

    </div>

</div>

</body>
</html>