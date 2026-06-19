# Sistema de Ventas al Crédito — RAO MOTOS

## Servicio vía E-mail (POP3/SMTP)

---

## Propósito del Proyecto

**INGENIERÍA INFORMÁTICA**  
**TECNOLOGÍA WEB — INF-513 SA**  
**GESTIÓN 2025**

Sistema de gestión de ventas al crédito para **"RAO MOTOS"**, implementado mediante comunicación vía correo electrónico. El sistema permite a clientes, proveedores y administradores realizar operaciones CRUD sobre entidades del negocio (Usuarios, Productos, Inventario, Compras, Pedidos, Ventas, Créditos, Pagos) enviando comandos en el **Subject** del correo.

### ¿Cómo funciona?

1. **Cliente envía correo** a `grupo02sa@tecnoweb.org.bo` con un comando en el Subject
2. **Servidor POP3** lee el correo cada 10 segundos (loop automático)
3. **Sistema procesa** el comando: valida sintaxis, ejecuta operación en PostgreSQL, genera respuesta
4. **Servidor SMTP** responde al remitente con el resultado

---

## Credenciales del Grupo

**Grupo02sa:**

| Servicio      | Valor                     |
| ------------- | ------------------------- |
| Servidor      | mail.tecnoweb.org.bo      |
| Puerto SMTP   | 25                        |
| Puerto POP3   | 110                       |
| Base de Datos | db_grupo02sa              |

