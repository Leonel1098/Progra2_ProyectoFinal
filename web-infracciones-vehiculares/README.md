# Web de Infracciones Vehiculares (Frontend)

Aplicación web que permite consultar infracciones de tránsito por **placa** o por **número de infracción**. Es el *frontend* del proyecto: no tiene base de datos propia, sino que consume un servicio **SOAP** (el backend `api-infracciones-vehiculares`) para obtener los datos.

> **Estado actual:** está implementada la **vista pública de consulta** y su conexión con el SOAP. Inicio de sesión, roles (Admin, Agente, Ciudadano) y pagos están planificados pero **aún no existen**.

---

## Tabla de contenido

1. [Tecnologías](#1-tecnologías)
2. [Arquitectura general](#2-arquitectura-general)
3. [Creación del proyecto](#3-creación-del-proyecto)
4. [Estructura de carpetas](#4-estructura-de-carpetas)
5. [Configuración](#5-configuración)
6. [Cómo ejecutarlo](#6-cómo-ejecutarlo)
7. [El contrato SOAP que consumimos](#7-el-contrato-soap-que-consumimos)
8. [Cómo se consume el API (paso a paso)](#8-cómo-se-consume-el-api-paso-a-paso)
9. [Cómo funciona la vista de consulta](#9-cómo-funciona-la-vista-de-consulta)
10. [Privacidad: qué se muestra y qué no](#10-privacidad-qué-se-muestra-y-qué-no)
11. [Flujo completo de una consulta](#11-flujo-completo-de-una-consulta)
12. [Pruebas manuales](#12-pruebas-manuales)
13. [Problemas frecuentes](#13-problemas-frecuentes)
14. [Siguientes pasos](#14-siguientes-pasos)

---

## 1. Tecnologías

| Componente | Uso |
|---|---|
| Java 17 (se ejecuta con JDK 21) | Lenguaje |
| Spring Boot 4.1.1 (`spring-boot-starter-webmvc`) | Framework web (MVC) |
| JSP + JSTL | Vistas del lado del servidor |
| Tomcat embebido + Jasper | Servidor y compilador de JSP |
| `java.net.http.HttpClient` | Cliente HTTP para llamar al SOAP (incluido en Java) |
| `javax.xml` (DOM) | Lectura de las respuestas XML (incluido en Java) |
| HTML / CSS / JS | Interfaz (fuente *Plus Jakarta Sans* desde Google Fonts) |
| Maven (`mvnw`) | Compilación y dependencias |

No se usa ninguna librería SOAP adicional: el XML se arma y se lee manualmente.

---

## 2. Arquitectura general

```text
┌──────────────┐   HTTP    ┌─────────────────────────┐   SOAP/HTTP   ┌────────────────────────┐
│  Navegador   │ ────────► │  Este proyecto (web)    │ ────────────► │  api-infracciones-     │
│              │ ◄──────── │  Spring Boot  :8081     │ ◄──────────── │  vehiculares  :8080    │
└──────────────┘   HTML    └─────────────────────────┘      XML      └───────────┬────────────┘
                                                                                  │
                                                                           ┌──────▼──────┐
                                                                           │  Base de    │
                                                                           │  datos MySQL│
                                                                           └─────────────┘
```

- El **navegador nunca habla con el SOAP**. Solo el servidor de este proyecto lo hace.
- Este proyecto corre en el puerto **8081**; el backend SOAP, en el **8080**.

---

## 3. Creación del proyecto

Esta es la secuencia con la que se creó, para poder reproducirla.

### 3.1 Generar el proyecto base

Con [Spring Initializr](https://start.spring.io) (o el asistente de IntelliJ):

- **Project:** Maven · **Language:** Java · **Spring Boot:** 4.1.1
- **Group:** `com.infracciones` · **Artifact:** `web-infracciones-vehiculares`
- **Packaging:** `jar` · **Java:** 17
- **Dependencia:** *Spring Web* (`spring-boot-starter-webmvc`)

### 3.2 Agregar soporte para JSP

Spring Boot no compila JSP por defecto. En el `pom.xml` se añadieron:

```xml
<!-- Motor para compilar y ejecutar JSP -->
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
</dependency>

<!-- Etiquetas JSTL para las vistas JSP -->
<dependency>
    <groupId>jakarta.servlet.jsp.jstl</groupId>
    <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
</dependency>
<dependency>
    <groupId>org.glassfish.web</groupId>
    <artifactId>jakarta.servlet.jsp.jstl</artifactId>
</dependency>
```

Y Tomcat con alcance `provided`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

### 3.3 Crear la estructura de carpetas

Se creó el paquete raíz `com.infracciones.web` con sus subpaquetes y las carpetas de recursos y vistas (ver sección 4). Las JSP **no** van en `resources`: van en `src/main/webapp/WEB-INF/jsp/`.

> **Importante:** todas las clases deben estar bajo el mismo paquete raíz que la clase con `@SpringBootApplication`. Spring solo busca controladores y servicios en ese paquete y sus subpaquetes. En este proyecto la clase principal es `com.infracciones.web.WebInfraccionesVehicularesApplication`.

### 3.4 Convenciones de nombres

Paquetes, carpetas y archivos de vistas/estáticos van en **minúsculas** (`consulta.jsp`, `consulta.css`, `dto/request`, `dto/response`) para evitar problemas entre sistemas operativos.

---

## 4. Estructura de carpetas

```text
web-infracciones-vehiculares/
├── pom.xml
├── mvnw / mvnw.cmd
├── README.md
├── .gitignore
└── src/
    ├── main/
    │   ├── java/com/infracciones/web/
    │   │   ├── WebInfraccionesVehicularesApplication.java   ← punto de entrada
    │   │   ├── controller/
    │   │   │   └── ConsultaController.java                  ← rutas y validación
    │   │   ├── service/
    │   │   │   ├── ConsultaInfraccionService.java           ← cliente SOAP
    │   │   │   └── ServicioInfraccionesException.java       ← error de integración
    │   │   └── dto/
    │   │       ├── request/ConsultaRequest.java             ← (reservado, sin uso aún)
    │   │       └── response/
    │   │           ├── InfraccionResponse.java              ← respuesta COMPLETA del SOAP
    │   │           └── InfraccionPublica.java               ← versión reducida para la vista
    │   ├── resources/
    │   │   ├── application.properties                       ← configuración local (ignorada por Git)
    │   │   ├── application.properties.example               ← plantilla de configuración
    │   │   └── static/
    │   │       ├── css/consulta.css                         ← estilos
    │   │       └── js/consulta.js                           ← (reservado, sin uso aún)
    │   └── webapp/WEB-INF/jsp/
    │       └── consulta.jsp                                 ← vista principal
    └── test/java/com/infracciones/web/
        └── WebInfraccionesVehicularesApplicationTests.java
```

Qué hace cada capa:

| Capa | Responsabilidad |
|---|---|
| **Vista (JSP)** | Solo muestra datos. No llama al SOAP ni contiene reglas de negocio. |
| **Controlador** | Recibe el formulario, valida la entrada, decide qué consultar y qué enviar a la vista. |
| **Servicio** | Habla con el SOAP: arma la petición, la envía y convierte la respuesta en objetos Java. |
| **DTO** | Objetos simples que transportan datos entre capas. |

---

## 5. Configuración

### 5.1 `application.properties`

Este archivo **no se sube a Git** (está en `.gitignore`). Copia la plantilla y ajústala:

```powershell
Copy-Item src\main\resources\application.properties.example src\main\resources\application.properties
```

Contenido:

```properties
spring.application.name=web-infracciones-vehiculares

# Puerto del frontend
server.port=8081

# Ubicación de las vistas JSP
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp

# Endpoints del Backend
soap.infracciones.url=http://localhost:8080/services/infracciones
```

| Propiedad | Para qué sirve |
|---|---|
| `server.port` | Puerto de esta aplicación. Es **8081** porque el SOAP ya usa el 8080. |
| `spring.mvc.view.prefix` / `suffix` | Cuando un controlador devuelve `"consulta"`, Spring renderiza `/WEB-INF/jsp/consulta.jsp`. |
| `soap.infracciones.url` | Dirección del endpoint SOAP. Se inyecta en el servicio con `@Value`. |

> Si el SOAP cambia de puerto o de servidor, solo se edita `soap.infracciones.url`; no hay que tocar código.

### 5.2 Por qué `application.properties` está en `.gitignore`

La configuración suele variar entre equipos (puertos, URLs, y más adelante credenciales). Por eso el repositorio guarda únicamente `application.properties.example`, igual que en el proyecto `api-infracciones-vehiculares`. Cada persona crea su copia local.

---

## 6. Cómo ejecutarlo

**Requisitos:** JDK 17 o superior y el **backend SOAP en ejecución** en el puerto 8080.

1. Verifica el backend abriendo `http://localhost:8080/services/infracciones?wsdl` (debe mostrar un XML).
2. Crea tu `application.properties` desde el `.example` (sección 5).
3. Inicia el frontend, con una de estas opciones:
   - **IntelliJ:** ejecuta la configuración `WebInfraccionesVehicularesApplication`.
   - **Terminal:**
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```
4. Abre `http://localhost:8081/`.

Pruebas automáticas:

```powershell
.\mvnw.cmd test
```

---

## 7. El contrato SOAP que consumimos

- **Endpoint:** `http://localhost:8080/services/infracciones`
- **WSDL:** `http://localhost:8080/services/infracciones?wsdl`
- **Namespace:** `https://soap.apiinfraccionesvehiculares.infracciones.com/`
- **Encabezados:** solo `Content-Type: text/xml; charset=UTF-8`. **No se usa `SOAPAction`**: el servidor (CXF) decide la operación según el elemento dentro de `<soapenv:Body>`.

### Operaciones disponibles

| Operación | Parámetro | ¿La usa la web? |
|---|---|---|
| `consultarPorPlaca` | `<placa>` | **Sí** (consulta por placa) |
| `consultarPorNumero` | `<numeroInfraccion>` | **Sí** (consulta por número) |
| `listarInfracciones` | ninguno | No. Devuelve todos los registros; no es apta para la consulta pública. |
| `registrarInfraccion` | número, fecha, dirección, DPI, placa, usuario agente, código de tipo | No. Es una acción del rol Agente. |

### Ejemplo de petición (`consultarPorPlaca`)

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:tns="https://soap.apiinfraccionesvehiculares.infracciones.com/">
   <soapenv:Body>
      <tns:consultarPorPlaca>
         <placa>P123ABC</placa>
      </tns:consultarPorPlaca>
   </soapenv:Body>
</soapenv:Envelope>
```

### Ejemplo de respuesta

```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Body>
        <ns2:consultarPorPlacaResponse xmlns:ns2="https://soap.apiinfraccionesvehiculares.infracciones.com/">
            <return>
                <direccionInfraccion>Zona 10, Ciudad de Guatemala</direccionInfraccion>
                <dpiPersona>1234567890101</dpiPersona>
                <estado>Pendiente</estado>
                <fechaInfraccion>2026-10-13</fechaInfraccion>
                <idInfraccion>1</idInfraccion>
                <montoOriginal>550.00</montoOriginal>
                <nombrePersona>Juan Pérez</nombrePersona>
                <numeroInfraccion>INFR-0001</numeroInfraccion>
                <placaVehiculo>P123ABC</placaVehiculo>
                <tipoInfraccion>Exceso de velocidad</tipoInfraccion>
                <usuarioAgente>jperez</usuarioAgente>
            </return>
        </ns2:consultarPorPlacaResponse>
    </soap:Body>
</soap:Envelope>
```

- Cada infracción viene en un elemento `<return>`. Si hay **varias**, llegan varios `<return>`; si **no hay ninguna**, no llega ninguno (lista vacía).
- Los estados posibles en la base de datos son: `Pendiente`, `Pagada`, `Vencida` y `Anulada`.

---

## 8. Cómo se consume el API (paso a paso)

Todo ocurre en `ConsultaInfraccionService`.

### Paso 1: recibir la URL

```java
public ConsultaInfraccionService(@Value("${soap.infracciones.url}") String soapUrl) {
    this.soapUrl = soapUrl;
}
```

Spring lee `soap.infracciones.url` de `application.properties` y la entrega al constructor.

### Paso 2: exponer una operación por cada tipo de búsqueda

```java
public List<InfraccionResponse> consultarPorPlaca(String placa) {
    return invocar("consultarPorPlaca", "placa", placa);
}

public List<InfraccionResponse> consultarPorNumero(String numeroInfraccion) {
    return invocar("consultarPorNumero", "numeroInfraccion", numeroInfraccion);
}
```

Ambas usan el método privado `invocar`, que recibe el nombre de la operación, el nombre del parámetro y su valor.

### Paso 3: construir el XML

`invocar` arma el mismo sobre SOAP que se envía desde Postman. El valor del usuario pasa por `escapar(...)`, que reemplaza `& < > " '` por sus entidades XML, para que nadie pueda inyectar etiquetas en la petición.

### Paso 4: enviar la petición HTTP

```java
HttpRequest request = HttpRequest.newBuilder(URI.create(soapUrl))
        .timeout(Duration.ofSeconds(10))
        .header("Content-Type", "text/xml; charset=UTF-8")
        .POST(HttpRequest.BodyPublishers.ofString(envelope, StandardCharsets.UTF_8))
        .build();
```

- Es un `POST` con el XML como cuerpo.
- Tiene un tiempo máximo de 10 segundos (y 5 para conectar), para que la página no quede colgada si el backend no responde.

### Paso 5: verificar el estado HTTP

Si el código de respuesta no es `200`, se lanza `ServicioInfraccionesException`.

### Paso 6: leer el XML de respuesta

`leerRespuesta` convierte el XML en una lista de `InfraccionResponse`:

1. Crea el *parser* con protección contra **XXE** (`disallow-doctype-decl`).
2. Busca todos los elementos `<return>`.
3. Por cada uno, lee los campos (`numeroInfraccion`, `estado`, `montoOriginal`, etc.) con el método `texto(...)`, que devuelve `""` si un campo no existe.

### Paso 7: manejar los errores

Cualquier problema de conexión, lectura o interrupción se convierte en `ServicioInfraccionesException`. El controlador la captura y muestra un mensaje genérico al usuario, **sin revelar detalles técnicos**.

### Resumen visual

```text
consultarPorPlaca("P123ABC")
        │
        ▼
 invocar(...) ── arma XML ──► POST http://localhost:8080/services/infracciones
                                      │
                                      ▼
                              respuesta XML (200)
                                      │
                                      ▼
 leerRespuesta(...) ── un <return> por infracción ──► List<InfraccionResponse>
```

---

## 9. Cómo funciona la vista de consulta

### 9.1 Rutas

| Método | Ruta | Qué hace |
|---|---|---|
| `GET` | `/` y `/consulta` | Muestra el formulario vacío. |
| `POST` | `/consulta` | Procesa el formulario y muestra el resultado. |

### 9.2 El formulario (`consulta.jsp`)

- **Pestañas:** *Por Placa* y *Por No. Infracción*. Un pequeño script (`seleccionarModo`) cambia las etiquetas, el *placeholder* y la longitud máxima, y oculta o muestra el selector de tipo de placa.
- **Campo oculto `tipoBusqueda`:** guarda el modo elegido. Sus valores válidos son `PLACA` e `INFRACCION` (deben coincidir exactamente con lo que espera el controlador).
- **Selector `tipoPlaca`:** `P`, `M`, `C` o `A`, dentro del recuadro de la placa.
- **Campo `valor`:** los números y letras de la placa, o el número de infracción.
- El formulario se envía por `POST`, de modo que el dato **no queda en la URL**.

### 9.3 Lo que hace el controlador

`ConsultaController.consultar(...)` realiza estos pasos:

1. **Normaliza** el dato: quita espacios y lo pasa a mayúsculas.
2. **Arma el criterio.**
   - Por placa: `tipoPlaca` + el valor sin caracteres especiales. Ejemplo: `P` + `123ABC` = `P123ABC`.
   - Por número: el valor tal cual, por ejemplo `INFR-0001`.
3. **Valida el formato** con expresiones regulares, antes de llamar al backend:
   - Placa: `^[PMCA]\d{3}[A-Z]{3}$`
   - Número de infracción: `^[A-Z0-9-]{3,30}$`
4. **Consulta el servicio** (`consultarPorPlaca` o `consultarPorNumero`).
5. **Decide qué mostrar** (ver tabla).

| Situación | Atributo que recibe la JSP | Lo que ve el usuario |
|---|---|---|
| Formato inválido | `mensaje` | Aviso con un ejemplo del formato correcto. |
| Placa sin infracciones | `solvente = true` | Tarjeta verde "Vehículo Solvente / Paz y Salvo". |
| Número sin resultados | `mensaje` | "No se encontró una infracción con ese número." |
| Una o varias infracciones | `infracciones` (lista) | Tarjetas con boleta, fecha, motivo, monto y estado. |
| El backend falla | `mensaje` | "No pudimos realizar la consulta en este momento." |

### 9.3.1 Tarjetas de resultado

Cada tarjeta muestra:

- **Boleta** y **fecha**.
- Etiqueta de **estado**: roja si está *Pendiente*, verde en cualquier otro caso.
- **Motivo** (tipo de infracción) y **monto** en quetzales (Q).
- Botón **"Pagar Infracción"** únicamente si está *Pendiente*. Por ahora lleva a `/login?redirect=pago&infraccionId=<número>`; esa ruta **todavía no existe**.

### 9.4 Cómo se conecta JSP con Java

La JSP lee atributos que el controlador puso en el `Model`:

```jsp
<c:forEach var="inf" items="${infracciones}">
    ${inf.numeroBoleta}   <%-- llama a getNumeroBoleta() --%>
    ${inf.pendiente}      <%-- llama a isPendiente()    --%>
</c:forEach>
```

Cuando se usa `${inf.xxx}`, la JSP llama al método `getXxx()` (o `isXxx()` si es booleano) del objeto `InfraccionPublica`. Por eso esa clase tiene *getters* y no es un `record`.

### 9.5 Estilos

`consulta.css` define:

- Barra superior con el botón **Iniciar sesión**.
- Encabezado, tarjeta principal y pestañas.
- Recuadro de la placa con el selector de tipo.
- Tarjetas de resultado, etiquetas de estado y botón de pago.
- Diseño adaptable a celulares (`@media`).

---

## 10. Privacidad: qué se muestra y qué no

La consulta pública la puede hacer **cualquier persona** que conozca una placa o un número de infracción. Por eso hay dos objetos distintos:

| Objeto | Contenido | Dónde se usa |
|---|---|---|
| `InfraccionResponse` | **Todo** lo que devuelve el SOAP (incluye DPI, nombre, dirección, agente) | Solo en el servidor |
| `InfraccionPublica` | Número, fecha, motivo, monto y estado | Es lo único que llega a la JSP |

`InfraccionPublica.desde(...)` copia únicamente los cinco campos permitidos. Así, aunque el SOAP envíe datos personales, **nunca llegan al navegador** en la consulta pública.

Cuando exista inicio de sesión, se mostrará más información según el rol, y **el servidor** (no la interfaz) comprobará el permiso:

| Rol | Alcance previsto |
|---|---|
| **Admin** | Acceso y acciones completas. |
| **Agente** | Crea y asigna infracciones. |
| **Ciudadano** | Ve sus propias infracciones e historial y puede pagar. No crea ni elimina multas ni usuarios. |

Buenas prácticas ya aplicadas: validación de formato antes de consultar, escape del XML de entrada, parser protegido contra XXE, mensajes de error genéricos y valores escapados (`<c:out>`) al pintarlos en la página.

---

## 11. Flujo completo de una consulta

Ejemplo: el usuario busca la placa `P-123ABC`.

```text
1. El navegador envía POST /consulta
      tipoBusqueda=PLACA, tipoPlaca=P, valor=123ABC

2. ConsultaController
      criterio = "P" + "123ABC" = "P123ABC"
      valida con la expresión regular  ✔

3. ConsultaInfraccionService.consultarPorPlaca("P123ABC")
      POST XML ──► http://localhost:8080/services/infracciones

4. El SOAP responde con uno o más <return>
      → List<InfraccionResponse>

5. Controlador
      si lista vacía    → solvente = true
      si hay elementos  → infracciones = lista de InfraccionPublica

6. consulta.jsp se renderiza y el navegador muestra el resultado
```

---

## 12. Pruebas manuales

Con el backend en el 8080 y el frontend en el 8081:

| Caso | Entrada | Resultado esperado |
|---|---|---|
| Placa con infracción | `P` + `123ABC` | Tarjeta de `INFR-0001`, estado Pendiente, botón Pagar |
| Placa sin infracciones | una placa válida distinta | "Vehículo Solvente" |
| Por número | `INFR-0001` | La infracción correspondiente |
| Número inexistente | `INFR-9999` | Mensaje de que no se encontró |
| Formato inválido | `12` | Mensaje con el formato esperado |
| Backend apagado | cualquier consulta válida | "No pudimos realizar la consulta…" |

Para comprobar el SOAP de forma aislada, usa Postman: `POST http://localhost:8080/services/infracciones`, encabezado `Content-Type: text/xml; charset=UTF-8` y el XML de la sección 7.

---

## 13. Problemas frecuentes

| Síntoma | Causa probable | Solución |
|---|---|---|
| *Whitelabel Error Page* (404) en `/` | Falta el mapeo de la ruta | El controlador debe tener `@GetMapping({"/", "/consulta"})`. |
| Siempre aparece "No pudimos realizar la consulta" | URL del SOAP mal escrita, backend apagado o puerto incorrecto | Revisa `soap.infracciones.url` (debe decir `services`, no `serices`) y abre el `?wsdl` en el navegador. |
| El servidor no arranca: "Port already in use" | Dos aplicaciones en el mismo puerto | Frontend en 8081 y backend en 8080. |
| `ClassNotFoundException` al ejecutar desde IntelliJ | La configuración apunta a una clase principal antigua | Edita la configuración y elige `com.infracciones.web.WebInfraccionesVehicularesApplication`. |
| Controlador o servicio no detectado | Está fuera del paquete de la clase principal | Mantén todo bajo `com.infracciones.web`. |
| La JSP aparece como texto o da 404 | Faltan las dependencias de JSP o el prefijo | Revisa el `pom.xml` y `spring.mvc.view.prefix`. |
| Falta `application.properties` tras un `git pull` | El archivo está ignorado | Cópialo desde `application.properties.example`. |
| Las pestañas no cambian de modo | Los valores de `seleccionarModo(...)` no coinciden | Deben ser exactamente `'PLACA'` e `'INFRACCION'`. |

---

## 14. Siguientes pasos

- [ ] Vista de **inicio de sesión** (`/login`) y autenticación contra el backend.
- [ ] Manejo de **roles** (Admin, Agente, Ciudadano) con permisos validados en el servidor.
- [ ] Flujo de **pago**: el botón "Pagar" debe continuar después del login y comprobar que el ciudadano es el titular.
- [ ] Vistas autenticadas: historial del ciudadano, registro de infracciones (Agente), administración (Admin).
- [ ] Protección contra consultas automatizadas (límite de intentos).
- [ ] Pruebas automáticas del controlador y del servicio (con un SOAP simulado).
- [ ] Mantener el selector de tipo de placa y la pestaña activa tras enviar el formulario.
