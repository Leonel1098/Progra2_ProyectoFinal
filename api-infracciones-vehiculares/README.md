# API Infracciones Vehiculares — Backend SOAP

Backend del sistema de gestión y pago de infracciones vehiculares. Expone servicios **SOAP/XML**
construidos con **Spring Boot + Apache CXF (JAX-WS)** y se conecta a **MySQL** mediante **JDBC**.

Este backend es el **único componente con acceso a la base de datos**. El frontend (proyecto
aparte) nunca debe conectarse directamente a MySQL; toda operación pasa por este servicio SOAP.

```text
Frontend Web  --SOAP/XML-->  Backend (este proyecto)  --JDBC-->  MySQL
```

---

## 1. Requisitos previos

Antes de clonar el proyecto, instala en tu máquina:

| Herramienta | Versión mínima | Notas |
|---|---|---|
| **Java (JDK)** | 21 | Usa la distribución Temurin o cualquier JDK 21 |
| **MySQL Server** | 8.x | Puede ser MySQL local o Workbench |
| **Git** | cualquiera | Para clonar el repositorio |
| **Maven** | No es necesario instalarlo | El proyecto incluye Maven Wrapper (`mvnw.cmd`) |
| **Postman** o **SoapUI** | opcional | Para probar los endpoints SOAP manualmente |

Verifica que Java esté correctamente instalado:

```powershell
java -version
```

Debe mostrar la versión `21.x.x`.

---

## 2. Clonar el repositorio

```powershell
git clone https://github.com/Leonel1098/Progra2_ProyectoFinal.git
cd Progra2_ProyectoFinal\api-infracciones-vehiculares
```

---

## 3. Crear la base de datos en MySQL

Abre MySQL Workbench (o la consola de MySQL) y ejecuta el siguiente script completo. Esto crea la
base de datos `infracciones_vehiculares` con todas sus tablas y datos iniciales.

```sql
CREATE DATABASE infracciones_vehiculares;
USE infracciones_vehiculares;

CREATE TABLE rol(
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL,
    descripcion VARCHAR(250)
);

INSERT INTO rol(nombre_rol, descripcion) VALUES
('Admin', 'Administrador del Sistema'),
('Agente', 'Usuario encargado de registrar infracciones'),
('Ciudadano', 'Usuario que consulta y paga infracciones');

CREATE TABLE tipo_infraccion(
    id_tipo_infraccion INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(250) NOT NULL,
    descripcion VARCHAR(250),
    monto DECIMAL(10,2) NOT NULL
);

INSERT INTO tipo_infraccion (codigo, nombre, descripcion, monto) VALUES
('INF-001', 'Exceso de velocidad', 'Conducir por encima del límite permitido', 500.00),
('INF-002', 'Estacionamiento prohibido', 'Estacionar en un área donde está prohibido', 250.00),
('INF-003', 'No portar licencia', 'Conducir sin portar la licencia correspondiente', 300.00);

CREATE TABLE estado_infraccion(
    id_estado INT AUTO_INCREMENT PRIMARY KEY,
    nombre_estado VARCHAR(250) NOT NULL
);

INSERT INTO estado_infraccion (nombre_estado) VALUES
('Pendiente'),
('Pagada'),
('Vencida'),
('Anulada');

CREATE TABLE regla_descuento(
    id_regla INT AUTO_INCREMENT PRIMARY KEY,
    porcentaje DECIMAL(10,2) NOT NULL,
    dias_limite INT NOT NULL,
    estado BOOLEAN NOT NULL
);

INSERT INTO regla_descuento (porcentaje, dias_limite, estado) VALUES (50.00, 4, TRUE);

CREATE TABLE persona(
    id_persona INT AUTO_INCREMENT PRIMARY KEY,
    dpi_persona VARCHAR(20) NOT NULL UNIQUE,
    nombre_persona VARCHAR(250) NOT NULL,
    apellido_persona VARCHAR(250) NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    direccion VARCHAR(250) NOT NULL
);

CREATE TABLE usuario(
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(20) NOT NULL UNIQUE,
    contraseña VARCHAR(250) NOT NULL,
    estado BOOLEAN NOT NULL,
    id_persona INT NOT NULL UNIQUE,
    id_rol INT NOT NULL,
    FOREIGN KEY (id_persona) REFERENCES persona(id_persona),
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

CREATE TABLE vehiculo(
    id_vehiculo INT AUTO_INCREMENT PRIMARY KEY,
    placa_vehiculo VARCHAR(50) NOT NULL UNIQUE,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    color VARCHAR(50) NOT NULL
);

CREATE TABLE infraccion(
    id_infraccion INT AUTO_INCREMENT PRIMARY KEY,
    numero_infraccion VARCHAR(50) NOT NULL UNIQUE,
    fecha_infraccion DATE NOT NULL,
    direccion_infraccion VARCHAR(250) NOT NULL,
    monto_original DECIMAL(10,2) NOT NULL,
    id_persona INT NOT NULL,
    id_vehiculo INT NOT NULL,
    id_usuario INT NOT NULL,
    id_tipo_infraccion INT NOT NULL,
    id_estado INT NOT NULL,
    FOREIGN KEY(id_persona) REFERENCES persona(id_persona),
    FOREIGN KEY(id_vehiculo) REFERENCES vehiculo(id_vehiculo),
    FOREIGN KEY(id_usuario) REFERENCES usuario(id_usuario),
    FOREIGN KEY(id_tipo_infraccion) REFERENCES tipo_infraccion(id_tipo_infraccion),
    FOREIGN KEY(id_estado) REFERENCES estado_infraccion(id_estado)
);

CREATE TABLE pago(
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_infraccion INT NOT NULL,
    monto_inicial DECIMAL(10,2) NOT NULL,
    porcentaje_descuento DECIMAL(5,2) NOT NULL,
    monto_descuento DECIMAL(10,2) NOT NULL,
    monto_pagado DECIMAL(10,2) NOT NULL,
    fecha_pago DATE NOT NULL,
    FOREIGN KEY(id_infraccion) REFERENCES infraccion(id_infraccion)
);
```

