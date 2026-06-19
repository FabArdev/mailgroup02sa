package org.mailgrupo02.presentacion.email;

public class PAyuda {

    public static String generarHtml() {
        StringBuilder c = new StringBuilder();

        c.append(PlantillaBase.titulo("RAO MOTOS &mdash; Sistema de Ventas por Correo"));
        c.append("<p style=\"color:#4a5568;font-size:15px;margin-bottom:10px;\">")
         .append("Escribe el comando en el <strong>Asunto</strong> del correo a ")
         .append("<strong>grupo02sa@tecnoweb.org.bo</strong>. El cuerpo puede ir vac&iacute;o.</p>");
        c.append("<div style=\"background:#eff6ff;border:2px solid #bfdbfe;border-radius:10px;padding:14px 18px;" +
                 "font-size:14px;color:#1d4ed8;margin-bottom:20px;line-height:1.65;\">")
         .append("<strong>&#9432; C&oacute;mo usar:</strong> Escribe el comando con sus par&aacute;metros entre ")
         .append("<code style=\"font-family:'Courier New',monospace;background:#dbeafe;color:#1d4ed8;padding:1px 5px;border-radius:3px;\">[ ]</code>")
         .append(" separados por comas. <strong>id</strong> siempre es un n&uacute;mero entero. ")
         .append("Los valores de tipo enumerado deben ir en MAY&Uacute;SCULAS.")
         .append("</div>");

        // ── PRIMEROS PASOS ───────────────────────────────────────────────────
        c.append(seccion("&#127381; Primeros pasos &mdash; &iexcl;Empieza aqu&iacute;!", new String[][]{
            {"HELP",
             "Muestra esta p&aacute;gina de ayuda.",
             "HELP"},
            {"CREATEUSUARIO[nombre,email,password,telefono,direccion]",
             "Reg&iacute;strate como cliente. Usa el <strong>mismo email</strong> desde el que env&iacute;as el correo.<br>"
             + "<em>telefono</em> &rarr; texto &nbsp;|&nbsp; <em>direccion</em> &rarr; texto",
             "CREATEUSUARIO[Juan Perez,juan@gmail.com,clave123,70000000,Av. Principal 123]"},
            {"WHOAMI",
             "Muestra tu perfil: ID, nombre, email, rol y datos de contacto.",
             "WHOAMI"},
            {"LISTARPRODUCTOS[*]",
             "Cat&aacute;logo completo de productos disponibles con precios.",
             "LISTARPRODUCTOS[*]"},
            {"GETPRODUCTO[id]",
             "Detalle de un producto espec&iacute;fico.",
             "GETPRODUCTO[1]"},
        }));

        // ── CLIENTE ──────────────────────────────────────────────────────────
        c.append(seccion("&#128101; Comandos para clientes (rol: CLIENTE)", new String[][]{
            {"PEDIDO[productoId:cantidad,...]",
             "Crea un pedido con uno o m&aacute;s productos.<br>"
             + "Formato: <code>productoId:cantidad</code> por cada &iacute;tem, separados con coma.<br>"
             + "El pedido queda en estado <strong>SOLICITADO</strong> hasta que el propietario lo procese.",
             "PEDIDO[1:2,3:1]"},
            {"MISPEDIDOS",
             "Lista todos tus pedidos (historial completo).",
             "MISPEDIDOS"},
            {"MIPEDIDO[pedidoId]",
             "Muestra el detalle de un pedido tuyo con sus productos.<br>"
             + "<em>pedidoId</em> &rarr; n&uacute;mero del pedido.",
             "MIPEDIDO[5]"},
            {"CANCELARPEDIDO[pedidoId]",
             "Cancela un pedido propio en estado <strong>SOLICITADO</strong>.<br>"
             + "No se puede cancelar un pedido ya procesado o despachado.",
             "CANCELARPEDIDO[5]"},
            {"MISCREDITOS",
             "Lista todos tus cr&eacute;ditos activos con saldo pendiente.",
             "MISCREDITOS"},
            {"MISCUOTAS[creditoId]",
             "Muestra las cuotas de uno de tus cr&eacute;ditos (pagadas y pendientes).<br>"
             + "<em>creditoId</em> &rarr; n&uacute;mero del cr&eacute;dito.",
             "MISCUOTAS[2]"},
            {"PAGARCUOTA[creditoId,numeroCuota]",
             "Genera el c&oacute;digo QR de PagoF&aacute;cil para pagar una cuota completa.<br>"
             + "El monto se toma autom&aacute;ticamente del cr&eacute;dito.",
             "PAGARCUOTA[2,1]"},
        }));

        // ── PROVEEDOR ────────────────────────────────────────────────────────
        c.append(seccion("&#128666; Comandos para proveedores (rol: PROVEEDOR)", new String[][]{
            {"LISTARCOMPRAS[*]",
             "Lista todas las &oacute;rdenes de compra del sistema.",
             "LISTARCOMPRAS[*]"},
            {"GETCOMPRA[id]",
             "<em>id</em> &rarr; n&uacute;mero de la compra.",
             "GETCOMPRA[1]"},
        }));

        // ── PROPIETARIO ─ Usuarios ───────────────────────────────────────────
        c.append(seccion("&#128273; Gesti&oacute;n de Usuarios (solo PROPIETARIO)", new String[][]{
            {"LISTARUSUARIOS[*]",
             "Lista todos los usuarios del sistema.",
             "LISTARUSUARIOS[*]"},
            {"GETUSUARIO[id]",
             "<em>id</em> &rarr; n&uacute;mero del usuario.",
             "GETUSUARIO[1]"},
            {"CREATEUSUARIO[nombre,email,password,telefono,direccion]",
             "Crea un usuario (siempre como CLIENTE). Usar <code>CAMBIARROL</code> para cambiar su rol despu&eacute;s.",
             "CREATEUSUARIO[Juan Rao,juan@mail.com,clave123,70123456,Av. Banzer 100]"},
            {"UPDATEUSUARIO[id,nombre,email,password,rol,telefono,direccion,activo]",
             "<em>activo</em> &rarr; <code>true</code> / <code>false</code>",
             "UPDATEUSUARIO[1,Juan Rao,juan@mail.com,clave123,CLIENTE,70123456,Av. Banzer 100,true]"},
            {"UPDATECLIENTE[id,nitCi,tipoCliente]",
             "<em>tipoCliente</em> &rarr; <code>REGULAR</code> / <code>FRECUENTE</code> / <code>MAYORISTA</code>",
             "UPDATECLIENTE[3,1234567,FRECUENTE]"},
            {"DELETEUSUARIO[id]",
             "<em>id</em> &rarr; n&uacute;mero del usuario.",
             "DELETEUSUARIO[1]"},
            {"CAMBIARROL[userId,nuevoRol]",
             "Cambia el rol de un usuario y migra sus datos de subtabla.<br>"
             + "<em>nuevoRol</em> &rarr; <code>PROPIETARIO</code> / <code>CLIENTE</code> / <code>PROVEEDOR</code>",
             "CAMBIARROL[5,PROVEEDOR]"},
        }));

        // ── PROPIETARIO ─ Productos ──────────────────────────────────────────
        c.append(seccion("&#128230; Gesti&oacute;n de Productos (solo PROPIETARIO)", new String[][]{
            {"CREATEPRODUCTO[codigo,nombre,marca,modelo,descripcion,precioVentaBase]",
             "<em>codigo</em> &rarr; texto (ej. MOT-001)<br>"
             + "<em>precioVentaBase</em> &rarr; decimal en Bs. (ej. 8500.00)",
             "CREATEPRODUCTO[MOT-001,Moto Sport 150,Honda,CB150,Moto deportiva,8500.00]"},
            {"UPDATEPRODUCTO[id,codigo,nombre,marca,modelo,descripcion,precio,activo]",
             "<em>activo</em> &rarr; <code>true</code> / <code>false</code>",
             "UPDATEPRODUCTO[1,MOT-001,Moto Sport 150,Honda,CB150,Descripcion,8500.00,true]"},
            {"DELETEPRODUCTO[id]",
             "<em>id</em> &rarr; n&uacute;mero.",
             "DELETEPRODUCTO[1]"},
        }));

        // ── PROPIETARIO ─ Pedidos (Admin) ────────────────────────────────────
        c.append(seccion("&#128221; Gesti&oacute;n de Pedidos (solo PROPIETARIO)", new String[][]{
            {"LISTARPEDIDOS[*]",
             "Lista todos los pedidos del sistema.",
             "LISTARPEDIDOS[*]"},
            {"GETPEDIDO[id]",
             "Ver detalle de un pedido por ID.",
             "GETPEDIDO[1]"},
            {"PROCESARPEDIDO[id,CONTADO,metodoPago]",
             "Convierte un pedido en venta al contado.<br>"
             + "<em>metodoPago</em> &rarr; <code>EFECTIVO</code> / <code>QR</code> / <code>TARJETA</code>",
             "PROCESARPEDIDO[3,CONTADO,EFECTIVO]"},
            {"PROCESARPEDIDO[id,CREDITO,cuotas,tasaInteres,metodoPago]",
             "Convierte un pedido en venta a cr&eacute;dito.<br>"
             + "<em>cuotas</em> &rarr; entero (ej. 12) &nbsp;|&nbsp; <em>tasaInteres</em> &rarr; % (ej. 5.0)",
             "PROCESARPEDIDO[3,CREDITO,12,5.0,QR]"},
            {"DESPACHARPEDIDO[id]",
             "Marca el pedido como despachado (entregado).",
             "DESPACHARPEDIDO[1]"},
            {"ANULARPEDIDO[id]",
             "Anula un pedido.",
             "ANULARPEDIDO[1]"},
        }));

        // ── PROPIETARIO ─ Compras ────────────────────────────────────────────
        c.append(seccion("&#128666; Gesti&oacute;n de Compras (solo PROPIETARIO)", new String[][]{
            {"CREARCOMPRA[proveedorId,total]",
             "<em>proveedorId</em> &rarr; n&uacute;mero &nbsp;|&nbsp; <em>total</em> &rarr; decimal Bs.",
             "CREARCOMPRA[2,15000.00]"},
            {"ANULARCOMPRA[id]",
             "Anula una compra.",
             "ANULARCOMPRA[1]"},
        }));

        // ── PROPIETARIO ─ Inventario ─────────────────────────────────────────
        c.append(seccion("&#128200; Inventario (solo PROPIETARIO)", new String[][]{
            {"VERINVENTARIO[*]",
             "Stock actual de todos los productos.",
             "VERINVENTARIO[*]"},
            {"VERINVENTARIO[productoId]",
             "Stock de un producto espec&iacute;fico.",
             "VERINVENTARIO[1]"},
            {"REGISTRARINGRESO[productoId,cantidad,motivo]",
             "<em>cantidad</em> &rarr; entero &nbsp;|&nbsp; <em>motivo</em> &rarr; texto libre",
             "REGISTRARINGRESO[1,10,Compra nueva]"},
            {"REGISTRAREGRESO[productoId,cantidad,motivo]",
             "<em>cantidad</em> &rarr; entero &nbsp;|&nbsp; <em>motivo</em> &rarr; texto libre",
             "REGISTRAREGRESO[1,2,Venta directa]"},
        }));

        // ── PROPIETARIO ─ Ventas ─────────────────────────────────────────────
        c.append(seccion("&#128176; Ventas directas (solo PROPIETARIO)", new String[][]{
            {"LISTARVENTAS[*]",
             "Lista todas las ventas.",
             "LISTARVENTAS[*]"},
            {"GETVENTA[id]",
             "Detalle de venta incluyendo cuotas si es cr&eacute;dito.",
             "GETVENTA[1]"},
            {"CREARVENTA_CONTADO[clienteId,fecha,montoTotal,metodoPago]",
             "<em>fecha</em> &rarr; <code>YYYY-MM-DDThh:mm:ss</code><br>"
             + "<em>metodoPago</em> &rarr; <code>EFECTIVO</code> / <code>QR</code> / <code>TARJETA</code>",
             "CREARVENTA_CONTADO[3,2026-06-18T10:00:00,5000.00,EFECTIVO]"},
            {"CREARVENTA_CREDITO[clienteId,fecha,montoTotal,nroCuotas,tasaInteres,metodoPago]",
             "<em>nroCuotas</em> &rarr; entero &nbsp;|&nbsp; <em>tasaInteres</em> &rarr; % (ej. 5.0)",
             "CREARVENTA_CREDITO[3,2026-06-18T10:00:00,8500.00,12,5.0,QR]"},
            {"DELETEVENTA[id]",
             "Elimina una venta.",
             "DELETEVENTA[1]"},
        }));

        // ── PROPIETARIO ─ Cr&eacute;ditos ────────────────────────────────────
        c.append(seccion("&#128184; Cr&eacute;ditos (solo PROPIETARIO)", new String[][]{
            {"LISTARCREDITOS[*]",
             "Lista todos los cr&eacute;ditos con saldo pendiente.",
             "LISTARCREDITOS[*]"},
            {"VERCUOTAS[creditoId]",
             "Cuotas de un cr&eacute;dito por ID.",
             "VERCUOTAS[1]"},
        }));

        // ── PROPIETARIO ─ Reportes ───────────────────────────────────────────
        c.append(seccion("&#128202; Reportes (solo PROPIETARIO)", new String[][]{
            {"REPORT_VENTAS_POR_MES[YYYY-MM]",
             "Ventas del mes con totales por tipo (contado/cr&eacute;dito).",
             "REPORT_VENTAS_POR_MES[2026-06]"},
            {"REPORT_VENTAS_POR_CLIENTE[clienteId]",
             "Historial de ventas de un cliente.",
             "REPORT_VENTAS_POR_CLIENTE[3]"},
            {"REPORT_MORAS_PENDIENTES[*]",
             "Cuotas vencidas con d&iacute;as de retraso y mora.",
             "REPORT_MORAS_PENDIENTES[*]"},
        }));

        return PlantillaBase.envolver("Sistema de Ventas por Correo &bull; Grupo 02 SA", c.toString());
    }

