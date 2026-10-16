<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Portal Ciudadano - Movilidad y Tránsito</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@700&display=swap" rel="stylesheet">
    <script src="https://unpkg.com/lucide@latest"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    fontFamily: {
                        sans: ['Plus Jakarta Sans', 'sans-serif'],
                        mono: ['JetBrains Mono', 'monospace']
                    },
                    colors: {
                        brand: {
                            50: '#eef5fc', 100: '#d7e7f9', 500: '#1a4b8c', 600: '#153e74',
                            700: '#0f2f59', 800: '#0b213f', 900: '#07162b'
                        }
                    }
                }
            }
        }
    </script>
</head>
<body class="bg-[#f7f9fb] font-sans antialiased text-slate-800 min-h-screen scroll-smooth">

<!-- ================= HEADER ================= -->
<header class="sticky top-0 z-40 bg-white/90 backdrop-blur-md border-b border-slate-200/80">
    <div class="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between gap-4">
        <a href="${pageContext.request.contextPath}/inicio" class="flex items-center gap-2.5">
            <div class="w-9 h-9 rounded-xl bg-brand-500 text-white flex items-center justify-center shadow-md shadow-brand-500/20">
                <i data-lucide="landmark" class="w-5 h-5"></i>
            </div>
            <div>
                <span class="text-[10px] uppercase font-bold tracking-wider text-slate-400 block leading-tight">Portal Ciudadano</span>
                <span class="text-base font-extrabold text-slate-900 leading-tight">Mi cuenta</span>
            </div>
        </a>

        <!-- Menú de escritorio -->
        <nav class="hidden lg:flex items-center gap-6 text-sm font-semibold">
            <a href="#buscar" class="text-slate-500 hover:text-brand-700">Buscar</a>
            <a href="#pendientes" class="text-slate-500 hover:text-brand-700">Pendientes</a>
            <a href="#historial" class="text-slate-500 hover:text-brand-700">Historial</a>
        </nav>

        <a href="${pageContext.request.contextPath}/inicio"
           class="inline-flex items-center gap-1.5 px-3 py-2 rounded-full bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold transition">
            <i data-lucide="arrow-left" class="w-4 h-4"></i>
            <span class="hidden sm:inline">Volver al inicio</span>
            <span class="sm:hidden">Inicio</span>
        </a>
    </div>
</header>