Confirma que la base quedó creada:

```sql
SHOW TABLES;
```

Deberías ver 9 tablas: `rol`, `tipo_infraccion`, `estado_infraccion`, `regla_descuento`,
`persona`, `usuario`, `vehiculo`, `infraccion`, `pago`.

---

## 4. Configurar la conexión a MySQL

Este proyecto **no versiona el archivo con tus credenciales reales**. En su lugar, incluye una
plantilla de ejemplo:

```text
src\main\resources\application.properties.example
```

La primera vez que descargues el proyecto, debes copiar esa plantilla y renombrarla:

```powershell
Copy-Item src\main\resources\application.properties.example src\main\resources\application.properties
```

Después abre el nuevo archivo generado:

```text
src\main\resources\application.properties
```

Y reemplaza los valores de ejemplo con tus credenciales reales de MySQL local:

```properties
spring.application.name=api-infracciones-vehiculares

spring.datasource.url=jdbc:mysql://localhost:3306/infracciones_vehiculares?useSSL=false&serverTimezone=America/Guatemala&allowPublicKeyRetrieval=true
spring.datasource.username=TU_USUARIO_MYSQL
spring.datasource.password=TU_CONTRASENA_MYSQL
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.sql.init.mode=never

cxf.servlet.enabled=true
cxf.servlet.mapping=/services/*
```

> ⚠️ **Importante:** El archivo `application.properties` está incluido en `.gitignore`, por lo que
> tus credenciales **nunca se subirán a GitHub**. Solo `application.properties.example` (con
> valores ficticios) se versiona en el repositorio. Si tu MySQL corre en un puerto distinto a
> `3306`, ajústalo también en la URL.
>
> Si ya tenías un `application.properties` con datos reales creado antes de este cambio, no hace
> falta que lo borres: seguirá funcionando localmente, simplemente ya no se subirá al repositorio.

---

## 5. Compilar el proyecto y ejecutar las pruebas

Desde la carpeta `api-infracciones-vehiculares`, usa el Maven Wrapper incluido (no necesitas
tener Maven instalado):

```powershell
.\mvnw.cmd clean test
```

Si todo está correctamente configurado, el resultado final debe ser:

```text
BUILD SUCCESS
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

Si aparece un error relacionado con `Failed to determine a suitable driver class` o
`Communications link failure`, revisa el paso 4 (credenciales) y que el servicio de MySQL esté
iniciado.

---

## 6. Levantar el backend

```powershell
.\mvnw.cmd spring-boot:run
```

Espera a ver un mensaje similar a:

```text
Started ApiInfraccionesVehicularesApplication in X.XXX seconds
```

El backend queda escuchando en:

```text
http://localhost:8080
```

Deja esa terminal abierta mientras el backend esté en uso.

---

## 7. Verificar que los servicios SOAP están publicados

Con el backend corriendo, abre en el navegador (o con `Invoke-WebRequest`) las siguientes URLs
para confirmar que cada servicio expone su contrato WSDL correctamente:

```text
http://localhost:8080/services/tipoinfracciones?wsdl
http://localhost:8080/services/personas?wsdl
http://localhost:8080/services/usuarios?wsdl
http://localhost:8080/services/vehiculos?wsdl
http://localhost:8080/services/infracciones?wsdl
http://localhost:8080/services/pagos?wsdl
```

Cada URL debe mostrarte un documento XML con las operaciones del servicio (por ejemplo
`crearPersona`, `listarVehiculos`, `registrarInfraccion`). Si el WSDL aparece **sin operaciones**
(un `portType` vacío), revisa la sección 11 (“Regla importante al agregar un servicio nuevo”).
Ver el WSDL **no** significa que puedas usar el
servicio desde el navegador — el WSDL es solo el contrato. Las operaciones reales se invocan con
peticiones `POST` (ver siguiente paso).

---

## 8. Probar las operaciones con Postman

Dentro de este proyecto existe una colección lista para importar:

```text
postman\Proyecto Infracciones.postman_collection.json
```

Para usarla:

1. Abre Postman.
2. Ve a **File → Import**.
3. Selecciona el archivo `Proyecto Infracciones.postman_collection.json`.
4. Prueba las peticiones ya configuradas (todas son `POST` con `Content-Type: text/xml`).

> La colección puede no incluir todavía los servicios más recientes (personas, usuarios, vehículos
> e infracciones). Para esos, usa los ejemplos XML de la sección 10.

Si prefieres armar una petición manual, la estructura general es:

- **Método:** `POST`
- **URL:** `http://localhost:8080/services/tipoinfracciones` (o `/infracciones`, según el servicio)
- **Header:** `Content-Type: text/xml; charset=UTF-8`
- **Body → raw → XML**, por ejemplo:

