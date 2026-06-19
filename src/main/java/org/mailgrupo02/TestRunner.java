package org.mailgrupo02;

import org.mailgrupo02.presentacion.email.ClienteSMTP;
import org.mailgrupo02.presentacion.email.ComandoEmailNuevo;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.regex.*;

/**
 * Suite de pruebas que envía correos reales al email que el usuario ingresa.
 *
 * Ejecutar:
 *   mvn exec:java -Dexec.mainClass="org.mailgrupo02.TestRunner"
 * O desde el menú interactivo, opción 4.
 *
 * El script procesa cada comando a través del pipeline completo y envía
 * la respuesta HTML al correo indicado. Requiere estar en la red de la
 * universidad (o VPN) para que el SMTP de tecnoweb permita el relay.
 */
public class TestRunner {

    private static String emailDestino;
    private static final ClienteSMTP smtp = new ClienteSMTP();
    private static ComandoEmailNuevo cmd;

    private static int enviados = 0;
    private static int erroresEnvio = 0;
    private static int totalPruebas = 0;

    // IDs extraídos para encadenar comandos
    private static int clienteId      = -1;
    private static int proveedorId    = -1;
    private static int productoId     = -1;
    private static int compraId       = -1;
    private static int pedidoId       = -1;
    private static int ventaContadoId = -1;
    private static int ventaCreditoId = -1;
    private static int creditoId      = -1;

    // ─────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        linea('═', 58);
        System.out.println("  TEST RUNNER — RAO MOTOS  |  Pruebas por correo real");
        linea('═', 58);

        // ── Pedir correo ────────────────────────────────────────────────────
        Scanner sc = new Scanner(System.in);
        System.out.print("\n  Ingresa tu correo electrónico: ");
        emailDestino = sc.nextLine().trim();
        if (emailDestino.isEmpty()) {
            System.out.println("  Correo vacío. Saliendo.");
            return;
        }

        // ── Conectar a la BD ────────────────────────────────────────────────
        System.out.println("\n  Conectando con la base de datos...");
        try {
            cmd = new ComandoEmailNuevo();
            System.out.println("  Conexión OK.\n");
        } catch (Exception e) {
            System.out.println("  [ERROR] No se pudo conectar: " + e.getMessage());
            return;
        }

        String ahora = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        String mes = ahora.substring(0, 7);

        System.out.println("  Destino : " + emailDestino);
        System.out.println("  Cada correo tarda ~2 seg entre envíos.\n");
        linea('─', 58);

        // ════════════════════════════════════════════════════════════════════
        // BLOQUE 1 — Ayuda y listados básicos
        // ════════════════════════════════════════════════════════════════════
        grupo("AYUDA Y LISTADOS");
        enviar("HELP");
        enviar("LISTARUSUARIOS[*]");
        enviar("LISTARPRODUCTOS[*]");
        enviar("VERINVENTARIO[*]");
        enviar("LISTARVENTAS[*]");
        enviar("LISTARCREDITOS[*]");
        enviar("LISTARPEDIDOS[*]");
        enviar("LISTARCOMPRAS[*]");
        enviar("REPORT_MORAS_PENDIENTES[*]");

        // ════════════════════════════════════════════════════════════════════
        // BLOQUE 2 — Crear usuario CLIENTE
        // ════════════════════════════════════════════════════════════════════
        grupo("CREAR ENTIDADES DE PRUEBA");
        String rCliente = enviar(
            "CREATEUSUARIO[Test Cliente,testcli@runner.com,pass123,CLIENTE,79001001,Av. Prueba 100]");
        clienteId = extractId(rCliente);
        note("clienteId extraído = " + clienteId);

        String rProv = enviar(
            "CREATEUSUARIO[Test Proveedor,testprov@runner.com,pass456,PROVEEDOR,79001002,Av. Prueba 200]");
        proveedorId = extractId(rProv);
        note("proveedorId extraído = " + proveedorId);

        String rProd = enviar(
            "CREATEPRODUCTO[TST-001,Moto Test Runner,HondaTest,CB150T,Producto de prueba,4999.00]");
        productoId = extractId(rProd);
        note("productoId extraído = " + productoId);

        // ════════════════════════════════════════════════════════════════════
        // BLOQUE 3 — Leer entidades
        // ════════════════════════════════════════════════════════════════════
        grupo("LECTURA (GET)");
        if (clienteId  > 0) enviar("GETUSUARIO["  + clienteId  + "]");
        if (productoId > 0) enviar("GETPRODUCTO[" + productoId + "]");