<!-- ================= CONTENIDO ================= -->
<main class="w-full max-w-6xl mx-auto p-4 pb-28 lg:pb-8 grid grid-cols-1 lg:grid-cols-3 gap-4 lg:gap-6 items-start">

    <!-- ===== Columna izquierda ===== -->
    <aside class="space-y-4 lg:col-span-1 lg:sticky lg:top-20">

        <!-- Perfil -->
        <section class="bg-gradient-to-br from-white to-blue-50/40 p-4 rounded-2xl border border-blue-100 shadow-sm">
            <div class="flex justify-between items-start gap-3">
                <div class="min-w-0">
                    <div class="flex items-center gap-1.5 text-[11px] font-bold text-brand-500 uppercase tracking-wider mb-1">
                        <i data-lucide="badge-check" class="w-3.5 h-3.5 text-blue-500"></i>
                        <span>Perfil verificado</span>
                    </div>
                    <h1 class="text-xl font-extrabold text-slate-900 tracking-tight">¡Hola!</h1>
                    <p class="text-xs text-slate-500 mt-0.5">Infracciones pendientes e historial asociado a tu DPI.</p>
                </div>
                <div class="w-9 h-9 shrink-0 rounded-full bg-blue-100 text-blue-700 flex items-center justify-center">
                    <i data-lucide="fingerprint" class="w-5 h-5"></i>
                </div>
            </div>
            <c:if test="${not empty dpi}">
                <div class="flex flex-wrap gap-2 mt-3">
                    <div class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-slate-100/90 border border-slate-200/80 text-[11px] font-semibold text-slate-700">
                        <i data-lucide="id-card" class="w-3.5 h-3.5 text-slate-500"></i>
                        <span>DPI: <strong><c:out value="${dpi}"/></strong></span>
                    </div>
                </div>
            </c:if>
        </section>

        <!-- Aviso de estado -->
        <c:if test="${not empty mensaje}">
            <div class="bg-sky-50 border border-sky-200 rounded-2xl p-3.5 flex items-start gap-3 shadow-sm" role="status">
                <i data-lucide="info" class="w-5 h-5 text-sky-600 shrink-0 mt-0.5"></i>
                <span class="text-sm text-sky-900"><c:out value="${mensaje}"/></span>
            </div>
        </c:if>

        <c:if test="${empty mensaje}">
            <c:choose>
                <c:when test="${pendientes.size() > 0}">
                    <a href="#pendientes" class="bg-amber-50/90 border border-amber-200/80 rounded-2xl p-3.5 flex items-center justify-between shadow-sm hover:bg-amber-50 transition">
                        <div class="flex items-center gap-3">
                            <div class="w-10 h-10 rounded-xl bg-amber-100 text-amber-700 flex items-center justify-center shrink-0">
                                <i data-lucide="alert-triangle" class="w-5 h-5"></i>
                            </div>
                            <div>
                                <span class="text-[10px] font-bold uppercase tracking-wider text-amber-800">Estado de conductor</span>
                                <h4 class="text-sm font-bold text-slate-900 leading-snug">${pendientes.size()} infracción(es) pendiente(s)</h4>
                            </div>
                        </div>
                        <i data-lucide="arrow-down" class="w-4 h-4 text-amber-800"></i>
                    </a>
                </c:when>
                <c:otherwise>
                    <div class="bg-emerald-50 border border-emerald-200 rounded-2xl p-3.5 flex items-center gap-3 shadow-sm">
                        <div class="w-10 h-10 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center shrink-0">
                            <i data-lucide="shield-check" class="w-5 h-5"></i>
                        </div>
                        <div>
                            <span class="text-[10px] font-bold uppercase tracking-wider text-emerald-800">Estado de conductor</span>
                            <h4 class="text-sm font-bold text-slate-900 leading-snug">Sin infracciones pendientes</h4>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>

        <!-- Buscador -->
        <section id="buscar" class="bg-white rounded-2xl border border-slate-200 shadow-sm p-4 scroll-mt-24">
            <h2 class="text-sm font-extrabold text-slate-900">Buscar entre tus infracciones</h2>
            <p class="text-xs text-slate-500 mt-0.5 mb-3">Los resultados se limitan a las infracciones asociadas a tu cuenta.</p>

            <form method="post" action="${pageContext.request.contextPath}/ciudadano/infracciones/consulta" class="space-y-3">
                <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">

                <div>
                    <label for="tipoBusqueda" class="block text-[11px] font-bold uppercase tracking-wider text-slate-500 mb-1">Buscar por</label>
                    <select id="tipoBusqueda" name="tipoBusqueda" onchange="actualizarBusqueda()"
                            class="w-full rounded-xl border border-slate-300 bg-white px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-brand-500/40">
                        <option value="PLACA" ${tipoBusqueda eq 'PLACA' ? 'selected' : ''}>Placa</option>
                        <option value="INFRACCION" ${tipoBusqueda eq 'INFRACCION' ? 'selected' : ''}>Número de infracción</option>
                    </select>
                </div>

                <div id="campoTipoPlaca" class="${tipoBusqueda eq 'INFRACCION' ? 'hidden' : ''}">
                    <label for="tipoPlaca" class="block text-[11px] font-bold uppercase tracking-wider text-slate-500 mb-1">Tipo de placa</label>
                    <select id="tipoPlaca" name="tipoPlaca"
                            class="w-full rounded-xl border border-slate-300 bg-white px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-brand-500/40">
                        <c:forTokens items="P,M,C,A" delims="," var="t">
                            <option value="${t}" ${t eq tipoPlaca ? 'selected' : ''}>${t}</option>
                        </c:forTokens>
                    </select>
                </div>

                <div>
                    <label for="valor" id="labelBusqueda" class="block text-[11px] font-bold uppercase tracking-wider text-slate-500 mb-1">${tipoBusqueda eq 'INFRACCION' ? 'Número de infracción' : 'Placa'}</label>
                    <input id="valor" name="valor" type="text" maxlength="30" required autocomplete="off"
                           placeholder="${tipoBusqueda eq 'INFRACCION' ? 'INFR-0001' : '123ABC'}"
                           value="<c:out value='${valor}'/>"
                           class="w-full rounded-xl border border-slate-300 px-3 py-2.5 text-sm font-mono uppercase focus:outline-none focus:ring-2 focus:ring-brand-500/40">
                </div>

                <button type="submit"
                        class="w-full py-2.5 px-4 rounded-xl bg-brand-700 hover:bg-brand-800 text-white font-bold text-xs flex items-center justify-center gap-2 shadow-sm transition">
                    <i data-lucide="search" class="w-4 h-4"></i>
                    <span>Buscar mis infracciones</span>
                </button>
            </form>

            <c:if test="${not empty mensajeBusqueda}">
                <div class="mt-3 rounded-xl border border-rose-200 bg-rose-50 text-rose-800 text-xs font-medium p-3" role="alert">
                    <c:out value="${mensajeBusqueda}"/>
                </div>
            </c:if>
            <c:if test="${sinResultados}">
                <p class="mt-3 text-xs text-slate-500">No se encontraron infracciones tuyas con ese criterio.</p>
            </c:if>
        </section>
    </aside>

    <!-- ===== Columna derecha ===== -->
    <div class="space-y-6 lg:col-span-2 min-w-0">

        <!-- Resultados de búsqueda -->
        <c:if test="${not empty resultadosBusqueda}">
            <section>
                <div class="flex items-center justify-between mb-2 px-1">
                    <h3 class="text-[11px] font-bold uppercase tracking-wider text-slate-500">Resultados de la búsqueda</h3>
                    <span class="text-xs font-bold text-brand-600">${resultadosBusqueda.size()} registro(s)</span>
                </div>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                    <c:forEach var="inf" items="${resultadosBusqueda}">
                        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm p-4 space-y-2">
                            <div class="flex items-start justify-between gap-2">
                                <span class="text-[11px] font-mono text-slate-400">Boleta #<c:out value="${inf.numeroBoleta}"/></span>
                                <span class="px-2 py-0.5 rounded-md border text-[10px] font-bold ${inf.estado eq 'Pendiente' ? 'bg-amber-50 text-amber-700 border-amber-200' : 'bg-emerald-50 text-emerald-700 border-emerald-200'}">
                                    <c:out value="${inf.estado}"/>
                                </span>
                            </div>
                            <p class="text-sm text-slate-700"><strong>Fecha:</strong> <c:out value="${inf.fecha}"/></p>
                            <p class="text-sm text-slate-700"><strong>Motivo:</strong> <c:out value="${inf.motivo}"/></p>
                            <div class="pt-2 border-t border-slate-100 flex items-center justify-between">
                                <span class="text-xs text-slate-500">Monto</span>
                                <span class="text-lg font-black text-brand-700">Q <c:out value="${inf.monto}"/></span>
                            </div>
                            <c:if test="${inf.estado eq 'Pendiente'}">
                                <c:url var="urlPagoBusqueda" value="/ciudadano/pago">
                                    <c:param name="numero" value="${inf.numeroBoleta}"/>
                                </c:url>
                                <a href="${urlPagoBusqueda}"
                                   class="w-full py-2.5 px-4 rounded-xl bg-brand-700 hover:bg-brand-800 text-white font-bold text-xs flex items-center justify-center gap-2 transition">
                                    <i data-lucide="credit-card" class="w-4 h-4"></i><span>Pagar infracción</span>
                                </a>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </section>
        </c:if>

        <!-- Pendientes -->
        <section id="pendientes" class="scroll-mt-24">
            <div class="flex items-center justify-between mb-2 px-1">
                <h3 class="text-[11px] font-bold uppercase tracking-wider text-slate-500">Pendientes de pago</h3>
                <span class="text-xs font-bold text-brand-600">${pendientes.size()} registro(s)</span>
            </div>

            <div class="grid grid-cols-1 xl:grid-cols-2 gap-3">
                <c:forEach var="inf" items="${pendientes}">
                    <article class="bg-white rounded-2xl border border-slate-200 overflow-hidden shadow-sm flex flex-col">
                        <div class="bg-gradient-to-r from-brand-700 to-brand-500 text-white px-3.5 py-2 flex items-center justify-between text-xs font-semibold">
                            <div class="flex items-center gap-1.5">
                                <i data-lucide="alert-circle" class="w-4 h-4 text-amber-300"></i>
                                <span>Atención inmediata</span>
                            </div>
                            <span class="font-mono text-[11px]"><c:out value="${inf.fechaInfraccion}"/></span>
                        </div>

                        <div class="p-4 space-y-3 flex-1 flex flex-col">
                            <div class="flex justify-between items-start gap-3">
                                <div class="min-w-0">
                                    <span class="text-[11px] font-mono text-slate-400">Boleta #<c:out value="${inf.numeroInfraccion}"/></span>
                                    <h3 class="text-base font-extrabold text-slate-900 leading-tight mt-0.5 break-words"><c:out value="${inf.tipoInfraccion}"/></h3>
                                    <div class="flex items-start gap-1 text-xs text-rose-600 font-medium mt-1">
                                        <i data-lucide="map-pin" class="w-3.5 h-3.5 shrink-0 mt-0.5"></i>
                                        <span class="break-words"><c:out value="${inf.direccionInfraccion}"/></span>
                                    </div>
                                </div>
                                <div class="bg-slate-50 border border-slate-300 rounded px-2 py-1 text-center shrink-0">
                                    <span class="text-[8px] font-bold tracking-widest text-slate-400 block">GUATEMALA</span>
                                    <span class="text-xs font-black font-mono tracking-wider text-slate-800"><c:out value="${inf.placaVehiculo}"/></span>
                                    <span class="text-[7px] text-slate-400 block font-semibold">C.A.</span>
                                </div>
                            </div>

                            <div class="pt-2 border-t border-slate-100 flex items-end justify-between mt-auto">
                                <div>
                                    <span class="text-[10px] text-slate-400 block">Monto original</span>
                                    <span class="text-2xl font-black text-brand-700">Q <c:out value="${inf.montoOriginal}"/></span>
                                </div>
                                <span class="inline-flex items-center gap-1 px-2 py-1 rounded-md bg-amber-50 text-amber-700 border border-amber-200 text-xs font-semibold">
                                    <i data-lucide="clock" class="w-3.5 h-3.5"></i><c:out value="${inf.estado}"/>
                                </span>
                            </div>

                            <c:url var="urlPago" value="/ciudadano/pago">
                                <c:param name="numero" value="${inf.numeroInfraccion}"/>
                            </c:url>
                            <a href="${urlPago}"
                               class="w-full py-2.5 px-4 rounded-xl bg-brand-700 hover:bg-brand-800 text-white font-bold text-xs flex items-center justify-center gap-2 shadow-sm transition">
                                <i data-lucide="credit-card" class="w-4 h-4"></i>
                                <span>Pagar infracción</span>
                            </a>
                        </div>
                    </article>
                </c:forEach>
            </div>

            <c:if test="${empty pendientes and empty mensaje}">
                <div class="bg-white rounded-2xl border border-slate-200 p-6 text-center text-sm text-slate-500">
                    No tienes infracciones pendientes.
                </div>
            </c:if>
        </section>

        <!-- Historial -->
        <section id="historial" class="scroll-mt-24">
            <div class="flex items-center justify-between mb-2 px-1">
                <h3 class="text-[11px] font-bold uppercase tracking-wider text-slate-500">Historial</h3>
                <span class="text-xs font-bold text-brand-600">${historial.size()} registro(s)</span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                <c:forEach var="inf" items="${historial}">
                    <article class="bg-white p-3.5 rounded-2xl border border-slate-200 shadow-sm flex items-start gap-3">
                        <div class="w-9 h-9 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center shrink-0">
                            <i data-lucide="file-check" class="w-4 h-4"></i>
                        </div>
                        <div class="min-w-0 flex-1">
                            <div class="flex items-center justify-between gap-2">
                                <span class="text-xs font-bold text-slate-900 truncate">Boleta #<c:out value="${inf.numeroInfraccion}"/></span>
                                <span class="bg-emerald-100 text-emerald-800 text-[9px] font-bold px-1.5 py-0.5 rounded shrink-0"><c:out value="${inf.estado}"/></span>
                            </div>
                            <p class="text-[11px] text-slate-600 break-words"><c:out value="${inf.tipoInfraccion}"/></p>
                            <p class="text-[10px] text-slate-400">
                                <c:out value="${inf.fechaInfraccion}"/> • Placa <c:out value="${inf.placaVehiculo}"/> • Q <c:out value="${inf.montoOriginal}"/>
                            </p>
                        </div>
                    </article>
                </c:forEach>
            </div>

            <c:if test="${empty historial and empty mensaje}">
                <div class="bg-white rounded-2xl border border-slate-200 p-6 text-center text-sm text-slate-500">
                    Aún no tienes historial.
                </div>
            </c:if>
        </section>
    </div>
