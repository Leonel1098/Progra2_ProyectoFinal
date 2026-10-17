# Web de Infracciones Vehiculares

Aplicación web MVC para consultar infracciones y operar las funciones de los roles Ciudadano, Agente y Admin. El frontend usa JSP y consume por SOAP las operaciones existentes de `api-infracciones-vehiculares`; no mantiene una base de datos propia ni recrea el CRUD del API.

## Funcionalidad actual

- Consulta pública de infracciones por placa o número.
- Registro e inicio de sesión, cierre de sesión y control de acceso por rol.
- Portal Ciudadano: infracciones asociadas al DPI autenticado, búsqueda por placa o número, historial y flujo de pago.
- Portal Agente: registro de infracciones, gestión de personas y vehículos, y consulta autenticada de infracciones.
- Panel Admin: consultar y anular infracciones; crear, consultar, actualizar y eliminar personas, vehículos y tipos de infracción; crear, consultar y eliminar usuarios, actualizar su rol/estado y cambiar su contraseña.
- Formularios responsivos para escritorio y móvil.

## Tecnologías

| Componente | Uso |
|---|---|
| Java 17 | Lenguaje |
| Spring Boot 4.1.1 | Aplicación MVC |
| JSP + JSTL | Vistas del servidor |
| Tomcat embebido + Jasper | Servidor y compilador JSP |
| `java.net.http.HttpClient` | Cliente HTTP SOAP |
| DOM XML | Lectura de respuestas SOAP |
| Maven Wrapper | Compilación y ejecución |

El cliente SOAP está implementado en `SoapClient`; no se usa una biblioteca SOAP adicional. La boleta imprimible tampoco requiere una dependencia PDF: usa el diálogo de impresión del navegador y la opción **Guardar como PDF**.

## Arquitectura

```text
Navegador ──HTTP──> Frontend Spring Boot :8081 ──SOAP/HTTP──> API :8080 ──> MySQL
```

El navegador solo se comunica con el frontend. Los controladores validan formularios, los servicios llaman al API con `SoapClient` y las JSP presentan los datos. En operaciones autenticadas se envía el token del usuario en el encabezado `Authorization: Bearer ...`; la autorización final se aplica en el API.

## Rutas principales

| Ruta | Acceso | Función |
|---|---|---|
| `/` o `/consulta` | Público | Consultar infracciones por placa o número |
| `/registro` | Público | Crear una cuenta de ciudadano |
| `/login` | Público | Iniciar sesión |
| `/inicio` | Sesión iniciada | Menú según el rol |
| `/ciudadano/infracciones` | Ciudadano | Ver y buscar infracciones propias, historial y pagos |
| `/agente/infracciones/nueva` | Agente/Admin según autorización del API | Registrar una infracción |
| `/agente/operaciones` | Agente/Admin según autorización del API | Personas, vehículos y consultas de infracciones |
| `/admin/infracciones` | Admin | Listar, filtrar, consultar por placa/número/DPI, ver detalle y anular |
| `/admin/personas` | Admin | Listar/buscar, crear, actualizar y eliminar personas |
| `/admin/vehiculos` | Admin | Listar/buscar, crear, actualizar y eliminar vehículos |
| `/admin/tipos-infraccion` | Admin | Listar/buscar, crear, actualizar y eliminar tipos de infracción |
| `/admin/usuarios` | Admin | Listar/buscar, crear, actualizar rol/estado y contraseña, y eliminar usuarios |

El panel Admin no permite pagos. No se agregaron secciones independientes para estados, reglas de descuento o roles: el API no expone operaciones para esos catálogos y el rol se asigna al crear o actualizar un usuario.

Las rutas POST de formularios autenticados incluyen token CSRF de sesión. El interceptor web protege las rutas privadas, y el API vuelve a validar token, rol y propiedad de datos.

## Funciones del agente

### Registro de infracción

La vista `agente-infraccion.jsp` carga desde el API:

- Vehículos y placas disponibles, como selector.
- Personas y DPI disponibles, como selector.
- Catálogo de tipos de infracción, como selector compacto con tipo, monto y descripción.

El agente puede crear una persona o vehículo desde formularios integrados en la misma página. Al guardarlos, se vuelve al formulario y se conservan los datos de la infracción para continuar. La fecha solo permite el día actual y el servidor rechaza fechas distintas.

Al registrar, se genera el número de infracción en el frontend y se envía `registrarInfraccion` al API. El servicio devuelve el detalle de la infracción. La confirmación incluye **Guardar como PDF**, que imprime únicamente la boleta; en el diálogo del navegador se elige guardar como PDF.

### Personas, vehículos y consultas

La vista `agente-operaciones.jsp` consume las operaciones de personas, vehículos e infracciones del API:

- Personas: crear, listar y consultar por DPI.
- Vehículos: crear, listar y consultar por placa.
- Infracciones: consultar por placa, por número y por DPI.
- Los filtros de búsqueda usan selectores con los registros disponibles. Las personas se muestran como nombre, apellido y DPI; los vehículos, como placa, marca y modelo.
- Los resultados se presentan en tablas en pantallas grandes y tarjetas en pantallas pequeñas.