```xml
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:tns="https://soap.apiinfraccionesvehiculares.infracciones.com/">
    <soapenv:Header/>
    <soapenv:Body>
        <tns:listarTipoInfracciones/>
    </soapenv:Body>
</soapenv:Envelope>
```

---

## 9. Estructura del proyecto

```text
src\main\java\com\infracciones\apiinfraccionesvehiculares
├── Config       → SoapConfig (publica los endpoints SOAP) y SecurityConfig (cifrador BCrypt)
├── Excepcion    → Excepciones de negocio (no encontrado, duplicado, en uso)
├── Model        → Clases que viajan en las respuestas (Persona, Usuario, Vehiculo,
│                  TipoInfraccion, InfraccionDetalle)
├── Repository   → Acceso a datos vía JDBC (JdbcTemplate), único lugar con SQL
├── Service      → Lógica de negocio y validaciones
└── soap         → Contratos (interfaces) e implementaciones de los servicios SOAP

postman\         → Colección de Postman con peticiones de ejemplo
src\main\resources\application.properties.example → Plantilla de configuración
```

Regla de dependencia entre capas (no te saltes niveles):

```text
soap (endpoint SOAP) → Service (reglas de negocio) → Repository (SQL) → MySQL
```

---

## 10. Servicios SOAP disponibles

El backend publica **7 servicios SOAP**. Cada servicio tiene **una sola URL** y todas sus operaciones
se invocan con `POST` a esa URL.

| Servicio | URL del servicio (POST) | WSDL (contrato) | Qué administra |
|---|---|---|---|
| Tipos de infracción | `http://localhost:8080/services/tipoinfracciones` | `...?wsdl` | Catálogo de infracciones y su monto |
| Personas | `http://localhost:8080/services/personas` | `...?wsdl` | Datos de ciudadanos (identificados por DPI) |
| Usuarios | `http://localhost:8080/services/usuarios` | `...?wsdl` | Cuentas del sistema, roles y contraseñas |
| Vehículos | `http://localhost:8080/services/vehiculos` | `...?wsdl` | Vehículos (identificados por placa) |
| Infracciones | `http://localhost:8080/services/infracciones` | `...?wsdl` | Registro, consulta y anulación de infracciones |
| Pagos | `http://localhost:8080/services/pagos` | `...?wsdl` | Cálculo y registro de pagos con descuento por pronto pago |
| Autenticación | `http://localhost:8080/services/auth` | `...?wsdl` | Inicio de sesión, registro de cuenta ciudadana y cierre de sesión |

### 10.1 ¿Cómo sabe el backend qué operación ejecutar si la URL es la misma?

A diferencia de REST, en SOAP **no existen URLs como `/infracciones/registrar`**. La acción va
**dentro del XML**: el primer elemento dentro de `<soapenv:Body>` es el nombre de la operación.

```xml
<soapenv:Body>
    <tns:registrarInfraccion>   <!-- este elemento le dice la acción a ejecutar -->
        ...
    </tns:registrarInfraccion>
</soapenv:Body>
```

Apache CXF lee ese elemento, lo compara con los métodos `@WebMethod` del servicio y ejecuta el método
Java correspondiente.

> **Sobre el encabezado `SOAPAction`:** este proyecto **no lo utiliza**. Las operaciones se publican
> con `soapAction=""` y basta con enviar `Content-Type: text/xml; charset=UTF-8`. Puedes
> comprobarlo abriendo cualquier WSDL y buscando `soapAction=`.

### 10.2 Configuración común de Postman (para todas las operaciones)

| Campo | Valor |
|---|---|
| Método | `POST` |
| URL | La del servicio (sin `?wsdl`) |
| Header | `Content-Type: text/xml; charset=UTF-8` |
| Body | `raw` → `XML` |

Todos los ejemplos usan esta estructura base (solo cambia el contenido dentro de `<soapenv:Body>`):

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:tns="https://soap.apiinfraccionesvehiculares.infracciones.com/">
   <soapenv:Body>
      ... operación ...
   </soapenv:Body>
