<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  PLANTILLA RESPONSIVE (solo diseño). Atributos que espera del controller:
    infracciones   List<InfraccionResponse>   (numeroInfraccion, placaVehiculo, tipoInfraccion,
                                               direccionInfraccion, fechaInfraccion, montoOriginal, estado)
    total          long     total de registros
    totalPaginas   int
    pagina         int      (empieza en 0)
    tamano         int
    q, estado, tipo, fecha  (String) valores actuales de los filtros
    estados, tipos          List<String> para los selects
    exito, error            (String) mensajes opcionales
  Busca los comentarios "CONECTAR" para ver dónde enlazar tus datos.
--%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administración - Infracciones</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;600;700&display=swap" rel="stylesheet">
    <script src="https://unpkg.com/lucide@latest"></script>
    <script>
        tailwind.config = {
            theme: { extend: {
                fontFamily: { sans: ['Plus Jakarta Sans', 'sans-serif'], mono: ['JetBrains Mono', 'monospace'] },
                colors: { brand: { 50:'#eef5fc',100:'#d7e7f9',200:'#b9d3f2',500:'#1a4b8c',600:'#153e74',700:'#0f2f59',800:'#0b213f',900:'#07162b' } }
            } }
        }
    </script>
</head>
<body class="bg-[#f0f3f6] font-sans antialiased text-slate-800 min-h-screen lg:flex">

<div id="overlay" onclick="toggleMenu()" class="fixed inset-0 bg-black/40 z-40 hidden lg:hidden"></div>

