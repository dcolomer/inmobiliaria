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

# Diagrama E/R (Entidad/Relación)

 ![Diagrama entidad/relacion](https://github.com/dcolomer/inmobiliaria/blob/main/imagenes/image001.png)

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
| **Campo** |                      **Descripción**                      |   **Tipo**  |
|:---------:|:---------------------------------------------------------:|:-----------:|
| NIF_PROP  | NIF del propietario del piso. Clave primaria de la tabla. | SERIAL(*)   |
| NOMBRE    | Nombre del propietario del piso.                          | VARCHAR(50) |
| APEL      | Apellidos del propietario del piso.                       | VARCHAR(50) |
| DIR       | Dirección del propietario del piso.                       | BOOLEAN     |
| LOC       | Localidad del propietario del piso.                       | VARCHAR(10) |

**CLI: Datos pertenecientes al cliente que alquila un piso.**
| **Campo** |                          **Descripción**                         |   **Tipo**   |
|:---------:|:----------------------------------------------------------------:|:------------:|
| NIF_CLI   | NIF del cliente que alquila un piso. Clave primaria de la tabla. | VARCHAR(10)  |
| NOMBRE    | Nombre del cliente.                                              | VARCHAR(50)  |
| APEL      | Apellidos del cliente.                                           | VARCHAR(100) |

**PEDIDO: Registra los datos de cada alquiler que hace un cliente.**
| **Campo** |                                          **Descripción**                                         |         **Tipo**         |
|:---------:|:------------------------------------------------------------------------------------------------:|:------------------------:|
| N_PEDIDO  | Clave primaria de la tabla. Contiene el código del pedido.                                       | SERIAL                   |
| NIF_CLI   | NIF del cliente. Clave externa de la tabla CLI. Contiene el NIF del cliente que alquila un piso. | VARCHAR(10)              |
| N_PISO    | Código del piso. Clave externa de la tabla PISO. Contiene el código del piso alquilado.          | BIGINT UNSIGNED NOT NULL |
| LLEGADA   | Fecha de llegada del cliente (disponible desde la 10 de la mañana).                              | DATE                     |
| PARTIDA   | Fecha de partida del cliente (se debe abandonar el piso antes de las 10 de la mañana).           | DATE                     |
| PAGADO    | Indica si el cliente ha pagado todo el pedido.                                                   | BOOLEAN                  |
| CANCELADO | Indica si el cliente ha anulado el pedido.                                                       | BOOLEAN                  |
 
**CAJA: Lleva el control de todas las operaciones de entrada y salida de dinero de la agencia.**
| **Campo** |                                              **Descripción**                                             |        **Tipo**       |
|:---------:|:--------------------------------------------------------------------------------------------------------:|:---------------------:|
| N_FACTURA | Clave primaria de la tabla. Contiene el código de la factura.                                            | SERIAL                |
| OPERACION | Codifica el tipo de operación realizada. Los códigos pueden ser: A, B, C, D y E (ver detalle más abajo). | CHAR(1)               |
| N_PEDIDO  | Código del pedido. Clave externa de la tabla PEDIDO.                                                     | BIGINT UNSIGNED  NULL |
| IMPORTE   | Dinero implicado en la operación.                                                                        | DECIMAL(10,2)         |
| PAGADO    | Indica si el pedido ha sido pagado al propietario del piso.                                              | BOOLEAN               |
| DIA       | Día en que se hace efectiva la operación (no necesariamente el de hoy).                                  | DATE                  |

**Códigos de operación:**
| **Campo** |                                              **Descripción**                                             |
|:---------:|:--------------------------------------------------------------------------------------------------------:|
| A         | Pago de un nuevo pedido del cliente a la agencia (el 50% del total)                                      |
| B         | Pago del resto de un pedido del cliente a la agencia (el 50% restante)                                   |
| C         | El cliente ha anulado el pedido. Sólo se puede anular un pedido si no está totalmente pagado.            |
| D         | La agencia paga al propietario del piso el importe del alquiler menos el porcentaje de la comisión que se gana la agencia. No se puede pagar al propietario hasta que el cliente haya pagado la totalidad del pedido (operación tipo B). El importe para este tipo de pedidos se registrará en negativo.              |
| E         | Los pedidos con este código no reflejan ningún pago, pues simulan el balance final del día en contabilidad. Una vez que se registra un pedido con este código de operación no se permite ninguna otra operación de caja para esa fecha.                                       |

