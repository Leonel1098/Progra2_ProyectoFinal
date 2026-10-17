<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inicio</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/consulta.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
</head>
<body>

<header class="navbar">
    <div class="nav-container">
        <a class="brand" href="${pageContext.request.contextPath}/inicio">
            <div class="brand-badge">🏛️</div>
            <div class="brand-text">
                <span class="brand-title">Movilidad &amp; Tránsito</span>
                <span class="brand-sub">PORTAL OFICIAL DE CONSULTA</span>
            </div>
        </a>
        <form method="post" action="${pageContext.request.contextPath}/logout">
            <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
            <button type="submit" class="btn-login">Cerrar sesión</button>
        </form>
    </div>
</header>

<main class="main-wrapper">
    <section class="hero-section">
        <span class="badge-tag"><c:out value="${sessionScope.usuarioSesion.nombreRol}"/></span>
        <h1>Hola, <c:out value="${sessionScope.usuarioSesion.nombrePersona}"/></h1>
        <p>Selecciona qué deseas hacer.</p>
    </section>

    <c:if test="${not empty infraccionPorPagar and sessionScope.usuarioSesion.nombreRol eq 'Ciudadano'}">
        <c:url var="urlPagoPendiente" value="/ciudadano/pago">
            <c:param name="numero" value="${infraccionPorPagar}"/>
        </c:url>
        <div class="alert-box" role="status">
            <span class="alert-icon">💳</span>
            <span>Elegiste pagar la infracción <strong><c:out value="${infraccionPorPagar}"/></strong>.
                <a href="${urlPagoPendiente}">Continuar con el pago</a></span>
        </div>
    </c:if>

    <div class="opciones">
        <c:if test="${sessionScope.usuarioSesion.nombreRol eq 'Ciudadano'}">
            <a class="opcion" href="${pageContext.request.contextPath}/ciudadano/infracciones">
                <strong>📋 Mis infracciones e historial</strong>
                <span>Pendientes, pagadas y detalle completo. Desde aquí puedes pagar.</span>
            </a>
        </c:if>

        <c:if test="${sessionScope.usuarioSesion.nombreRol eq 'Agente' or sessionScope.usuarioSesion.nombreRol eq 'Admin'}">
            <a class="opcion" href="${pageContext.request.contextPath}/agente/infracciones/nueva">
                <strong>📝 Registrar infracción</strong>
                <span>Crear una boleta con los datos del vehículo y la infracción.</span>
            </a>
            <c:if test="${sessionScope.usuarioSesion.nombreRol eq 'Agente'}">
                <a class="opcion" href="${pageContext.request.contextPath}/agente/operaciones">
                    <strong>👥 Personas, vehículos y consultas</strong>
                    <span>Crear y consultar personas y vehículos; buscar infracciones por placa, número o DPI.</span>
                </a>
            </c:if>
        </c:if>

        <c:if test="${sessionScope.usuarioSesion.nombreRol eq 'Admin'}">
            <div class="opcion opcion-pendiente">
                <strong>⚙️ Administración</strong>
                <span>Usuarios, tipos de infracción y reportes. Próximamente</span>
            </div>
        </c:if>
    </div></main>

</body>
</html>
