-- =====================================================
-- DATOS DE PRUEBA - RAO MOTOS
-- Repuestos y accesorios para motocicletas
-- Ejecutar LUEGO de database_schema.sql
-- psql -h mail.tecnoweb.org.bo -U grupo02sa -d db_grupo02sa -f seed_data.sql
-- (usa las credenciales del archivo .env o variables de entorno)
-- =====================================================

BEGIN;

-- =====================================================
-- LIMPIEZA (orden inverso a FK)
-- =====================================================
DELETE FROM pago_cuota;
DELETE FROM credito;
DELETE FROM detalle_venta;
DELETE FROM venta;
DELETE FROM detalle_pedido;
DELETE FROM pedido;
DELETE FROM detalle_compra;
DELETE FROM compra;
DELETE FROM movimiento_inventario;
DELETE FROM inventario;
DELETE FROM producto;
DELETE FROM propietario;
DELETE FROM proveedor;
DELETE FROM cliente;
DELETE FROM usuario;

ALTER SEQUENCE usuario_id_seq RESTART WITH 1;
ALTER SEQUENCE producto_id_seq RESTART WITH 1;
ALTER SEQUENCE inventario_id_seq RESTART WITH 1;
ALTER SEQUENCE movimiento_inventario_id_seq RESTART WITH 1;
ALTER SEQUENCE compra_id_seq RESTART WITH 1;
ALTER SEQUENCE detalle_compra_id_seq RESTART WITH 1;
ALTER SEQUENCE pedido_id_seq RESTART WITH 1;
ALTER SEQUENCE detalle_pedido_id_seq RESTART WITH 1;
ALTER SEQUENCE venta_id_seq RESTART WITH 1;
ALTER SEQUENCE detalle_venta_id_seq RESTART WITH 1;
ALTER SEQUENCE credito_id_seq RESTART WITH 1;
ALTER SEQUENCE pago_cuota_id_seq RESTART WITH 1;

-- =====================================================
-- CU1 - USUARIOS
-- =====================================================
INSERT INTO usuario (id, nombre, email, telefono, direccion, password, rol, activo) VALUES
(1,  'Carlos Diego Marca Peñaranda',  'carlos@raomotos.bo',      '77123401', 'Av. America 123, Cochabamba',  'admin123', 'PROPIETARIO', TRUE),
(2,  'Arnez Fernández Fabio Alejandro', 'fabio@raomotos.bo',   '77123402', 'Calle Punata 456, Cochabamba', 'admin123', 'PROPIETARIO', TRUE),
(3,  'Reymar Loaiza Labarden',     'reymar@raomotos.bo',       '77123403', 'Av. Ayacucho 789, Cochabamba', 'admin123', 'PROPIETARIO', TRUE),
(4,  'Distribuidora Japonesa Ltda.', 'ventas@djaponesa.bo', '44123401', 'Zona Industrial, Santa Cruz',  'prov1234', 'PROVEEDOR', TRUE),
(5,  'Importadora China del Sur',    'pedidos@ichinasur.bo','44123402', 'Av. Brasil 1001, La Paz',      'prov1234', 'PROVEEDOR', TRUE),
(6,  'Juan Perez Mamani',     'juan.perez@email.com',       '72123401', 'Calle Lanza 100, Cochabamba',   'cli1234', 'CLIENTE', TRUE),
(7,  'Maria Flores Quispe',   'maria.flores@email.com',     '72123402', 'Av. Heroinas 200, Cochabamba',  'cli1234', 'CLIENTE', TRUE),
(8,  'Pedro Gutierrez Soliz', 'pedro.gutierrez@email.com',  '72123403', 'Calle Bolivar 300, Cochabamba', 'cli1234', 'CLIENTE', TRUE),
(9,  'Ana Rodriguez Lopez',   'ana.rodriguez@email.com',    '72123404', 'Av. Oquendo 400, Cochabamba',   'cli1234', 'CLIENTE', TRUE),
(10, 'Luis Vargas Rojas',     'luis.vargas@email.com',      '72123405', 'Calle Espana 500, Cochabamba',  'cli1234', 'CLIENTE', TRUE);