<!-- ================= SIDEBAR ================= -->
<aside id="sidebar"
       class="fixed inset-y-0 left-0 z-50 w-64 -translate-x-full transition-transform duration-200
              lg:sticky lg:top-0 lg:h-screen lg:translate-x-0
              bg-white border-r border-slate-200/90 flex flex-col shrink-0 select-none">

    <div class="p-4 border-b border-slate-100 flex items-center justify-between gap-3">
        <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-xl bg-brand-700 text-white flex items-center justify-center shadow-md shadow-brand-700/20">
                <i data-lucide="database" class="w-5 h-5"></i>
            </div>
            <div>
                <h1 class="text-sm font-extrabold text-slate-900 leading-tight">Panel de administración</h1>
                <span class="text-[10px] font-bold tracking-wider uppercase text-slate-400 block mt-0.5">Movilidad &amp; Tránsito</span>
            </div>
        </div>
        <button onclick="toggleMenu()" class="lg:hidden w-8 h-8 rounded-lg bg-slate-100 flex items-center justify-center" aria-label="Cerrar menú">
            <i data-lucide="x" class="w-4 h-4"></i>
        </button>
    </div>

    <%-- CONECTAR: ajusta las rutas si tus URLs son distintas --%>
    <nav class="flex-1 overflow-y-auto p-3 space-y-0.5 text-xs">
        <span class="text-[10px] font-bold uppercase tracking-wider text-slate-400 px-2 block mb-1.5">Operación</span>
        <a href="${pageContext.request.contextPath}/admin/infracciones"
           class="flex items-center justify-between px-3 py-2.5 rounded-xl bg-brand-700 text-white font-bold shadow-sm">
            <span class="flex items-center gap-2"><i data-lucide="table-2" class="w-4 h-4"></i>Infracciones</span>
            <span class="bg-blue-400/30 text-white text-[9px] px-1.5 py-0.5 rounded">Core</span>
        </a>
        <a href="${pageContext.request.contextPath}/admin/pagos" class="flex items-center gap-2 px-3 py-2.5 rounded-xl text-slate-600 hover:bg-slate-100 hover:text-slate-900 font-medium transition">
            <i data-lucide="credit-card" class="w-4 h-4 text-slate-400"></i>Pagos
        </a>

        <span class="text-[10px] font-bold uppercase tracking-wider text-slate-400 px-2 block pt-3 pb-1.5">Personas y vehículos</span>
        <a href="${pageContext.request.contextPath}/admin/personas" class="flex items-center gap-2 px-3 py-2.5 rounded-xl text-slate-600 hover:bg-slate-100 hover:text-slate-900 font-medium transition">
            <i data-lucide="users" class="w-4 h-4 text-slate-400"></i>Personas
        </a>
        <a href="${pageContext.request.contextPath}/admin/vehiculos" class="flex items-center gap-2 px-3 py-2.5 rounded-xl text-slate-600 hover:bg-slate-100 hover:text-slate-900 font-medium transition">
            <i data-lucide="car" class="w-4 h-4 text-slate-400"></i>Vehículos
        </a>

        <span class="text-[10px] font-bold uppercase tracking-wider text-slate-400 px-2 block pt-3 pb-1.5">Catálogos</span>
        <a href="${pageContext.request.contextPath}/admin/tipos-infraccion" class="flex items-center gap-2 px-3 py-2.5 rounded-xl text-slate-600 hover:bg-slate-100 hover:text-slate-900 font-medium transition">
            <i data-lucide="alert-triangle" class="w-4 h-4 text-slate-400"></i>Tipos de infracción
        </a>
        <a href="${pageContext.request.contextPath}/admin/estados" class="flex items-center gap-2 px-3 py-2.5 rounded-xl text-slate-600 hover:bg-slate-100 hover:text-slate-900 font-medium transition">
            <i data-lucide="git-commit" class="w-4 h-4 text-slate-400"></i>Estados
        </a>
        <a href="${pageContext.request.contextPath}/admin/reglas-descuento" class="flex items-center gap-2 px-3 py-2.5 rounded-xl text-slate-600 hover:bg-slate-100 hover:text-slate-900 font-medium transition">
            <i data-lucide="percent" class="w-4 h-4 text-slate-400"></i>Reglas de descuento
        </a>

        <span class="text-[10px] font-bold uppercase tracking-wider text-slate-400 px-2 block pt-3 pb-1.5">Seguridad</span>
        <a href="${pageContext.request.contextPath}/admin/usuarios" class="flex items-center gap-2 px-3 py-2.5 rounded-xl text-slate-600 hover:bg-slate-100 hover:text-slate-900 font-medium transition">
            <i data-lucide="user-cog" class="w-4 h-4 text-slate-400"></i>Usuarios
        </a>
        <a href="${pageContext.request.contextPath}/admin/roles" class="flex items-center gap-2 px-3 py-2.5 rounded-xl text-slate-600 hover:bg-slate-100 hover:text-slate-900 font-medium transition">
            <i data-lucide="shield" class="w-4 h-4 text-slate-400"></i>Roles
        </a>
    </nav>

    <div class="p-3 border-t border-slate-100">
        <a href="${pageContext.request.contextPath}/inicio"
           class="flex items-center gap-2.5 px-3 py-2.5 rounded-xl text-slate-600 hover:bg-slate-100 text-xs font-medium transition">
            <i data-lucide="log-out" class="w-4 h-4"></i><span>Salir</span>
        </a>
    </div>
</aside>

