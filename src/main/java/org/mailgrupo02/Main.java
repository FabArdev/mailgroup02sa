package org.mailgrupo02;

import org.mailgrupo02.presentacion.email.ClientePOP;
import org.mailgrupo02.presentacion.email.ClienteSMTP;
import org.mailgrupo02.presentacion.email.ComandoEmailNuevo;
import org.mailgrupo02.datos.conexion.Conexion;
import org.mailgrupo02.datos.modelo.*;
import org.mailgrupo02.negocio.usuarios.UsuarioService;
import org.mailgrupo02.negocio.productos.ProductoService;
import org.mailgrupo02.negocio.ventas.VentaService;
import java.sql.Connection;
import java.sql.SQLException;
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
                System.out.println("4. Salir");
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

        public ServicioEmail() {
            try {
                this.cmd = new ComandoEmailNuevo();
            } catch (SQLException e) {
                System.err.println("Error inicializando ComandoEmail: " + e.getMessage());
            }
        }

        public void iniciar() {
            while (true) {
                try {
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
                    Thread.sleep(10000);
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ie) {
                        break;
                    }
                }
            }
        }

        private void procesarCorreo(String correo) {
            try {
                String from = extraer(correo, "From: ", 6);
                String subj = extraer(correo, "Subject: ", 9);
                System.out.println("  De: " + from + " | " + subj);
                String resp = cmd.evaluarYEjecutar(subj);
                smtp.enviarCorreo(extraerEmail(from), "Re: " + subj, resp);
                System.out.println("  Enviado (" + resp.length() + " chars)");
            } catch (Exception e) {
                System.err.println("  Error: " + e.getMessage());
            }
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
