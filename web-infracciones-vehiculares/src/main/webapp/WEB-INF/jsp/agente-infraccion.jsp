<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrar infracción - Portal PMT</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@600;700;800&display=swap" rel="stylesheet">
    <script src="https://unpkg.com/lucide@latest"></script>
    <script>
        tailwind.config = { theme: { extend: {
            fontFamily: { sans: ['Plus Jakarta Sans', 'sans-serif'], mono: ['JetBrains Mono', 'monospace'] },
            colors: { brand: { 50:'#eef5fc',100:'#d7e7f9',500:'#1a4b8c',600:'#153e74',700:'#0f2f59',800:'#0b213f',900:'#07162b' } }
        } } };
    </script>
    <style>
        html { font-size: 18px; }
        @media (max-width: 767px) { html { font-size: 16px; } }
        @media (min-width: 1600px) { html { font-size: 19px; } }
        @media print {
            body * { visibility: hidden !important; }
            #boletaImprimible, #boletaImprimible * { visibility: visible !important; }
            #boletaImprimible { position: absolute; inset: 0 auto auto 0; width: 100%; border: 0; box-shadow: none; }
            .no-print { display: none !important; }
        }
    </style>
</head>
<body class="bg-[#f7f9fb] font-sans antialiased text-slate-800 min-h-screen">

<header class="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-slate-200/90">
    <div class="w-full max-w-screen-2xl mx-auto px-4 sm:px-6 py-3 flex items-center justify-between gap-4">
        <a href="${pageContext.request.contextPath}/inicio" class="flex items-center gap-2.5">
            <div class="w-10 h-10 rounded-xl bg-brand-500 text-white flex items-center justify-center shadow-md shadow-brand-500/20"><i data-lucide="shield-alert" class="w-5 h-5"></i></div>
            <div><span class="text-xs uppercase font-bold tracking-wider text-slate-500 block">Portal PMT</span><span class="text-lg font-extrabold text-slate-900">Registrar infracción</span></div>
        </a>
        <nav class="hidden xl:flex items-center gap-6 text-sm font-semibold"><a href="#formulario" class="text-brand-700">Nueva infracción</a></nav>
        <div class="flex items-center gap-2">
        <a href="${pageContext.request.contextPath}/inicio" class="inline-flex items-center gap-1.5 rounded-full bg-blue-50 px-3 py-2 text-xs font-bold text-brand-700 transition hover:bg-blue-100">
            <i data-lucide="arrow-left" class="h-4 w-4"></i><span>Volver al inicio</span>
        </a>
        <form method="post" action="${pageContext.request.contextPath}/logout" class="m-0">
            <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
            <button type="submit" class="inline-flex items-center gap-1.5 px-3 py-2 rounded-full bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold transition"><i data-lucide="log-out" class="w-4 h-4"></i><span class="hidden sm:inline">Cerrar sesión</span></button>
        </form>
        </div>
    </div>
</header>

