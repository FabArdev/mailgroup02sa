package org.mailgrupo02.presentacion.email.controladores;

import org.mailgrupo02.datos.modelo.ProductoM;
import org.mailgrupo02.negocio.productos.ProductoService;
import org.mailgrupo02.negocio.productos.ProductoN;
import org.mailgrupo02.presentacion.email.PProductos;

import java.util.List;
import java.util.regex.*;

public class ProductoControlador {

    public static boolean canHandle(String cmd) {
        if (cmd == null) return false;
        switch (cmd.toUpperCase()) {
            case "LISTARPRODUCTOS": case "LISTARPRODUCTO":
            case "CREATEPRODUCTO":
            case "UPDATEPRODUCTO":
            case "DELETEPRODUCTO":
            case "GETPRODUCTO":
                return true;
            default:
                return false;
        }
    }

    public static String handle(String cmd, List<String> params) {
        try {
            ProductoService service = new ProductoService(new ProductoM());

            switch (cmd.toUpperCase()) {

                case "LISTARPRODUCTOS":
                case "LISTARPRODUCTO":
                    return PProductos.generarHtml(cmd, service.obtenerProductos());

                case "GETPRODUCTO": {
                    if (params.isEmpty())
                        return PProductos.generarHtml(cmd, "Error: se requiere el ID del producto.");
                    int id = Integer.parseInt(params.get(0).trim());
                    ProductoN p = service.leerProducto(id);
                    return PProductos.generarHtml(cmd, fichaProducto(p, "get"));
                }

                case "CREATEPRODUCTO": {
                    if (params.size() < 6)
                        return PProductos.generarHtml(cmd, "Error: se requieren 6 parámetros [codigo,nombre,marca,modelo,descripcion,precioVentaBase].");
                    String msg = service.agregarProducto(
                        params.get(0), params.get(1), params.get(2),
                        params.get(3), params.get(4), Double.parseDouble(params.get(5).trim()));
                    int newId = extraerId(msg);
                    if (newId > 0) {
                        ProductoN p = service.leerProducto(newId);
                        return PProductos.generarHtml(cmd, fichaProducto(p, "create"));
                    }
                    return PProductos.generarHtml(cmd, msg);
                }

                case "UPDATEPRODUCTO": {
                    if (params.size() < 8)
                        return PProductos.generarHtml(cmd, "Error: se requieren 8 parámetros [id,codigo,nombre,marca,modelo,descripcion,precio,activo].");
                    int id = Integer.parseInt(params.get(0).trim());
                    ProductoN antes = service.leerProducto(id);
                    service.actualizarProducto(id,
                        params.get(1), params.get(2), params.get(3), params.get(4), params.get(5),
                        Double.parseDouble(params.get(6).trim()),
                        Boolean.parseBoolean(params.get(7).trim()));
                    ProductoN despues = service.leerProducto(id);
                    return PProductos.generarHtml(cmd, diffProducto(antes, despues));
                }

                case "DELETEPRODUCTO": {
                    if (params.isEmpty())
                        return PProductos.generarHtml(cmd, "Error: se requiere el ID del producto.");
                    int id = Integer.parseInt(params.get(0).trim());
                    ProductoN p = null;
                    try { p = service.leerProducto(id); } catch (Exception ignored) {}
                    String rawResult = service.eliminarProducto(id);
                    if (p != null && !rawResult.toLowerCase().startsWith("error")
                            && !rawResult.toLowerCase().contains("no encontrado")) {
                        return PProductos.generarHtml(cmd, fichaProducto(p, "delete"));
                    }
                    return PProductos.generarHtml(cmd, rawResult);
                }

                default:
                    return PProductos.generarHtml(cmd, "Error: Comando de productos no soportado.");
            }

        } catch (Exception e) {
            return PProductos.generarHtml(cmd, "Error: " + e.getMessage());
        }
    }

    // ─── Tarjetas HTML (estilos 100% inline — compatibles con Gmail) ──────────

