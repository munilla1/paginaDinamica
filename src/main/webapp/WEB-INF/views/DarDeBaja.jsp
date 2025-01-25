<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
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
	    <a href="/usuarios/registro-login" class="botonDesplegable">Login/Registro</a>
	    
	    <a href="/usuarios/DarDeBaja" class="botonDesplegable">Eliminar usuario</a>
	</div>
    <div class="contcerrarsesion">
    	<a href="${pageContext.request.contextPath}/logout">
        <input type="submit" id="cerrarsesion" value="Cerrar sesión">
        </a>
        <p class="mensaje">Usuario: ${sessionScope.nombre}</p>
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
		    
		    
		    <form action="/usuarios/eliminar" method="POST">
		        <label for="correo">Correo electronico:</label>
	            <input type="text" id="correo" name="correo" class="etivertical" required>
		
		        <label for="contrasenaIngresada">Contraseña:</label>
		        <input type="password" id="contrasenaIngresada" name="contrasenaIngresada" class="etipass" required>
		
		        <button class="botonRegistrarse" type="submit" id="enviar">Eliminar</button>
		    </form>
	    </div>
</div>

<div class="pie-pagina">
    <h3 class="pie">Esta es una web creada por A.Munilla. 2023-2024</h3>
</div>

</body>
</html>