El API restringe `listarInfracciones` al rol Admin. Para ofrecer opciones de número al agente, el frontend recopila los números consultando las infracciones por cada placa registrada. Cuando se selecciona un número, consulta ese registro con `consultarPorNumero`.

## Funciones del administrador

El panel de administración está organizado en vistas responsivas para infracciones, personas, vehículos, tipos de infracción y usuarios. Las vistas reutilizan el token de sesión y llaman a los servicios SOAP del API; las operaciones de escritura usan POST con token CSRF.

- Infracciones: listar y filtrar por estado, tipo y fecha; buscar por placa, número o DPI; consultar el detalle y solicitar la anulación.
- Personas y vehículos: listar, consultar por DPI o placa, crear, actualizar y eliminar.
- Tipos de infracción: listar, consultar por código, crear, actualizar y eliminar.
- Usuarios: listar y consultar por nombre de usuario; crear con contraseña inicial, cambiar rol/estado, actualizar contraseña y eliminar.
- No se muestran acciones de pago en el panel Admin. Los catálogos independientes de estados y reglas de descuento tampoco aparecen porque el API no expone operaciones para administrarlos.

La anulación de una infracción pagada está bloqueada por el API. La vista oculta el botón para ese estado y muestra que no se puede anular porque ya está pagada; si el estado cambia entre la carga y la acción, el rechazo del API se presenta con el mismo mensaje.

La navegación del panel destaca la sección activa y funciona en escritorio y móvil. Los rótulos con caracteres acentuados se renderizan con entidades HTML para evitar errores de codificación en las pestañas.

## API SOAP y configuración

Los endpoints locales están en `src/main/resources/application.properties.example`:

```properties
server.port=8081
soap.infracciones.url=http://localhost:8080/services/infracciones
soap.tipos.url=http://localhost:8080/services/tipoinfracciones
soap.personas.url=http://localhost:8080/services/personas
soap.vehiculos.url=http://localhost:8080/services/vehiculos
soap.auth.url=http://localhost:8080/services/auth
soap.pagos.url=http://localhost:8080/services/pagos
soap.usuarios.url=http://localhost:8080/services/usuarios
```

Crea la configuración local desde la plantilla:

```powershell
Copy-Item src\main\resources\application.properties.example src\main\resources\application.properties
```

`application.properties` está ignorado por Git para permitir valores locales. Asegúrate de tener el API activo en el puerto 8080 y ajustar las URL si el servidor cambia.

### Operaciones consumidas

| Servicio | Operaciones usadas |
|---|---|
| Infracciones | `consultaPublicaPorPlaca`, `consultaPublicaPorNumero`, `listarInfracciones`, `consultarPorPlaca`, `consultarPorNumero`, `consultarPorDpi`, `registrarInfraccion`, `anularInfraccion` |
| Tipos de infracción | Listar/consultar y operaciones de creación, actualización y eliminación |
| Personas | `listarPersonas`, `consultarPersonaPorDpi`, `crearPersona`, actualización y eliminación |
| Vehículos | `listarVehiculos`, `consultarVehiculoPorPlaca`, `crearVehiculo`, actualización y eliminación |
| Usuarios | Listar, consultar, crear, actualizar, cambiar contraseña y eliminar |
| Autenticación | Operaciones de registro, login y logout provistas por el API |
| Pagos | Operaciones de consulta y pago provistas por el API |

Las operaciones autenticadas se invocan con el token de sesión. El frontend no agrega endpoints ni lógica de persistencia al backend.

## Ejecutar

Requisitos: JDK 17 o superior y el API en ejecución.

1. Verifica el WSDL, por ejemplo `http://localhost:8080/services/infracciones?wsdl`.
2. Crea `application.properties` desde `.example`.
3. Inicia la aplicación:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. Abre `http://localhost:8081/`.

Para compilar:

```powershell
.\mvnw.cmd -DskipTests compile
```

## Estructura relevante

```text
src/main/java/com/infracciones/web/
├── config/       # Sesión, roles e interceptor
├── controller/   # Consulta, autenticación, ciudadano, agente y administración
├── service/      # Cliente SOAP y servicios de integración por dominio
└── dto/          # Datos de sesión y respuestas

src/main/webapp/WEB-INF/jsp/
├── consulta.jsp
├── login.jsp
├── registro.jsp
├── inicio.jsp
├── mis-infracciones.jsp
├── pago.jsp
├── agente-infraccion.jsp
├── agente-operaciones.jsp
├── admin-infracciones.jsp
├── admin-infraccion-detalle.jsp
├── admin-registros.jsp
└── admin-nav.jspf
```

## Consideraciones

- La consulta pública usa respuestas reducidas para no exponer datos personales.
- Las consultas privadas de ciudadano y agente consumen operaciones autenticadas del API.
- Las acciones y permisos deben permanecer validados en el API; ocultar botones en una JSP no sustituye autorización.
- El PDF se obtiene desde la impresión del navegador. La aplicación no almacena ni descarga automáticamente un archivo PDF.
