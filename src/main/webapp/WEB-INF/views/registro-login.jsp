<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
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
	    	<h1>Registro de usuario</h1>
	    	
	    	<!-- Muestra el mensaje de error si existe -->
		    <c:if test="${not empty error}">
		        <p style="color: red;">${error}</p>
		    </c:if>
		    
	        <form action="/usuarios/guardar" method="POST">
	            <label for="nombre">Nombre:</label>
	            <input type="text" id="nombre" name="nombre" class="etivertical" required>
	
	            <label for="correo">Correo electronico:</label>
	            <input type="text" id="correo" name="correo" class="etivertical" required>
	
	            <label for="contras">Contraseña:</label>
	            <input type="password" id="contras" name="contras" class="etipass" required>
	
	            <button class="botonRegistrarse" type="submit" id="enviar">Registrarse</button>
	        </form>
	    </div>
    
    
	    <div class="form">
		    <h1>Iniciar sesión</h1>
		    
		    <!-- Muestra el mensaje de error si las credenciales son incorrectas -->
		    <c:if test="${not empty error}">
		        <p style="color: red;">${error}</p>
		    </c:if>
		    
		    
		    <form action="/usuarios/acceso" method="POST">
		        <label for="correo">Correo electronico:</label>
	            <input type="text" id="correo" name="correo" class="etivertical" required>
		
		        <label for="contrasenaIngresada">Contraseña:</label>
		        <input type="password" id="contrasenaIngresada" name="contrasenaIngresada" class="etipass" required>
		
		        <button class="botonRegistrarse" type="submit" id="enviar">Iniciar sesión</button>
		    </form>
	    </div>
    </div>
	
	
    
    <div class="pie-pagina">
        <h3 class="pie">Esta es una web creada por A.Munilla. 2023-2024</h3>
    </div>


</body>
</html>