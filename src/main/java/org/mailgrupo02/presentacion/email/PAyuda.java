package org.mailgrupo02.presentacion.email;

public class PAyuda {

    // ── Badges de acceso ──────────────────────────────────────────────────────
    private static final String TODOS        = badge("#dcfce7", "#166534", "Todos");
    private static final String PROPIETARIO  = badge("#fee2e2", "#991b1b", "Propietario");

    private static String badge(String bg, String color, String label) {
        return "<span style=\"display:inline-block;padding:3px 9px;background-color:" + bg
             + ";color:" + color + ";border-radius:10px;font-size:11px;font-weight:700;"
             + "white-space:nowrap;\">" + label + "</span>";
    }

    // ─────────────────────────────────────────────────────────────────────────

    public static String generarHtml() {
        StringBuilder c = new StringBuilder();

        c.append(PlantillaBase.titulo("RAO MOTOS &mdash; Sistema de Ventas por Correo"));

        c.append("<p style=\"color:#4a5568;font-size:15px;margin-bottom:10px;\">")
         .append("Escribe el comando en el <strong>Asunto</strong> del correo a ")
         .append("<strong>grupo02sa@tecnoweb.org.bo</strong>. El cuerpo puede ir vac&iacute;o.</p>");

        c.append("<div style=\"background-color:#eff6ff;border:2px solid #bfdbfe;border-radius:10px;"
               + "padding:14px 18px;font-size:14px;color:#1d4ed8;margin-bottom:12px;line-height:1.65;\">")
         .append("<strong>&#9432; C&oacute;mo usar:</strong> Escribe el comando con sus par&aacute;metros entre ")
         .append("<code style=\"font-family:'Courier New',monospace;background:#dbeafe;color:#1d4ed8;"
               + "padding:1px 5px;border-radius:3px;\">[ ]</code>")
         .append(" separados por comas. <strong>id</strong> es siempre n&uacute;mero entero. "
               + "Los valores enumerados van en MAY&Uacute;SCULAS.")
         .append("</div>");

        c.append("<div style=\"background-color:#fef9c3;border:1px solid #fde047;border-radius:8px;"
               + "padding:10px 16px;font-size:13px;color:#713f12;margin-bottom:22px;\">")
         .append("&#9888; <strong>Acceso:</strong> El control de acceso por roles est&aacute; dise&ntilde;ado "
               + "pero a&uacute;n no est&aacute; activo &mdash; actualmente cualquier remitente puede ejecutar "
               + "cualquier comando. La columna <em>Acceso</em> indica qui&eacute;n deber&iacute;a usarlo "
               + "seg&uacute;n el dise&ntilde;o del sistema.")
         .append("</div>");

        // ── GENERALES ────────────────────────────────────────────────────────
        c.append(seccion("&#128681; Generales", new String[][]{
            {"HELP",
             "Muestra esta p&aacute;gina de ayuda con todos los comandos disponibles.",
             "HELP", TODOS},
            {"WHOAMI",
             "Muestra tu perfil registrado: ID, nombre, email, rol y datos de contacto.",
             "WHOAMI", TODOS},
        }));

        // ── CU1 USUARIOS ─────────────────────────────────────────────────────
        c.append(seccion("CU1 &bull; &#128101; Gesti&oacute;n de Usuarios", new String[][]{
            {"LISTARUSUARIOS[*]",
             "Lista todos los usuarios del sistema.",
             "LISTARUSUARIOS[*]", PROPIETARIO},
            {"GETUSUARIO[id]",
             "Detalle completo de un usuario por ID.",
             "GETUSUARIO[6]", PROPIETARIO},
            {"CREATEUSUARIO[nombre,email,pass,rol,telefono,direccion]",
             "<em>rol</em> &rarr; <code>PROPIETARIO</code> / <code>CLIENTE</code>",
             "CREATEUSUARIO[Juan Rao,juan@mail.com,clave123,CLIENTE,70123456,Av. Banzer 100]", PROPIETARIO},
            {"UPDATEUSUARIO[id,nombre,email,pass,rol,tel,dir,activo]",
             "<em>activo</em> &rarr; <code>true</code> / <code>false</code>",
             "UPDATEUSUARIO[6,Juan Rao,juan@mail.com,clave,CLIENTE,70123456,Av. Banzer,true]", PROPIETARIO},
            {"UPDATECLIENTE[id,nitCi,tipoCliente]",
             "<em>tipoCliente</em> &rarr; <code>REGULAR</code> / <code>FRECUENTE</code> / <code>MAYORISTA</code>",
             "UPDATECLIENTE[6,12345678,FRECUENTE]", PROPIETARIO},
            {"DELETEUSUARIO[id]",
             "Elimina un usuario del sistema (en cascada sus subtablas).",
             "DELETEUSUARIO[6]", PROPIETARIO},
            {"CAMBIARROL[userId,nuevoRol]",
             "Cambia el rol de un usuario y migra sus datos de subtabla.<br>"
             + "<em>nuevoRol</em> &rarr; <code>PROPIETARIO</code> / <code>CLIENTE</code>",
             "CAMBIARROL[6,PROPIETARIO]", PROPIETARIO},
        }));

        // ── PROVEEDORES ───────────────────────────────────────────────────────
        c.append(seccion("&#128666; Gesti&oacute;n de Proveedores", new String[][]{
            {"LISTARPROVEEDORES[*]",
             "Lista todos los proveedores registrados.",
             "LISTARPROVEEDORES[*]", PROPIETARIO},
            {"GETPROVEEDOR[id]",
             "Detalle de un proveedor por ID.",
             "GETPROVEEDOR[1]", PROPIETARIO},
            {"CREATEPROVEEDOR[razonSocial,contacto,telefono]",
             "<em>razonSocial</em> &rarr; nombre de la empresa<br>"
             + "<em>contacto</em> &rarr; nombre del contacto principal",
             "CREATEPROVEEDOR[Honda Bolivia,Carlos Mamani,77712345]", PROPIETARIO},
            {"UPDATEPROVEEDOR[id,razonSocial,contacto,telefono]",
             "Actualiza datos de un proveedor existente.",
             "UPDATEPROVEEDOR[1,Honda Bolivia,Luis Perez,76543210]", PROPIETARIO},
            {"DELETEPROVEEDOR[id]",
             "Elimina un proveedor del sistema.",
             "DELETEPROVEEDOR[1]", PROPIETARIO},
        }));

        // ── CU2 PRODUCTOS ─────────────────────────────────────────────────────
        c.append(seccion("CU2 &bull; &#128230; Gesti&oacute;n de Productos", new String[][]{
            {"LISTARPRODUCTOS[*]",
             "Cat&aacute;logo completo de productos disponibles con precios.",
             "LISTARPRODUCTOS[*]", TODOS},
            {"GETPRODUCTO[id]",
             "Detalle de un producto espec&iacute;fico.",
             "GETPRODUCTO[1]", TODOS},
            {"CREATEPRODUCTO[codigo,nombre,marca,modelo,descripcion,precio]",
             "<em>codigo</em> &rarr; texto (ej. MOT-001)<br>"
             + "<em>precio</em> &rarr; decimal en Bs. (ej. 8500.00)",
             "CREATEPRODUCTO[MOT-001,Moto Sport 150,Honda,CB150,Deportiva,8500.00]", PROPIETARIO},
            {"UPDATEPRODUCTO[id,codigo,nombre,marca,modelo,desc,precio,activo]",
             "<em>activo</em> &rarr; <code>true</code> / <code>false</code>",
             "UPDATEPRODUCTO[1,MOT-001,Moto Sport,Honda,CB150,Deportiva,8500.00,true]", PROPIETARIO},
            {"DELETEPRODUCTO[id]",
             "Elimina un producto del cat&aacute;logo.",
             "DELETEPRODUCTO[1]", PROPIETARIO},
        }));

        // ── CU3 COMPRAS ───────────────────────────────────────────────────────
        c.append(seccion("CU3 &bull; &#128666; Gesti&oacute;n de Compras a Proveedores", new String[][]{
            {"LISTARCOMPRAS[*]",
             "Lista todas las &oacute;rdenes de compra registradas.",
             "LISTARCOMPRAS[*]", PROPIETARIO},
            {"GETCOMPRA[id]",
             "Detalle de una orden de compra por ID.",
             "GETCOMPRA[1]", PROPIETARIO},
            {"CREARCOMPRA[proveedorId,monto]",
             "<em>proveedorId</em> &rarr; ID del proveedor<br>"
             + "<em>monto</em> &rarr; total de la compra en Bs.",
             "CREARCOMPRA[1,15000.00]", PROPIETARIO},
            {"ANULARCOMPRA[id]",
             "Anula una orden de compra existente.",
             "ANULARCOMPRA[2]", PROPIETARIO},
        }));

        // ── CU4 PEDIDOS ───────────────────────────────────────────────────────
        c.append(seccion("CU4 &bull; &#128221; Gesti&oacute;n de Pedidos", new String[][]{
            {"LISTARPEDIDOS[*]",
             "Lista todos los pedidos del sistema.",
             "LISTARPEDIDOS[*]", PROPIETARIO},
            {"GETPEDIDO[id]",
             "Detalle de un pedido con sus productos.",
             "GETPEDIDO[1]", PROPIETARIO},
            {"CREARPEDIDO[clienteId]",
             "Crea un pedido en estado <strong>SOLICITADO</strong> para el cliente indicado.",
             "CREARPEDIDO[6]", PROPIETARIO},
            {"DESPACHARPEDIDO[id]",
             "Marca el pedido como <strong>DESPACHADO</strong> (entregado al cliente).",
             "DESPACHARPEDIDO[1]", PROPIETARIO},
            {"ANULARPEDIDO[id]",
             "Anula un pedido.",
             "ANULARPEDIDO[2]", PROPIETARIO},
        }));

        // ── CU5 INVENTARIO ────────────────────────────────────────────────────
        c.append(seccion("CU5 &bull; &#128200; Inventario", new String[][]{
            {"VERINVENTARIO[*]",
             "Stock actual de todos los productos.",
             "VERINVENTARIO[*]", PROPIETARIO},
            {"VERINVENTARIO[productoId]",
             "Stock de un producto espec&iacute;fico.",
             "VERINVENTARIO[1]", PROPIETARIO},
            {"REGISTRARINGRESO[productoId,cantidad,descripcion]",
             "Registra una entrada de stock.<br>"
             + "<em>descripcion</em> &rarr; motivo (texto libre)",
             "REGISTRARINGRESO[1,10,Compra nueva]", PROPIETARIO},
            {"REGISTRAREGRESO[productoId,cantidad,descripcion]",
             "Registra una salida de stock.<br>"
             + "<em>descripcion</em> &rarr; motivo (texto libre)",
             "REGISTRAREGRESO[1,2,Devolucion cliente]", PROPIETARIO},
        }));

        // ── CU6 VENTAS ────────────────────────────────────────────────────────
        c.append(seccion("CU6 &bull; &#128176; Ventas", new String[][]{
            {"LISTARVENTAS[*]",
             "Lista todas las ventas registradas.",
             "LISTARVENTAS[*]", PROPIETARIO},
            {"GETVENTA[id]",
             "Detalle de una venta con sus productos.",
             "GETVENTA[1]", PROPIETARIO},
            {"CREARVENTA_CONTADO[clienteId,fecha,monto,metodoPago]",
             "<em>fecha</em> &rarr; <code>YYYY-MM-DDThh:mm:ss</code><br>"
             + "<em>metodoPago</em> &rarr; <code>EFECTIVO</code> / <code>QR</code> / <code>TARJETA</code>",
             "CREARVENTA_CONTADO[6,2026-06-19T10:00:00,5000.00,EFECTIVO]", PROPIETARIO},
            {"CREARVENTA_CREDITO[clienteId,fecha,monto,cuotas,interes,metodoPago]",
             "<em>cuotas</em> &rarr; n&uacute;mero entero<br>"
             + "<em>interes</em> &rarr; porcentaje (ej. <code>5.0</code>)<br>"
             + "<em>metodoPago</em> &rarr; <code>EFECTIVO</code> / <code>QR</code> / <code>TARJETA</code>",
             "CREARVENTA_CREDITO[6,2026-06-19T10:00:00,8500.00,12,5.0,QR]", PROPIETARIO},
            {"DELETEVENTA[id]",
             "Elimina una venta del sistema.",
             "DELETEVENTA[1]", PROPIETARIO},
        }));

        // ── CU7 CRÉDITOS Y PAGOS ──────────────────────────────────────────────
        c.append(seccion("CU7 &bull; &#128184; Cr&eacute;ditos y Pagos", new String[][]{
            {"LISTARCREDITOS[*]",
             "Lista todos los cr&eacute;ditos con saldo pendiente.",
             "LISTARCREDITOS[*]", PROPIETARIO},
            {"VERCUOTAS[creditoId]",
             "Muestra el plan de cuotas de un cr&eacute;dito (pagadas y pendientes).",
             "VERCUOTAS[1]", PROPIETARIO},
            {"PAGARCUOTA[creditoId,numeroCuota]",
             "Genera c&oacute;digo QR de PagoF&aacute;cil para pagar una cuota.<br>"
             + "El monto se toma autom&aacute;ticamente del plan de cuotas.",
             "PAGARCUOTA[1,1]", PROPIETARIO},
        }));

        // ── REPORTES ──────────────────────────────────────────────────────────
        c.append(seccion("&#128202; Reportes Gerenciales", new String[][]{
            {"REPORT_VENTAS_POR_MES[yyyy-MM]",
             "Resumen del mes: total de ventas desglosado por tipo (contado / cr&eacute;dito).",
             "REPORT_VENTAS_POR_MES[2026-06]", PROPIETARIO},
            {"REPORT_VENTAS_POR_CLIENTE[clienteId]",
             "Historial completo de ventas de un cliente espec&iacute;fico.",
             "REPORT_VENTAS_POR_CLIENTE[6]", PROPIETARIO},
            {"REPORT_MORAS_PENDIENTES[*]",
             "Cuotas vencidas con d&iacute;as de retraso y monto de mora acumulado.",
             "REPORT_MORAS_PENDIENTES[*]", PROPIETARIO},
        }));

        return PlantillaBase.envolver("Sistema de Ventas por Correo &bull; Grupo 02 SA", c.toString());
    }