<main class="w-full max-w-screen-2xl mx-auto p-4 sm:p-6 pb-24">
    <c:if test="${not empty boleta}">
        <section id="boletaImprimible" class="mb-5 rounded-2xl border border-emerald-200 bg-white p-5 shadow-sm sm:p-7" role="status">
            <div class="flex flex-col gap-4 border-b border-slate-200 pb-4 sm:flex-row sm:items-start sm:justify-between">
                <div><p class="text-xs font-bold uppercase tracking-[.18em] text-brand-600">Portal PMT · Comprobante</p><h2 class="mt-1 text-2xl font-extrabold text-slate-900">Infracción generada</h2><p class="mt-1 text-sm text-slate-500">Boleta <strong class="font-mono text-slate-800"><c:out value="${boleta.numero}"/></strong></p></div>
                <button type="button" onclick="window.print()" class="no-print inline-flex items-center justify-center gap-2 rounded-xl bg-brand-800 px-4 py-3 text-sm font-bold text-white hover:bg-brand-900"><i data-lucide="printer" class="h-4 w-4"></i>Guardar como PDF</button>
            </div>
            <dl class="mt-5 grid grid-cols-1 gap-x-8 gap-y-4 sm:grid-cols-2">
                <div><dt class="text-xs font-bold uppercase tracking-wide text-slate-500">Fecha</dt><dd class="mt-1 font-semibold"><c:out value="${boleta.fecha}"/></dd></div>
                <div><dt class="text-xs font-bold uppercase tracking-wide text-slate-500">Estado</dt><dd class="mt-1 font-semibold"><c:out value="${boleta.estado}"/></dd></div>
                <div><dt class="text-xs font-bold uppercase tracking-wide text-slate-500">Placa del vehículo</dt><dd class="mt-1 font-mono text-lg font-extrabold"><c:out value="${boleta.placa}"/></dd></div>
                <div><dt class="text-xs font-bold uppercase tracking-wide text-slate-500">Propietario</dt><dd class="mt-1 font-semibold"><c:out value="${boleta.nombrePersona}"/> · DPI <c:out value="${boleta.dpi}"/></dd></div>
                <div class="sm:col-span-2"><dt class="text-xs font-bold uppercase tracking-wide text-slate-500">Tipo de infracción</dt><dd class="mt-1 text-lg font-bold"><c:out value="${boleta.tipoInfraccion}"/></dd></div>
                <div class="sm:col-span-2"><dt class="text-xs font-bold uppercase tracking-wide text-slate-500">Dirección</dt><dd class="mt-1 font-medium"><c:out value="${boleta.direccion}"/></dd></div>
                <div><dt class="text-xs font-bold uppercase tracking-wide text-slate-500">Agente</dt><dd class="mt-1 font-medium"><c:out value="${boleta.agente}"/></dd></div>
                <div><dt class="text-xs font-bold uppercase tracking-wide text-slate-500">Monto</dt><dd class="mt-1 text-2xl font-black text-brand-800">Q <c:out value="${boleta.monto}"/></dd></div>
            </dl>
        </section>
    </c:if>
    <c:if test="${not empty error}">
        <div class="mb-5 bg-rose-50 border border-rose-200 rounded-2xl p-4 flex items-start gap-3" role="alert"><i data-lucide="alert-triangle" class="w-5 h-5 text-rose-600 shrink-0 mt-0.5"></i><span class="text-sm text-rose-800"><c:out value="${error}"/></span></div>
    </c:if>
    <c:if test="${not empty mensajeAlta}"><div class="mb-5 rounded-2xl border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-900" role="status"><c:out value="${mensajeAlta}"/></div></c:if>
    <c:if test="${not empty errorAlta}"><div class="mb-5 rounded-2xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-800" role="alert"><c:out value="${errorAlta}"/></div></c:if>
    <c:if test="${not empty errorCatalogo}">
        <div class="mb-5 bg-amber-50 border border-amber-200 rounded-2xl p-4 flex items-start gap-3" role="status"><i data-lucide="info" class="w-5 h-5 text-amber-600 shrink-0 mt-0.5"></i><span class="text-sm text-amber-900"><c:out value="${errorCatalogo}"/></span></div>
    </c:if>

    <div class="grid grid-cols-1 xl:grid-cols-4 gap-5 xl:gap-8 items-start">
        <aside class="xl:col-span-1">
            <section class="bg-gradient-to-br from-white to-blue-50/40 p-5 rounded-2xl border border-blue-100 shadow-sm">
                <div class="flex items-center gap-3">
                    <div class="w-12 h-12 rounded-xl bg-brand-800 text-white flex items-center justify-center shadow-inner"><i data-lucide="user-round" class="w-6 h-6"></i></div>
                    <div class="min-w-0"><span class="text-xs font-bold uppercase tracking-wider text-brand-500">Agente en sesión</span><h1 class="text-lg font-extrabold text-slate-900 truncate"><c:out value="${agente.nombrePersona}"/></h1><p class="text-xs text-slate-500 font-mono">Usuario: <c:out value="${agente.usuario}"/></p></div>
                </div>
                <div class="mt-4 flex items-center gap-2 bg-slate-50 px-3 py-2 rounded-xl border border-slate-200 text-xs text-slate-600"><span class="w-2 h-2 rounded-full bg-emerald-500"></span>Acceso autenticado y activo</div>
            </section>
            <div class="mt-4 bg-blue-50 border border-blue-100 rounded-2xl p-4 flex items-start gap-3 text-sm text-blue-900"><i data-lucide="shield-check" class="w-5 h-5 text-brand-600 shrink-0"></i><span>La infracción se registra con el usuario de agente asociado a tu sesión.</span></div>

        </aside>

        <section id="formulario" class="xl:col-span-3 bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
            <div class="p-5 sm:p-6 border-b border-slate-100"><span class="text-xs font-bold uppercase tracking-wider text-brand-500">Nueva boleta</span><h2 class="text-xl font-extrabold text-slate-900 mt-1">Datos de la infracción</h2><p class="text-sm text-slate-500 mt-1">Completa los datos para registrar la infracción en el sistema.</p></div>

            <form method="post" action="${pageContext.request.contextPath}/agente/infracciones" class="p-5 sm:p-6 space-y-6">
                <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label for="placa" class="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">Placa del vehículo</label>
                        <select id="placa" name="placa" required class="w-full bg-slate-50 text-sm font-semibold px-3 py-3 rounded-xl border border-slate-200 focus:bg-white focus:outline-none focus:ring-2 focus:ring-brand-500/40">
                            <option value="">Selecciona un vehículo</option>
                            <c:forEach var="vehiculo" items="${vehiculos}">
                                <option value="<c:out value='${vehiculo.placa}'/>" ${vehiculo.placa eq placa ? 'selected' : ''}><c:out value="${vehiculo.placa}"/> · <c:out value="${vehiculo.marca}"/> <c:out value="${vehiculo.modelo}"/></option>
                            </c:forEach>
                        </select>
                        <button type="button" onclick="document.getElementById('dialogoVehiculo').showModal()" class="mt-2 inline-flex items-center gap-1 text-xs font-bold text-brand-700 underline underline-offset-2">+ Crear vehículo</button>
                        <c:if test="${empty vehiculos}"><p class="mt-1 text-xs text-amber-700"><c:out value="${errorVehiculos}"/> Registra un vehículo antes de emitir la boleta.</p></c:if>
                    </div>
                    <div>
                        <label for="dpiConductor" class="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">DPI del propietario</label>
                        <select id="dpiConductor" name="dpiConductor" required class="w-full bg-slate-50 text-sm font-semibold px-3 py-3 rounded-xl border border-slate-200 focus:bg-white focus:outline-none focus:ring-2 focus:ring-brand-500/40">
                            <option value="">Selecciona al propietario</option>
                            <c:forEach var="persona" items="${personas}">
                                <option value="<c:out value='${persona.dpi}'/>" ${persona.dpi eq dpiConductor ? 'selected' : ''}><c:out value="${persona.dpi}"/> · <c:out value="${persona.nombre}"/> <c:out value="${persona.apellido}"/></option>
                            </c:forEach>
                        </select>
                        <button type="button" onclick="document.getElementById('dialogoPersona').showModal()" class="mt-2 inline-flex items-center gap-1 text-xs font-bold text-brand-700 underline underline-offset-2">+ Crear persona</button>
                        <c:if test="${empty personas}"><p class="mt-1 text-xs text-amber-700"><c:out value="${errorPersonas}"/> Registra una persona antes de emitir la boleta.</p></c:if>
                    </div>
                    <div>
                        <label for="fecha" class="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">Fecha de infracción</label>
                        <input id="fecha" name="fecha" type="date" required min="<c:out value='${fechaHoy}'/>" max="<c:out value='${fechaHoy}'/>" value="<c:out value='${fecha}'/>" class="w-full bg-slate-50 text-sm px-3 py-3 rounded-xl border border-slate-200 focus:bg-white focus:outline-none focus:ring-2 focus:ring-brand-500/40">
                    </div>
                    <div class="md:col-span-2">
                        <label for="direccion" class="block text-xs font-bold uppercase tracking-wider text-slate-500 mb-1.5">Dirección</label>
                        <input id="direccion" name="direccion" type="text" maxlength="200" required placeholder="Ej. 7a Avenida y 12 Calle, Zona 10" value="<c:out value='${direccion}'/>" class="w-full bg-slate-50 text-sm px-3 py-3 rounded-xl border border-slate-200 focus:bg-white focus:outline-none focus:ring-2 focus:ring-brand-500/40">
                    </div>
                </div>

                <fieldset>
                    <legend class="text-xs font-bold uppercase tracking-wider text-slate-500 mb-2">Tipo de infracción</legend>
                    <c:choose>
                        <c:when test="${not empty catalogo}">
                            <label for="codigoInfraccion" class="sr-only">Tipo de infracción</label>
                            <select id="codigoInfraccion" name="codigoInfraccion" required class="w-full bg-slate-50 text-sm font-semibold px-3 py-3 rounded-xl border border-slate-200 focus:bg-white focus:outline-none focus:ring-2 focus:ring-brand-500/40">
                                <option value="">Selecciona el tipo de infracción</option>
                                <c:forEach var="tipo" items="${catalogo}">
                                    <option value="<c:out value='${tipo.codigo}'/>" ${codigoInfraccion eq tipo.codigo ? 'selected' : ''}><c:out value="${tipo.nombre}"/> · Q <c:out value="${tipo.monto}"/> — <c:out value="${tipo.descripcion}"/></option>
                                </c:forEach>
                            </select>
                        </c:when>
                        <c:otherwise><p class="text-sm text-slate-500 bg-slate-50 rounded-xl p-4">El catálogo no está disponible. No se puede registrar una infracción en este momento.</p></c:otherwise>
                    </c:choose>
                </fieldset>

                <div class="pt-4 border-t border-slate-200 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                    <button type="submit" ${empty catalogo or empty vehiculos or empty personas ? 'disabled' : ''} class="w-full sm:w-auto py-3.5 px-6 rounded-xl bg-brand-800 hover:bg-brand-900 disabled:opacity-50 disabled:cursor-not-allowed text-white font-extrabold text-sm flex items-center justify-center gap-2 shadow-lg shadow-brand-900/20 transition"><i data-lucide="file-check-2" class="w-4 h-4"></i><span>Generar infracción</span></button>
                </div>
            </form>

            <dialog id="dialogoVehiculo" class="w-[min(92vw,34rem)] rounded-2xl p-0 shadow-2xl backdrop:bg-slate-900/50">
                <form method="post" action="${pageContext.request.contextPath}/agente/infracciones/vehiculo" class="space-y-4 p-5 sm:p-6">
                    <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
                    <input type="hidden" name="dpiConductor" value=""><input type="hidden" name="fecha" value=""><input type="hidden" name="direccion" value=""><input type="hidden" name="codigoInfraccion" value="">
                    <div class="flex items-start justify-between gap-3"><div><h3 class="text-lg font-extrabold">Crear vehículo</h3><p class="text-sm text-slate-500">Se agregará a la lista de placas disponibles.</p></div><button type="button" onclick="document.getElementById('dialogoVehiculo').close()" aria-label="Cerrar" class="rounded-lg px-2 py-1 text-xl text-slate-500">×</button></div>
                    <div class="grid grid-cols-1 gap-3 sm:grid-cols-2"><input name="placaNueva" required maxlength="7" placeholder="Placa completa (P123ABC)" class="rounded-xl border px-3 py-3"><input name="marcaNueva" required maxlength="60" placeholder="Marca" class="rounded-xl border px-3 py-3"><input name="modeloNuevo" required maxlength="60" placeholder="Modelo" class="rounded-xl border px-3 py-3"><input name="colorNuevo" required maxlength="40" placeholder="Color" class="rounded-xl border px-3 py-3"></div>
                    <button class="w-full rounded-xl bg-brand-800 px-4 py-3 font-bold text-white">Guardar vehículo</button>
                </form>
            </dialog>
            <dialog id="dialogoPersona" class="w-[min(92vw,34rem)] rounded-2xl p-0 shadow-2xl backdrop:bg-slate-900/50">
                <form method="post" action="${pageContext.request.contextPath}/agente/infracciones/persona" class="space-y-4 p-5 sm:p-6">
                    <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
                    <input type="hidden" name="placa" value=""><input type="hidden" name="fecha" value=""><input type="hidden" name="direccion" value=""><input type="hidden" name="codigoInfraccion" value="">
                    <div class="flex items-start justify-between gap-3"><div><h3 class="text-lg font-extrabold">Crear persona</h3><p class="text-sm text-slate-500">Se agregará a la lista de propietarios disponibles.</p></div><button type="button" onclick="document.getElementById('dialogoPersona').close()" aria-label="Cerrar" class="rounded-lg px-2 py-1 text-xl text-slate-500">×</button></div>
                    <div class="grid grid-cols-1 gap-3 sm:grid-cols-2"><input name="dpiNuevo" required inputmode="numeric" maxlength="17" placeholder="DPI" class="rounded-xl border px-3 py-3"><input name="nombreNuevo" required maxlength="100" placeholder="Nombre(s)" class="rounded-xl border px-3 py-3"><input name="apellidoNuevo" required maxlength="100" placeholder="Apellido(s)" class="rounded-xl border px-3 py-3"><input name="telefonoNuevo" maxlength="20" placeholder="Teléfono" class="rounded-xl border px-3 py-3"><input name="direccionNueva" maxlength="200" placeholder="Dirección" class="rounded-xl border px-3 py-3 sm:col-span-2"></div>
                    <button class="w-full rounded-xl bg-brand-800 px-4 py-3 font-bold text-white">Guardar persona</button>
                </form>
            </dialog>
        </section>
    </div>
</main>

<nav class="fixed bottom-0 inset-x-0 xl:hidden bg-white/95 backdrop-blur-md border-t border-slate-200 px-4 py-2 flex justify-center items-center z-50">
    <a href="#formulario" class="flex items-center gap-2 text-brand-700 text-sm font-bold"><i data-lucide="file-plus-2" class="w-5 h-5"></i><span>Nueva infracción</span></a>
</nav>
<script>
    function copiarDatosInfraccion(dialogoId) {
        const modal = document.getElementById(dialogoId);
        const formulario = document.querySelector('form[action$="/agente/infracciones"]');
        ['placa', 'dpiConductor', 'fecha', 'direccion', 'codigoInfraccion'].forEach(nombre => {
            const campo = formulario.elements[nombre];
            const destino = modal.querySelector(`[name="${nombre}"]`);
            if (destino) destino.value = campo ? campo.value : '';
        });
    }
    document.querySelectorAll('button[onclick*="showModal"]').forEach(boton => {
        boton.addEventListener('click', () => copiarDatosInfraccion(boton.getAttribute('onclick').includes('Vehiculo') ? 'dialogoVehiculo' : 'dialogoPersona'));
    });
    lucide.createIcons();
</script>
</body>
</html>