</soapenv:Envelope>
```

### 10.3 Servicio de tipos de infracción — `/services/tipoinfracciones`

Administra el catálogo de infracciones (código, nombre, descripción y monto). El monto definido
aquí es el que se copia a cada infracción cuando se registra.

| Operación | Parámetros | Qué hace |
|---|---|---|
| `listarTipoInfracciones` | — | Devuelve todos los tipos |
| `consultarTipoInfraccionPorId` | `id` | Devuelve un tipo por su id |
| `consultarTipoInfraccionPorCodigo` | `codigo` | Devuelve un tipo por su código (ej. `INF-001`) |
| `crearTipoInfraccion` | `codigo`, `nombre`, `descripcion`, `monto` | Crea un tipo nuevo |
| `actualizarTipoInfraccion` | `id`, `codigo`, `nombre`, `descripcion`, `monto` | Modifica un tipo existente |
| `eliminarTipoInfraccion` | `id` | Elimina un tipo |

Reglas:
- El `codigo` no se puede repetir.
- El `monto` debe ser mayor a cero.
- **No se puede eliminar** un tipo que ya tenga infracciones registradas (error `TipoInfraccionEnUsoException`).

Ejemplo — crear:

```xml
<tns:crearTipoInfraccion>
   <codigo>INF-004</codigo>
   <nombre>Semáforo en rojo</nombre>
   <descripcion>Cruzar con la luz roja</descripcion>
   <monto>400.00</monto>
</tns:crearTipoInfraccion>
```

### 10.4 Servicio de personas — `/services/personas`

Administra a los ciudadanos. **Toda la identificación se hace por DPI** (no por el id interno).

| Operación | Parámetros | Qué hace |
|---|---|---|
| `listarPersonas` | — | Devuelve todas las personas |
| `consultarPersonaPorDpi` | `dpi` | Devuelve una persona por su DPI |
| `crearPersona` | `dpi`, `nombre`, `apellido`, `telefono`, `direccion` | Registra una persona |
| `actualizarPersona` | `dpi`, `nombre`, `apellido`, `telefono`, `direccion` | Modifica los datos de la persona identificada por el DPI |
| `eliminarPersona` | `dpi` | Elimina una persona |

Reglas:
- Todos los campos son obligatorios y el DPI no se puede repetir.
- **El DPI no se puede modificar**: en `actualizarPersona` se usa solo para ubicar el registro.
- **No se puede eliminar** una persona que tenga un usuario asociado o infracciones a su nombre
  (error `PersonaEnUsoException`).

Ejemplo — crear:

```xml
<tns:crearPersona>
   <dpi>1234567890101</dpi>
   <nombre>Juan</nombre>
   <apellido>Pérez</apellido>
   <telefono>55551234</telefono>
   <direccion>Zona 1, Ciudad de Guatemala</direccion>
</tns:crearPersona>
```

### 10.5 Servicio de usuarios — `/services/usuarios`

Administra las cuentas que usarán el sistema. Cada usuario pertenece a **una persona** (que debe
existir) y tiene **un rol** (`Admin`, `Agente` o `Ciudadano`). Se identifica por su **nombre de usuario**.

| Operación | Parámetros | Qué hace |
|---|---|---|
| `listarUsuarios` | — | Devuelve todos los usuarios |
| `consultarUsuarioPorUsername` | `usuario` | Devuelve un usuario por su nombre de usuario |
| `crearUsuario` | `dpiPersona`, `usuario`, `contrasena`, `nombreRol`, `estado` | Crea la cuenta de una persona existente |
| `actualizarUsuario` | `usuario`, `nombreRol`, `estado` | Cambia el rol y/o el estado (activo/inactivo) |
| `cambiarContrasena` | `usuario`, `nuevaContrasena` | Cambia la contraseña |
| `eliminarUsuario` | `usuario` | Elimina la cuenta |

Reglas y decisiones:
- `crearUsuario` recibe el **DPI** de la persona; el backend busca internamente su `id_persona`.
- El rol se envía **por nombre** (`Admin`, `Agente`, `Ciudadano`); el backend lo traduce a `id_rol`.
- Una persona solo puede tener **un** usuario, y el nombre de usuario no se puede repetir.
- **La contraseña se guarda cifrada con BCrypt** y **nunca se devuelve** en las respuestas XML.
- **No se puede eliminar** un usuario que haya registrado infracciones (error `UsuarioEnUsoException`).

Ejemplo — crear:

```xml
<tns:crearUsuario>
   <dpiPersona>1234567890101</dpiPersona>
   <usuario>jperez</usuario>
   <contrasena>MiClave123</contrasena>
   <nombreRol>Agente</nombreRol>
   <estado>true</estado>
</tns:crearUsuario>
```

### 10.6 Servicio de vehículos — `/services/vehiculos`

Administra los vehículos. **Se identifican por su placa.**

| Operación | Parámetros | Qué hace |
|---|---|---|
| `listarVehiculos` | — | Devuelve todos los vehículos |
| `consultarVehiculoPorPlaca` | `placa` | Devuelve un vehículo por su placa |
| `crearVehiculo` | `placa`, `marca`, `modelo`, `color` | Registra un vehículo |
| `actualizarVehiculo` | `placa`, `marca`, `modelo`, `color` | Modifica marca, modelo y color (la placa no cambia) |
| `eliminarVehiculo` | `placa` | Elimina un vehículo |

Reglas:
- Todos los campos son obligatorios y la placa no se puede repetir.
- **No se puede eliminar** un vehículo que tenga infracciones asociadas (error `VehiculoEnUsoException`).

Ejemplo — crear:

```xml
<tns:crearVehiculo>
   <placa>P123ABC</placa>
   <marca>Toyota</marca>
   <modelo>Corolla 2020</modelo>
   <color>Rojo</color>
