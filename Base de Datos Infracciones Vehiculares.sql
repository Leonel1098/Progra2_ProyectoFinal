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