        // ════════════════════════════════════════════════════════════════════
        // BLOQUE 4 — Inventario
        // ════════════════════════════════════════════════════════════════════
        grupo("INVENTARIO");
        if (productoId > 0) {
            enviar("REGISTRARINGRESO[" + productoId + ",20,Ingreso de prueba runner]");
            enviar("VERINVENTARIO["    + productoId + "]");
            enviar("REGISTRAREGRESO["  + productoId + ",4,Egreso de prueba runner]");
        } else {
            skip("INVENTARIO — productoId no disponible");
        }

        // ════════════════════════════════════════════════════════════════════
        // BLOQUE 5 — Compras
        // ════════════════════════════════════════════════════════════════════
        grupo("COMPRAS");
        if (proveedorId > 0) {
            String rCompra = enviar("CREARCOMPRA[" + proveedorId + ",12000.00]");
            compraId = extractId(rCompra);
            note("compraId extraído = " + compraId);
            if (compraId > 0) enviar("GETCOMPRA[" + compraId + "]");

            // Segunda compra para probar ANULARCOMPRA
            String rCompra2 = enviar("CREARCOMPRA[" + proveedorId + ",500.00]");
            int compra2Id = extractId(rCompra2);
            note("compra2Id extraído = " + compra2Id);
            if (compra2Id > 0) enviar("ANULARCOMPRA[" + compra2Id + "]");

            enviar("LISTARCOMPRAS[*]");
        } else {
            skip("COMPRAS — proveedorId no disponible");
        }

        // ════════════════════════════════════════════════════════════════════
        // BLOQUE 6 — Pedidos
        // ════════════════════════════════════════════════════════════════════
        grupo("PEDIDOS");
        if (clienteId > 0) {
            String rPed = enviar("CREARPEDIDO[" + clienteId + "]");
            pedidoId = extractId(rPed);
            note("pedidoId extraído = " + pedidoId);
            if (pedidoId > 0) {
                enviar("GETPEDIDO["       + pedidoId + "]");
                enviar("DESPACHARPEDIDO[" + pedidoId + "]");
            }

            // Segundo pedido para probar ANULARPEDIDO
            String rPed2 = enviar("CREARPEDIDO[" + clienteId + "]");
            int pedido2Id = extractId(rPed2);
            note("pedido2Id extraído = " + pedido2Id);
            if (pedido2Id > 0) enviar("ANULARPEDIDO[" + pedido2Id + "]");

            enviar("LISTARPEDIDOS[*]");
        } else {
            skip("PEDIDOS — clienteId no disponible");
        }

        // ════════════════════════════════════════════════════════════════════
        // BLOQUE 7 — Ventas
        // ════════════════════════════════════════════════════════════════════
        grupo("VENTAS");
        if (clienteId > 0) {
            String rVC = enviar(
                "CREARVENTA_CONTADO[" + clienteId + "," + ahora + ",800.00,EFECTIVO]");
            ventaContadoId = extractId(rVC);
            note("ventaContadoId = " + ventaContadoId);

            if (ventaContadoId > 0) enviar("GETVENTA[" + ventaContadoId + "]");

            String rVCred = enviar(
                "CREARVENTA_CREDITO[" + clienteId + "," + ahora + ",3600.00,6,5.0,QR]");
            ventaCreditoId = extractId(rVCred);
            creditoId      = extractSecondId(rVCred);
            note("ventaCreditoId = " + ventaCreditoId + "  |  creditoId = " + creditoId);

            enviar("LISTARVENTAS[*]");
        } else {
            skip("VENTAS — clienteId no disponible");
        }

        // ════════════════════════════════════════════════════════════════════
        // BLOQUE 8 — Pagos y créditos (header azul)
        // ════════════════════════════════════════════════════════════════════
        grupo("PAGOS Y CRÉDITOS");
        enviar("LISTARCREDITOS[*]");
        if (creditoId > 0) {
            enviar("VERCUOTAS[" + creditoId + "]");
            enviar("PAGARCUOTA[" + creditoId + ",1]");
        } else {
            skip("PAGARCUOTA — creditoId no disponible");
        }

        // ════════════════════════════════════════════════════════════════════
        // BLOQUE 9 — Reportes
        // ════════════════════════════════════════════════════════════════════
        grupo("REPORTES");
        enviar("REPORT_VENTAS_POR_MES[" + mes + "]");
        if (clienteId > 0)
            enviar("REPORT_VENTAS_POR_CLIENTE[" + clienteId + "]");
        enviar("REPORT_MORAS_PENDIENTES[*]");