</tns:crearVehiculo>
```

### 10.7 Servicio de infracciones — `/services/infracciones`

Es el servicio principal. **No hay eliminación**: las infracciones se **anulan** para conservar el historial.

| Operación | Parámetros | Qué hace |
|---|---|---|
| `listarInfracciones` | — | Devuelve todas las infracciones (más recientes primero) |
| `consultarPorNumero` | `numeroInfraccion` | Devuelve una infracción (requiere sesión; el ciudadano solo accede a la suya) |
| `consultarPorPlaca` | `placa` | Devuelve las infracciones de un vehículo (requiere sesión; para Ciudadano filtra por su DPI) |
| `consultarPorDpi` | `dpi` | Devuelve las infracciones de una persona (requiere sesión; Ciudadano solo puede consultar su DPI) |
| `registrarInfraccion` | `numeroInfraccion`, `fechaInfraccion`, `direccionInfraccion`, `dpiPersona`, `placaVehiculo`, `usuarioAgente`, `codigoTipoInfraccion` | Registra una infracción nueva |
| `consultaPublicaPorPlaca` | `placa` | Consulta pública resumida; puede devolver infracciones de distintas personas asociadas a esa placa |
| `consultaPublicaPorNumero` | `numeroInfraccion` | Consulta pública resumida por número de infracción |
| `anularInfraccion` | `numeroInfraccion` | Cambia el estado a `Anulada` |

**¿Qué devuelve cada infracción?** (objeto `InfraccionDetalle`, pensado para que el frontend no necesite
hacer varias consultas):

| Campo | Contenido |
|---|---|
| `idInfraccion` | Id interno |
| `numeroInfraccion` | Número único de la infracción |
| `fechaInfraccion` | Fecha en formato `YYYY-MM-DD` |
| `direccionInfraccion` | Lugar donde ocurrió |
| `montoOriginal` | Monto tomado del tipo de infracción |
| `dpiPersona` / `nombrePersona` | DPI y nombre completo del infractor |
| `placaVehiculo` | Placa del vehículo |
| `tipoInfraccion` | Nombre del tipo de infracción |
| `estado` | `Pendiente`, `Pagada`, `Vencida` o `Anulada` |
| `usuarioAgente` | Agente que la registró |

**¿Qué pasa al registrar una infracción?** (`registrarInfraccion`, paso a paso)

1. Se valida que el número y la dirección no estén vacíos y que el **número no exista** ya.
2. Si no se envía fecha, se usa la **fecha de hoy**.
3. Se buscan los datos relacionados: persona (por DPI), vehículo (por placa), agente (por el usuario
   asociado a la sesión autenticada) y tipo de infracción (por código). Si alguno **no existe**, se
   devuelve un error claro.
4. El **monto** se toma del tipo de infracción en la base de datos (**el cliente nunca envía el monto**).
5. Se guarda con estado inicial **`Pendiente`**.
6. Se devuelve la infracción completa ya registrada.

**¿Qué pasa al anular?** Solo se puede anular una infracción que **no esté `Pagada` ni `Anulada`**.
No se borra: solo cambia su estado a `Anulada`.

Ejemplo — registrar:

```xml
<tns:registrarInfraccion>
   <numeroInfraccion>INFR-0001</numeroInfraccion>
   <fechaInfraccion>2026-10-13</fechaInfraccion>
   <direccionInfraccion>Zona 10, Ciudad de Guatemala</direccionInfraccion>
   <dpiPersona>1234567890101</dpiPersona>
   <placaVehiculo>P123ABC</placaVehiculo>
   <usuarioAgente>jperez</usuarioAgente> <!-- compatibilidad: el backend usa el usuario del token -->
   <codigoTipoInfraccion>INF-001</codigoTipoInfraccion>