INSERT INTO propietario (id, nivel_acceso) VALUES
(1, 'TOTAL'),
(2, 'TOTAL'),
(3, 'TOTAL');

INSERT INTO proveedor (id, razon_social, contacto_principal) VALUES
(4, 'Distribuidora Japonesa Ltda.', 'Tanaka Suzuki'),
(5, 'Importadora China del Sur',    'Li Wei');

INSERT INTO cliente (id, nit_ci, tipo_cliente) VALUES
(6, '12345601', 'REGULAR'),
(7, '12345602', 'FRECUENTE'),
(8, '12345603', 'MAYORISTA'),
(9, '12345604', 'REGULAR'),
(10,'12345605', 'FRECUENTE');

-- =====================================================
-- CU2 - PRODUCTOS (15 repuestos de moto)
-- =====================================================
INSERT INTO producto (id, codigo, nombre, marca, modelo, descripcion, precio_venta_base, activo) VALUES
(1,  'CAD-001', 'Kit Transmision Completo',  'DID',      '520VX3',  'Kit cadena 520 + pinon + corona 150cc',        350.00, TRUE),
(2,  'CAD-002', 'Cadena de Distribucion',    'Tsubaki',  'DID830',  'Cadena distribucion para motos 200cc',         180.00, TRUE),
(3,  'CAD-003', 'Pinon de Acero 15T',        'Sunstar',  '15T-520', 'Pinon delantero 15 dientes cadena 520',         85.00, TRUE),
(4,  'FRE-001', 'Pastillas Freno Delantero', 'EBC',      'FA209',   'Pastillas sinterizadas para freno disco',      120.00, TRUE),
(5,  'FRE-002', 'Disco Freno Trasero',       'Brembo',   'DB203',   'Disco freno trasero 220mm',                    250.00, TRUE),
(6,  'FRE-003', 'Cable de Freno Acero',      'Venhill',  'CB-15',   'Cable freno delantero con funda acero',         65.00, TRUE),
(7,  'BUJ-001', 'Bujia Iridium',             'NGK',      'CR8EIX',  'Bujia iridio para motos 125-250cc',             55.00, TRUE),
(8,  'BUJ-002', 'Bobina de Encendido',       'Denso',    '129700',  'Bobina encendido universal 12V',               180.00, TRUE),
(9,  'BUJ-003', 'CDI Electronico',           'Mitsubishi','CDI-150','Modulo encendido CDI para motos 150cc',        220.00, TRUE),
(10, 'FIL-001', 'Filtro de Aceite',          'Hiflofiltro','HF-204','Filtro aceite para motos 125-250cc',           35.00, TRUE),
(11, 'FIL-002', 'Filtro Aire Deportivo',     'K&N',      'KA-1508', 'Filtro aire alto flujo',                       160.00, TRUE),
(12, 'FIL-003', 'Filtro de Gasolina',        'Bosch',    '045-123', 'Filtro combustible universal',                  25.00, TRUE),
(13, 'ACC-001', 'Espejo Retrovisor Universal','TST',     'MR-01',   'Espejo retrovisor negro universal',             70.00, TRUE),
(14, 'ACC-002', 'Manillar Deportivo',        'Renthal',  'RC-971',  'Manillar aluminio 28mm',                       200.00, TRUE),
(15, 'ACC-003', 'Cubre Carter Aluminio',     'Givi',     'GC-150',  'Cubre carter aluminio pulido',                 310.00, TRUE);

