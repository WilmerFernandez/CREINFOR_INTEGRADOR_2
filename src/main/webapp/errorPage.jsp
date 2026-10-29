<%-- src/main/webapp/WEB-INF/views/errorPage.jsp --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; padding: 20px; border-radius: 5px; text-align: center; }
        h1 { color: #dc3545; }
        p { margin-top: 15px; }
    </style>
</head>
<body>
    <h1>¡Ha ocurrido un error!</h1>
    <p>Lo sentimos, no pudimos procesar su solicitud en este momento.</p>
    <c:if test="${not empty errorMessage}">
        <p>Detalle del error: <strong>${errorMessage}</strong></p>
    </c:if>
    <p><a href="${pageContext.request.contextPath}/">Volver a la página de inicio</a></p>
</body>
</html>