<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Pago de infracción</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/consulta.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/auth.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pago.css">
</head>
<body class="pago-page">

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
        <span class="badge-tag">PAGOS</span>
        <h1>Pago de infracción</h1>
    </section>

    <div class="card-consulta">
        <c:if test="${not empty mensaje}">
            <div class="alert-box" role="alert">
                <span class="alert-icon">ℹ️</span>
                <span><c:out value="${mensaje}"/></span>
            </div>
        </c:if>

        <c:if test="${not empty pago}">
            <div class="infraccion-card pago-card">
                <div class="infraccion-body pago-body">
                    <p class="infraccion-motivo"><strong>Boleta:</strong> <c:out value="${pago.numeroInfraccion}"/></p>
                    <p class="infraccion-motivo"><strong>Monto inicial:</strong> Q <c:out value="${pago.montoInicial}"/></p>
                    <p class="infraccion-motivo"><strong>Descuento (<c:out value="${pago.porcentajeDescuento}"/>%):</strong> Q <c:out value="${pago.montoDescuento}"/></p>
                    <div class="infraccion-monto">
                        <span class="monto-label">Total a pagar:</span>
                        <span class="monto-valor">Q <c:out value="${pago.montoPagado}"/></span>
                    </div>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/ciudadano/pago" class="infraccion-actions">
                    <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
                    <input type="hidden" name="numero" value="<c:out value='${numero}'/>">
                    <button type="submit" class="btn-pagar">💳 Confirmar pago</button>
                </form>
            </div>
        </c:if>

        <c:if test="${not empty comprobante}">
            <div class="result-box result-success pago-result">
                <div class="result-badge-icon">✅</div>
                <div class="result-details">
                    <h3>Pago realizado</h3>
                    <p>Boleta <c:out value="${comprobante.numeroInfraccion}"/> —
                        Total pagado: Q <c:out value="${comprobante.montoPagado}"/>
                        (descuento Q <c:out value="${comprobante.montoDescuento}"/>) el
                        <c:out value="${comprobante.fechaPago}"/>.</p>
                </div>
                <span class="status-pill status-solvente">Pagada</span>
            </div>
        </c:if>

        <div class="infraccion-actions">
            <a class="btn-pagar" href="${pageContext.request.contextPath}/ciudadano/infracciones">Ver mis infracciones</a>
        </div>
    </div>
</main>

</body>
</html>
