<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Portal Agente PMT - Nueva multa</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@600;700;800&display=swap" rel="stylesheet">
    <script src="https://unpkg.com/lucide@latest"></script>
    <script>
        tailwind.config = {
            theme: { extend: {
                fontFamily: { sans: ['Plus Jakarta Sans', 'sans-serif'], mono: ['JetBrains Mono', 'monospace'] },
                colors: { brand: { 50:'#eef5fc',100:'#d7e7f9',500:'#1a4b8c',600:'#153e74',700:'#0f2f59',800:'#0b213f',900:'#07162b' } }
            } }
        }
    </script>
</head>
<body class="bg-[#f7f9fb] font-sans antialiased text-slate-800 min-h-screen scroll-smooth">

<!-- ============ HEADER ============ -->
<header class="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-slate-200/90">
    <div class="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between gap-4">
        <div class="flex items-center gap-2.5">
            <div class="w-9 h-9 rounded-xl bg-brand-500 text-white flex items-center justify-center shadow-md shadow-brand-500/20">
                <i data-lucide="shield-alert" class="w-5 h-5"></i>
            </div>
            <div>
                <div class="flex items-center gap-1.5 leading-tight">
                    <span class="text-[10px] uppercase font-bold tracking-wider text-slate-500">Oficial PMT - Movilidad</span>
                    <span class="inline-flex items-center gap-1 text-[9px] font-bold text-emerald-600 bg-emerald-50 px-1.5 py-0.5 rounded-full border border-emerald-200">
                        <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>En servicio
                    </span>
                </div>
                <span class="text-base font-extrabold text-slate-900 leading-tight">Nueva multa</span>
            </div>
        </div>

        <nav class="hidden lg:flex items-center gap-6 text-sm font-semibold">
            <a href="${pageContext.request.contextPath}/agente/infracciones/nueva" class="text-brand-700">Nueva multa</a>
            <a href="${pageContext.request.contextPath}/agente/historial" class="text-slate-500 hover:text-brand-700">Historial</a>
        </nav>

        <a href="${pageContext.request.contextPath}/inicio"
           class="inline-flex items-center gap-1.5 px-3 py-2 rounded-full bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold transition">
            <i data-lucide="log-out" class="w-4 h-4"></i><span class="hidden sm:inline">Salir</span>
        </a>
    </div>
</header>

