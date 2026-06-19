package org.mailgrupo02.datos.backup;

import org.mailgrupo02.datos.conexion.Conexion;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDateTime;

public class BackupService {

    private static final String BACKUP_FILE = "backup_grupo02.sql";
    private static final String SCHEMA_FILE = "database_schema.sql";

    // Orden FK-safe: primero las tablas padre, luego las hijas
    private static final String[] TABLAS_ORDEN = {
        "usuario", "cliente", "proveedor", "propietario",
        "producto", "inventario", "movimiento_inventario",
        "compra", "detalle_compra",
        "pedido", "detalle_pedido",
        "venta", "detalle_venta",
        "credito", "pago_cuota"
    };

    // Solo las tablas con columna id SERIAL (necesitan reset de secuencia)
    private static final String[] TABLAS_CON_SERIAL = {
        "usuario", "producto", "inventario", "movimiento_inventario",
        "compra", "detalle_compra", "pedido", "detalle_pedido",
        "venta", "detalle_venta", "credito", "pago_cuota"
    };

    // ── Health check ─────────────────────────────────────────────────────────
    public static boolean healthCheck() {
        try (Connection conn = Conexion.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM usuario LIMIT 1")) {
            ps.executeQuery();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ── Guardar backup completo como SQL ─────────────────────────────────────
    public static void backup() {
        try (Connection conn = Conexion.conectar()) {
            StringBuilder sb = new StringBuilder();
            sb.append("-- RAO MOTOS Backup | ").append(LocalDateTime.now()).append("\n\n");

            for (String tabla : TABLAS_ORDEN) {
                sb.append("-- ").append(tabla).append("\n");
                sb.append(exportarTabla(conn, tabla));
                sb.append("\n");
            }

            // Resetear secuencias SERIAL para que los próximos INSERT auto-increment funcionen
            sb.append("-- Resetear secuencias\n");
            for (String tabla : TABLAS_CON_SERIAL) {
                sb.append("SELECT setval('").append(tabla).append("_id_seq', ")
                  .append("COALESCE((SELECT MAX(id) FROM ").append(tabla).append("), 1));\n");
            }

            Files.writeString(Path.of(BACKUP_FILE), sb.toString());
            System.out.println("[Backup] Guardado: " + BACKUP_FILE + " | " + LocalDateTime.now());

        } catch (Exception e) {
            System.err.println("[Backup] Error al guardar backup: " + e.getMessage());
        }
    }

    // ── Recrear tablas desde database_schema.sql ─────────────────────────────
    public static boolean reconstruirTablas() {
        System.out.println("[Backup] Reconstruyendo tablas desde " + SCHEMA_FILE + "...");

        String contenido = leerArchivo(SCHEMA_FILE);
        if (contenido == null) {
            System.err.println("[Backup] No se encontró " + SCHEMA_FILE + " en el directorio actual.");
            return false;
        }

        // Hacer idempotente: CREATE TABLE → CREATE TABLE IF NOT EXISTS
        contenido = contenido.replaceAll("(?i)CREATE TABLE\\s+(?!IF)", "CREATE TABLE IF NOT EXISTS ");
        contenido = contenido.replaceAll("(?i)CREATE INDEX\\s+(?!IF)", "CREATE INDEX IF NOT EXISTS ");

        try (Connection conn = Conexion.conectar()) {
            Statement stmt = conn.createStatement();
            int ok = 0, omitidos = 0;

            for (String linea : contenido.split("\n")) {
                String t = linea.trim();
                if (t.startsWith("--") || t.isEmpty()) continue;
                // acumular sentencias hasta el ;
            }

            // Ejecutar sentencia por sentencia (mismo enfoque que CrearTablas.java)
            StringBuilder sql = new StringBuilder();
            for (String linea : contenido.split("\n")) {
                String t = linea.trim();
                if (!t.startsWith("--") && !t.isEmpty()) {
                    sql.append(linea).append("\n");
                }
            }
            for (String s : sql.toString().split(";")) {
                String t = s.trim();
                if (t.isEmpty()) continue;
                try {
                    stmt.execute(t);
                    ok++;
                } catch (Exception e) {
                    omitidos++; // tabla/índice ya existía
                }
            }
            stmt.close();
            System.out.println("[Backup] Tablas: " + ok + " creadas, " + omitidos + " ya existían.");
            return true;

        } catch (Exception e) {
            System.err.println("[Backup] Error al reconstruir tablas: " + e.getMessage());
            return false;
        }
    }

    // ── Restaurar datos desde el archivo de backup ────────────────────────────
    public static void restaurar() {
        File file = new File(BACKUP_FILE);
        if (!file.exists()) {
            System.out.println("[Backup] No hay backup disponible para restaurar.");
            return;
        }
        System.out.println("[Backup] Restaurando desde " + BACKUP_FILE + "...");

        try (Connection conn = Conexion.conectar()) {
            String contenido = Files.readString(file.toPath());
            Statement stmt = conn.createStatement();
            int ok = 0, err = 0;

            for (String linea : contenido.split("\n")) {
                String t = linea.trim();
                if (t.isEmpty() || t.startsWith("--")) continue;
                try {
                    stmt.execute(t);
                    ok++;
                } catch (Exception e) {
                    err++;
                    // ON CONFLICT DO NOTHING maneja duplicados; errores reales se loguean
                    if (!e.getMessage().contains("duplicate") && !e.getMessage().contains("ya existe")) {
                        System.err.println("[Backup] Fila omitida: " + e.getMessage());
                    }
                }
            }
            stmt.close();
            System.out.println("[Backup] Restauración: " + ok + " OK, " + err + " omitidos.");

        } catch (Exception e) {
            System.err.println("[Backup] Error al restaurar: " + e.getMessage());
        }
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    private static String exportarTabla(Connection conn, String tabla) {
        StringBuilder sb = new StringBuilder();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + tabla + " ORDER BY id")) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            // Lista de columnas (se evalúa una vez)
            StringBuilder colList = new StringBuilder();
            for (int i = 1; i <= cols; i++) {
                if (i > 1) colList.append(", ");
                colList.append(meta.getColumnName(i));
            }
            String colsPart = colList.toString();

            while (rs.next()) {
                sb.append("INSERT INTO ").append(tabla)
                  .append(" (").append(colsPart).append(") VALUES (");
                for (int i = 1; i <= cols; i++) {
                    if (i > 1) sb.append(", ");
                    sb.append(formatVal(rs, meta, i));
                }
                sb.append(") ON CONFLICT (id) DO NOTHING;\n");
            }

        } catch (Exception e) {
            System.err.println("[Backup] Error exportando " + tabla + ": " + e.getMessage());
        }
        return sb.toString();
    }

    private static String formatVal(ResultSet rs, ResultSetMetaData meta, int col) throws SQLException {
        String val = rs.getString(col);
        if (val == null || rs.wasNull()) return "NULL";

        int type = meta.getColumnType(col);

        if (esNumerico(type)) return val;

        if (type == Types.BOOLEAN || type == Types.BIT) {
            return rs.getBoolean(col) ? "true" : "false";
        }

        // Strings, fechas, timestamps, text → entre comillas simples
        return "'" + val.replace("'", "''") + "'";
    }

    private static boolean esNumerico(int t) {
        return t == Types.INTEGER  || t == Types.BIGINT   || t == Types.SMALLINT ||
               t == Types.DECIMAL  || t == Types.NUMERIC  || t == Types.FLOAT    ||
               t == Types.DOUBLE   || t == Types.REAL;
    }

    private static String leerArchivo(String ruta) {
        try {
            return Files.readString(Path.of(ruta));
        } catch (Exception e) {
            return null;
        }
    }
}
