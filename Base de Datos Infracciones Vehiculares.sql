CREATE DATABASE infracciones_vehiculares;
USE infracciones_vehiculares;

CREATE TABLE rol(
id_rol int auto_increment primary key,
nombre_rol varchar(50) not null,
descripcion varchar(250)
);

insert into rol(nombre_rol, descripcion) values
('Admin', 'Administrador del Sistema'),
('Agente', 'Usuario encargado de registrar infracciones'),
('Ciudadano', 'Usuario que consulta y paga infracciones');

CREATE TABLE tipo_infraccion(
id_tipo_infraccion int auto_increment primary key,
codigo varchar(50) not null unique,
nombre varchar(250) not null,
descripcion varchar(250),
monto decimal(10,2) not null
);
insert into tipo_infraccion
(codigo, nombre, descripcion, monto)
VALUES
('INF-001', 'Exceso de velocidad', 'Conducir por encima del límite permitido', 500.00),
('INF-002', 'Estacionamiento prohibido', 'Estacionar en un área donde está prohibido', 250.00),
('INF-003', 'No portar licencia', 'Conducir sin portar la licencia correspondiente', 300.00);


CREATE TABLE estado_infraccion(
id_estado int auto_increment primary key,
nombre_estado varchar(250) not null
);

insert into estado_infraccion (nombre_estado)
VALUES
('Pendiente'),
('Pagada'),
('Vencida'),
('Anulada');

CREATE TABLE regla_descuento(
id_regla int auto_increment primary key,
porcentaje decimal(10,2) not null,
dias_limite int not null,
estado boolean not null
);

INSERT INTO regla_descuento (porcentaje, dias_limite, estado)
VALUES (50.00, 4, true);

CREATE TABLE persona(
id_persona int auto_increment primary key,
dpi_persona varchar(20) not null unique,
nombre_persona varchar(250) not null,
apellido_persona varchar (250) not null,
telefono varchar(50) not null,
direccion varchar(250) not null
);

CREATE TABLE usuario(
id_usuario int auto_increment primary key,
usuario varchar(20) not null unique,
contraseña varchar(250) not null,
estado boolean not null,
id_persona int not null unique, 
id_rol int not null,
foreign key (id_persona) references persona(id_persona),
foreign key(id_rol) references rol(id_rol)
);


CREATE TABLE vehiculo(
id_vehiculo int auto_increment primary key,
placa_vehiculo varchar(50) not null unique,
marca varchar(50) not null,
modelo varchar(50) not null,
color varchar(50) not null
);

CREATE TABLE infraccion(
id_infraccion int auto_increment primary key,
numero_infraccion varchar(50) not null unique,
fecha_infraccion date not null,
direccion_infraccion varchar(250) not null,
monto_original decimal(10,2) not null,
id_persona int not null,
id_vehiculo int not null,
id_usuario int not null,
id_tipo_infraccion int not null,
id_estado int not null,
foreign key(id_persona) references persona(id_persona),
foreign key(id_vehiculo) references vehiculo(id_vehiculo),
foreign key(id_usuario) references usuario(id_usuario),
foreign key(id_tipo_infraccion) references tipo_infraccion(id_tipo_infraccion),
foreign key(id_estado) references estado_infraccion(id_estado)
);

CREATE TABLE pago(
id_pago int auto_increment primary key,
id_infraccion int not null,
monto_inicial decimal(10,2) not null,
porcentaje_descuento decimal(5,2) not null,
monto_descuento decimal(10,2) not null,
monto_pagado decimal(10,2) not null,
fecha_pago date not null,
foreign key(id_infraccion) references infraccion(id_infraccion)
);