<main class="w-full max-w-6xl mx-auto p-4 pb-28 lg:pb-8">

    <!-- Mensajes -->
    <c:if test="${not empty boleta}">
        <div class="mb-4 bg-emerald-50 border border-emerald-200 rounded-2xl p-4 flex items-start gap-3" role="status">
            <i data-lucide="check-circle-2" class="w-5 h-5 text-emerald-600 shrink-0 mt-0.5"></i>
            <div class="text-sm text-emerald-900">
                <strong>Boleta <c:out value="${boleta.numeroInfraccion}"/> generada.</strong>
                Placa <c:out value="${boleta.placa}"/> · <c:out value="${boleta.tipoInfraccion}"/> · Q <c:out value="${boleta.monto}"/>
            </div>
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="mb-4 bg-rose-50 border border-rose-200 rounded-2xl p-4 flex items-start gap-3" role="alert">
            <i data-lucide="alert-triangle" class="w-5 h-5 text-rose-600 shrink-0 mt-0.5"></i>
            <span class="text-sm text-rose-800"><c:out value="${error}"/></span>
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/agente/infracciones"
          class="grid grid-cols-1 lg:grid-cols-3 gap-4 lg:gap-6 items-start">
        <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
        <input type="hidden" name="latitud" id="latitud">
        <input type="hidden" name="longitud" id="longitud">

        <!-- ===== Columna izquierda: agente, vehículo, conductor ===== -->
        <div class="space-y-4 lg:col-span-1">

            <section class="bg-gradient-to-br from-white to-blue-50/40 p-4 rounded-2xl border border-blue-100 shadow-sm">
                <div class="flex items-center gap-3">
                    <div class="w-11 h-11 rounded-xl bg-brand-800 text-white flex items-center justify-center shadow-inner">
                        <i data-lucide="user" class="w-5 h-5"></i>
                    </div>
                    <div class="min-w-0">
                        <h2 class="text-sm font-extrabold text-slate-900">Agente de tránsito</h2>
                        <p class="text-[11px] text-slate-500 font-mono truncate">DPI <c:out value="${agenteDpi}"/></p>
                    </div>
                </div>
                <div class="mt-3 flex items-center justify-between bg-slate-50 px-2.5 py-1.5 rounded-lg border border-slate-200/80 text-[10px] text-slate-600 font-medium">
                    <span class="flex items-center gap-1"><span id="gpsDot" class="w-2 h-2 rounded-full bg-slate-300"></span><span id="gpsTxt">GPS: buscando…</span></span>
                    <span class="text-brand-700 font-semibold">Modo online</span>
                </div>
            </section>

            <section class="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm space-y-3">
                <div class="flex items-center gap-2">
                    <i data-lucide="car" class="w-4 h-4 text-brand-600"></i>
                    <h3 class="text-sm font-bold text-slate-900">Vehículo</h3>
                </div>
                <div class="grid grid-cols-5 gap-2">
                    <div class="col-span-2">
                        <label for="tipoPlaca" class="text-[10px] font-bold text-slate-500 block mb-1">Tipo</label>
                        <select id="tipoPlaca" name="tipoPlaca" class="w-full bg-slate-100 text-xs font-semibold px-2.5 py-2.5 rounded-xl border border-slate-200">
                            <c:forTokens items="P,M,C,A" delims="," var="t">
                                <option value="${t}" ${t eq form.tipoPlaca ? 'selected' : ''}>${t}</option>
                            </c:forTokens>
                        </select>
                    </div>
                    <div class="col-span-3">
                        <label for="placa" class="text-[10px] font-bold text-slate-500 block mb-1">Placa</label>
                        <input id="placa" name="placa" type="text" maxlength="7" required autocomplete="off" placeholder="123ABC"
                               value="<c:out value='${form.placa}'/>"
                               class="w-full bg-slate-100 font-mono font-black text-sm tracking-widest uppercase px-3 py-2.5 rounded-xl border border-slate-300 text-center focus:bg-white focus:outline-none focus:ring-2 focus:ring-brand-500/40">
                    </div>
                </div>
            </section>

            <section class="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm space-y-3">
                <div class="flex items-center gap-2">
                    <i data-lucide="contact" class="w-4 h-4 text-brand-600"></i>
                    <h3 class="text-sm font-bold text-slate-900">Conductor / licencia</h3>
                </div>
                <label class="flex items-center gap-2 p-2 rounded-xl bg-slate-50 border border-slate-200 text-xs text-slate-700 cursor-pointer">
                    <input type="checkbox" id="conductorAusente" name="conductorAusente" value="true"
                           ${form.conductorAusente ? 'checked' : ''} onchange="actualizarConductor()"
                           class="w-4 h-4 rounded border-slate-300">
                    <span>Conductor ausente / vehículo desatendido</span>
                </label>
                <div id="datosConductor" class="grid grid-cols-3 gap-2">
                    <div>
                        <label for="tipoLicencia" class="text-[10px] font-bold text-slate-500 block mb-1">Tipo lic.</label>
                        <select id="tipoLicencia" name="tipoLicencia" class="w-full bg-slate-100 text-xs font-semibold p-2.5 rounded-xl border border-slate-200">
                            <c:forTokens items="A,B,C,M" delims="," var="l">
                                <option value="${l}" ${l eq form.tipoLicencia ? 'selected' : ''}>Tipo ${l}</option>
                            </c:forTokens>
                        </select>
                    </div>
                    <div class="col-span-2">
                        <label for="dpiConductor" class="text-[10px] font-bold text-slate-500 block mb-1">DPI del conductor</label>
                        <input id="dpiConductor" name="dpiConductor" type="text" inputmode="numeric" maxlength="17" autocomplete="off"
                               placeholder="0000 00000 0000" value="<c:out value='${form.dpiConductor}'/>"
                               class="w-full bg-slate-100 font-mono text-xs font-bold p-2.5 rounded-xl border border-slate-200 focus:bg-white focus:outline-none focus:ring-2 focus:ring-brand-500/40">
                    </div>
                </div>
            </section>
        </div>

        <!-- ===== Columna derecha: infracción, lugar, observaciones, emisión ===== -->
        <div class="space-y-4 lg:col-span-2 min-w-0">

            <section class="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm space-y-3">
                <div class="flex items-center justify-between">
                    <div class="flex items-center gap-2">
                        <i data-lucide="scale" class="w-4 h-4 text-brand-600"></i>
                        <h3 class="text-sm font-bold text-slate-900">Infracción (Ley de Tránsito)</h3>
                    </div>
                    <span class="text-[10px] font-mono text-slate-400 font-semibold">Decreto 132-96</span>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-2">
                    <c:forEach var="t" items="${catalogo}">
                        <label class="p-3 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 flex items-start gap-3 cursor-pointer
                                      has-[:checked]:border-2 has-[:checked]:border-brand-600 has-[:checked]:bg-blue-50/40">
                            <input type="radio" name="codigoInfraccion" value="<c:out value='${t.codigo}'/>"
                                   data-monto="${t.monto}" required
                                   ${form.codigoInfraccion eq t.codigo ? 'checked' : ''}
                                   onchange="actualizarTotal()" class="mt-0.5 w-4 h-4 accent-brand-600">
                            <div class="flex-1 min-w-0">
                                <div class="flex justify-between items-start gap-2">
                                    <h4 class="text-xs font-bold text-slate-900 leading-snug"><c:out value="${t.titulo}"/></h4>
                                    <span class="text-xs font-black font-mono text-brand-800 shrink-0">Q <c:out value="${t.monto}"/></span>
                                </div>
                                <p class="text-[10px] text-slate-500 mt-0.5"><c:out value="${t.detalle}"/></p>
                            </div>
                        </label>
                    </c:forEach>
                </div>
                <c:if test="${empty catalogo}">
                    <p class="text-sm text-slate-500">No hay infracciones disponibles en el catálogo.</p>
                </c:if>

                <div class="pt-3 border-t border-slate-200 flex items-baseline justify-between">
                    <span class="text-sm font-extrabold text-slate-900">Total a boleta (referencia):</span>
                    <span id="total" class="text-xl font-black font-mono text-brand-800">Q 0.00</span>
                </div>
            </section>

            <section class="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm space-y-3">
                <div class="flex items-center gap-2">
                    <i data-lucide="map-pin" class="w-4 h-4 text-brand-600"></i>
                    <h3 class="text-sm font-bold text-slate-900">Lugar y observaciones</h3>
                </div>
                <div>
                    <label for="direccion" class="text-[10px] font-bold text-slate-500 block mb-1">Dirección de la infracción</label>
                    <input id="direccion" name="direccion" type="text" maxlength="200" required
                           value="<c:out value='${form.direccion}'/>" placeholder="Ej. 7a Avenida y 12 Calle, Zona 10"
                           class="w-full bg-slate-50 text-sm p-2.5 rounded-xl border border-slate-200 focus:bg-white focus:outline-none focus:ring-2 focus:ring-brand-500/40">
                </div>
                <div>
                    <label for="observaciones" class="text-[10px] font-bold text-slate-500 block mb-1">Observaciones y circunstancias</label>
                    <textarea id="observaciones" name="observaciones" rows="3" maxlength="500"
                              class="w-full bg-slate-50 text-sm p-3 rounded-xl border border-slate-200 focus:bg-white focus:outline-none focus:ring-2 focus:ring-brand-500/40 resize-none"><c:out value="${form.observaciones}"/></textarea>
                </div>
            </section>

            <section class="space-y-2">
                <button type="submit"
                        class="w-full py-3.5 px-4 rounded-xl bg-brand-800 hover:bg-brand-900 text-white font-extrabold text-sm flex items-center justify-center gap-2.5 shadow-lg shadow-brand-900/20 active:scale-[0.99] transition">
                    <i data-lucide="printer" class="w-4 h-4"></i>
                    <span>Generar boleta</span>
                </button>
                <div class="p-3 bg-blue-50/50 rounded-xl border border-blue-100 flex items-start gap-2 text-[11px] text-slate-600 leading-normal">
                    <i data-lucide="shield-check" class="w-4 h-4 text-brand-600 shrink-0 mt-0.5"></i>
                    <span>La infracción se procesa en línea según el Decreto 132-96. El monto final lo calcula el sistema.</span>
                </div>
            </section>
        </div>
    </form>