-- =====================================================
-- INVENTARIO
-- =====================================================
INSERT INTO inventario (id, producto_id, stock_actual, tecnica_inventario, tecnica_costo) VALUES
(1,  1,  10, 'PERMANENTE', 'PROMEDIO'),
(2,  2,  15, 'PERMANENTE', 'PROMEDIO'),
(3,  3,  25, 'PERMANENTE', 'PROMEDIO'),
(4,  4,  20, 'PERMANENTE', 'PROMEDIO'),
(5,  5,  12, 'PERMANENTE', 'PROMEDIO'),
(6,  6,  30, 'PERMANENTE', 'PROMEDIO'),
(7,  7,  35, 'PERMANENTE', 'PROMEDIO'),
(8,  8,   8, 'PERMANENTE', 'PROMEDIO'),
(9,  9,  10, 'PERMANENTE', 'PROMEDIO'),
(10, 10, 50, 'PERMANENTE', 'PROMEDIO'),
(11, 11, 12, 'PERMANENTE', 'PROMEDIO'),
(12, 12, 60, 'PERMANENTE', 'PROMEDIO'),
(13, 13, 20, 'PERMANENTE', 'PROMEDIO'),
(14, 14, 10, 'PERMANENTE', 'PROMEDIO'),
(15, 15,  8, 'PERMANENTE', 'PROMEDIO');

INSERT INTO movimiento_inventario (inventario_id, tipo_movimiento, cantidad, motivo, fecha) VALUES
(1,  'INGRESO', 10, 'Compra inicial', '2026-01-10 09:00:00'),
(2,  'INGRESO', 15, 'Compra inicial', '2026-01-10 09:00:00'),
(3,  'INGRESO', 25, 'Compra inicial', '2026-01-10 09:00:00'),
(4,  'INGRESO', 20, 'Compra inicial', '2026-01-10 09:00:00'),
(5,  'INGRESO', 12, 'Compra inicial', '2026-01-10 09:00:00'),
(6,  'INGRESO', 30, 'Compra inicial', '2026-01-10 09:00:00'),
(7,  'INGRESO', 35, 'Compra inicial', '2026-01-10 09:00:00'),
(8,  'INGRESO',  8, 'Compra inicial', '2026-01-10 09:00:00'),
(9,  'INGRESO', 10, 'Compra inicial', '2026-01-10 09:00:00'),
(10, 'INGRESO', 50, 'Compra inicial', '2026-01-10 09:00:00'),
(11, 'INGRESO', 12, 'Compra inicial', '2026-01-10 09:00:00'),
(12, 'INGRESO', 60, 'Compra inicial', '2026-01-10 09:00:00'),
(13, 'INGRESO', 20, 'Compra inicial', '2026-01-10 09:00:00'),
(14, 'INGRESO', 10, 'Compra inicial', '2026-01-10 09:00:00'),
(15, 'INGRESO',  8, 'Compra inicial', '2026-01-10 09:00:00');

-- =====================================================
-- CU3 - COMPRAS
-- =====================================================
INSERT INTO compra (id, proveedor_id, fecha, total, estado) VALUES
(1, 4, '2026-01-10 09:00:00', 12500.00, 'RECIBIDA'),
(2, 5, '2026-03-15 14:00:00',  8400.00, 'RECIBIDA');

INSERT INTO detalle_compra (compra_id, producto_id, cantidad, precio_unitario) VALUES
(1, 1,  10, 250.00),
(1, 2,  15, 130.00),
(1, 3,  25,  60.00),
(1, 4,  20,  85.00),
(1, 5,  12, 175.00),
(1, 6,  30,  45.00),
(1, 7,  35,  38.00),
(2, 8,   8, 130.00),
(2, 9,  10, 160.00),
(2, 10, 50,  22.00),
(2, 11, 12, 115.00),
(2, 12, 60,  15.00),
(2, 13, 20,  48.00),
(2, 14, 10, 145.00),
(2, 15,  8, 225.00);

-- =====================================================
-- CU4 - PEDIDOS
-- =====================================================
INSERT INTO pedido (id, cliente_id, fecha, estado) VALUES
(1, 6, '2026-02-10 08:00:00', 'DESPACHADO'),
(2, 8, '2026-03-18 10:00:00', 'DESPACHADO'),
(3, 7, '2026-05-25 16:00:00', 'SOLICITADO');

