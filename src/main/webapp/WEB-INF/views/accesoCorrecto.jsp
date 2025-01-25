<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	    <a href="/usuarios/registro-login" class="botonDesplegable">Login</a>

	    <a href="/usuarios/registro-login" class="botonDesplegable">Registro</a>
	    
	    <a href="/usuarios/registro-login" class="botonDesplegable">Eliminar usuario</a>
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

<div class="contenedor-mensaje">
    <div class="cont">
        <img class="telefono" src="${pageContext.request.contextPath}/imagenes/palomita-correcto.png">
        <h3 class="mensajeconf">Bienvenido: ${sessionScope.usuario.nombre}, acceso confirmado.</h3>
    </div>
</div>

<div class="container-botonJugar">
	<a href="/" class="botonDesplegable">Jugar ya!</a>
</div>

<div class="pie-pagina">
    <h3 class="pie">Esta es una web creada por A.Munilla. 2023-2024</h3>
</div>

</body>
</html>