<!-- ================= CONTENIDO ================= -->
<div class="flex-1 flex flex-col min-w-0">

    <header class="bg-white border-b border-slate-200/90 px-4 lg:px-6 py-3 flex items-center justify-between gap-3 sticky top-0 z-30">
        <div class="flex items-center gap-3 min-w-0">
            <button onclick="toggleMenu()" class="lg:hidden w-9 h-9 shrink-0 rounded-xl bg-slate-100 hover:bg-slate-200 flex items-center justify-center" aria-label="Abrir menú">
                <i data-lucide="menu" class="w-4 h-4"></i>
            </button>
            <div class="min-w-0">
                <span class="text-[10px] uppercase font-bold tracking-wider text-slate-400 block leading-tight">Rol: Admin</span>
                <span class="text-base font-extrabold text-slate-900 leading-tight">Infracciones</span>
            </div>
        </div>
        <div class="w-9 h-9 rounded-xl bg-brand-800 text-white flex items-center justify-center shadow-sm shrink-0">
            <i data-lucide="user-check" class="w-4 h-4"></i>
        </div>
    </header>

    <main class="flex-1 p-4 lg:p-6 space-y-4 lg:space-y-5 overflow-x-hidden">

        <%-- Mensajes --%>
        <c:if test="${not empty exito}">
            <div class="bg-emerald-50 border border-emerald-200 rounded-2xl p-3.5 flex items-start gap-3" role="status">
                <i data-lucide="check-circle-2" class="w-5 h-5 text-emerald-600 shrink-0"></i>
                <span class="text-sm text-emerald-900"><c:out value="${exito}"/></span>
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="bg-rose-50 border border-rose-200 rounded-2xl p-3.5 flex items-start gap-3" role="alert">
                <i data-lucide="alert-triangle" class="w-5 h-5 text-rose-600 shrink-0"></i>
                <span class="text-sm text-rose-800"><c:out value="${error}"/></span>
            </div>
        </c:if>

        <!-- Cabecera -->
        <div class="bg-white p-4 lg:p-5 rounded-2xl border border-slate-200 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div class="flex items-start gap-2.5 min-w-0">
                <div class="w-8 h-8 shrink-0 rounded-lg bg-brand-50 text-brand-700 flex items-center justify-center border border-brand-200">
                    <i data-lucide="table-2" class="w-4 h-4"></i>
                </div>
                <div class="min-w-0">
                    <h2 class="text-base sm:text-lg font-black text-slate-900">Gestión de infracciones</h2>
                    <p class="text-xs text-slate-500 mt-0.5">Multas emitidas por los agentes de tránsito.</p>
                </div>
            </div>
            <div class="grid grid-cols-2 sm:flex items-center gap-2">
                <%-- CONECTAR: ruta del formulario de nueva infracción --%>
                <a href="${pageContext.request.contextPath}/admin/infracciones/nueva"
                   class="col-span-2 sm:col-span-1 bg-brand-700 hover:bg-brand-800 text-white font-bold text-xs px-4 py-2.5 rounded-xl shadow-md shadow-brand-700/20 flex items-center justify-center gap-2 transition">
                    <i data-lucide="plus-circle" class="w-4 h-4"></i><span>Nuevo registro</span>
                </a>
                <button type="button" class="bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold text-xs px-3.5 py-2.5 rounded-xl border border-slate-200 flex items-center justify-center gap-1.5 transition">
                    <i data-lucide="download" class="w-4 h-4 text-slate-600"></i><span>Exportar</span>
                </button>
                <a href="${pageContext.request.contextPath}/admin/infracciones" title="Recargar"
                   class="bg-slate-100 hover:bg-slate-200 text-slate-600 border border-slate-200 rounded-xl flex items-center justify-center px-3.5 py-2.5 transition">
                    <i data-lucide="refresh-cw" class="w-3.5 h-3.5"></i>
                </a>
            </div>
        </div>

        <!-- Métricas -->
        <%-- CONECTAR: agrega al modelo "totalPendientes" y "totalPagadas" si quieres estas tarjetas --%>
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
            <div class="bg-white p-3.5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between gap-2">
                <div>
                    <span class="text-[10px] font-bold uppercase tracking-wider text-slate-400 block">Total registros</span>
                    <span class="text-xl font-black text-slate-900 font-mono"><c:out value="${total}" default="0"/></span>
                </div>
                <div class="w-9 h-9 shrink-0 rounded-xl bg-blue-50 text-brand-600 flex items-center justify-center"><i data-lucide="rows" class="w-4 h-4"></i></div>
            </div>
            <div class="bg-white p-3.5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between gap-2">
                <div>
                    <span class="text-[10px] font-bold uppercase tracking-wider text-slate-400 block">Pendientes</span>
                    <span class="text-xl font-black text-amber-600 font-mono"><c:out value="${totalPendientes}" default="0"/></span>
                </div>
                <div class="w-9 h-9 shrink-0 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center"><i data-lucide="clock" class="w-4 h-4"></i></div>
            </div>
            <div class="bg-white p-3.5 rounded-2xl border border-slate-200 shadow-sm flex items-center justify-between gap-2">
                <div>
                    <span class="text-[10px] font-bold uppercase tracking-wider text-slate-400 block">Pagadas</span>
                    <span class="text-xl font-black text-emerald-600 font-mono"><c:out value="${totalPagadas}" default="0"/></span>
                </div>
                <div class="w-9 h-9 shrink-0 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center"><i data-lucide="check-circle" class="w-4 h-4"></i></div>
            </div>
        </div>

        <!-- Filtros (GET) -->
        <form method="get" action="${pageContext.request.contextPath}/admin/infracciones"
              class="bg-white p-3.5 rounded-2xl border border-slate-200 shadow-sm">
            <input type="hidden" name="tamano" value="<c:out value='${tamano}'/>">
            <div class="flex flex-col xl:flex-row xl:items-center justify-between gap-3">
                <div class="flex-1 relative">
                    <label for="q" class="sr-only">Búsqueda rápida</label>
                    <i data-lucide="search" class="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2"></i>
                    <input id="q" name="q" type="text" value="<c:out value='${q}'/>" placeholder="Búsqueda rápida por boleta, placa o motivo…"
                           class="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:bg-white focus:border-brand-500 outline-none transition">
                </div>

                <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-5 xl:flex xl:items-center gap-2 text-xs">
                    <select name="estado" class="w-full bg-slate-50 border border-slate-200 font-medium px-3 py-2.5 rounded-xl text-slate-700 outline-none">
                        <option value="">Estado: Todos</option>
                        <c:forEach var="e" items="${estados}">
                            <option value="<c:out value='${e}'/>" ${e eq estado ? 'selected' : ''}><c:out value="${e}"/></option>
                        </c:forEach>
                    </select>
                    <select name="tipo" class="w-full bg-slate-50 border border-slate-200 font-medium px-3 py-2.5 rounded-xl text-slate-700 outline-none">
                        <option value="">Tipo: Todas</option>
                        <c:forEach var="t" items="${tipos}">
                            <option value="<c:out value='${t}'/>" ${t eq tipo ? 'selected' : ''}><c:out value="${t}"/></option>
                        </c:forEach>
                    </select>
                    <input type="date" name="fecha" value="<c:out value='${fecha}'/>"
                           class="w-full bg-slate-50 border border-slate-200 font-medium px-3 py-2.5 rounded-xl text-slate-700 outline-none text-xs">
                    <button type="submit" class="w-full bg-brand-700 hover:bg-brand-800 text-white font-bold px-3 py-2.5 rounded-xl flex items-center justify-center gap-1.5 transition">
                        <i data-lucide="filter" class="w-3.5 h-3.5"></i><span>Filtrar</span>
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/infracciones"
                       class="w-full bg-slate-100 hover:bg-slate-200 text-slate-600 px-3 py-2.5 rounded-xl flex items-center justify-center gap-1 font-semibold transition">
                        <i data-lucide="filter-x" class="w-3.5 h-3.5"></i><span>Limpiar</span>
                    </a>
                </div>
            </div>
        </form>

        <!-- Banner de selección en lote (aparece solo con registros marcados) -->
        <div id="bannerLote" class="hidden bg-blue-50/80 border border-blue-200 rounded-2xl px-3.5 py-2.5 flex flex-col md:flex-row md:items-center justify-between gap-2 text-xs">
            <div class="flex items-center gap-2 text-brand-800 font-bold">
                <i data-lucide="check-check" class="w-4 h-4 text-brand-600"></i>
                <span><span id="contador">0</span> registros seleccionados</span>
            </div>
            <div class="flex flex-wrap items-center gap-2">
                <%-- CONECTAR: acciones en lote (ej. exportar). Los ids marcados están en .chk:checked --%>
                <button type="button" class="bg-white hover:bg-slate-50 text-slate-700 border border-slate-200 px-2.5 py-1.5 rounded-lg font-bold text-[11px] flex items-center gap-1 transition">
                    <i data-lucide="file-output" class="w-3 h-3"></i><span>Exportar selección</span>
                </button>
            </div>
        </div>

        <!-- Registros -->
        <div class="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">

            <!-- Tabla (tableta y escritorio) -->
            <div class="hidden md:block overflow-x-auto">
                <table class="w-full text-left text-xs border-collapse">
                    <thead>
                    <tr class="bg-slate-50/80 border-b border-slate-200 text-[10px] uppercase font-bold text-slate-500 tracking-wider">
                        <th class="p-3 w-10 text-center">
                            <input id="chkTodos" type="checkbox" class="rounded border-slate-300 text-brand-700 focus:ring-brand-500" aria-label="Seleccionar todos">
                        </th>
                        <th class="p-3">N° Boleta</th>
                        <th class="p-3">Placa</th>
                        <th class="p-3">Infracción</th>
                        <th class="p-3 hidden xl:table-cell">Lugar</th>
                        <th class="p-3 hidden lg:table-cell">Fecha</th>
                        <th class="p-3 text-right">Monto</th>
                        <th class="p-3 text-center">Estado</th>
                        <th class="p-3 text-center">Acciones</th>
                    </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-100 font-medium">
                    <%-- CONECTAR: cambia los campos de "inf" por los de tu DTO --%>
                    <c:forEach var="inf" items="${infracciones}">
                        <tr class="hover:bg-slate-50/70 transition">
                            <td class="p-3 text-center">
                                <input type="checkbox" class="chk rounded border-slate-300 text-brand-700 focus:ring-brand-500"
                                       data-id="<c:out value='${inf.numeroInfraccion}'/>" aria-label="Seleccionar">
                            </td>
                            <td class="p-3 font-mono font-bold text-brand-700 whitespace-nowrap"><c:out value="${inf.numeroInfraccion}"/></td>
                            <td class="p-3">
                                <span class="inline-block px-2 py-0.5 rounded bg-slate-100 border border-slate-300 font-mono font-bold text-[11px] text-slate-800">
                                    <c:out value="${inf.placaVehiculo}"/>
                                </span>
                            </td>
                            <td class="p-3 font-bold text-slate-800"><c:out value="${inf.tipoInfraccion}"/></td>
                            <td class="p-3 hidden xl:table-cell text-slate-500 max-w-[14rem] truncate"><c:out value="${inf.direccionInfraccion}"/></td>
                            <td class="p-3 hidden lg:table-cell text-slate-500 whitespace-nowrap"><c:out value="${inf.fechaInfraccion}"/></td>
                            <td class="p-3 text-right font-mono font-bold text-slate-900 whitespace-nowrap">Q <c:out value="${inf.montoOriginal}"/></td>
                            <td class="p-3 text-center">
                                <span class="inline-block px-2.5 py-0.5 rounded-full border text-[10.5px] font-bold
                                    ${inf.estado eq 'Pendiente' ? 'bg-amber-50 text-amber-700 border-amber-200'
                                    : (inf.estado eq 'Anulado' ? 'bg-slate-100 text-slate-600 border-slate-200'
                                    : (inf.estado eq 'Impugnado' ? 'bg-rose-50 text-rose-700 border-rose-200'
                                    : 'bg-emerald-50 text-emerald-700 border-emerald-200'))}">
                                    <c:out value="${inf.estado}"/>
                                </span>
                            </td>
                            <td class="p-3 text-center">
                                <div class="inline-flex items-center gap-1">
                                    <%-- CONECTAR: rutas de ver / editar / anular --%>
                                    <a href="${pageContext.request.contextPath}/admin/infracciones/${inf.numeroInfraccion}" title="Ver"
                                       class="w-7 h-7 rounded-lg bg-slate-100 hover:bg-slate-200 text-slate-600 flex items-center justify-center transition">
                                        <i data-lucide="eye" class="w-3.5 h-3.5"></i>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/admin/infracciones/${inf.numeroInfraccion}/editar" title="Editar"
                                       class="w-7 h-7 rounded-lg bg-blue-50 hover:bg-blue-100 text-brand-700 flex items-center justify-center transition">
                                        <i data-lucide="edit-3" class="w-3.5 h-3.5"></i>
                                    </a>
                                    <button type="button" title="Anular" data-numero="<c:out value='${inf.numeroInfraccion}'/>" onclick="abrirAnular(this)"
                                            class="w-7 h-7 rounded-lg bg-rose-50 hover:bg-rose-100 text-rose-600 flex items-center justify-center transition">
                                        <i data-lucide="ban" class="w-3.5 h-3.5"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- Tarjetas (móvil) -->
            <div class="md:hidden divide-y divide-slate-100">
                <c:forEach var="inf" items="${infracciones}">
                    <article class="p-4 space-y-2">
                        <div class="flex items-start justify-between gap-2">
                            <label class="flex items-center gap-2 min-w-0">
                                <input type="checkbox" class="chk rounded border-slate-300 text-brand-700 focus:ring-brand-500"
                                       data-id="<c:out value='${inf.numeroInfraccion}'/>" aria-label="Seleccionar">
                                <span class="font-mono font-bold text-brand-700 text-xs break-all"><c:out value="${inf.numeroInfraccion}"/></span>
                            </label>
                            <span class="shrink-0 px-2.5 py-0.5 rounded-full border text-[10.5px] font-bold
                                ${inf.estado eq 'Pendiente' ? 'bg-amber-50 text-amber-700 border-amber-200'
                                : (inf.estado eq 'Anulado' ? 'bg-slate-100 text-slate-600 border-slate-200'
                                : (inf.estado eq 'Impugnado' ? 'bg-rose-50 text-rose-700 border-rose-200'
                                : 'bg-emerald-50 text-emerald-700 border-emerald-200'))}">
                                <c:out value="${inf.estado}"/>
                            </span>
                        </div>
                        <p class="text-sm font-bold text-slate-900"><c:out value="${inf.tipoInfraccion}"/></p>
                        <div class="flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-slate-500">
                            <span class="px-2 py-0.5 rounded bg-slate-100 border border-slate-300 font-mono font-bold text-[11px] text-slate-800"><c:out value="${inf.placaVehiculo}"/></span>
                            <span><c:out value="${inf.fechaInfraccion}"/></span>
                        </div>
                        <p class="text-xs text-slate-500 break-words"><c:out value="${inf.direccionInfraccion}"/></p>
                        <div class="flex items-center justify-between pt-1">
                            <span class="font-mono font-black text-slate-900">Q <c:out value="${inf.montoOriginal}"/></span>
                            <div class="flex items-center gap-1.5">
                                <a href="${pageContext.request.contextPath}/admin/infracciones/${inf.numeroInfraccion}" class="px-3 py-1.5 rounded-lg bg-slate-100 text-slate-700 text-xs font-bold">Ver</a>
                                <a href="${pageContext.request.contextPath}/admin/infracciones/${inf.numeroInfraccion}/editar" class="px-3 py-1.5 rounded-lg bg-blue-50 text-brand-700 border border-blue-100 text-xs font-bold">Editar</a>
                                <button type="button" data-numero="<c:out value='${inf.numeroInfraccion}'/>" onclick="abrirAnular(this)"
                                        class="px-3 py-1.5 rounded-lg bg-rose-50 text-rose-700 border border-rose-200 text-xs font-bold">Anular</button>
                            </div>
                        </div>
                    </article>
                </c:forEach>
            </div>

            <c:if test="${empty infracciones}">
                <div class="p-8 text-center text-sm text-slate-500">No se encontraron registros.</div>
            </c:if>

            <!-- Paginación -->
            <c:if test="${totalPaginas > 1}">
                <c:url var="urlAnterior" value="/admin/infracciones">
                    <c:param name="q" value="${q}"/><c:param name="estado" value="${estado}"/><c:param name="tipo" value="${tipo}"/>
                    <c:param name="fecha" value="${fecha}"/><c:param name="tamano" value="${tamano}"/><c:param name="pagina" value="${pagina - 1}"/>
                </c:url>
                <c:url var="urlSiguiente" value="/admin/infracciones">
                    <c:param name="q" value="${q}"/><c:param name="estado" value="${estado}"/><c:param name="tipo" value="${tipo}"/>
                    <c:param name="fecha" value="${fecha}"/><c:param name="tamano" value="${tamano}"/><c:param name="pagina" value="${pagina + 1}"/>
                </c:url>
                <div class="px-4 lg:px-5 py-3 border-t border-slate-200 flex items-center justify-between gap-3 text-xs text-slate-500">
                    <c:choose>
                        <c:when test="${pagina > 0}">
                            <a href="${urlAnterior}" class="px-3 py-2 rounded-lg border border-slate-200 hover:bg-slate-100 font-semibold text-slate-700 flex items-center gap-1">
                                <i data-lucide="chevron-left" class="w-3.5 h-3.5"></i><span>Anterior</span></a>
                        </c:when>
                        <c:otherwise><span class="px-3 py-2 rounded-lg border border-slate-100 text-slate-300 flex items-center gap-1"><i data-lucide="chevron-left" class="w-3.5 h-3.5"></i><span>Anterior</span></span></c:otherwise>
                    </c:choose>

                    <span class="text-center">Página <strong class="text-slate-800">${pagina + 1}</strong> de <strong class="text-slate-800">${totalPaginas}</strong></span>

                    <c:choose>
                        <c:when test="${pagina + 1 < totalPaginas}">
                            <a href="${urlSiguiente}" class="px-3 py-2 rounded-lg border border-slate-200 hover:bg-slate-100 font-semibold text-slate-700 flex items-center gap-1">
                                <span>Siguiente</span><i data-lucide="chevron-right" class="w-3.5 h-3.5"></i></a>
                        </c:when>
                        <c:otherwise><span class="px-3 py-2 rounded-lg border border-slate-100 text-slate-300 flex items-center gap-1"><span>Siguiente</span><i data-lucide="chevron-right" class="w-3.5 h-3.5"></i></span></c:otherwise>
                    </c:choose>
                </div>
            </c:if>
        </div>
    </main>
