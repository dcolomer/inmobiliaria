# inmobiliaria (INMOB10)
> # Una aplicacion Java distribuida no web

## Introducción
Esta aplicación permite que Inmob10, una empresa inmobiliaria dedicada al alquiler de pisos en zonas turísticas, lleve a cabo su operativa diaria: gestión de cobros y pagos, así como las operaciones CRUD de clientes, propietarios y pisos.

Debido a que los pisos que gestiona Inmob10 no son de su propiedad, obtiene los beneficios cobrando comisión por las transferencias de dinero entre quien alquila un piso y el propietario del mismo.

Trabajamos con la base de datos inmobiliaria, que tiene las siguientes tablas:
| **Nombre** | **Descripción**                                                                                         |
|------------|---------------------------------------------------------------------------------------------------------|
| PISO       | Pisos disponibles para alquilar.                                                                        |
| PROP       | Propietarios de los pisos que cobraran su parte de alquiler.                                            |
| CLI        | Clientes que alquilarán los pisos.                                                                      |
| PEDIDO     | Guarda los términos referentes a los alquileres de los pisos.                                           |
| CAJA       | Lleva el registro de todas las transferencias monetarias entre la agencia, el cliente y el propietario. |

## Descripción de la base de datos

A continuación se describe qué datos contiene cada una de las tablas:

**PISO: Datos referentes a los pisos.**
| **Campo** | **Descripción**                                               | **Tipo**     |
|-----------|---------------------------------------------------------------|--------------|
| N_PISO    | Clave primaria de la tabla. Contiene el código del piso.      | SERIAL(*)    |
| DIR       | Dirección del piso.                                           | VARCHAR(50)  |
| LOC       | Localidad del piso.                                           | VARCHAR(50)  |
| PISCINA   | Cierto si el piso tiene piscina.                              | BOOLEAN      |
| NIF_PROP  | NIF del propietario del piso. Clave externa de la tabla PROP. | VARCHAR(10)  |
| PRECIO    | Precio diario del piso que debe pagar el cliente.             | DECIMAL(6,2) |
| COMISION  | Porcentaje del precio que la agencia retiene como comisión.   | DECIMAL(5,2) | 

(*) En MySQL la palabra SERIAL es un alias para BIGINT UNSIGNED NOT NULL AUTO_INCREMENT.

**PROP: Datos relativos a los propietarios de los pisos a alquilar.**
| **Campo** | **Descripción**                                               | **Tipo**     |
|-----------|---------------------------------------------------------------|--------------|
| N_PISO    | Clave primaria de la tabla. Contiene el código del piso.      | SERIAL(*)    |
| DIR       | Dirección del piso.                                           | VARCHAR(50)  |
| LOC       | Localidad del piso.                                           | VARCHAR(50)  |
| PISCINA   | Cierto si el piso tiene piscina.                              | BOOLEAN      |
| NIF_PROP  | NIF del propietario del piso. Clave externa de la tabla PROP. | VARCHAR(10)  |
| PRECIO    | Precio diario del piso que debe pagar el cliente.             | DECIMAL(6,2) |
| COMISION  | Porcentaje del precio que la agencia retiene como comisión.   | DECIMAL(5,2) | 

**CLI: Datos pertenecientes al cliente que alquila un piso.**
| **Campo** | **Descripción**                                               | **Tipo**     |
|-----------|---------------------------------------------------------------|--------------|
| N_PISO    | Clave primaria de la tabla. Contiene el código del piso.      | SERIAL(*)    |
| DIR       | Dirección del piso.                                           | VARCHAR(50)  |
| LOC       | Localidad del piso.                                           | VARCHAR(50)  |
| PISCINA   | Cierto si el piso tiene piscina.                              | BOOLEAN      |
| NIF_PROP  | NIF del propietario del piso. Clave externa de la tabla PROP. | VARCHAR(10)  |
| PRECIO    | Precio diario del piso que debe pagar el cliente.             | DECIMAL(6,2) |
| COMISION  | Porcentaje del precio que la agencia retiene como comisión.   | DECIMAL(5,2) |  

**PEDIDO: Registra los datos de cada alquiler que hace un cliente.**
| **Campo** | **Descripción**                                               | **Tipo**     |
|-----------|---------------------------------------------------------------|--------------|
| N_PISO    | Clave primaria de la tabla. Contiene el código del piso.      | SERIAL(*)    |
| DIR       | Dirección del piso.                                           | VARCHAR(50)  |
| LOC       | Localidad del piso.                                           | VARCHAR(50)  |
| PISCINA   | Cierto si el piso tiene piscina.                              | BOOLEAN      |
| NIF_PROP  | NIF del propietario del piso. Clave externa de la tabla PROP. | VARCHAR(10)  |
| PRECIO    | Precio diario del piso que debe pagar el cliente.             | DECIMAL(6,2) |
| COMISION  | Porcentaje del precio que la agencia retiene como comisión.   | DECIMAL(5,2) | 
 
**CAJA: Lleva el control de todas las operaciones de entrada y salida de dinero de la agencia.**

| **Campo** | **Descripción**                                               | **Tipo**     |
|-----------|---------------------------------------------------------------|--------------|
| N_PISO    | Clave primaria de la tabla. Contiene el código del piso.      | SERIAL(*)    |
| DIR       | Dirección del piso.                                           | VARCHAR(50)  |
| LOC       | Localidad del piso.                                           | VARCHAR(50)  |
| PISCINA   | Cierto si el piso tiene piscina.                              | BOOLEAN      |
| NIF_PROP  | NIF del propietario del piso. Clave externa de la tabla PROP. | VARCHAR(10)  |
| PRECIO    | Precio diario del piso que debe pagar el cliente.             | DECIMAL(6,2) |
| COMISION  | Porcentaje del precio que la agencia retiene como comisión.   | DECIMAL(5,2) | 

**Códigos de operación:**
| **Campo** | **Descripción**                                               | **Tipo**     |
|-----------|---------------------------------------------------------------|--------------|
| N_PISO    | Clave primaria de la tabla. Contiene el código del piso.      | SERIAL(*)    |
| DIR       | Dirección del piso.                                           | VARCHAR(50)  |
| LOC       | Localidad del piso.                                           | VARCHAR(50)  |
| PISCINA   | Cierto si el piso tiene piscina.                              | BOOLEAN      |
| NIF_PROP  | NIF del propietario del piso. Clave externa de la tabla PROP. | VARCHAR(10)  |
| PRECIO    | Precio diario del piso que debe pagar el cliente.             | DECIMAL(6,2) |
| COMISION  | Porcentaje del precio que la agencia retiene como comisión.   | DECIMAL(5,2) | 

Diagrama E/R (Entidad/Relación)

 
