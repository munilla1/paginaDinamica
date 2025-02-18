<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Destruction app</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/navbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/pp.css">
    <link href="contactob.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
</head>
<body>
<div class="container-menu">
	<div class="centered">

		    <a href="/registro-login" class="botonDesplegable">Login/Registro</a>

		    <a href="/DarDeBaja" class="botonDesplegable">Eliminar usuario</a>

	</div>
    <div class="contcerrarsesion">
	    <c:if test="${not empty sessionScope.usuario}">
	        <p class="mensaje">Usuario: ${sessionScope.usuario.username}</p>
	        <form action="${pageContext.request.contextPath}/logout" method="POST">
	            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	            <button type="submit" id="cerrarsesion">Cerrar sesión</button>
	        </form>
	    </c:if>
	</div>


</div>

<video class="video-container" id="video-bg" muted autoplay loop>
    <source src="${pageContext.request.contextPath}/imagenes/espacio.mp4" type="video/mp4">
</video>

<div class="contenedor-mensaje">
    <div class="cont">
        <img class="telefono" src="${pageContext.request.contextPath}/imagenes/palomita-correcto.png">
        <h3 class="mensajeconf">Bienvenido: ${sessionScope.usuario.username}, acceso confirmado.</h3>
    </div>
</div>

<div class="container-botonJugar">
    <button class="botonJugar">Jugar 🚀</button>
</div>


<div class="pie-pagina">
    <h3 class="pie">Esta es una web creada por A.Munilla. 2023-2024</h3>
</div>

</body>
</html>