INSERT INTO detalle_pedido (pedido_id, producto_id, cantidad) VALUES
(1, 1, 2),
(1, 4, 3),
(1, 7, 4),
(2, 10, 8),
(2, 12, 10),
(3, 3, 3),
(3, 2, 1),
(3, 8, 1);

-- =====================================================
-- CU6 - VENTAS
-- =====================================================
INSERT INTO venta (id, cliente_id, fecha, monto_total, tipo_venta, metodo_pago, estado) VALUES
(1, 6, '2026-02-15 10:30:00', 1490.00, 'CONTADO', 'EFECTIVO', 'COMPLETADA'),
(2, 7, '2026-03-10 15:00:00',  220.00, 'CONTADO', 'QR',       'COMPLETADA'),
(3, 9, '2026-03-20 11:00:00',  280.00, 'CONTADO', 'EFECTIVO', 'COMPLETADA'),
(4, 10,'2026-04-05 09:45:00',  250.00, 'CREDITO', 'TARJETA',  'PENDIENTE'),
(5, 8, '2026-05-12 14:00:00', 4700.00, 'CREDITO', 'EFECTIVO', 'PENDIENTE');

INSERT INTO detalle_venta (venta_id, producto_id, cantidad, precio_unitario) VALUES
-- V1 (contado): 2 kits + 3 pastillas + 4 bujias + 3 espejos = 700+360+220+210 = 1490
(1, 1,  2, 350.00),
(1, 4,  3, 120.00),
(1, 7,  4,  55.00),
(1, 13, 3,  70.00),

-- V2 (contado): 4 bujias = 220
(2, 7,  4, 55.00),

-- V3 (contado): 8 filtros aceite = 280
(3, 10, 8, 35.00),

-- V4 (credito): 1 filtro aire + 1 cable freno + 1 filtro gasolina = 160+65+25 = 250
(4, 11, 1, 160.00),
(4, 6,  1,  65.00),
(4, 12, 1,  25.00),

-- V5 (credito): 5 kits+4 discos+6 manillares+2 CDI+1 cubre = 1750+1000+1200+440+310 = 4700
(5, 1,  5, 350.00),
(5, 5,  4, 250.00),
(5, 14, 6, 200.00),
(5, 9,  2, 220.00),
(5, 15, 1, 310.00);

-- =====================================================
-- CU7 - CREDITOS Y PLAN DE PAGOS
-- =====================================================
INSERT INTO credito (id, venta_id, numero_cuotas, tasa_interes, saldo_pendiente, estado) VALUES
(1, 4, 3,  3.50, 250.00, 'VIGENTE'),
(2, 5, 6, 10.00, 5170.00, 'MOROSO');

-- Credito 1: venta 250, 3 cuotas, 3.5% interes -> (250*1.035)/3 = 86.25
INSERT INTO pago_cuota (credito_id, numero_cuota, monto_cuota, fecha_vencimiento, fecha_pago, mora, estado) VALUES
(1, 1, 86.25, '2026-05-05', '2026-05-02', 0.00, 'PAGADO'),
(1, 2, 86.25, '2026-06-05',  NULL,       0.00, 'PENDIENTE'),
(1, 3, 86.25, '2026-07-05',  NULL,       0.00, 'PENDIENTE');

-- Credito 2: venta 4700, 6 cuotas, 10% interes -> (4700*1.10)/6 = 861.67
INSERT INTO pago_cuota (credito_id, numero_cuota, monto_cuota, fecha_vencimiento, fecha_pago, mora, estado) VALUES
(2, 1, 861.67, '2026-06-12', '2026-06-10', 0.00, 'PAGADO'),
(2, 2, 861.67, '2026-07-12',  NULL,       0.00, 'VENCIDO'),
(2, 3, 861.67, '2026-08-12',  NULL,       0.00, 'PENDIENTE'),
(2, 4, 861.67, '2026-09-12',  NULL,       0.00, 'PENDIENTE'),
(2, 5, 861.67, '2026-10-12',  NULL,       0.00, 'PENDIENTE'),
(2, 6, 861.67, '2026-11-12',  NULL,       0.00, 'PENDIENTE');

COMMIT;