</main>

<!-- ================= BARRA INFERIOR (solo móvil/tableta) ================= -->
<nav class="fixed bottom-0 inset-x-0 lg:hidden bg-white/95 backdrop-blur-md border-t border-slate-200 px-4 py-2 flex justify-around items-center z-50">
    <a href="#buscar" class="flex flex-col items-center text-slate-500 hover:text-brand-700">
        <i data-lucide="search" class="w-5 h-5"></i>
        <span class="text-[10px] font-bold mt-0.5">Buscar</span>
    </a>
    <a href="#pendientes" class="flex flex-col items-center text-slate-500 hover:text-brand-700">
        <i data-lucide="receipt" class="w-5 h-5"></i>
        <span class="text-[10px] font-bold mt-0.5">Pendientes</span>
    </a>
    <a href="#historial" class="flex flex-col items-center text-slate-500 hover:text-brand-700">
        <i data-lucide="history" class="w-5 h-5"></i>
        <span class="text-[10px] font-bold mt-0.5">Historial</span>
    </a>
</nav>

<script>
    function actualizarBusqueda() {
        const porPlaca = document.getElementById('tipoBusqueda').value === 'PLACA';
        document.getElementById('campoTipoPlaca').classList.toggle('hidden', !porPlaca);
        document.getElementById('labelBusqueda').textContent = porPlaca ? 'Placa' : 'Número de infracción';
        document.getElementById('valor').placeholder = porPlaca ? '123ABC' : 'INFR-0001';
    }
    lucide.createIcons();
</script>
</body>
</html>