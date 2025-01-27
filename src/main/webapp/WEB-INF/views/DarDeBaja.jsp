<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!doctype html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Destruction app</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/navbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pp.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">

</head>
<body>
<div class="container-menu">
	<div class="centered"> 
	    <sec:authorize access="isAnonymous()">
		    <a href="/registro-login" class="botonDesplegable">Login/Registro</a>
		</sec:authorize>
	    
	    <sec:authorize access="isAnonymous()">
		    <a href="/DarDeBaja" class="botonDesplegable">Eliminar usuario</a>
	    </sec:authorize>
	</div>
    <div class="contcerrarsesion">
	    <c:if test="${not empty sessionScope.usuario}">
	        <p class="mensaje">Usuario: ${sessionScope.usuario.nombre}</p>
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

<div class="formularios">
	<div class="form">
		    <h1>Eliminar usuario</h1>
		    
		    <!-- Muestra el mensaje de error si las credenciales son incorrectas -->
		    <c:if test="${not empty error}">
		        <p style="color: red;">${error}</p>
		    </c:if>
		    
		    
		    <form action="/eliminar" method="POST">
    			
		        <label for="correo">Correo electronico:</label>
	            <input type="text" id="correo" name="correo" class="etivertical" required>
		
		        <label for="contrasenaIngresada">Contraseña:</label>
		        <input type="password" id="contrasenaIngresada" name="contrasenaIngresada" class="etipass" required>
		
		        <button class="botonRegistrarse" type="submit" id="enviar">Eliminar usuario</button>
		    </form>
	    </div>
</div>

<div class="pie-pagina">
    <h3 class="pie">Esta es una web creada por A.Munilla. 2023-2024</h3>
</div>

</body>
</html>