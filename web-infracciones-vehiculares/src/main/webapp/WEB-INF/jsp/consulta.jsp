<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Consulta de Infracciones Vehiculares</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/consulta.css">
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

        <c:choose>
            <c:when test="${not empty sessionScope.usuarioSesion}">
                <a class="btn-login" href="${pageContext.request.contextPath}/inicio">
                    <span>Mi cuenta</span>
                </a>
            </c:when>
            <c:otherwise>
                <a class="btn-login" href="${pageContext.request.contextPath}/login">
                    <span>Iniciar sesión</span>
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</header>

<main class="main-wrapper">
    <section class="hero-section">
        <span class="badge-tag">REGISTRO DE INFRACCIONES</span>
        <h1>Consulta de Infracciones</h1>
        <p>Verifica el estado de multas y solvencias vehiculares en tiempo real.</p>
    </section>

    <div class="card-consulta">
        <!-- Pestañas de modo -->
        <div class="tab-selector">
            <button type="button" class="tab-btn active" id="tab-placa" onclick="seleccionarModo('PLACA')">
                🚗 Por Placa
            </button>
            <button type="button" class="tab-btn" id="tab-infraccion" onclick="seleccionarModo('INFRACCION')">
                📄 Por No. Infracción
            </button>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/consulta" class="form-body">
            <input type="hidden" name="tipoBusqueda" id="tipoBusqueda" value="${empty tipoBusqueda ? 'PLACA' : tipoBusqueda}">

            <div class="field-header">
                <label id="input-label" for="valor">Placa del Vehículo</label>
                <span class="badge-req">Obligatorio</span>
            </div>

            <div class="plate-box" id="plateBox">
                <div class="plate-top">REPÚBLICA DE GUATEMALA</div>

                <div class="plate-input-container">
                    <!-- Selector de tipo de placa -->
                    <div class="plate-type-wrapper" id="plateTypeWrapper">
                        <select id="tipoPlaca" name="tipoPlaca" class="plate-type-select">

                            <c:forTokens items="P,M,C,A" delims="," var="t">
                                <option value="${t}" ${t eq (empty tipoPlaca ? 'P' : tipoPlaca) ? 'selected' : ''}>${t}</option>
                            </c:forTokens>
                        </select>
                        <span class="plate-separator">-</span>
                    </div>

                    <!-- Input para números/letras de la placa o ID -->
                    <input id="valor"
                           name="valor"
                           type="text"
                           class="plate-input"
                           maxlength="7"
                           autocomplete="off"
                           placeholder="123ABC"
                           value="<c:out value='${valor}'/>"
                           required>
                </div>

                <div class="plate-bottom" id="plateSub">CENTROAMÉRICA</div>
            </div>

            <p class="helper-text" id="helperText">Ingresa letras y números sin caracteres especiales.</p>

            <button type="submit" class="btn-submit">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
                <span>Consultar Infracciones</span>
            </button>
        </form>

        <c:if test="${not empty mensaje}">
            <div class="alert-box" role="status">
                <span class="alert-icon">ℹ️</span>
                <span><c:out value="${mensaje}"/></span>
            </div>
        </c:if>
        <!-- ================= SECCIÓN DE RESULTADOS ================= -->
        <!-- Caso 1: Solvente / Paz y Salvo (Se activará cuando 'solvente' sea true) -->
        <c:if test="${solvente eq true}">
            <div class="result-box result-success">
                <div class="result-badge-icon">✅</div>
                <div class="result-details">
                    <h3>Vehículo Solvente</h3>
                    <p>No se encontraron infracciones pendientes ni sanciones registradas para este criterio de búsqueda.</p>
                </div>
                <span class="status-pill status-solvente">Paz y Salvo</span>
            </div>
        </c:if>

        <!-- Caso 2: Listado de Infracciones encontradas -->
        <c:if test="${not empty infracciones}">
            <div class="infracciones-container">
                <div class="infracciones-header">
                    <h3>Infracciones Encontradas</h3>
                    <span class="count-badge">${infracciones.size()} registro(s)</span>
                </div>

                <div class="infracciones-list">
                    <c:forEach var="inf" items="${infracciones}">
                        <div class="infraccion-card">
                            <div class="infraccion-top">
                                <div>
                                    <span class="boleta-tag">Boleta #<c:out value="${inf.numeroBoleta}"/></span>
                                    <span class="infraccion-fecha"><c:out value="${inf.fecha}"/></span>
                                </div>
                                <span class="status-pill ${inf.pendiente ? 'status-pendiente' : 'status-pagada'}">
                                                            <c:out value="${inf.estado}"/>
                                </span>
                            </div>

                            <div class="infraccion-body">
                                <p class="infraccion-motivo"><strong>Motivo:</strong> <c:out value="${inf.motivo}"/></p>
                                <div class="infraccion-monto">
                                    <span class="monto-label">Monto total:</span>
                                    <span class="monto-valor">Q <c:out value="${inf.monto}"/></span>
                                </div>
                            </div>

                            <!-- Botón Pagar sólo si está pendiente -->
                            <c:if test="${inf.pendiente}">
                                <c:url var="urlPago" value="/login">
                                    <c:param name="redirect" value="pago"/>
                                    <c:param name="infraccionId" value="${inf.numeroBoleta}"/>
                                </c:url>
                                <div class="infraccion-actions">
                                    <a href="${urlPago}" class="btn-pagar">
                                        <span>💳 Pagar Infracción</span>
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/></svg>
                                    </a>
                                </div>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>
        <!-- ================= FIN RESULTADOS ================= -->


        <footer class="card-footer">
            🔒 Consulta pública protegida. Para ver información completa o realizar pagos deberás iniciar sesión.
        </footer>
    </div>
</main>

<script>
    function seleccionarModo(modo) {
        const hiddenInput = document.getElementById('tipoBusqueda');
        const tabPlaca = document.getElementById('tab-placa');
        const tabInfraccion = document.getElementById('tab-infraccion');
        const label = document.getElementById('input-label');
        const input = document.getElementById('valor');
        const helper = document.getElementById('helperText');
        const plateSub = document.getElementById('plateSub');
        const typeWrapper = document.getElementById('plateTypeWrapper');

        hiddenInput.value = modo;

        if (modo === 'PLACA') {
            tabPlaca.classList.add('active');
            tabInfraccion.classList.remove('active');
            label.textContent = 'Placa del Vehículo';
            typeWrapper.style.display = 'flex';
            input.placeholder = '123ABC';
            input.maxLength = 7;
            helper.textContent = 'Selecciona el tipo de placa e ingresa 3 números y 3 letras. Ej: 123ABC';
            plateSub.textContent = 'CENTROAMÉRICA';
        } else {
            tabInfraccion.classList.add('active');
            tabPlaca.classList.remove('active');
            label.textContent = 'Número de Infracción (ID)';
            typeWrapper.style.display = 'none';
            input.placeholder = 'INFR-0001';
            input.maxLength = 30;
            helper.textContent = 'Ingresa el código identificador de la boleta de infracción.';
            plateSub.textContent = 'BOLETA OFICIAL';
        }
    }
    seleccionarModo('${tipoBusqueda eq "INFRACCION" ? "INFRACCION" : "PLACA"}');
</script>
</body>
</html>