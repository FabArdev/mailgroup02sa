package org.mailgrupo02;

import org.mailgrupo02.presentacion.email.ClientePOP;
import org.mailgrupo02.presentacion.email.ClienteSMTP;
import org.mailgrupo02.presentacion.email.ComandoEmailNuevo;
import org.mailgrupo02.presentacion.email.PPagos;
import org.mailgrupo02.datos.conexion.Conexion;
import org.mailgrupo02.datos.modelo.*;
import org.mailgrupo02.datos.backup.BackupService;
import org.mailgrupo02.negocio.pagos.PagoFacilService;
import org.mailgrupo02.negocio.pagos.PagoCuotaService;
import org.mailgrupo02.negocio.usuarios.UsuarioService;
import org.mailgrupo02.negocio.productos.ProductoService;
import org.mailgrupo02.negocio.ventas.VentaService;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("email")) {
            iniciarServicioEmail();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("\n=== SISTEMA DE VENTAS AL CRÉDITO - RAO MOTOS Grupo 02 ===");
                System.out.println("1. Probar conexion BD");
                System.out.println("2. Probar servicios CRUD");
                System.out.println("3. Servicio de correo");
                System.out.println("4. Ejecutar suite de pruebas (TestRunner)");
                System.out.println("5. Salir");
                System.out.print("Opcion: ");
                int opcion = scanner.nextInt();
                switch (opcion) {
                    case 1:
                        probarConexionBD();
                        break;
                    case 2:
                        probarServicios();
                        break;
                    case 3:
                        iniciarServicioEmail();
                        break;
                    case 4:
                        TestRunner.main(new String[]{});
                        break;
                    case 5:
                        System.out.println("Saliendo...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Opcion invalida");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    private static void probarConexionBD() throws SQLException {
        System.out.println("\n=== PROBANDO CONEXION BD ===");
        Connection conn = Conexion.conectar();
        if (conn != null && !conn.isClosed()) {
            System.out.println("OK - db_grupo02sa en mail.tecnoweb.org.bo");
            conn.close();
        } else {
            System.out.println("ERROR conexion");
        }
    }

    private static void probarServicios() throws SQLException {
        System.out.println("\n=== PROBANDO SERVICIOS ===");
        UsuarioService us = new UsuarioService(new UsuarioM());
        System.out.println("Usuarios: " + us.obtenerUsuarios());
        ProductoService ps = new ProductoService(new ProductoM());
        System.out.println("Productos: " + ps.obtenerProductos());
        VentaService ves = new VentaService(new VentaM(), new CreditoM());
        System.out.println("Ventas: " + ves.obtenerVentas());
    }

    private static void iniciarServicioEmail() {
        System.out.println("\n=== SERVICIO EMAIL ===");
        System.out.println("Revisando cada 10s... Ctrl+C para salir");
        new ServicioEmail().iniciar();
    }

    static class ServicioEmail {
        private ClientePOP pop = new ClientePOP();
        private ClienteSMTP smtp = new ClienteSMTP();
        private ComandoEmailNuevo cmd;

        // Backup: bandera de "hay datos nuevos" + contador para backup periódico
        private volatile boolean backupNecesario = false;
        private int cicloCount = 0;
        private static final int CICLOS_BACKUP_PERIODICO = 60; // 60 × 5s = 5 minutos

        public ServicioEmail() {
            try {
                this.cmd = new ComandoEmailNuevo();
            } catch (SQLException e) {
                System.err.println("Error inicializando ComandoEmail: " + e.getMessage());
            }
        }

        public void iniciar() {
            // Backup inicial al arrancar (por si el servicio fue reiniciado)
            System.out.println("[Backup] Generando backup inicial...");
            BackupService.backup();

            while (true) {
                try {
                    cicloCount++;

                    // 1. Health check: si las tablas no existen → reconstruir y restaurar
                    if (!BackupService.healthCheck()) {
                        System.err.println("[ALERTA] Base de datos no disponible. Reconstruyendo...");
                        boolean reconstruido = BackupService.reconstruirTablas();
                        if (reconstruido) {
                            BackupService.restaurar();
                            backupNecesario = false; // los datos vienen del backup, no hace falta volver a guardar
                        } else {
                            System.err.println("[ALERTA] No se pudo reconstruir. Reintentando en el próximo ciclo.");
                            Thread.sleep(5000);
                            continue;
                        }
                    }

                    // 2. Backup si hay datos nuevos o toca ciclo periódico
                    if (backupNecesario || cicloCount % CICLOS_BACKUP_PERIODICO == 0) {
                        BackupService.backup();
                        backupNecesario = false;
                    }

                    reconciliarPagosQR();
                    System.out.println("[" + java.time.LocalDateTime.now() + "] Revisando...");
                    pop.conectar();
                    int total = pop.obtenerTotalDeCorreos();
                    if (total > 0) {
                        System.out.println(total + " correos");
                        for (int i = 1; i <= total; i++) {
                            procesarCorreo(pop.obtenerCorreoYEliminar(i));
                        }
                    } else {
                        System.out.println("  Sin correos nuevos");
                    }
                    pop.desconectar();
                    Thread.sleep(5000);
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        break;
                    }
                }
            }
        }

        private void reconciliarPagosQR() {
            Map<String, String> transacciones = PagoFacilService.cargarTransacciones();
            if (transacciones.isEmpty()) return;

            System.out.println("[Reconciliacion] " + transacciones.size() + " transaccion(es) QR pendiente(s)...");
            List<String> completadas = new ArrayList<>();

            for (Map.Entry<String, String> entry : transacciones.entrySet()) {
                String txId = entry.getKey();
                String[] parts = entry.getValue().split(";");
                if (parts.length < 4) continue;

                String email = parts[0];
                double monto;
                long pfTxId;
                try {
                    monto = Double.parseDouble(parts[1]);
                    pfTxId = Long.parseLong(parts[3]);
                } catch (NumberFormatException e) {
                    System.err.println("[Reconciliacion] Error al parsear transaccion " + txId + ": " + e.getMessage());
                    continue;
                }

                System.out.println("[Reconciliacion] Consultando " + txId + " (PF ID: " + pfTxId + ")...");
                boolean pagado = PagoFacilService.consultarEstado(pfTxId);

                if (pagado) {
                    System.out.println("[Reconciliacion] Pago confirmado: " + txId);
                    if (txId.startsWith("CUO-")) {
                        try {
                            String[] cparts = txId.substring(4).split("-");
                            int creditoId = Integer.parseInt(cparts[0]);
                            int numeroCuota = Integer.parseInt(cparts[1]);
                            String resultado = new PagoCuotaService().confirmarPago(creditoId, numeroCuota);
                            System.out.println("[Reconciliacion] " + resultado);
                            String html = PPagos.generarHtml("PAGARCUOTA",
                                "Pago de Cuota " + numeroCuota + " del Credito #" + creditoId
                                + " confirmado exitosamente. Monto: " + String.format("%.2f", monto) + " Bs.");
                            smtp.enviarCorreo(email, "Confirmacion de Pago - " + txId, html);
                        } catch (Exception e) {
                            System.err.println("[Reconciliacion] Error al confirmar " + txId + ": " + e.getMessage());
                        }
                    }
                    completadas.add(txId);
                } else {
                    System.out.println("[Reconciliacion] " + txId + " sigue pendiente.");
                }
            }

            for (String txId : completadas) {
                PagoFacilService.removerTransaccion(txId);
            }
        }

        private void procesarCorreo(String correo) {
            try {
                String from = extraer(correo, "From: ", 6);
                String subj = extraer(correo, "Subject: ", 9);
                System.out.println("  De: " + from + " | " + subj);
                String emailRemitente = extraerEmail(from);
                String resp = cmd.evaluarYEjecutar(subj, emailRemitente);
                smtp.enviarCorreo(extraerEmail(from), "Re: " + subj, resp);
                System.out.println("  Enviado (" + resp.length() + " chars)");
                // Marcar backup necesario tras cualquier operación de escritura
                if (esComandoEscritura(subj)) {
                    backupNecesario = true;
                }
            } catch (Exception e) {
                System.err.println("  Error: " + e.getMessage());
            }
        }

        private boolean esComandoEscritura(String subject) {
            if (subject == null) return false;
            String s = subject.toUpperCase();
            return s.startsWith("CREATE") || s.startsWith("UPDATE") || s.startsWith("DELETE")
                || s.startsWith("CREAR")  || s.startsWith("ANULAR") || s.startsWith("DESPACHAR")
                || s.startsWith("REGISTRAR") || s.startsWith("PROCESAR") || s.startsWith("CAMBIAR")
                || s.startsWith("PEDIDO[") || s.startsWith("CANCELAR") || s.startsWith("PAGAR");
        }

        private String extraerEmail(String from) {
            int ini = from.lastIndexOf('<');
            int fin = from.lastIndexOf('>');
            if (ini != -1 && fin != -1 && fin > ini) {
                return from.substring(ini + 1, fin);
            }
            return from;
        }

        private String extraer(String txt, String key, int offset) {
            int i = txt.toLowerCase().indexOf(key.toLowerCase());
            if (i != -1) {
                int fin = txt.indexOf("\n", i + offset);
                if (fin != -1) {
                    String val = txt.substring(i + offset, fin).trim();
                    // RFC 2822: cabeceras plegadas continúan con espacio/tab en sig. línea
                    int cont = fin + 1;
                    while (cont < txt.length() && (txt.charAt(cont) == ' ' || txt.charAt(cont) == '\t')) {
                        int nl = txt.indexOf("\n", cont);
                        if (nl == -1) break;
                        val += txt.substring(cont, nl).trim();
                        cont = nl + 1;
                    }
                    return val;
                }
            }
            return "unknown";
        }
    }
}