</tns:registrarInfraccion>
```

Aunque el contrato SOAP conserva el parámetro `usuarioAgente`, el backend **lo ignora** al registrar.
El `id_usuario` guardado es el del agente autenticado en el encabezado HTTP `Authorization: Bearer <token>`.
Por eso, varias boletas creadas con la misma sesión de agente tendrán el mismo `id_usuario`, aunque
correspondan a diferentes personas multadas (`id_persona`).

Las consultas `consultaPublicaPorPlaca` y `consultaPublicaPorNumero` no requieren token y devuelven
solo el resumen definido por `InfraccionPublica`. La búsqueda pública por placa puede incluir más de
una persona. Para información completa se usan las consultas protegidas, sujetas a los permisos de rol.

Ejemplos — consultar y anular:

```xml
<tns:consultarPorNumero><numeroInfraccion>INFR-0001</numeroInfraccion></tns:consultarPorNumero>
<tns:consultarPorPlaca><placa>P123ABC</placa></tns:consultarPorPlaca>
<tns:consultarPorDpi><dpi>1234567890101</dpi></tns:consultarPorDpi>
<tns:anularInfraccion><numeroInfraccion>INFR-0001</numeroInfraccion></tns:anularInfraccion>
<tns:listarInfracciones/>
```

### 10.8 Servicio de autenticación — `/services/auth`

El servicio permite iniciar sesión, registrar una cuenta ciudadana y cerrar una sesión. El registro
no crea una persona: valida que ya exista por DPI y que su nombre y apellido coincidan. La persona
puede crear entonces su propio usuario y contraseña; la cuenta recibe automáticamente el rol
`Ciudadano`. Si ya tiene usuario asociado, no puede crear otra cuenta.

| Operación | Parámetros | Requisito |
|---|---|---|
| `login` | `usuario`, `contrasena` | Público; devuelve token, usuario, rol, DPI, nombre y expiración |
| `registrarCuenta` | `dpiPersona`, `nombre`, `apellido`, `usuario`, `contrasena` | Público; valida persona existente y crea una cuenta Ciudadano |
| `logout` | — | Requiere el token de la sesión |

Ejemplo — iniciar sesión:

```xml
<tns:login>
   <usuario>jperez</usuario>
   <contrasena>MiClave123</contrasena>