    public static String generarError(String mensaje) {
        String contenido =
            PlantillaBase.titulo("Acceso denegado o comando no reconocido") +
            PlantillaBase.errCard("NO SE PUDO PROCESAR EL COMANDO<br>" + mensaje.replace("\n", "<br>")) +
            "<p style=\"margin-top:14px;font-size:14px;color:#4a5568;\">Env&iacute;e "
            + "<code style=\"font-family:'Courier New',monospace;background:#eff6ff;color:#1d4ed8;"
            + "padding:2px 6px;border-radius:3px;\">HELP</code>"
            + " en el asunto para ver la lista completa de comandos disponibles.</p>";
        return PlantillaBase.envolver("Sistema de Ventas por Correo &bull; Grupo 02 SA", contenido);
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    private static String seccion(String titulo, String[][] comandos) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"margin-bottom:20px;\">");
        sb.append("<div style=\"font-size:13px;font-weight:800;color:#fff;background-color:#c0392b;"
                + "padding:9px 14px;border-radius:8px 8px 0 0;margin:0;\">")
          .append(titulo).append("</div>");
        sb.append("<table style=\"width:100%;border-collapse:collapse;font-size:13px;"
                + "border:1px solid #e2e8f0;border-top:none;\">");
        sb.append("<tr>")
          .append(th("Asunto del correo", "29%"))
          .append(th("Descripci&oacute;n",  "34%"))
          .append(th("Ejemplo",             "25%"))
          .append(th("Acceso",              "12%"))
          .append("</tr>");
        boolean par = false;
        for (String[] cmd : comandos) {
            String bg = par ? "#f9fafb" : "#ffffff";
            sb.append("<tr style=\"background-color:").append(bg).append(";\">");
            sb.append("<td style=\"padding:9px 12px;border-bottom:1px solid #f1f5f9;vertical-align:top;\">")
              .append("<code style=\"font-family:'Courier New',monospace;font-size:11px;background:#eff6ff;"
                    + "color:#1d4ed8;padding:3px 6px;border-radius:4px;word-break:break-all;"
                    + "display:inline-block;line-height:1.6;\">")
              .append(cmd[0]).append("</code></td>");
            sb.append("<td style=\"padding:9px 12px;border-bottom:1px solid #f1f5f9;vertical-align:top;"
                    + "font-size:12px;line-height:1.65;color:#374151;\">")
              .append(cmd[1]).append("</td>");
            sb.append("<td style=\"padding:9px 12px;border-bottom:1px solid #f1f5f9;vertical-align:top;\">")
              .append("<code style=\"font-family:'Courier New',monospace;font-size:11px;background:#f0fdf4;"
                    + "color:#166534;padding:3px 6px;border-radius:4px;word-break:break-all;"
                    + "display:inline-block;line-height:1.6;\">")
              .append(cmd[2]).append("</code></td>");
            sb.append("<td style=\"padding:9px 12px;border-bottom:1px solid #f1f5f9;vertical-align:top;"
                    + "text-align:center;\">")
              .append(cmd[3]).append("</td>");
            sb.append("</tr>");
            par = !par;
        }
        sb.append("</table></div>");
        return sb.toString();
    }

    private static String th(String label, String width) {
        return "<th style=\"background-color:#4a5568;color:#fff;padding:8px 12px;text-align:left;"
             + "font-size:12px;font-weight:700;text-transform:uppercase;width:" + width + ";\">"
             + label + "</th>";
    }
}
