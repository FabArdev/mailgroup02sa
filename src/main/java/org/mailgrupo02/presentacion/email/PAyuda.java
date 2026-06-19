package org.mailgrupo02.presentacion.email;

public class PAyuda {

    public static String generarHtml() {
        StringBuilder contenido = new StringBuilder();
        contenido.append("<h2 class=\"card-title\">Comandos Disponibles &mdash; RAO MOTOS</h2>");
        contenido.append("<p style=\"color:#6b7280;font-size:14px;margin-bottom:10px;\">")
                 .append("Escribe el comando en el <strong>Asunto</strong> del correo a ")
                 .append("<strong>grupo02sa@tecnoweb.org.bo</strong>. El cuerpo del correo puede ir vacío.</p>");
        contenido.append("<div class=\"tip\">")
                 .append("<strong>&#9432; Cómo usar:</strong> Escribe el comando con sus parámetros entre <code>[ ]</code> separados por comas. ")
                 .append("<strong>id</strong> siempre es un número entero del registro en la base de datos. ")
                 .append("Los valores escritos deben respetar exactamente las opciones indicadas (mayúsculas).")
                 .append("</div>");

        contenido.append(seccion("&#128101; Usuarios", new String[][]{
            {"LISTARUSUARIOS[*]",
             "Lista todos los usuarios registrados.",
             "LISTARUSUARIOS[*]"},
            {"GETUSUARIO[id]",
             "<em>id</em> &rarr; número (ej. 1, 2, 3…)",
             "GETUSUARIO[1]"},
            {"CREATEUSUARIO[nombre,email,password,rol,telefono,direccion]",
             "<em>rol</em> &rarr; escrito: <code>PROPIETARIO</code> / <code>PROVEEDOR</code> / <code>CLIENTE</code><br>"
             + "<em>telefono</em> &rarr; texto &nbsp;|&nbsp; <em>direccion</em> &rarr; texto",
             "CREATEUSUARIO[Juan Rao,juan@mail.com,clave123,CLIENTE,70123456,Av. Banzer 100]"},
            {"UPDATEUSUARIO[id,nombre,email,password,rol,telefono,direccion,activo]",
             "<em>id</em> &rarr; número &nbsp;|&nbsp; <em>activo</em> &rarr; <code>true</code> / <code>false</code>",
             "UPDATEUSUARIO[1,Juan Rao,juan@mail.com,clave123,CLIENTE,70123456,Av. Banzer 100,true]"},
            {"DELETEUSUARIO[id]",
             "<em>id</em> &rarr; número",
             "DELETEUSUARIO[1]"},
        }));

        contenido.append(seccion("&#128230; Productos", new String[][]{
            {"LISTARPRODUCTOS[*]",
             "Lista todos los productos del catálogo.",
             "LISTARPRODUCTOS[*]"},
            {"GETPRODUCTO[id]",
             "<em>id</em> &rarr; número",
             "GETPRODUCTO[1]"},
            {"CREATEPRODUCTO[codigo,nombre,marca,modelo,descripcion,precioVentaBase]",
             "<em>codigo</em> &rarr; texto (ej. MOT-001)<br>"
             + "<em>precioVentaBase</em> &rarr; número decimal en Bs. (ej. 8500.00)",
             "CREATEPRODUCTO[MOT-001,Moto Sport 150,Honda,CB150,Moto deportiva,8500.00]"},
            {"UPDATEPRODUCTO[id,codigo,nombre,marca,modelo,descripcion,precio,activo]",
             "<em>id</em> &rarr; número &nbsp;|&nbsp; <em>precio</em> &rarr; decimal Bs.<br>"
             + "<em>activo</em> &rarr; <code>true</code> / <code>false</code>",
             "UPDATEPRODUCTO[1,MOT-001,Moto Sport 150,Honda,CB150,Moto deportiva,8500.00,true]"},
            {"DELETEPRODUCTO[id]",
             "<em>id</em> &rarr; número",
             "DELETEPRODUCTO[1]"},
        }));

        contenido.append(seccion("&#128666; Compras a Proveedores", new String[][]{
            {"LISTARCOMPRAS[*]",
             "Lista todas las compras registradas.",
             "LISTARCOMPRAS[*]"},
            {"GETCOMPRA[id]",
             "<em>id</em> &rarr; número",
             "GETCOMPRA[1]"},
            {"CREARCOMPRA[proveedorId,total]",
             "<em>proveedorId</em> &rarr; número (ID del proveedor en la BD)<br>"
             + "<em>total</em> &rarr; número decimal en Bs.",
             "CREARCOMPRA[2,15000.00]"},
            {"ANULARCOMPRA[id]",
             "<em>id</em> &rarr; número",
             "ANULARCOMPRA[1]"},
        }));

        contenido.append(seccion("&#128221; Pedidos", new String[][]{
            {"LISTARPEDIDOS[*]",
             "Lista todos los pedidos.",
             "LISTARPEDIDOS[*]"},
            {"GETPEDIDO[id]",
             "<em>id</em> &rarr; número",
             "GETPEDIDO[1]"},
            {"CREARPEDIDO[clienteId]",
             "<em>clienteId</em> &rarr; número (ID del cliente en la BD)",
             "CREARPEDIDO[3]"},
            {"DESPACHARPEDIDO[id]",
             "Cambia el estado del pedido a despachado.<br><em>id</em> &rarr; número",
             "DESPACHARPEDIDO[1]"},
            {"ANULARPEDIDO[id]",
             "<em>id</em> &rarr; número",
             "ANULARPEDIDO[1]"},
        }));

        contenido.append(seccion("&#128200; Inventario", new String[][]{
            {"VERINVENTARIO[*]",
             "Muestra el stock actual de todos los productos.",
             "VERINVENTARIO[*]"},
            {"VERINVENTARIO[productoId]",
             "<em>productoId</em> &rarr; número (ID del producto en la BD)",
             "VERINVENTARIO[1]"},
            {"REGISTRARINGRESO[productoId,cantidad,motivo]",
             "<em>productoId</em> &rarr; número &nbsp;|&nbsp; <em>cantidad</em> &rarr; número entero<br>"
             + "<em>motivo</em> &rarr; texto libre",
             "REGISTRARINGRESO[1,10,Compra nueva]"},
            {"REGISTRAREGRESO[productoId,cantidad,motivo]",
             "<em>productoId</em> &rarr; número &nbsp;|&nbsp; <em>cantidad</em> &rarr; número entero<br>"
             + "<em>motivo</em> &rarr; texto libre",
             "REGISTRAREGRESO[1,2,Venta directa]"},
        }));

        contenido.append(seccion("&#128176; Ventas", new String[][]{
            {"LISTARVENTAS[*]",
             "Lista todas las ventas registradas.",
             "LISTARVENTAS[*]"},
            {"GETVENTA[id]",
             "Muestra detalle de la venta y cuotas si es a crédito.<br><em>id</em> &rarr; número",
             "GETVENTA[1]"},
            {"CREARVENTA_CONTADO[clienteId,fecha,montoTotal,metodoPago]",
             "<em>clienteId</em> &rarr; número (ID del cliente)<br>"
             + "<em>fecha</em> &rarr; formato: <code>YYYY-MM-DDThh:mm:ss</code><br>"
             + "<em>montoTotal</em> &rarr; decimal Bs.<br>"
             + "<em>metodoPago</em> &rarr; escrito: <code>EFECTIVO</code> / <code>QR</code> / <code>TARJETA</code>",
             "CREARVENTA_CONTADO[3,2026-06-18T10:00:00,5000.00,EFECTIVO]"},
            {"CREARVENTA_CREDITO[clienteId,fecha,montoTotal,nroCuotas,tasaInteres,metodoPago]",
             "<em>clienteId</em> &rarr; número (ID del cliente)<br>"
             + "<em>fecha</em> &rarr; formato: <code>YYYY-MM-DDThh:mm:ss</code><br>"
             + "<em>nroCuotas</em> &rarr; número entero (ej. 6, 12, 24)<br>"
             + "<em>tasaInteres</em> &rarr; decimal porcentaje (ej. <code>5.0</code>)<br>"
             + "<em>metodoPago</em> &rarr; escrito: <code>EFECTIVO</code> / <code>QR</code> / <code>TARJETA</code>",
             "CREARVENTA_CREDITO[3,2026-06-18T10:00:00,8500.00,12,5.0,QR]"},
            {"DELETEVENTA[id]",
             "<em>id</em> &rarr; número",
             "DELETEVENTA[1]"},
        }));

        contenido.append(seccion("&#128184; Pagos y Créditos", new String[][]{
            {"LISTARCREDITOS[*]",
             "Lista todos los créditos activos con su saldo pendiente.",
             "LISTARCREDITOS[*]"},
            {"VERCUOTAS[creditoId]",
             "Muestra todas las cuotas de un crédito (pagadas y pendientes).<br>"
             + "<em>creditoId</em> &rarr; número (ID del crédito)",
             "VERCUOTAS[1]"},
            {"PAGARCUOTA[creditoId,numeroCuota,montoCuota]",
             "<em>creditoId</em> &rarr; número &nbsp;|&nbsp; <em>numeroCuota</em> &rarr; número entero<br>"
             + "<em>montoCuota</em> &rarr; decimal Bs. (monto exacto de la cuota)<br>"
             + "Genera código QR de PagoFácil en la respuesta.",
             "PAGARCUOTA[1,1,708.33]"},
        }));

        contenido.append(seccion("&#128202; Reportes", new String[][]{
            {"REPORT_VENTAS_POR_MES[YYYY-MM]",
             "Reporte de ventas del mes con totales por tipo (contado/crédito).<br>"
             + "<em>YYYY-MM</em> &rarr; año y mes (ej. <code>2026-06</code>)",
             "REPORT_VENTAS_POR_MES[2026-06]"},
            {"REPORT_VENTAS_POR_CLIENTE[clienteId]",
             "Historial completo de ventas de un cliente.<br>"
             + "<em>clienteId</em> &rarr; número (ID del cliente en la BD)",
             "REPORT_VENTAS_POR_CLIENTE[3]"},
            {"REPORT_MORAS_PENDIENTES[*]",
             "Lista las cuotas vencidas con días de retraso y monto de mora acumulado.",
             "REPORT_MORAS_PENDIENTES[*]"},
        }));

        return construirPlantillaBase(contenido.toString());
    }