</div>

<!-- ================= MODAL: ANULAR ================= -->
<dialog id="dlgAnular" class="rounded-2xl p-0 w-[92vw] max-w-md backdrop:bg-black/40 shadow-xl">
    <form id="formAnular" method="post" class="p-5 space-y-3">
        <input type="hidden" name="_csrf" value="<c:out value='${sessionScope.csrfToken}'/>">
        <div class="flex items-center gap-2">
            <div class="w-9 h-9 rounded-xl bg-rose-50 text-rose-600 flex items-center justify-center"><i data-lucide="ban" class="w-4 h-4"></i></div>
            <div>
                <h3 class="text-sm font-extrabold text-slate-900">Anular infracción</h3>
                <p class="text-[11px] text-slate-500 font-mono" id="numeroAnular"></p>
            </div>
        </div>
        <div>
            <label for="motivo" class="text-[10px] font-bold text-slate-500 block mb-1">Motivo de la anulación</label>
            <textarea id="motivo" name="motivo" rows="3" maxlength="300" required
                      class="w-full bg-slate-50 text-sm p-3 rounded-xl border border-slate-200 focus:bg-white focus:outline-none focus:ring-2 focus:ring-brand-500/40 resize-none"></textarea>
        </div>
        <div class="flex gap-2 pt-1">
            <button type="button" onclick="document.getElementById('dlgAnular').close()"
                    class="flex-1 py-2.5 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold text-xs transition">Cancelar</button>
            <button type="submit" class="flex-1 py-2.5 rounded-xl bg-rose-600 hover:bg-rose-700 text-white font-bold text-xs transition">Anular</button>
        </div>
    </form>