</tns:login>
```

Las operaciones protegidas reciben el token en el **encabezado HTTP**, no como un campo del XML:

```text
Authorization: Bearer <token-devuelto-por-login>
```

El token identifica el usuario y su rol. Las sesiones duran 120 minutos por defecto y se pueden
revocar con `logout`. Las contraseñas se almacenan con BCrypt.

**Permisos principales:**

| Rol | Acceso |
|---|---|
| `Admin` | Administración de usuarios, personas, vehículos y tipos; listado completo de infracciones y pagos; anulación de infracciones |
| `Agente` | Registro de infracciones y gestión operativa de personas y vehículos |
| `Ciudadano` | Consulta sus propias infracciones y puede calcular o realizar sus pagos |

Las operaciones autenticadas de infracciones limitan al ciudadano a su DPI. Las operaciones de pago
también verifican que la infracción pertenezca al ciudadano de la sesión. El resto de roles solo
accede a las operaciones permitidas para su rol.

---

## 11. Cómo funciona internamente (qué hace cada capa)

Cada operación SOAP atraviesa las mismas capas. Ninguna capa se salta a otra:

```text
soap  →  Service  →  Repository  →  MySQL
```

| Capa | Carpeta | Para qué sirve |
|---|---|---|
| **Contrato SOAP** | `soap\*SoapService.java` | Interfaz que declara qué operaciones existen y sus parámetros (`@WebService`, `@WebMethod`, `@WebParam`). De aquí se genera el WSDL. |
| **Implementación SOAP** | `soap\*SoapServiceImpl.java` | Recibe la llamada SOAP y **solo delega** al servicio de negocio. No tiene lógica. |
| **Servicio (negocio)** | `Service\*.java` | Valida datos, aplica reglas (duplicados, existencia, estados) y decide qué errores lanzar. |
| **Repositorio** | `Repository\*.java` | Único lugar con SQL. Usa `JdbcTemplate` con consultas parametrizadas (`?`) para evitar inyección SQL. |
| **Modelo** | `Model\*.java` | Clases que viajan en las respuestas XML. Requieren constructor vacío y getters/setters. |
| **Excepciones** | `Excepcion\*.java` | Errores de negocio con mensajes claros (no encontrado, duplicado, en uso). |
| **Configuración** | `Config\*.java` | `SoapConfig` publica cada servicio SOAP en su URL; `SecurityConfig` define el cifrador BCrypt. |

### Ejemplo: recorrido completo de `registrarInfraccion`

1. **Postman** envía el XML por `POST` a `/services/infracciones`.
2. **CXF** lee `<tns:registrarInfraccion>` y llama a `InfraccionServiceImpl.registrarInfraccion(...)`.
3. La implementación delega a **`InfraccionServiceLayer`**, que valida y busca persona, vehículo,
   agente y tipo de infracción usando sus repositorios.
4. **`InfraccionRepository`** ejecuta el `INSERT` en MySQL.
5. El servicio vuelve a consultar la infracción (con los `JOIN` a persona, vehículo, tipo, estado y
   usuario) y devuelve un **`InfraccionDetalle`**.
6. CXF lo convierte a XML y responde al cliente.

### Archivos por entidad

| Entidad | Model | Repository | Service | soap |
|---|---|---|---|---|
| Tipo de infracción | `TipoInfraccion` | `TipoInfraccionRepository` | `TipoInfraccionService` | `TipoInfraccionSoapService` / `Impl` |
| Persona | `Persona` | `PersonaRepository` | `PersonaService` | `PersonaSoapService` / `Impl` |
| Usuario | `Usuario` | `UsuarioRepository` | `UsuarioService` | `UsuarioSoapService` / `Impl` |
| Vehículo | `Vehiculo` | `VehiculoRepository` | `VehiculoService` | `VehiculoSoapService` / `Impl` |
| Infracción | `InfraccionDetalle` | `InfraccionRepository` | `InfraccionServiceLayer` | `InfraccionService` / `Impl` |

> El servicio de negocio de infracciones se llama `InfraccionServiceLayer` para no chocar con la
> interfaz SOAP `InfraccionService`.

### Regla importante al agregar un servicio nuevo

Cuando crees un servicio SOAP nuevo, en `Config\SoapConfig.java` el bean debe recibir la **interfaz
del paquete `soap`** (por ejemplo `VehiculoSoapService`), **no** la clase de la capa `Service`.
Si se publica la clase equivocada, el WSDL se genera **sin operaciones** y con otro namespace.

---

## 12. Reglas de negocio y decisiones de diseño

| Tema | Decisión |
|---|---|
| Identificadores públicos | Se usan datos reales y legibles (DPI, placa, nombre de usuario, número de infracción, código de tipo) en lugar de ids internos. El backend traduce a ids. |
| Integridad referencial | Antes de eliminar, el servicio comprueba si el registro está en uso y devuelve un mensaje claro en vez del error crudo de MySQL. |
| Campos inmutables | El DPI de una persona y la placa de un vehículo no se modifican. |
| Contraseñas | Se cifran con **BCrypt** (`spring-security-crypto`) y nunca se devuelven al cliente. |
| Montos | El cliente **nunca** envía montos: se toman del tipo de infracción en la base de datos. |
| Infracciones | No se borran: se **anulan**. Una infracción pagada no se puede anular. |
| Fechas | Viajan como texto `YYYY-MM-DD`. |
| Dinero | Se usa `BigDecimal` para evitar errores de redondeo. |

---

## 13. Orden recomendado para probar

Las tablas dependen unas de otras, así que conviene probar en este orden:

1. **Tipo de infracción:** ya existen `INF-001`, `INF-002` e `INF-003` por el script SQL.
2. **Persona:** `crearPersona`.
3. **Usuario (agente):** `crearUsuario` con el DPI de esa persona y rol `Agente`.
4. **Vehículo:** `crearVehiculo`.
5. **Infracción:** `registrarInfraccion` usando el DPI, la placa, el usuario agente y el código de tipo anteriores.
6. **Consultas:** `consultarPorNumero`, `consultarPorPlaca`, `consultarPorDpi`, `listarInfracciones`.
7. **Anulación:** `anularInfraccion`.

Casos de error recomendados: número de infracción repetido, DPI o placa inexistente, anular una
infracción ya anulada, eliminar una persona con usuario o infracciones, eliminar un vehículo con
infracciones.

---

## 14. Conectar el backend a una base de datos en otra computadora

Si MySQL corre en **otra máquina**, además de cambiar credenciales hay que preparar ambos lados.

**En la máquina de MySQL:**

1. Permitir conexiones remotas en `my.ini`: `bind-address = 0.0.0.0` y reiniciar el servicio MySQL.
2. Crear un usuario que acepte conexiones remotas:

```sql
CREATE USER 'infracciones_user'@'%' IDENTIFIED BY 'contraseña_segura';
GRANT ALL PRIVILEGES ON infracciones_vehiculares.* TO 'infracciones_user'@'%';
FLUSH PRIVILEGES;
```

3. Abrir el **puerto 3306** en el Firewall de Windows (regla de entrada TCP).
4. Obtener su IP con `ipconfig` (IPv4).

**En tu máquina (`application.properties`):** cambia `localhost` por la IP de esa máquina:

```properties
spring.datasource.url=jdbc:mysql://192.168.1.50:3306/infracciones_vehiculares?useSSL=false&serverTimezone=America/Guatemala&allowPublicKeyRetrieval=true
```

**Verificar conectividad antes de levantar el backend:**

```powershell
Test-NetConnection -ComputerName 192.168.1.50 -Port 3306
```

`TcpTestSucceeded` debe ser `True`. Ambas máquinas deben estar en la misma red (o conectadas por VPN).
Si la IP se asigna por DHCP, puede cambiar entre sesiones: vuelve a revisarla si aparece `Connection timed out`.

---

## 15. Cómo consumirá el frontend estos servicios

El frontend **no escribe XML a mano**. Genera un cliente SOAP a partir del WSDL:

1. Se apunta a la URL del WSDL, por ejemplo `http://localhost:8080/services/infracciones?wsdl`.
2. Con `wsimport` o el plugin de CXF para Maven se generan las clases Java (interfaz y modelos).
3. En los controllers se llama como métodos normales:

```java
List<InfraccionDetalle> lista = infraccionService.consultarPorPlaca("P123ABC");
```

La librería arma el XML, lo envía por `POST` a la misma URL y devuelve objetos Java.
Lo único que el frontend necesita del backend es **la URL de cada WSDL**.

---

## 16. Servicio de pagos y regla de descuento — `/services/pagos`

