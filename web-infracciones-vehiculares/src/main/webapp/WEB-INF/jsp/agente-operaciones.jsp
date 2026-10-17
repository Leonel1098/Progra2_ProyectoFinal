<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Gestión del agente</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <script>tailwind.config={theme:{extend:{fontFamily:{sans:['Plus Jakarta Sans','sans-serif']},colors:{brand:{50:'#eef5fc',100:'#d7e7f9',500:'#1a4b8c',600:'#153e74',700:'#0f2f59',800:'#0b213f'}}}}}</script>
</head>
<body class="min-h-screen bg-slate-50 font-sans text-slate-800">
<header class="sticky top-0 z-20 border-b bg-white/95 backdrop-blur">
    <div class="mx-auto flex max-w-7xl items-center justify-between gap-3 px-4 py-3">
        <div><p class="text-xs font-bold uppercase tracking-wider text-brand-600">Portal Agente PMT</p><h1 class="text-lg font-extrabold">Gestión y consultas</h1></div>
        <nav class="flex flex-wrap items-center justify-end gap-2 text-sm font-semibold">
            <a class="rounded-lg px-3 py-2 text-slate-600 hover:bg-slate-100" href="${pageContext.request.contextPath}/agente/infracciones/nueva">Registrar infracción</a>
            <a class="rounded-lg px-3 py-2 text-slate-600 hover:bg-slate-100" href="${pageContext.request.contextPath}/inicio">Inicio</a>
        </nav>
    </div>