</dialog>

<script>
    function toggleMenu() {
        document.getElementById('sidebar').classList.toggle('-translate-x-full');
        document.getElementById('overlay').classList.toggle('hidden');
    }

    // CONECTAR: ajusta la URL si tu ruta de anulación es distinta
    function abrirAnular(btn) {
        var numero = btn.dataset.numero;
        document.getElementById('numeroAnular').textContent = numero;
        document.getElementById('formAnular').action =
            '${pageContext.request.contextPath}/admin/infracciones/' + encodeURIComponent(numero) + '/anular';
        document.getElementById('motivo').value = '';
        document.getElementById('dlgAnular').showModal();
    }

    // Selección en lote (tabla y tarjetas sincronizadas)
    function actualizarSeleccion() {
        var ids = {};
        document.querySelectorAll('.chk:checked').forEach(function (c) { ids[c.dataset.id] = true; });
        var n = Object.keys(ids).length;
        var total = new Set(Array.prototype.map.call(document.querySelectorAll('.chk'), function (c) { return c.dataset.id; })).size;
        document.getElementById('contador').textContent = n;
        document.getElementById('bannerLote').classList.toggle('hidden', n === 0);
        document.getElementById('chkTodos').checked = total > 0 && n === total;
    }

    document.addEventListener('change', function (ev) {
        var t = ev.target;
        if (t.classList.contains('chk')) {
            document.querySelectorAll('.chk').forEach(function (c) {
                if (c.dataset.id === t.dataset.id) c.checked = t.checked;
            });
            actualizarSeleccion();
        } else if (t.id === 'chkTodos') {
            document.querySelectorAll('.chk').forEach(function (c) { c.checked = t.checked; });
            actualizarSeleccion();
        }
    });

    actualizarSeleccion();
    lucide.createIcons();
</script>
</body>
</html>