    private static String fichaProducto(ProductoN p, String tipo) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"border:1px solid #e2e8f0;border-radius:8px;overflow:hidden;margin-bottom:8px;\">");
        switch (tipo) {
            case "create":
                sb.append("<div style=\"display:block;background-color:#dcfce7;color:#166534;"
                        + "padding:10px 16px;font-weight:700;font-size:14px;"
                        + "border-bottom:1px solid #bbf7d0;\">")
                  .append("&#10003; Producto Registrado Exitosamente &mdash; ID: ").append(p.getId()).append("</div>");
                break;
            case "delete":
                sb.append("<div style=\"display:block;background-color:#fee2e2;color:#991b1b;"
                        + "padding:10px 16px;font-weight:700;font-size:14px;"
                        + "border-bottom:1px solid #fca5a5;\">")
                  .append("&#128465; Producto Eliminado &mdash; ID: ").append(p.getId()).append("</div>");
                break;
            default:
                sb.append("<div style=\"display:block;background-color:#dcfce7;color:#166534;"
                        + "padding:10px 16px;font-weight:700;font-size:14px;"
                        + "border-bottom:1px solid #bbf7d0;\">")
                  .append("&#128230; Datos del Producto &mdash; ID: ").append(p.getId()).append("</div>");
        }
        sb.append("<table style=\"width:100%;border-collapse:collapse;font-size:14px;\">");
        fila(sb, "ID",                  String.valueOf(p.getId()));
        fila(sb, "C&oacute;digo",       nvl(p.getCodigo()));
        fila(sb, "Nombre",              nvl(p.getNombre()));
        fila(sb, "Marca",               nvl(p.getMarca()));
        fila(sb, "Modelo",              nvl(p.getModelo()));
        fila(sb, "Descripci&oacute;n",  nvl(p.getDescripcion()));
        fila(sb, "Precio Base",         String.format("%.2f Bs.", p.getPrecioVentaBase()));
        fila(sb, "Estado",              p.isActivo() ? "&#10004; Activo" : "Inactivo");
        fila(sb, "Fecha Reg.",          nvl(p.getFechaReg()));
        sb.append("</table></div>");
        return sb.toString();
    }

    private static String diffProducto(ProductoN a, ProductoN d) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"border:1px solid #e2e8f0;border-radius:8px;overflow:hidden;margin-bottom:8px;\">");
        sb.append("<div style=\"display:block;background-color:#fef9c3;color:#713f12;"
                + "padding:10px 16px;font-weight:700;font-size:14px;"
                + "border-bottom:1px solid #fef08a;\">")
          .append("&#9998; Producto Actualizado &mdash; ID: ").append(d.getId()).append("</div>");
        sb.append("<table style=\"width:100%;border-collapse:collapse;font-size:14px;\">");
        sb.append("<tr>")
          .append("<th style=\"background-color:#4a5568;color:#fff;padding:8px 14px;font-weight:700;width:28%;\">Campo</th>")
          .append("<th style=\"background-color:#fef2f2;color:#991b1b;padding:8px 14px;font-weight:700;width:36%;\">&#8592; Antes</th>")
          .append("<th style=\"background-color:#f0fdf4;color:#166534;padding:8px 14px;font-weight:700;width:36%;\">Despu&eacute;s &#8594;</th>")
          .append("</tr>");
        difFila(sb, "C&oacute;digo",       nvl(a.getCodigo()),    nvl(d.getCodigo()));
        difFila(sb, "Nombre",              nvl(a.getNombre()),    nvl(d.getNombre()));
        difFila(sb, "Marca",               nvl(a.getMarca()),     nvl(d.getMarca()));
        difFila(sb, "Modelo",              nvl(a.getModelo()),    nvl(d.getModelo()));
        difFila(sb, "Descripci&oacute;n",  nvl(a.getDescripcion()), nvl(d.getDescripcion()));
        difFila(sb, "Precio Base",
            String.format("%.2f Bs.", a.getPrecioVentaBase()),
            String.format("%.2f Bs.", d.getPrecioVentaBase()));
        difFila(sb, "Estado",
            a.isActivo() ? "Activo" : "Inactivo",
            d.isActivo() ? "Activo" : "Inactivo");
        sb.append("</table></div>");
        return sb.toString();
    }

    private static void fila(StringBuilder sb, String label, String val) {
        sb.append("<tr>")
          .append("<td style=\"padding:8px 14px;color:#6b7280;font-weight:600;width:35%;"
                + "border-bottom:1px solid #f1f5f9;vertical-align:top;\">").append(label).append("</td>")
          .append("<td style=\"padding:8px 14px;color:#111827;"
                + "border-bottom:1px solid #f1f5f9;vertical-align:top;\">").append(val).append("</td>")
          .append("</tr>");
    }

    private static void difFila(StringBuilder sb, String campo, String antes, String despues) {
        sb.append("<tr>")
          .append("<td style=\"padding:8px 14px;color:#6b7280;font-weight:600;"
                + "border-bottom:1px solid #f1f5f9;vertical-align:top;\">").append(campo).append("</td>")
          .append("<td style=\"padding:8px 14px;color:#991b1b;background-color:#fff5f5;"
                + "border-bottom:1px solid #f1f5f9;vertical-align:top;\">").append(antes).append("</td>")
          .append("<td style=\"padding:8px 14px;color:#166534;background-color:#f0fdf4;"
                + "border-bottom:1px solid #f1f5f9;vertical-align:top;\">").append(despues).append("</td>")
          .append("</tr>");
    }

    private static String nvl(String val) { return val != null ? val : "N/A"; }

    private static int extraerId(String msg) {
        if (msg == null) return -1;
        Matcher m = Pattern.compile("\\(ID:\\s*(\\d+)\\)").matcher(msg);
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }
}