Las credenciales se cargan desde el archivo `.env` en la raíz del proyecto (ver [Credenciales y .env](#credenciales-y-env)).

---

## Equipo

**INTEGRANTES:**

- **221090436** Carlos Diego Marca Peñaranda — Ingeniería en Sistemas
- **221043721** Arnez Fernández Fabio Alejandro — Ingeniería en Sistemas
- **216027438** Reymar Loaiza Labarden — Ingeniería en Sistemas

---

## Arquitectura del Proyecto

```
ProyectoMailGrupo02sa/
├── .env                                      # Credenciales (no versionado)
├── src/main/java/
│   ├── Database/
│   │   ├── ConfigEmailServer.java           # Configuración SMTP/POP3
│   │   └── EnvLoader.java                   # Carga variables desde .env
│   ├── org/mailgrupo02/
│   │   ├── Main.java                        # Runner principal (menú interactivo)
│   │   ├── CrearTablas.java                 # Utilitario para crear tablas desde Java
│   │   ├── servicioemail/                   # Capa de comunicación POP3/SMTP
│   │   │   ├── ClientePOP.java              # Cliente POP3 (leer correos)
│   │   │   ├── ClienteSMTP.java             # Cliente SMTP (enviar correos)
│   │   │   └── ComandoEmailNuevo.java       # Parser de comandos del Subject
│   │   └── sistema/
│   │       ├── conexion/
│   │       │   └── Conexion.java            # Singleton JDBC (PostgreSQL)
│   │       ├── modelo/                      # Capa de acceso a datos (16 clases)
│   │       │   ├── UsuarioM.java            # usuario
│   │       │   ├── ClienteM.java            # cliente (subtabla de usuario)
│   │       │   ├── ProveedorM.java          # proveedor (subtabla)
│   │       │   ├── PropietarioM.java        # propietario (subtabla)
│   │       │   ├── ProductoM.java           # producto
│   │       │   ├── InventarioM.java         # inventario
│   │       │   ├── MovimientoInventarioM.java   # movimiento_inventario
│   │       │   ├── CompraM.java             # compra
│   │       │   ├── DetalleCompraM.java      # detalle_compra
│   │       │   ├── PedidoM.java             # pedido
│   │       │   ├── PedidoDetalleM.java      # detalle_pedido
│   │       │   ├── VentaM.java              # venta
│   │       │   ├── DetalleVentaM.java       # detalle_venta
│   │       │   ├── CreditoM.java            # credito
│   │       │   ├── PagoCuotaM.java          # pago_cuota
│   │       │   └── EstadisticaM.java        # consultas agregadas/reportes
│   │       └── negocio/                     # Capa de lógica de negocio
│   │           ├── usuarios/
│   │           │   ├── UsuarioN.java        # DTO
│   │           │   ├── UsuarioService.java  # CRUD + lógica
│   │           │   └── UsuarioValidator.java
│   │           ├── productos/
│   │           │   ├── ProductoN.java
│   │           │   ├── ProductoService.java
│   │           │   └── ProductoValidator.java
│   │           ├── ventas/
│   │           │   ├── VentaN.java          # DTO con crédito + cuotas + detalles
│   │           │   ├── VentaService.java    # Creación contado/crédito + plan de pagos
│   │           │   └── VentaValidator.java
│   │           ├── compras/
│   │           │   ├── CompraN.java         # DTO
│   │           │   └── CompraService.java
│   │           ├── pedidos/
│   │           │   ├── PedidoN.java         # DTO
│   │           │   └── PedidoService.java
│   │           ├── inventario/
│   │           │   └── InventarioService.java  # Ingresos/egresos + stock
│   │           └── pagos/
│   │               └── PagoCuotaService.java  # Pago de cuotas + listar créditos
├── database_schema.sql                      # Script DDL completo (PostgreSQL)
├── pom.xml                                  # Maven (PostgreSQL JDBC driver)
└── README.md                                # Este archivo
```

### Flujo de datos:

1. **ClientePOP** lee correos del servidor POP3 (`mail.tecnoweb.org.bo:110`)
2. **ComandoEmailNuevo** parsea el Subject y extrae comando + parámetros
3. **Services** validan y ejecutan operaciones en la BD
4. **Modelos (M)** ejecutan queries SQL con PreparedStatement
5. **ClienteSMTP** envía respuesta al remitente (`mail.tecnoweb.org.bo:25`)

---

## Esquema de Base de Datos

**PostgreSQL en `db_grupo02sa`**

Basado en 8 Casos de Uso (CU):

### CU1 - Gestión de Usuarios (Propietario, Proveedor, Cliente)

```sql
CREATE TABLE usuario (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100)        NOT NULL,
    email       VARCHAR(255) UNIQUE NOT NULL,
    telefono    VARCHAR(20),
    direccion   VARCHAR(255),
    foto_url    VARCHAR(500),
    password    VARCHAR(255)        NOT NULL,
    rol         VARCHAR(20)         NOT NULL
                    CHECK (rol IN ('PROPIETARIO', 'PROVEEDOR', 'CLIENTE')),
    activo      BOOLEAN             DEFAULT TRUE,
    fecha_reg   TIMESTAMP           DEFAULT CURRENT_TIMESTAMP
);
```

Subtables (herencia con PK compartida):

```sql
CREATE TABLE cliente (
    id            INTEGER PRIMARY KEY REFERENCES usuario(id) ON DELETE CASCADE,
    nit_ci        VARCHAR(20) NOT NULL,
    tipo_cliente  VARCHAR(20) NOT NULL
                      CHECK (tipo_cliente IN ('REGULAR', 'FRECUENTE', 'MAYORISTA'))
);

CREATE TABLE proveedor (
    id                  INTEGER PRIMARY KEY REFERENCES usuario(id) ON DELETE CASCADE,
    razon_social        VARCHAR(255) NOT NULL,
    contacto_principal  VARCHAR(100)
);

CREATE TABLE propietario (
    id            INTEGER PRIMARY KEY REFERENCES usuario(id) ON DELETE CASCADE,
    nivel_acceso  VARCHAR(20) NOT NULL DEFAULT 'TOTAL'
);
```

### CU2 - Gestión de Productos

```sql
CREATE TABLE producto (
    id                  SERIAL PRIMARY KEY,
    codigo              VARCHAR(50) UNIQUE NOT NULL,
    nombre              VARCHAR(200)       NOT NULL,
    marca               VARCHAR(100),
    modelo              VARCHAR(100),
    descripcion         TEXT,
    precio_venta_base   DECIMAL(10,2)      NOT NULL CHECK (precio_venta_base > 0),
    foto_url            VARCHAR(500),
    activo              BOOLEAN            DEFAULT TRUE,
    fecha_reg           TIMESTAMP          DEFAULT CURRENT_TIMESTAMP
);
```

### CU3 - Gestión de Compras

```sql
CREATE TABLE compra (
    id           SERIAL PRIMARY KEY,
    proveedor_id INTEGER        NOT NULL REFERENCES proveedor(id) ON DELETE RESTRICT,
    fecha        TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    total        DECIMAL(12,2)  NOT NULL CHECK (total > 0),
    estado       VARCHAR(20)    NOT NULL
                     CHECK (estado IN ('PENDIENTE', 'RECIBIDA', 'ANULADA'))
);

CREATE TABLE detalle_compra (
    id              SERIAL PRIMARY KEY,
    compra_id       INTEGER       NOT NULL REFERENCES compra(id) ON DELETE CASCADE,
    producto_id     INTEGER       NOT NULL REFERENCES producto(id) ON DELETE RESTRICT,
    cantidad        INTEGER       NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(10,2) NOT NULL CHECK (precio_unitario > 0)
);
```

### CU4 - Gestión de Pedidos

```sql
CREATE TABLE pedido (
    id          SERIAL PRIMARY KEY,
    cliente_id  INTEGER     NOT NULL REFERENCES cliente(id) ON DELETE RESTRICT,
    fecha       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    estado      VARCHAR(20) NOT NULL
                    CHECK (estado IN ('SOLICITADO', 'EN_PROCESO', 'DESPACHADO', 'ANULADO'))
);

CREATE TABLE detalle_pedido (
    id          SERIAL PRIMARY KEY,
    pedido_id   INTEGER NOT NULL REFERENCES pedido(id) ON DELETE CASCADE,
    producto_id INTEGER NOT NULL REFERENCES producto(id) ON DELETE RESTRICT,
    cantidad    INTEGER NOT NULL CHECK (cantidad > 0)
);
```

### CU5 - Gestión de Inventario

```sql
CREATE TABLE inventario (
    id                  SERIAL PRIMARY KEY,
    producto_id         INTEGER        NOT NULL REFERENCES producto(id) ON DELETE RESTRICT,
    stock_actual        INTEGER        NOT NULL DEFAULT 0 CHECK (stock_actual >= 0),
    tecnica_inventario  VARCHAR(20)    NOT NULL
                            CHECK (tecnica_inventario IN ('PERMANENTE', 'PERIODICO')),
    tecnica_costo       VARCHAR(20)    NOT NULL
                            CHECK (tecnica_costo IN ('PEPS', 'UEPS', 'PROMEDIO')),
    fecha_actualizacion TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE movimiento_inventario (
    id               SERIAL PRIMARY KEY,
    inventario_id    INTEGER      NOT NULL REFERENCES inventario(id) ON DELETE RESTRICT,
    tipo_movimiento  VARCHAR(10)  NOT NULL CHECK (tipo_movimiento IN ('INGRESO', 'EGRESO')),
    cantidad         INTEGER      NOT NULL CHECK (cantidad > 0),
    motivo           VARCHAR(255),
    fecha            TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);
```

### CU6 - Gestión de Ventas (Contado, Crédito, QR, Tarjeta)

```sql
CREATE TABLE venta (
    id           SERIAL PRIMARY KEY,
    cliente_id   INTEGER        NOT NULL REFERENCES cliente(id) ON DELETE RESTRICT,
    fecha        TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    monto_total  DECIMAL(12,2)  NOT NULL CHECK (monto_total > 0),
    tipo_venta   VARCHAR(10)    NOT NULL CHECK (tipo_venta IN ('CONTADO', 'CREDITO')),
    metodo_pago  VARCHAR(20)    NOT NULL
                     CHECK (metodo_pago IN ('EFECTIVO', 'QR', 'TARJETA')),
    estado       VARCHAR(20)    NOT NULL
                     CHECK (estado IN ('COMPLETADA', 'PENDIENTE', 'ANULADA'))
                     DEFAULT 'PENDIENTE'
);

CREATE TABLE detalle_venta (
    id              SERIAL PRIMARY KEY,
    venta_id        INTEGER       NOT NULL REFERENCES venta(id) ON DELETE CASCADE,
    producto_id     INTEGER       NOT NULL REFERENCES producto(id) ON DELETE RESTRICT,
    cantidad        INTEGER       NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(10,2) NOT NULL CHECK (precio_unitario > 0)
);
```

### CU7 - Gestión de Pagos (Créditos, Moras, Interés)

```sql
CREATE TABLE credito (
    id               SERIAL PRIMARY KEY,
    venta_id         INTEGER        NOT NULL UNIQUE REFERENCES venta(id) ON DELETE RESTRICT,
    numero_cuotas    INTEGER        NOT NULL CHECK (numero_cuotas >= 2),
    tasa_interes     DECIMAL(5,2)   NOT NULL DEFAULT 0.00,
    saldo_pendiente  DECIMAL(12,2)  NOT NULL CHECK (saldo_pendiente >= 0),
    estado           VARCHAR(20)    NOT NULL
                         CHECK (estado IN ('VIGENTE', 'PAGADO', 'MOROSO'))
                         DEFAULT 'VIGENTE'
);

CREATE TABLE pago_cuota (
    id               SERIAL PRIMARY KEY,
    credito_id       INTEGER       NOT NULL REFERENCES credito(id) ON DELETE RESTRICT,
    numero_cuota     INTEGER       NOT NULL,
    monto_cuota      DECIMAL(10,2) NOT NULL CHECK (monto_cuota > 0),
    fecha_vencimiento DATE          NOT NULL,
    fecha_pago       DATE,
    mora             DECIMAL(10,2) DEFAULT 0.00,
    estado           VARCHAR(20)   NOT NULL
                         CHECK (estado IN ('PENDIENTE', 'PAGADO', 'VENCIDO'))
                         DEFAULT 'PENDIENTE'
);
```

### CU8 - Reportes y Estadísticas

No requiere tablas adicionales. Consultas agregadas sobre las tablas existentes:

| Consulta                               | Descripción                                      | Tablas involucradas                          |
| -------------------------------------- | ------------------------------------------------ | -------------------------------------------- |
| `REPORT_VENTAS_POR_MES[YYYY-MM]`       | Ventas del mes con totales y subtotales por tipo | venta, cliente, usuario                      |
| `REPORT_VENTAS_POR_CLIENTE[id]`        | Historial de ventas de un cliente                | venta                                        |
| `REPORT_MORAS_PENDIENTES[*]`           | Cuotas vencidas con mora calculada               | pago_cuota, credito, venta, cliente, usuario |
| `obtenerProductosMasVendidos(mes)`     | Top 10 productos más vendidos                    | detalle_venta, venta, producto               |
| `obtenerCreditosPendientesPorVencer()` | Créditos vigentes próximos a vencer              | credito, venta, cliente, usuario             |

### Resumen de tablas (15 tablas)

| Tabla                 | FK a             | Descripción                            |
| --------------------- | ---------------- | -------------------------------------- |
| usuario               | —                | Usuarios del sistema (3 roles)         |
| cliente               | usuario          | Clientes (REGULAR/FRECUENTE/MAYORISTA) |
| proveedor             | usuario          | Proveedores                            |
| propietario           | usuario          | Propietario (nivel de acceso)          |
| producto              | —                | Catálogo de productos                  |
| inventario            | producto         | Stock y técnica de costo               |
| movimiento_inventario | inventario       | Ingresos/Egresos de stock              |
| compra                | proveedor        | Compras a proveedores                  |
| detalle_compra        | compra, producto | Detalle de compras                     |
| pedido                | cliente          | Pedidos de clientes                    |
| detalle_pedido        | pedido, producto | Detalle de pedidos                     |
| venta                 | cliente          | Ventas (CONTADO/CREDITO)               |
| detalle_venta         | venta, producto  | Detalle de ventas                      |
| credito               | venta            | Créditos asociados a ventas            |
| pago_cuota            | credito          | Cuotas, vencimientos y moras           |

### Relaciones principales:

- **usuario → cliente/proveedor/propietario** (1:1): Herencia por rol
- **producto → inventario** (1:1): Un producto tiene un registro de inventario
- **inventario → movimiento_inventario** (1:N): Múltiples movimientos por producto
- **proveedor → compra** (1:N): Un proveedor puede tener múltiples compras
- **compra → detalle_compra** (1:N): Productos en cada compra
- **cliente → pedido** (1:N): Un cliente puede tener múltiples pedidos
- **pedido → detalle_pedido** (1:N): Productos en cada pedido
- **cliente → venta** (1:N): Un cliente puede tener múltiples ventas
- **venta → detalle_venta** (1:N): Productos en cada venta
- **venta → credito** (1:1): Una venta a crédito tiene un crédito
- **credito → pago_cuota** (1:N): Un crédito tiene N cuotas con vencimientos

---

## Cómo Ejecutar el Proyecto

### Requisitos:

- JDK 8 o superior
- Maven 3.6+
- PostgreSQL (driver incluido en `pom.xml`)
- Acceso a `mail.tecnoweb.org.bo` (red universitaria o VPN)

### PASO 1: Crear la Base de Datos

Opción recomendada — ejecutar `CrearTablas.java` desde Maven:

```bash
mvn clean compile exec:java -Dexec.mainClass="org.mailgrupo02.CrearTablas"
```

`CrearTablas.java` se conecta a PostgreSQL, lee `database_schema.sql`, ignora comentarios y líneas vacías, divide el script por `;` y ejecuta cada sentencia individualmente.

También puedes ejecutarlo desde pgAdmin/DBeaver/psql (usa las credenciales de `.env`).

### PASO 2: Compilar

```bash
mvn clean compile
```

### PASO 3: Ejecutar

```bash
mvn exec:java
```

**Opciones en Main.java (menú interactivo):**

- **1:** `probarConexionBD()` — Verifica conexión a PostgreSQL
- **2:** `probarServicios()` — Prueba CRUD de entidades
- **3:** `iniciarServicioEmail()` — Inicia loop de revisión de correos cada 10s (Ctrl+C para salir)
- **4:** Salir del programa

### Ejecutar directo el servicio de correo:

```bash
mvn exec:java -Dexec.args="email"
```

---

## Comandos Disponibles

### Formato general:

```
COMANDO[parametro1,parametro2,...]
```

Enviar `HELP` como Subject para recibir la lista completa por correo.

### Gestión de Usuarios (CU1)

| Comando                                                                 | Descripción                          |
| ----------------------------------------------------------------------- | ------------------------------------ |
| `LISTARUSUARIOS[*]`                                                     | Lista todos los usuarios             |
| `GETUSUARIO[id]`                                                        | Obtiene un usuario por ID            |
| `CREATEUSUARIO[nombre,email,password,rol,telefono,direccion]`           | Crea usuario + sub-entidad según rol |
| `UPDATEUSUARIO[id,nombre,email,password,rol,telefono,direccion,activo]` | Actualiza                            |
| `DELETEUSUARIO[id]`                                                     | Elimina un usuario                   |

**Roles disponibles:** `PROPIETARIO`, `PROVEEDOR`, `CLIENTE`

### Gestión de Productos (CU2)

| Comando                                                                            | Descripción                |
| ---------------------------------------------------------------------------------- | -------------------------- |
| `LISTARPRODUCTOS[*]`                                                               | Lista todos los productos  |
| `GETPRODUCTO[id]`                                                                  | Obtiene un producto por ID |
| `CREATEPRODUCTO[codigo,nombre,marca,modelo,descripcion,precioVentaBase]`           | Crea producto              |
| `UPDATEPRODUCTO[id,codigo,nombre,marca,modelo,descripcion,precioVentaBase,activo]` | Actualiza                  |
| `DELETEPRODUCTO[id]`                                                               | Elimina un producto        |

### Gestión de Compras (CU3)

| Comando                          | Descripción                     |
| -------------------------------- | ------------------------------- |
| `LISTARCOMPRAS[*]`               | Lista todas las compras         |
| `GETCOMPRA[id]`                  | Obtiene detalle de compra       |
| `CREARCOMPRA[proveedorId,total]` | Crea compra en estado PENDIENTE |
| `ANULARCOMPRA[id]`               | Cambia estado a ANULADA         |

### Gestión de Pedidos (CU4)

| Comando                  | Descripción                      |
| ------------------------ | -------------------------------- |
| `LISTARPEDIDOS[*]`       | Lista todos los pedidos          |
| `GETPEDIDO[id]`          | Obtiene detalle del pedido       |
| `CREARPEDIDO[clienteId]` | Crea pedido en estado SOLICITADO |
| `DESPACHARPEDIDO[id]`    | Cambia estado a DESPACHADO       |
| `ANULARPEDIDO[id]`       | Cambia estado a ANULADO          |

### Gestión de Inventario (CU5)

| Comando                                        | Descripción                     |
| ---------------------------------------------- | ------------------------------- |
| `VERINVENTARIO[*]`                             | Stock de todos los productos    |
| `VERINVENTARIO[productoId]`                    | Stock de un producto específico |
| `REGISTRARINGRESO[productoId,cantidad,motivo]` | Ingreso de stock + movimiento   |
| `REGISTRAREGRESO[productoId,cantidad,motivo]`  | Egreso de stock + movimiento    |

### Gestión de Ventas (CU6)

| Comando                                                                              | Descripción                           |
| ------------------------------------------------------------------------------------ | ------------------------------------- |
| `LISTARVENTAS[*]`                                                                    | Lista todas las ventas                |
| `GETVENTA[id]`                                                                       | Obtiene venta con crédito y cuotas    |
| `CREARVENTA_CONTADO[clienteId,fecha,montoTotal,metodoPago]`                          | Venta al contado (COMPLETADA)         |
| `CREARVENTA_CREDITO[clienteId,fecha,montoTotal,numeroCuotas,tasaInteres,metodoPago]` | Venta crédito + genera plan de cuotas |
| `DELETEVENTA[id]`                                                                    | Elimina una venta                     |

**Métodos de pago:** `EFECTIVO`, `QR`, `TARJETA`

### Gestión de Pagos y Créditos (CU7)

| Comando                                           | Descripción                 |
| ------------------------------------------------- | --------------------------- |
| `LISTARCREDITOS[*]`                               | Lista todos los créditos    |
| `VERCUOTAS[creditoId]`                            | Plan de pagos de un crédito |
| `PAGARCUOTA[creditoId,numeroCuota,montoCuota]`    | Marca cuota como pagada     |
| `REGISTRARPAGO[creditoId,numeroCuota,montoCuota]` | Alias de PAGARCUOTA         |

### Reportes (CU8)

| Comando                                | Descripción                     |
| -------------------------------------- | ------------------------------- |
| `REPORT_VENTAS_POR_MES[YYYY-MM]`       | Ventas del mes con subtotales   |
| `REPORT_VENTAS_POR_CLIENTE[idCliente]` | Historial de ventas del cliente |
| `REPORT_MORAS_PENDIENTES[*]`           | Cuotas vencidas con mora        |

---

## Implementar Manejo de Roles (Próximo Paso)

Actualmente el sistema **no autentica ni autoriza** al remitente. Cualquier persona que envíe un comando puede ejecutar cualquier operación. Para implementar control de acceso por rol se requiere:

### 1. Identificar al remitente al recibir el correo

En `Main.java:procesarCorreo()`:

```
from = extraer(correo, "From: ", 6);
email = extraerEmail(from);  // "cliente@mail.com"
```

### 2. Buscar el email en la tabla `usuario`

```java
// UsuarioService
public String buscarRolPorEmail(String email) {
    String sql = "SELECT rol FROM usuario WHERE email = ? AND activo = true";
    // retorna "PROPIETARIO" | "PROVEEDOR" | "CLIENTE" | null
}
```

### 3. Pasar el rol a `ComandoEmailNuevo`

Cambiar la firma de `evaluarYEjecutar`:

```java
public String evaluarYEjecutar(String email, String subject, String rol)
```

### 4. Definir matriz de permisos

| Comando            | ANONIMO      | CLIENTE | PROVEEDOR | PROPIETARIO |
| ------------------ | ------------ | ------- | --------- | ----------- |
| `CREATEUSUARIO`    | ✓            | ✗       | ✗         | ✓           |
| `LISTARUSUARIOS`   | ✗            | ✗       | ✗         | ✓           |
| `CREATEPRODUCTO`   | ✗            | ✗       | ✗         | ✓           |
| `LISTARPRODUCTOS`  | ✗            | ✓       | ✓         | ✓           |
| `CREARPEDIDO`      | ✗            | ✓       | ✗         | ✓           |
| `CREARCOMPRA`      | ✗            | ✗       | ✓         | ✓           |
| `VERINVENTARIO`    | ✗            | ✓       | ✓         | ✓           |
| `REGISTRARINGRESO` | ✗            | ✗       | ✓         | ✓           |
| `CREARVENTA_*`     | ✗            | ✓       | ✗         | ✓           |
| `PAGARCUOTA`       | ✗            | ✓       | ✗         | ✓           |
| `REPORT_*`         | ✗            | ✗       | ✗         | ✓           |
| `HELP`             | ✓ (filtrado) | ✓       | ✓         | ✓           |

### 5. Filtrar `HELP` según el rol

El método `obtenerComandosDisponibles(rol)` debe devolver solo las secciones de comandos que ese rol puede usar:

- **ANONIMO:** solo instrucciones para crear cuenta
- **CLIENTE:** pedidos, ventas, productos, inventario (lectura), pagos
- **PROVEEDOR:** compras, inventario, productos (lectura)
- **PROPIETARIO:** todos los comandos (CRUD completo + reportes)

### 6. Registrar automáticamente al primer envío

Si el email no existe en `usuario`, el sistema puede:

- Responder con HELP filtrado + instrucciones para crear cuenta
- No permitir ningún comando hasta que el usuario se registre
- Opcional: crear automáticamente un usuario `CLIENTE` con datos básicos

### Resumen de cambios necesarios

| Archivo                                          | Cambio                                                        |
| ------------------------------------------------ | ------------------------------------------------------------- |
| `Main.java:procesarCorreo()`                     | Extraer email, consultar rol, pasarlo a `evaluarYEjecutar`    |
| `ComandoEmailNuevo.evaluarYEjecutar()`           | Aceptar `email` y `rol`, verificar permisos antes de ejecutar |
| `ComandoEmailNuevo.obtenerComandosDisponibles()` | Aceptar `rol`, devolver solo comandos permitidos              |
| `UsuarioService` (nuevo método)                  | `buscarRolPorEmail(email)`                                    |

---

## Credenciales y .env

Las credenciales se almacenan en un archivo `.env` en la raíz del proyecto:

```
SMTP_PORT=25
SMTP_HOST=mail.tecnoweb.org.bo
EMAIL_USER=grupo02sa
EMAIL_PASSWORD=********
EMAIL_ADDRESS=grupo02sa@tecnoweb.org.bo

DB_URL=jdbc:postgresql://mail.tecnoweb.org.bo:5432/db_grupo02sa
DB_USER=grupo02sa
DB_PASSWORD=********

# PagoFacil QR (pagos electrónicos)
PAGOFACIL_TOKEN_SERVICE=
PAGOFACIL_TOKEN_SECRET=
PAGOFACIL_TEST_MODE=true
```

El `.env` **no se versiona** (está en `.gitignore`). Cada integrante debe crear su propio `.env` basado en el template de arriba.

### ¿Cómo se cargan?

- `EnvLoader.java` lee `.env` desde el directorio de trabajo (raíz del proyecto)
- Si no encuentra el archivo, fallback a `System.getenv()`
- `ConfigEmailServer.java` y `Conexion.java` obtienen sus valores desde `EnvLoader.get("KEY")`

## Notas de Seguridad

Este proyecto es **académico/demostrativo**. NO usar en producción sin:

- Autenticación y rate limiting
- Cifrado TLS
- Logging robusto