</header>
<main class="mx-auto max-w-7xl space-y-5 px-4 py-6 pb-12">
    <c:if test="${not empty error}"><div role="alert" class="rounded-xl border border-rose-200 bg-rose-50 p-4 text-sm font-medium text-rose-800"><c:out value="${error}"/></div></c:if>

    <section class="grid grid-cols-1 gap-4 xl:grid-cols-2">
        <div class="space-y-4">
            <article id="crear-persona" class="scroll-mt-24 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
                <div class="mb-4 flex flex-wrap items-center justify-between gap-2"><div><p class="text-xs font-bold uppercase tracking-wider text-brand-600">Personas</p><h2 class="text-lg font-extrabold">Administrar personas</h2></div>
                    <form method="post" action="${pageContext.request.contextPath}/agente/operaciones/personas/listar"><input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>"><button class="rounded-lg bg-brand-700 px-4 py-2 text-sm font-bold text-white">Listar personas</button></form></div>
                <form method="post" action="${pageContext.request.contextPath}/agente/operaciones/personas/consultar" class="mb-5 flex flex-col gap-2 sm:flex-row">
                    <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>"><label class="sr-only" for="dpiConsulta">Persona a consultar por DPI</label>
                    <select id="dpiConsulta" name="dpi" required class="min-w-0 flex-1 rounded-lg border bg-white px-3 py-2"><option value="">Consultar persona por DPI</option><c:forEach var="persona" items="${personasDisponibles}"><option value="<c:out value='${persona.dpiPersona}'/>" ${dpiConsultaSeleccionado eq persona.dpiPersona ? 'selected' : ''}><c:out value="${persona.nombrePersona}"/> <c:out value="${persona.apellidoPersona}"/> · DPI <c:out value="${persona.dpiPersona}"/></option></c:forEach></select>
                    <button class="rounded-lg border border-brand-700 px-4 py-2 text-sm font-bold text-brand-700">Buscar</button>
                </form>
                <h3 class="mb-2 text-sm font-bold">Crear persona</h3>
                <form method="post" action="${pageContext.request.contextPath}/agente/operaciones/personas/crear" class="grid grid-cols-1 gap-2 sm:grid-cols-2">
                    <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
                    <input name="dpi" required inputmode="numeric" maxlength="17" placeholder="DPI" class="rounded-lg border px-3 py-2">
                    <input name="nombre" required maxlength="100" placeholder="Nombre(s)" class="rounded-lg border px-3 py-2">
                    <input name="apellido" required maxlength="100" placeholder="Apellido(s)" class="rounded-lg border px-3 py-2">
                    <input name="telefono" maxlength="20" placeholder="Teléfono" class="rounded-lg border px-3 py-2">
                    <input name="direccion" maxlength="200" placeholder="Dirección" class="rounded-lg border px-3 py-2 sm:col-span-2">
                    <button class="rounded-lg bg-brand-700 px-4 py-2 text-sm font-bold text-white sm:col-span-2">Crear persona</button>
                </form>
            </article>

            <article id="crear-vehiculo" class="scroll-mt-24 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
                <div class="mb-4 flex flex-wrap items-center justify-between gap-2"><div><p class="text-xs font-bold uppercase tracking-wider text-brand-600">Vehículos</p><h2 class="text-lg font-extrabold">Administrar vehículos</h2></div>
                    <form method="post" action="${pageContext.request.contextPath}/agente/operaciones/vehiculos/listar"><input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>"><button class="rounded-lg bg-brand-700 px-4 py-2 text-sm font-bold text-white">Listar vehículos</button></form></div>
                <form method="post" action="${pageContext.request.contextPath}/agente/operaciones/vehiculos/consultar" class="mb-5 flex flex-col gap-2 sm:flex-row">
                    <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>"><label class="sr-only" for="placaConsulta">Vehículo a consultar por placa</label>
                    <select id="placaConsulta" name="placa" required class="min-w-0 flex-1 rounded-lg border bg-white px-3 py-2"><option value="">Consultar vehículo por placa</option><c:forEach var="vehiculo" items="${vehiculosDisponibles}"><option value="<c:out value='${vehiculo.placa}'/>" ${placaConsultaSeleccionada eq vehiculo.placa ? 'selected' : ''}><c:out value="${vehiculo.placa}"/> · <c:out value="${vehiculo.marca}"/> <c:out value="${vehiculo.modelo}"/></option></c:forEach></select>
                    <button class="rounded-lg border border-brand-700 px-4 py-2 text-sm font-bold text-brand-700">Buscar</button>
                </form>
                <h3 class="mb-2 text-sm font-bold">Crear vehículo</h3>
                <form method="post" action="${pageContext.request.contextPath}/agente/operaciones/vehiculos/crear" class="grid grid-cols-1 gap-2 sm:grid-cols-2">
                    <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
                    <input name="placa" required maxlength="7" placeholder="Placa completa (P123ABC)" class="rounded-lg border px-3 py-2 sm:col-span-2">
                    <input name="marca" required maxlength="60" placeholder="Marca" class="rounded-lg border px-3 py-2">
                    <input name="modeloVehiculo" required maxlength="60" placeholder="Modelo" class="rounded-lg border px-3 py-2">
                    <input name="color" required maxlength="40" placeholder="Color" class="rounded-lg border px-3 py-2 sm:col-span-2">
                    <button class="rounded-lg bg-brand-700 px-4 py-2 text-sm font-bold text-white sm:col-span-2">Crear vehículo</button>
                </form>
            </article>
        </div>

        <div class="space-y-4">
            <article class="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
                <p class="text-xs font-bold uppercase tracking-wider text-brand-600">Infracciones</p><h2 class="mb-4 text-lg font-extrabold">Consultar infracciones</h2>
                <div class="grid grid-cols-1 gap-3 sm:grid-cols-3">
                    <form method="post" action="${pageContext.request.contextPath}/agente/operaciones/infracciones/placa" class="space-y-2 rounded-xl bg-slate-50 p-3">
                        <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>"><label class="text-xs font-bold" for="placaInfraccion">Por placa</label>
                        <select id="placaInfraccion" name="placa" required class="w-full rounded-lg border bg-white px-3 py-2"><option value="">Selecciona una placa</option><c:forEach var="vehiculo" items="${vehiculosDisponibles}"><option value="<c:out value='${vehiculo.placa}'/>" ${placaSeleccionada eq vehiculo.placa ? 'selected' : ''}><c:out value="${vehiculo.placa}"/> · <c:out value="${vehiculo.marca}"/> <c:out value="${vehiculo.modelo}"/></option></c:forEach></select>
                        <c:if test="${empty vehiculosDisponibles}"><span class="block text-xs text-amber-700"><c:out value="${errorVehiculosDisponibles}"/> No hay placas disponibles.</span></c:if><button class="w-full rounded-lg bg-brand-700 px-3 py-2 text-sm font-bold text-white">Consultar</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/agente/operaciones/infracciones/numero" class="space-y-2 rounded-xl bg-slate-50 p-3">
                        <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>"><label class="text-xs font-bold" for="numeroInfraccion">Por número</label>
                        <select id="numeroInfraccion" name="numero" required class="w-full rounded-lg border bg-white px-3 py-2"><option value="">Selecciona una infracción</option><c:forEach var="numeroInf" items="${numerosDisponibles}"><option value="<c:out value='${numeroInf.numero}'/>" ${numeroSeleccionado eq numeroInf.numero ? 'selected' : ''}><c:out value="${numeroInf.numero}"/></option></c:forEach></select>
                        <c:if test="${empty numerosDisponibles}"><span class="block text-xs text-amber-700"><c:out value="${errorNumerosDisponibles}"/> No hay números disponibles.</span></c:if><button class="w-full rounded-lg bg-brand-700 px-3 py-2 text-sm font-bold text-white">Consultar</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/agente/operaciones/infracciones/dpi" class="space-y-2 rounded-xl bg-slate-50 p-3">
                        <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>"><label class="text-xs font-bold" for="dpiInfraccion">Por DPI</label>
                        <select id="dpiInfraccion" name="dpi" required class="w-full rounded-lg border bg-white px-3 py-2"><option value="">Selecciona una persona</option><c:forEach var="persona" items="${personasDisponibles}"><option value="<c:out value='${persona.dpiPersona}'/>" ${dpiSeleccionado eq persona.dpiPersona ? 'selected' : ''}><c:out value="${persona.nombrePersona}"/> <c:out value="${persona.apellidoPersona}"/> · DPI <c:out value="${persona.dpiPersona}"/></option></c:forEach></select>
                        <c:if test="${empty personasDisponibles}"><span class="block text-xs text-amber-700"><c:out value="${errorPersonasDisponibles}"/> No hay personas disponibles.</span></c:if><button class="w-full rounded-lg bg-brand-700 px-3 py-2 text-sm font-bold text-white">Consultar</button>
                    </form>
                </div>
            </article>

            <c:if test="${not empty tituloResultados}">
                <section class="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
                    <div class="flex items-center justify-between gap-2 border-b bg-slate-50 p-4"><h2 class="font-extrabold"><c:out value="${tituloResultados}"/></h2><span class="rounded-full bg-white px-3 py-1 text-xs font-bold text-slate-600"><c:out value="${filas.size()}"/> resultado(s)</span></div>
                    <c:choose><c:when test="${not empty filas}">
                        <div class="hidden max-h-[34rem] overflow-auto md:block"><table class="min-w-full text-left text-sm"><thead class="sticky top-0 bg-brand-800 text-white"><tr><c:forEach var="columna" items="${columnas}" varStatus="ciclo"><th class="whitespace-nowrap px-4 py-3 font-bold"><c:out value="${etiquetas[ciclo.index]}"/></th></c:forEach></tr></thead>
                            <tbody class="divide-y divide-slate-100"><c:forEach var="fila" items="${filas}"><tr class="odd:bg-white even:bg-slate-50/70 hover:bg-blue-50"><c:forEach var="columna" items="${columnas}"><td class="max-w-sm whitespace-normal break-words px-4 py-3 align-top text-slate-700"><c:out value="${fila[columna]}"/></td></c:forEach></tr></c:forEach></tbody></table></div>
                        <div class="grid gap-3 p-3 md:hidden"><c:forEach var="fila" items="${filas}"><article class="rounded-xl border border-slate-200 bg-slate-50 p-4"><c:forEach var="columna" items="${columnas}" varStatus="ciclo"><div class="flex flex-col gap-0.5 border-b border-slate-200 py-2 last:border-0"><span class="text-[11px] font-bold uppercase tracking-wide text-slate-500"><c:out value="${etiquetas[ciclo.index]}"/></span><span class="break-words text-sm font-medium text-slate-800"><c:out value="${fila[columna]}"/></span></div></c:forEach></article></c:forEach></div>
                    </c:when><c:otherwise><p class="p-4 text-sm text-slate-500">No se encontraron registros.</p></c:otherwise></c:choose>
                </section>
            </c:if>
        </div>
    </section>
</main>
</body>
</html>