    public static String generarError(String mensaje) {
        String contenido =
            PlantillaBase.titulo("Acceso denegado o comando no reconocido") +
            PlantillaBase.errCard("NO SE PUDO PROCESAR EL COMANDO<br>" + mensaje.replace("\n", "<br>")) +
            "<p style=\"margin-top:14px;font-size:14px;color:#4a5568;\">Env&iacute;e <code style=\"font-family:'Courier New',monospace;background:#eff6ff;color:#1d4ed8;padding:2px 6px;border-radius:3px;\">HELP</code> en el asunto para ver la lista completa de comandos disponibles.</p>";
        return PlantillaBase.envolver("Sistema de Ventas por Correo &bull; Grupo 02 SA", contenido);
    }

    private static String seccion(String titulo, String[][] comandos) {
        StringBuilder sb = new StringBuilder();
        // Título de sección
        sb.append("<div style=\"margin-bottom:20px;\">");
        sb.append("<div style=\"font-size:13px;font-weight:800;color:#fff;background:#c0392b;" +
                  "padding:9px 14px;border-radius:8px 8px 0 0;margin:0;\">")
          .append(titulo).append("</div>");
        // Tabla con bordes
        sb.append("<table style=\"width:100%;border-collapse:collapse;font-size:13px;" +
                  "border:1px solid #e2e8f0;border-top:none;border-radius:0 0 8px 8px;overflow:hidden;\">");
        // Cabecera
        sb.append("<tr>");
        sb.append("<th style=\"background:#4a5568;color:#fff;padding:8px 12px;text-align:left;" +
                  "font-size:12px;font-weight:700;text-transform:uppercase;width:34%;\">Asunto del correo</th>");
        sb.append("<th style=\"background:#4a5568;color:#fff;padding:8px 12px;text-align:left;" +
                  "font-size:12px;font-weight:700;text-transform:uppercase;width:36%;\">Descripci&oacute;n</th>");
        sb.append("<th style=\"background:#4a5568;color:#fff;padding:8px 12px;text-align:left;" +
                  "font-size:12px;font-weight:700;text-transform:uppercase;width:30%;\">Ejemplo</th>");
        sb.append("</tr>");
        // Filas de comandos
        boolean par = false;
        for (String[] cmd : comandos) {
            String bg = par ? "#f9fafb" : "#ffffff";
            sb.append("<tr style=\"background:").append(bg).append(";\">");
            // Columna: comando
            sb.append("<td style=\"padding:9px 12px;border-bottom:1px solid #f1f5f9;vertical-align:top;\">")
              .append("<code style=\"font-family:'Courier New',monospace;font-size:11px;background:#eff6ff;" +
                      "color:#1d4ed8;padding:3px 6px;border-radius:4px;word-break:break-all;" +
                      "display:inline-block;line-height:1.6;\">")
              .append(cmd[0]).append("</code></td>");
            // Columna: descripción
            sb.append("<td style=\"padding:9px 12px;border-bottom:1px solid #f1f5f9;vertical-align:top;" +
                      "font-size:12px;line-height:1.65;color:#374151;\">")
              .append(cmd[1]).append("</td>");
            // Columna: ejemplo
            sb.append("<td style=\"padding:9px 12px;border-bottom:1px solid #f1f5f9;vertical-align:top;\">")
              .append("<code style=\"font-family:'Courier New',monospace;font-size:11px;background:#f0fdf4;" +
                      "color:#166534;padding:3px 6px;border-radius:4px;word-break:break-all;" +
                      "display:inline-block;line-height:1.6;\">")
              .append(cmd[2]).append("</code></td>");
            sb.append("</tr>");
            par = !par;
        }
        sb.append("</table></div>");
        return sb.toString();
    }
}
