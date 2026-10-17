<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Iniciar sesión</title>
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
        <div class="auth-nav-actions">
            <a class="btn-login" href="${pageContext.request.contextPath}/registro">Crear cuenta</a>
            <a class="auth-nav-back" href="${pageContext.request.contextPath}/">Volver a la consulta</a>
        </div>
    </div>
</header>

<main class="main-wrapper auth-wrapper">
    <section class="hero-section">
        <span class="badge-tag">ACCESO AL SISTEMA</span>
        <h1>Iniciar sesión</h1>
        <p>Ingresa con tu usuario para ver tu información y realizar trámites.</p>
    </section>

    <div class="card-consulta">
        <c:if test="${not empty infraccionId and redirect eq 'pago'}">
            <div class="alert-box" role="status">
                <span class="alert-icon">💳</span>
                <span>Inicia sesión para continuar con el pago de la infracción
                    <strong><c:out value="${infraccionId}"/></strong>.</span>
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login" class="form-body" autocomplete="on">
            <input type="hidden" name="_csrf" value="<c:out value='${csrf}'/>">
            <input type="hidden" name="redirect" value="<c:out value='${redirect}'/>">
            <input type="hidden" name="infraccionId" value="<c:out value='${infraccionId}'/>">

            <div class="campo">
                <label for="usuario">Usuario</label>
                <input id="usuario" name="usuario" type="text" maxlength="20"
                       autocomplete="username" required autofocus
                       value="<c:out value='${usuario}'/>">
            </div>

            <div class="campo">
                <label for="contrasena">Contraseña</label>
                <input id="contrasena" name="contrasena" type="password" maxlength="128"
                       autocomplete="current-password" required>
            </div>

            <button type="submit" class="btn-submit">
                <span>Ingresar</span>
            </button>
        </form>

        <c:if test="${not empty mensaje}">
            <div class="alert-box alert-error" role="alert">
                <span class="alert-icon">⚠️</span>
                <span><c:out value="${mensaje}"/></span>
            </div>
        </c:if>
    </div>
</main>

</body>
</html>