    public static String generarError(String mensaje) {
        String contenido = "<h2 class=\"card-title\">Comando no reconocido</h2>" +
                "<div class=\"alert alert-error\">" +
                "<strong>NO SE PUDO PROCESAR EL COMANDO</strong><br>" +
                mensaje.replace("\n", "<br>") +
                "</div>" +
                "<p style=\"margin-top:12px;font-size:14px;color:#6b7280;\">Envíe <code>HELP</code> en el asunto para ver la lista completa de comandos.</p>";
        return construirPlantillaBase(contenido);
    }

    private static String seccion(String titulo, String[][] comandos) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"section\">")
          .append("<h3 class=\"section-title\">").append(titulo).append("</h3>")
          .append("<table>")
          .append("<tr>")
          .append("<th style=\"width:33%\">Asunto del correo</th>")
          .append("<th style=\"width:37%\">Descripci&oacute;n y par&aacute;metros</th>")
          .append("<th style=\"width:30%\">Ejemplo copiable</th>")
          .append("</tr>");
        for (String[] cmd : comandos) {
            sb.append("<tr>")
              .append("<td><code class=\"cmd\">").append(cmd[0]).append("</code></td>")
              .append("<td style=\"font-size:13px;line-height:1.7;color:#374151;\">").append(cmd[1]).append("</td>")
              .append("<td><code class=\"ejemplo\">").append(cmd[2]).append("</code></td>")
              .append("</tr>");
        }
        sb.append("</table></div>");
        return sb.toString();
    }

    private static String construirPlantillaBase(String contenido) {
        return "<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"utf-8\">\n<style>\n" +
               "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;background:#f1f5f9;color:#1e293b;margin:0;padding:0;}\n" +
               ".container{max-width:720px;margin:30px auto;background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 8px 30px rgba(0,0,0,0.10);border:1px solid #e2e8f0;}\n" +
               ".header{background:linear-gradient(135deg,#b91c1c,#7f1d1d);padding:30px 20px;text-align:center;color:#fff;}\n" +
               ".header h1{margin:0;font-size:26px;font-weight:800;letter-spacing:3px;text-transform:uppercase;}\n" +
               ".header p{margin:0;font-size:12px;letter-spacing:0.5px;opacity:0.75;}\n" +
               ".content{padding:26px 22px;}\n" +
               ".card-title{font-size:19px;font-weight:700;margin-top:0;margin-bottom:8px;color:#b91c1c;border-bottom:2px solid #fee2e2;padding-bottom:8px;}\n" +
               ".tip{background:#eff6ff;border:1px solid #bfdbfe;border-radius:8px;padding:12px 16px;font-size:14px;color:#1e40af;margin-bottom:18px;line-height:1.65;}\n" +
               ".section{margin-bottom:18px;}\n" +
               ".section-title{font-size:14px;font-weight:700;color:#fff;background:#4b5563;padding:7px 14px;border-radius:6px;margin:0 0 6px 0;}\n" +
               ".alert{padding:14px;border-radius:10px;margin-bottom:14px;font-size:15px;line-height:1.6;}\n" +
               ".alert-error{background:#fef2f2;border:1px solid #fecaca;color:#991b1b;}\n" +
               "table{width:100%;border-collapse:collapse;font-size:14px;}\n" +
               "th{background:#374151;color:#fff;font-weight:600;text-align:left;padding:9px 12px;font-size:12px;text-transform:uppercase;letter-spacing:0.4px;}\n" +
               "td{padding:9px 12px;border-bottom:1px solid #f1f5f9;vertical-align:top;}\n" +
               "tr:last-child td{border-bottom:none;}\n" +
               "tr:nth-child(even) td{background:#f9fafb;}\n" +
               ".cmd{font-family:'Courier New',monospace;font-size:12px;background:#eff6ff;color:#1d4ed8;padding:3px 6px;border-radius:3px;word-break:break-all;display:inline-block;}\n" +
               ".ejemplo{font-family:'Courier New',monospace;font-size:12px;background:#f0fdf4;color:#166534;padding:3px 6px;border-radius:3px;word-break:break-all;display:inline-block;}\n" +
               "code{font-family:'Courier New',monospace;background:#f1f5f9;color:#374151;padding:1px 5px;border-radius:3px;font-size:13px;}\n" +
               ".footer{background:#f8fafc;padding:16px;text-align:center;font-size:13px;color:#64748b;border-top:1px solid #e2e8f0;}\n" +
               "</style>\n</head>\n<body>\n" +
               "<div class=\"container\">\n" +
               "<div class=\"header\"><h1>RAO MOTOS</h1>" +
               "<div style=\"width:40px;height:2px;background:rgba(255,255,255,0.30);margin:10px auto 8px;border-radius:1px;\"></div>" +
               "<p>Sistema de Ventas por Correo &bull; Grupo 02 SA</p></div>\n" +
               "<div class=\"content\">" + contenido + "</div>\n" +
               "<div class=\"footer\"><strong>Grupo 02 SA &mdash; Tecnolog&iacute;a Web (UAGRM)</strong><br>Correo autom&aacute;tico &mdash; no responder directamente.</div>\n" +
               "</div>\n</body>\n</html>";
    }
}
