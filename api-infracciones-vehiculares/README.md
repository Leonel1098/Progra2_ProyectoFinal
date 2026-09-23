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
http://localhost:8080/services/infracciones?wsdl
http://localhost:8080/services/tipoinfracciones?wsdl
```

Debe mostrarte un documento XML con las operaciones disponibles (`consultarEstado`,
`crearTipoInfraccion`, `listarTipoInfracciones`, etc.). Esto **no** significa que puedas usar el
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
├── Config       → Configuración de CXF (publicación de endpoints SOAP)
├── Excepcion    → Excepciones de negocio personalizadas
├── Model        → Clases que representan las tablas de MySQL
├── Repository   → Acceso a datos vía JDBC (JdbcTemplate)
├── Service      → Lógica de negocio y validaciones
└── soap         → Contratos (interfaces) e implementaciones de los servicios SOAP
```

Regla de dependencia entre capas (no te saltes niveles):

```text
soap (endpoint SOAP) → Service (reglas de negocio) → Repository (SQL) → MySQL
```

---

## 10. Problemas comunes

| Error | Causa probable | Solución |
|---|---|---|
| `Failed to determine a suitable driver class` | Faltan `spring.datasource.*` en `application.properties` | Revisa el paso 4 |
| `Communications link failure` | MySQL no está iniciado o el puerto/URL está mal | Verifica que el servicio MySQL esté corriendo |
| `Attributes portName, serviceName and endpointInterface are not allowed in the @WebService annotation of an SEI` | Una interfaz de contrato SOAP (`soap\*Service.java`) tiene atributos que solo van en la implementación | Deja esos atributos únicamente en las clases `*ServiceImpl.java` |
| El navegador solo muestra XML al abrir la URL del servicio | Estás viendo el WSDL, no ejecutando una operación | Usa Postman/SoapUI con `POST` y el body XML de la operación |

---

## 11. Tecnologías utilizadas

- Java 21
- Spring Boot 4.1.1
- Apache CXF (JAX-WS) para SOAP
- Spring JDBC (`JdbcTemplate`)
- MySQL 8
- Maven (Maven Wrapper)
