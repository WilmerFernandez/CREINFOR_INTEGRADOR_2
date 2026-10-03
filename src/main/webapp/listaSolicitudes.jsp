<%-- src/main/webapp/WEB-INF/views/admin/listaSolicitudes.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Lista de Solicitudes - Panel de Administración</title>
        <!-- Incluimos Bootstrap para un diseño más profesional -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Incluimos íconos de Bootstrap -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
        <style>
            :root {
                --color-pendiente: #fff3cd;
                --color-proceso: #cce5ff;
                --color-finalizada: #d4edda;
                --color-rechazada: #f8d7da;
            }

            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f8f9fa;
            }

            .container-main {
                max-width: 1400px;
                margin: 30px auto;
                background-color: white;
                border-radius: 10px;
                box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
                padding: 30px;
            }

            .page-title {
                color: #2c3e50;
                margin-bottom: 30px;
                padding-bottom: 15px;
                border-bottom: 2px solid #e9ecef;
                font-weight: 600;
            }

            .search-card {
                background-color: #f8f9fa;
                border-radius: 8px;
                padding: 20px;
                margin-bottom: 25px;
                border: 1px solid #e0e0e0;
            }

            .badge-estado {
                padding: 6px 10px;
                border-radius: 20px;
                font-weight: 500;
                font-size: 0.85rem;
                text-transform: capitalize;
            }

            .badge-pendiente {
                background-color: var(--color-pendiente);
                color: #856404;
            }

            .badge-proceso {
                background-color: var(--color-proceso);
                color: #004085;
            }

            .badge-finalizada {
                background-color: var(--color-finalizada);
                color: #155724;
            }

            .badge-rechazada {
                background-color: var(--color-rechazada);
                color: #721c24;
            }

            .table-responsive {
                overflow-x: auto;
            }

            .table-custom {
                border-collapse: separate;
                border-spacing: 0;
            }

            .table-custom thead th {
                background-color: #2c3e50;
                color: white;
                position: sticky;
                top: 0;
            }

            .table-custom tbody tr:hover {
                background-color: rgba(0, 0, 0, 0.02);
            }

            .no-records {
                text-align: center;
                padding: 50px;
                color: #6c757d;
                font-size: 1.1rem;
            }

            .action-btn {
                padding: 5px 10px;
                border-radius: 4px;
                font-size: 0.85rem;
            }

            .btn-view {
                background-color: #17a2b8;
                color: white;
            }

            .btn-view:hover {
                background-color: #138496;
                color: white;
            }

            .pagination-info {
                font-size: 0.9rem;
                color: #6c757d;
            }
        </style>
    </head>
    <body>
        <div class="container container-main">
            <h1 class="page-title">
                <i class="bi bi-list-check"></i> Gestión de Solicitudes
            </h1>

            <%-- Mostrar mensajes de error o éxito --%>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle-fill"></i> ${errorMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle-fill"></i> ${successMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <%-- Formulario de búsqueda mejorado --%>
            <div class="search-card">
                <form action="${pageContext.request.contextPath}/solicitudes" method="get" class="row g-3">
                    <div class="col-md-4">
                        <label for="tipo_solicitud" class="form-label">Tipo</label>
                        <input type="text" class="form-control" id="tipo_solicitud" name="tipo_solicitud" 
                               value="${param.motivo}" placeholder="Buscar por tipo">
                    </div>

                    <div class="col-md-3">
                        <label for="colaborador" class="form-label">Colaborador</label>
                        <input type="text" class="form-control" id="colaborador" name="colaborador" 
                               value="${param.colaborador}" placeholder="Nombre o apellido">



                    </div>

                    <div class="col-md-3">
                        <label for="estado" class="form-label">Estado</label>
                        <input type="text" class="form-control" id="estado" name="estado" 
                               value="${param.estado}" placeholder="estado">
                    </div>

                    <div class="col-md-2 d-flex align-items-end">
                        <button type="submit" class="btn btn-primary me-2">
                            <i class="bi bi-search"></i> Buscar
                        </button>
                        <a href="${pageContext.request.contextPath}/solicitudes" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-counterclockwise"></i> Limpiar
                        </a>
                    </div>
                </form>
            </div>

            <%-- Lista de solicitudes --%>
            <c:choose>
                <c:when test="${not empty solicitudes}">
                    <div class="table-responsive">
                        <table class="table table-custom table-hover">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Tipo</th>
                                    <th>Motivo</th>
                                    <th>Fecha Registro</th>
                                    <th>Estado</th>
                                    <th>Cliente</th>
                                    <th>Email</th>
                                    <th>Colaborador</th>
                                    <th>Rol</th>
                                    <th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="solicitud" items="${solicitudes}">
                                    <tr>
                                        <td>${solicitud.idSolicitud}</td>
                                        <td>${solicitud.tipoSolicitud}</td>
                                        <td>
                                            <span data-bs-toggle="tooltip" title="${solicitud.motivo}">
                                                ${fn:substring(solicitud.motivo, 0, 30)}${fn:length(solicitud.motivo) > 30 ? '...' : ''}
                                            </span>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${solicitud.fechaRegistroAsUtilDate}" 
                                                            pattern="dd/MM/yyyy HH:mm"/>
                                        </td>
                                        <td>
                                            <c:set var="estadoClass" value="badge-${fn:replace(fn:toLowerCase(solicitud.estado), ' ', '-')}"/>
                                            <span class="badge-estado ${estadoClass}">
                                                ${solicitud.estado}
                                            </span>
                                        </td>
                                        <td>${solicitud.nombreCliente} ${solicitud.apellidoCliente}</td>
                                        <td>${solicitud.emailCliente}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty solicitud.nombreColaboradorAsignado}">
                                                    ${solicitud.nombreColaboradorAsignado} ${solicitud.apellidoColaboradorAsignado}
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted">No asignado</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty solicitud.rolColaboradorAsignado}">
                                                    ${solicitud.rolColaboradorAsignado}
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted">N/A</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/detalleSolicitud?idSolicitud=${solicitud.idSolicitud}"  
                                               class="btn btn-sm action-btn btn-view" 
                                               title="Ver detalles" 
                                               target="_blank">
                                                <i class="bi bi-eye-fill"></i>
                                            </a>

                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <%-- Paginación --%>
                    <div class="row mt-3">
                        <div class="col-md-6">
                            <span class="pagination-info">
                                Mostrando ${(currentPage - 1) * pageSize + 1} a 
                                ${currentPage * pageSize > totalItems ? totalItems : currentPage * pageSize} 
                                de ${totalItems} solicitudes
                            </span>
                        </div>
                        <div class="col-md-6">
                            <nav aria-label="Page navigation" class="float-end">
                                <ul class="pagination pagination-sm">
                                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="?page=1&motivo=${param.motivo}&colaborador=${param.colaborador}&estado=${param.estado}">
                                            <i class="bi bi-chevron-double-left"></i>
                                        </a>
                                    </li>
                                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="?page=${currentPage - 1}&motivo=${param.motivo}&colaborador=${param.colaborador}&estado=${param.estado}">
                                            <i class="bi bi-chevron-left"></i>
                                        </a>
                                    </li>

                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                                            <a class="page-link" href="?page=${i}&motivo=${param.motivo}&colaborador=${param.colaborador}&estado=${param.estado}">${i}</a>
                                        </li>
                                    </c:forEach>

                                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="?page=${currentPage + 1}&motivo=${param.motivo}&colaborador=${param.colaborador}&estado=${param.estado}">
                                            <i class="bi bi-chevron-right"></i>
                                        </a>
                                    </li>
                                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="?page=${totalPages}&motivo=${param.motivo}&colaborador=${param.colaborador}&estado=${param.estado}">
                                            <i class="bi bi-chevron-double-right"></i>
                                        </a>
                                    </li>
                                </ul>
                            </nav>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="no-records">
                        <i class="bi bi-inbox" style="font-size: 2rem;"></i>
                        <p class="mt-3">No se encontraron solicitudes con los criterios de búsqueda</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Scripts de Bootstrap y funcionalidad adicional -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Activar tooltips
            document.addEventListener('DOMContentLoaded', function () {
                var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
                var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
                    return new bootstrap.Tooltip(tooltipTriggerEl);
                });

                // Limpiar parámetros de búsqueda al hacer clic en el botón Limpiar
                document.getElementById('btnLimpiar').addEventListener('click', function () {
                    document.getElementById('motivo').value = '';
                    document.getElementById('colaborador').value = '';
                    document.getElementById('estado').value = '';
                });
            });
        </script>
    </body>
</html>