Un pago es una **transacción ya realizada**, por eso este servicio **no tiene actualizar ni eliminar**. El cliente
**solo envía el número de infracción**; el backend calcula todos los montos con la regla de la tabla `regla_descuento`.

| Operación | Parámetros | Qué hace |
|---|---|---|
| `calcularMontoAPagar` | `numeroInfraccion` | Muestra cuánto se pagaría hoy, **sin guardar nada** (útil para que el frontend confirme el monto) |
| `aplicarPago` | `numeroInfraccion` | Registra el pago y marca la infracción como `Pagada` |
| `consultarPagoPorInfraccion` | `numeroInfraccion` | Devuelve el pago de una infracción |
| `listarPagos` | — | Devuelve todos los pagos (más recientes primero) |

**¿Qué hace `aplicarPago`?** (paso a paso, todo dentro de **una transacción**)

1. Busca la infracción por su número. Si no existe, devuelve error.
2. Valida que **no esté `Anulada`**, que **no esté `Pagada`** y que **no tenga ya un pago**.
3. Si no está `Vencida`, lee la **regla activa** de `regla_descuento` (actualmente 50 % y 4 días). Si no hay una regla activa, devuelve error.
4. Calcula `días = fecha de pago − fecha de la infracción`.
5. Si `días <= dias_limite` aplica el descuento; si no, se paga el monto completo. Una infracción `Vencida` siempre se paga completa.
6. `montoDescuento = montoInicial × porcentaje / 100` y `montoPagado = montoInicial − montoDescuento`.
7. Inserta el registro en `pago` y cambia la infracción a `Pagada`. **Si algo falla, se revierte todo**: nunca queda un pago sin cambio de estado ni al revés.

La fecha de pago es la **fecha de hoy del servidor**. Los 4 días se cuentan desde la fecha de la infracción e **incluyen el día 4**.

Ejemplo — calcular y pagar:

```xml
<tns:calcularMontoAPagar>
   <numeroInfraccion>INFR-0001</numeroInfraccion>
</tns:calcularMontoAPagar>
```

```xml
<tns:aplicarPago>
   <numeroInfraccion>INFR-0001</numeroInfraccion>
</tns:aplicarPago>
```

La respuesta incluye `idPago`, `numeroInfraccion`, `montoInicial`, `porcentajeDescuento`, `montoDescuento`, `montoPagado` y `fechaPago`.

**Cómo probar la regla de los 4 días** (con un tipo `INF-001` de Q500):

- Infracción de hace **2 días**: descuento 50 % → `montoDescuento` 250.00 y `montoPagado` 250.00.
- Infracción de hace **13 días**: sin descuento → `montoPagado` 500.00.
- Pagar **dos veces** la misma infracción debe fallar con "ya fue pagada".

Archivos: `Model\Pago`, `Model\ReglaDescuento`, `Model\LocalDateAdapter`, `Repository\PagoRepository`,
`Repository\ReglaDescuentoRepository`, `Service\PagoService`, `soap\PagoSoapService` / `Impl` y
`Excepcion\PagoNoEncontradoException` / `ReglaDescuentoActivaException`.

Pendiente a futuro: un proceso que cambie automáticamente las infracciones a `Vencida`.

---

## 17. Problemas comunes

| Error | Causa probable | Solución |
|---|---|---|
| `Failed to determine a suitable driver class` | Faltan `spring.datasource.*` en `application.properties` | Revisa el paso 4 |
| `Communications link failure` | MySQL no está iniciado o el puerto/URL está mal | Verifica que el servicio MySQL esté corriendo |
| `Attributes portName, serviceName and endpointInterface are not allowed in the @WebService annotation of an SEI` | Una interfaz de contrato SOAP (`soap\*Service.java`) tiene atributos que solo van en la implementación | Deja esos atributos únicamente en las clases `*ServiceImpl.java` |
| El navegador solo muestra XML al abrir la URL del servicio | Estás viendo el WSDL, no ejecutando una operación | Usa Postman/SoapUI con `POST` y el body XML de la operación |
| El WSDL carga pero no muestra operaciones, o Postman responde `Message part {...} was not recognized` | En `SoapConfig` se publicó la clase de la capa `Service` en lugar de la interfaz `soap\*SoapService` | Haz que el bean `Endpoint` reciba la interfaz del paquete `soap` (ver sección 11) |
| `Failed to obtain JDBC Connection` con `Connection timed out` | La máquina de MySQL no responde (apagada, IP distinta, firewall o red distinta) | Revisa la IP en `application.properties` y prueba `Test-NetConnection <ip> -Port 3306` (ver sección 14) |
| Una operación responde con un error de negocio (`... no existe`, `... ya existe`, `... en uso`) | No es un fallo del sistema: se incumplió una regla de negocio | Lee el mensaje y revisa el orden de pruebas de la sección 13 |

---

## 18. Tecnologías utilizadas

- Java 21
- Spring Boot 4.1.1
- Apache CXF (JAX-WS) para SOAP
- Spring JDBC (`JdbcTemplate`)
- Spring Security Crypto (BCrypt) para cifrar contraseñas
- MySQL 8
- Maven (Maven Wrapper)
