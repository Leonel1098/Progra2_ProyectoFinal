<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Crear cuenta</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/consulta.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>
<header class="navbar">
    <div class="nav-container">
        <a class="brand" href="${pageContext.request.contextPath}/">
            <div class="brand-badge">🏛️</div>
            <div class="brand-text">
                <span class="brand-title">Movilidad &amp; Tránsito</span>
                <span class="brand-sub">PORTAL OFICIAL DE CONSULTA</span>
            </div>
        </a>
        <a class="btn-login" href="${pageContext.request.contextPath}/login">Iniciar sesión</a>
    </div>
</header>

<main class="main-wrapper auth-wrapper">
    <section class="hero-section">
        <span class="badge-tag">NUEVA CUENTA CIUDADANA</span>
        <h1>Crear cuenta</h1>
        <p>Usa los datos con los que estás registrado en el sistema para validar tu identidad.</p>
    </section>

    <div class="card-consulta">
        <form method="post" action="${pageContext.request.contextPath}/registro" class="form-body" autocomplete="on">
            <input type="hidden" name="_csrf" value="<c:out value='${csrf}'/>">
            <div class="campo">
                <label for="dpi">DPI</label>
                <input id="dpi" name="dpi" type="text" maxlength="20" autocomplete="off" required
                       value="<c:out value='${dpi}'/>">
            </div>
            <div class="campo">
                <label for="nombre">Nombre</label>
                <input id="nombre" name="nombre" type="text" maxlength="100" autocomplete="given-name" required
                       value="<c:out value='${nombre}'/>">
            </div>
            <div class="campo">
                <label for="apellido">Apellido</label>
                <input id="apellido" name="apellido" type="text" maxlength="100" autocomplete="family-name" required
                       value="<c:out value='${apellido}'/>">
            </div>
            <div class="campo">
                <label for="usuario">Nombre de usuario</label>
                <input id="usuario" name="usuario" type="text" maxlength="20" autocomplete="username" required
                       value="<c:out value='${usuario}'/>">
            </div>
            <div class="campo">
                <label for="contrasena">Contraseña</label>
                <input id="contrasena" name="contrasena" type="password" maxlength="128"
                       autocomplete="new-password" required>
            </div>
            <button type="submit" class="btn-submit"><span>Crear cuenta</span></button>
        </form>

        <c:if test="${not empty mensaje}">
            <div class="alert-box alert-error" role="alert">
                <span class="alert-icon">⚠️</span>
                <span><c:out value="${mensaje}"/></span>
            </div>
        </c:if>
        <p class="auth-switch">¿Ya tienes cuenta? <a href="${pageContext.request.contextPath}/login">Inicia sesión</a></p>
    </div>
</main>
</body>
</html>
