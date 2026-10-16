<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mis infracciones</title>
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
        <a class="btn-login" href="${pageContext.request.contextPath}/inicio">Volver al inicio</a>
    </div>
</header>

<main class="main-wrapper">
    <section class="hero-section">
        <span class="badge-tag">MI CUENTA</span>
        <h1>Mis infracciones</h1>
        <p>Infracciones pendientes e historial asociado a tu DPI.</p>
    </section>

    <div class="card-consulta">
        <section class="auth-search">
            <h2>Buscar entre tus infracciones</h2>
            <p>Los resultados se limitan a las infracciones asociadas a tu cuenta.</p>
            <form method="post" action="${pageContext.request.contextPath}/ciudadano/infracciones/consulta" class="form-body">
                <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
                <div class="campo">
                    <label for="tipoBusqueda">Buscar por</label>
                    <select id="tipoBusqueda" name="tipoBusqueda" class="auth-select" onchange="actualizarBusqueda()">
                        <option value="PLACA" ${tipoBusqueda eq 'PLACA' ? 'selected' : ''}>Placa</option>
                        <option value="INFRACCION" ${tipoBusqueda eq 'INFRACCION' ? 'selected' : ''}>Número de infracción</option>
                    </select>
                </div>
                <div class="campo" id="campoTipoPlaca" ${tipoBusqueda eq 'INFRACCION' ? 'hidden' : ''}>
                    <label for="tipoPlaca">Tipo de placa</label>
                    <select id="tipoPlaca" name="tipoPlaca" class="auth-select">
                        <c:forTokens items="P,M,C,A" delims="," var="t">
                            <option value="${t}" ${t eq tipoPlaca ? 'selected' : ''}>${t}</option>
                        </c:forTokens>
                    </select>
                </div>
                <div class="campo">
                    <label for="valor" id="labelBusqueda">${tipoBusqueda eq 'INFRACCION' ? 'Número de infracción' : 'Placa'}</label>
                    <input id="valor" name="valor" type="text" maxlength="30" required
                           placeholder="${tipoBusqueda eq 'INFRACCION' ? 'INFR-0001' : '123ABC'}"
                           value="<c:out value='${valor}'/>" autocomplete="off">
                </div>
                <button type="submit" class="btn-submit"><span>Buscar mis infracciones</span></button>
            </form>
            <c:if test="${not empty mensajeBusqueda}">
                <div class="alert-box alert-error" role="alert"><c:out value="${mensajeBusqueda}"/></div>
            </c:if>
            <c:if test="${sinResultados}">
                <p class="helper-text">No se encontraron infracciones tuyas con ese criterio.</p>
            </c:if>
            <c:if test="${not empty resultadosBusqueda}">
                <div class="infracciones-list">
                    <c:forEach var="inf" items="${resultadosBusqueda}">
                        <div class="infraccion-card">
                            <div class="infraccion-top">
                                <span class="boleta-tag">Boleta #<c:out value="${inf.numeroBoleta}"/></span>
                                <span class="status-pill ${inf.estado eq 'Pendiente' ? 'status-pendiente' : 'status-pagada'}"><c:out value="${inf.estado}"/></span>
                            </div>
                            <div class="infraccion-body">
                                <p class="infraccion-motivo"><strong>Fecha:</strong> <c:out value="${inf.fecha}"/></p>
                                <p class="infraccion-motivo"><strong>Motivo:</strong> <c:out value="${inf.motivo}"/></p>
                                <div class="infraccion-monto"><span class="monto-label">Monto:</span><span class="monto-valor">Q <c:out value="${inf.monto}"/></span></div>
                            </div>
                            <c:if test="${inf.estado eq 'Pendiente'}">
                                <c:url var="urlPagoBusqueda" value="/ciudadano/pago"><c:param name="numero" value="${inf.numeroBoleta}"/></c:url>
                                <div class="infraccion-actions"><a href="${urlPagoBusqueda}" class="btn-pagar">Pagar infracción</a></div>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
        </section>

        <c:if test="${not empty mensaje}">
            <div class="alert-box" role="status">
                <span class="alert-icon">ℹ️</span>
                <span><c:out value="${mensaje}"/></span>
            </div>
        </c:if>

        <div class="infracciones-container">
            <div class="infracciones-header">
                <h3>Pendientes de pago</h3>
                <span class="count-badge">${pendientes.size()} registro(s)</span>
            </div>
            <div class="infracciones-list">
                <c:forEach var="inf" items="${pendientes}">
                    <div class="infraccion-card">
                        <div class="infraccion-top">
                            <div>
                                <span class="boleta-tag">Boleta #<c:out value="${inf.numeroInfraccion}"/></span>
                                <span class="infraccion-fecha"><c:out value="${inf.fechaInfraccion}"/></span>
                            </div>
                            <span class="status-pill status-pendiente"><c:out value="${inf.estado}"/></span>
                        </div>
                        <div class="infraccion-body">
                            <p class="infraccion-motivo"><strong>Motivo:</strong> <c:out value="${inf.tipoInfraccion}"/></p>
                            <p class="infraccion-motivo"><strong>Placa:</strong> <c:out value="${inf.placaVehiculo}"/></p>
                            <p class="infraccion-motivo"><strong>Lugar:</strong> <c:out value="${inf.direccionInfraccion}"/></p>
                            <div class="infraccion-monto">
                                <span class="monto-label">Monto original:</span>
                                <span class="monto-valor">Q <c:out value="${inf.montoOriginal}"/></span>
                            </div>
                        </div>
                        <c:url var="urlPago" value="/ciudadano/pago">
                            <c:param name="numero" value="${inf.numeroInfraccion}"/>
                        </c:url>
                        <div class="infraccion-actions">
                            <a href="${urlPago}" class="btn-pagar"><span>💳 Pagar Infracción</span></a>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${empty pendientes and empty mensaje}">
                    <p class="helper-text">No tienes infracciones pendientes.</p>
                </c:if>
            </div>
        </div>

        <div class="infracciones-container">
            <div class="infracciones-header">
                <h3>Historial</h3>
                <span class="count-badge">${historial.size()} registro(s)</span>
            </div>
            <div class="infracciones-list">
                <c:forEach var="inf" items="${historial}">
                    <div class="infraccion-card">
                        <div class="infraccion-top">
                            <div>
                                <span class="boleta-tag">Boleta #<c:out value="${inf.numeroInfraccion}"/></span>
                                <span class="infraccion-fecha"><c:out value="${inf.fechaInfraccion}"/></span>
                            </div>
                            <span class="status-pill status-pagada"><c:out value="${inf.estado}"/></span>
                        </div>
                        <div class="infraccion-body">
                            <p class="infraccion-motivo"><strong>Motivo:</strong> <c:out value="${inf.tipoInfraccion}"/></p>
                            <p class="infraccion-motivo"><strong>Placa:</strong> <c:out value="${inf.placaVehiculo}"/></p>
                            <div class="infraccion-monto">
                                <span class="monto-label">Monto original:</span>
                                <span class="monto-valor">Q <c:out value="${inf.montoOriginal}"/></span>
                            </div>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${empty historial and empty mensaje}">
                    <p class="helper-text">Aún no tienes historial.</p>
                </c:if>
            </div>
        </div>
    </div>
</main>

<script>
    function actualizarBusqueda() {
        const porPlaca = document.getElementById('tipoBusqueda').value === 'PLACA';
        document.getElementById('campoTipoPlaca').hidden = !porPlaca;
        document.getElementById('labelBusqueda').textContent = porPlaca ? 'Placa' : 'Número de infracción';
        document.getElementById('valor').placeholder = porPlaca ? '123ABC' : 'INFR-0001';
    }
</script>

</body>
</html>