</main>

<!-- ============ BARRA INFERIOR (móvil/tableta) ============ -->
<nav class="fixed bottom-0 inset-x-0 lg:hidden bg-white/95 backdrop-blur-md border-t border-slate-200 px-4 py-2 flex justify-around items-center z-50">
    <a href="${pageContext.request.contextPath}/agente/infracciones/nueva" class="flex flex-col items-center text-brand-700">
        <i data-lucide="file-plus" class="w-5 h-5"></i><span class="text-[10px] font-extrabold mt-0.5">Nueva multa</span>
    </a>
    <a href="${pageContext.request.contextPath}/agente/historial" class="flex flex-col items-center text-slate-400 hover:text-slate-600">
        <i data-lucide="history" class="w-5 h-5"></i><span class="text-[10px] font-medium mt-0.5">Historial</span>
    </a>
</nav>

<script>
    function actualizarConductor() {
        const ausente = document.getElementById('conductorAusente').checked;
        const caja = document.getElementById('datosConductor');
        caja.classList.toggle('opacity-40', ausente);
        caja.querySelectorAll('input,select').forEach(el => el.disabled = ausente);
    }
    function actualizarTotal() {
        const sel = document.querySelector('input[name="codigoInfraccion"]:checked');
        const monto = sel ? parseFloat(sel.dataset.monto) : 0;
        document.getElementById('total').textContent = 'Q ' + monto.toFixed(2);
    }
    function ubicar() {
        if (!navigator.geolocation) { document.getElementById('gpsTxt').textContent = 'GPS no disponible'; return; }
        navigator.geolocation.getCurrentPosition(p => {
            document.getElementById('latitud').value = p.coords.latitude.toFixed(6);
            document.getElementById('longitud').value = p.coords.longitude.toFixed(6);
            document.getElementById('gpsDot').className = 'w-2 h-2 rounded-full bg-emerald-500';
            document.getElementById('gpsTxt').textContent = 'GPS activo';
        }, () => { document.getElementById('gpsTxt').textContent = 'GPS sin permiso'; });
    }
    actualizarConductor(); actualizarTotal(); ubicar();
    lucide.createIcons();
</script>
</body>
</html>