        // ════════════════════════════════════════════════════════════════════
        // BLOQUE 10 — Actualizar
        // ════════════════════════════════════════════════════════════════════
        grupo("ACTUALIZACIÓN");
        if (clienteId > 0) {
            enviar("UPDATEUSUARIO[" + clienteId
                + ",Test Cliente Upd,testcli@runner.com,pass123,CLIENTE,79001001,Av. Actualizada 100,true]");
            enviar("UPDATECLIENTE[" + clienteId + ",9876543210,FRECUENTE]");
        }
        if (productoId > 0)
            enviar("UPDATEPRODUCTO[" + productoId
                + ",TST-001,Moto Test v2,HondaTest,CB150T,Producto actualizado,5299.00,true]");

        // ════════════════════════════════════════════════════════════════════
        // BLOQUE 11 — Comando inválido (ver cómo se ve el error en el mail)
        // ════════════════════════════════════════════════════════════════════
        grupo("MANEJO DE ERRORES");
        enviar("COMANDOINVALIDO");
        enviar("CREATEUSUARIO[x,x@x.com,p,ROLINVALIDO,0,dir]");
        enviar("GETUSUARIO[]");

        // ════════════════════════════════════════════════════════════════════
        // BLOQUE 12 — Limpieza
        // ════════════════════════════════════════════════════════════════════
        grupo("LIMPIEZA DE DATOS DE PRUEBA");
        if (ventaContadoId > 0) enviar("DELETEVENTA["   + ventaContadoId + "]");
        if (ventaCreditoId > 0) enviar("DELETEVENTA["   + ventaCreditoId + "]");
        if (productoId     > 0) enviar("DELETEPRODUCTO[" + productoId    + "]");
        if (clienteId      > 0) enviar("DELETEUSUARIO[" + clienteId      + "]");
        if (proveedorId    > 0) enviar("DELETEUSUARIO[" + proveedorId    + "]");

        // ════════════════════════════════════════════════════════════════════
        // RESUMEN
        // ════════════════════════════════════════════════════════════════════
        System.out.println();
        linea('═', 58);
        System.out.println("  RESUMEN");
        linea('─', 58);
        System.out.printf("  Total enviados  : %d / %d%n", enviados, totalPruebas);
        System.out.printf("  Errores de envío: %d%n", erroresEnvio);
        System.out.println("  Revisa tu bandeja de entrada: " + emailDestino);
        linea('═', 58);
        System.out.println();
    }

    // ─── Envío ──────────────────────────────────────────────────────────────

    /**
     * Procesa el asunto, envía el HTML al email del usuario y retorna el HTML
     * (para extraer IDs en comandos de creación).
     */
    static String enviar(String asunto) {
        totalPruebas++;
        String cmd_nombre = asunto.split("\\[")[0];
        System.out.printf("  [%02d] %-35s ", totalPruebas, cmd_nombre);

        String html = cmd.evaluarYEjecutar(asunto, "PROPIETARIO");
        try {
            smtp.enviarCorreo(emailDestino, "Re: " + asunto, html);
            enviados++;
            System.out.println("✓ enviado");
        } catch (IOException e) {
            erroresEnvio++;
            System.out.println("✗ " + e.getMessage());
        }

        pausa(2000);
        return html;
    }

    static void skip(String msg) {
        System.out.println("  [--] SKIP: " + msg);
    }

    static void grupo(String titulo) {
        System.out.println("\n  ── " + titulo + " " + "─".repeat(Math.max(0, 50 - titulo.length())));
    }

    static void note(String texto) {
        System.out.println("       → " + texto);
    }

    static void linea(char c, int n) {
        System.out.println("  " + String.valueOf(c).repeat(n));
    }

    static void pausa(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    // ─── Extracción de IDs ──────────────────────────────────────────────────

    /** Busca "(ID: N)" en el texto plano del HTML. */
    static int extractId(String html) {
        String txt = strip(html);
        Matcher m = Pattern.compile("\\(ID:\\s*(\\d+)\\)").matcher(txt);
        if (m.find()) return Integer.parseInt(m.group(1));
        // fallback al primer número razonable
        m = Pattern.compile("\\b([1-9]\\d{0,4})\\b").matcher(txt);
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }

    /** Para "Venta a crédito (ID: N), Crédito ID: M" extrae M. */
    static int extractSecondId(String html) {
        String txt = strip(html);
        Matcher m = Pattern.compile("(?i)cr[eé]dito\\s+ID[:\\s]+(\\d+)").matcher(txt);
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }

    static String strip(String html) {
        return html.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
    }
}
