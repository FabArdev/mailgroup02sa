package org.mailgrupo02.presentacion.email;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlantillaBase {

    // ── CSS de respaldo (clientes que sí lo soportan) ────────────────────────
    static String css() {
        return
            "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;background:#f0f2f5;color:#1e293b;margin:0;padding:0;}\n" +
            ".container{max-width:700px;margin:20px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.12);border:1px solid #e2e8f0;}\n" +
            ".content{padding:28px 24px;}\n" +
            "table{border-collapse:collapse;}\n" +
            "code{font-family:'Courier New',monospace;background:#f1f5f9;color:#1d4ed8;padding:2px 6px;border-radius:3px;font-size:13px;}\n";
    }

    // ── Wrapper principal ────────────────────────────────────────────────────
    public static String envolver(String subtitulo, String contenido) {
        return "<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"utf-8\">\n" +
               "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">\n" +
               "<style>\n" + css() + "</style>\n</head>\n<body>\n" +
               "<div style=\"max-width:700px;margin:20px auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.12);border:1px solid #e2e8f0;\">\n" +
               // Header
               "<div style=\"background:linear-gradient(135deg,#c0392b,#7b241c);padding:28px 24px;text-align:center;color:#fff;\">" +
               "<span style=\"display:block;font-size:44px;line-height:1;margin-bottom:8px;\">&#x1F3CD;&#xFE0F;</span>" +
               "<h1 style=\"margin:0;font-size:30px;font-weight:900;letter-spacing:4px;text-transform:uppercase;color:#fff;\">RAO MOTOS</h1>" +
               "<div style=\"width:44px;height:3px;background:rgba(255,255,255,0.35);margin:10px auto 8px;border-radius:2px;\"></div>" +
               "<p style=\"margin:0;font-size:13px;letter-spacing:0.5px;opacity:0.85;color:#fff;\">" + subtitulo + "</p>" +
               "</div>\n" +
               // Content
               "<div style=\"padding:28px 24px;\">" + contenido + "</div>\n" +
               // Footer
               "<div style=\"background:#4a5568;padding:16px 24px;text-align:center;font-size:13px;color:#fff;\">" +
               "<strong>Grupo 02 SA &mdash; Tecnolog&iacute;a Web (UAGRM)</strong><br>" +
               "Correo autom&aacute;tico &mdash; no responder directamente." +
               "</div>\n" +
               "</div>\n</body>\n</html>";
    }

    // ── Título de sección ────────────────────────────────────────────────────
    public static String titulo(String texto) {
        return "<h2 style=\"font-size:22px;font-weight:800;margin-top:0;margin-bottom:18px;" +
               "color:#c0392b;border-bottom:3px solid #fca5a5;padding-bottom:8px;\">" +
               texto + "</h2>";
    }

    // ── Card de éxito ────────────────────────────────────────────────────────
    public static String okCard(String msg, String idStr) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"background:#fff;border:2px solid #fca5a5;border-top:6px solid #c0392b;" +
                  "border-radius:12px;padding:26px 28px;text-align:center;margin-bottom:18px;\">");
        sb.append("<span style=\"display:block;font-size:54px;color:#c0392b;line-height:1;margin-bottom:10px;\">&#10003;</span>");
        sb.append("<span style=\"display:block;font-size:20px;font-weight:900;color:#c0392b;" +
                  "letter-spacing:2px;text-transform:uppercase;margin-bottom:12px;\">OPERACI&Oacute;N EXITOSA</span>");
        sb.append("<span style=\"display:block;font-size:16px;color:#374151;line-height:1.7;\">");
        sb.append(msg.replace("\r\n", "<br>").replace("\n", "<br>"));
        sb.append("</span>");
        if (idStr != null) {
            sb.append("<span style=\"display:inline-block;background:#c0392b;color:#fff;padding:9px 26px;" +
                      "border-radius:28px;font-size:20px;font-weight:800;margin-top:14px;\">ID: ")
              .append(idStr).append("</span>");
        }
        sb.append("</div>");
        return sb.toString();
    }

    // ── Card de error ────────────────────────────────────────────────────────
    public static String errCard(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"background:#fff5f5;border:2px solid #fca5a5;border-top:6px solid #991b1b;" +
                  "border-radius:12px;padding:26px 28px;text-align:center;margin-bottom:18px;\">");
        sb.append("<span style=\"display:block;font-size:54px;color:#991b1b;line-height:1;margin-bottom:10px;\">&#10007;</span>");
        sb.append("<span style=\"display:block;font-size:20px;font-weight:900;color:#991b1b;" +
                  "letter-spacing:2px;text-transform:uppercase;margin-bottom:12px;\">ERROR EN LA OPERACI&Oacute;N</span>");
        sb.append("<span style=\"display:block;font-size:15px;color:#7f1d1d;line-height:1.65;\">");
        sb.append(msg.replace("\r\n", "<br>").replace("\n", "<br>"));
        sb.append("</span>");
        sb.append("</div>");
        return sb.toString();
    }

    // ── Card QR (pagos) ──────────────────────────────────────────────────────
    public static String qrCard(String htmlContenido) {
        return "<div style=\"background:linear-gradient(145deg,#dbeafe,#bfdbfe);border:2px solid #93c5fd;" +
               "border-top:6px solid #1d4ed8;border-radius:12px;padding:20px 28px 16px;text-align:center;margin-bottom:16px;\">" +
               "<span style=\"display:block;font-size:44px;line-height:1;margin-bottom:8px;\">&#128179;</span>" +
               "<span style=\"display:block;font-size:20px;font-weight:800;color:#1d4ed8;letter-spacing:2px;" +
               "text-transform:uppercase;margin-bottom:6px;\">ESCANEA EL C&Oacute;DIGO QR PARA PAGAR</span>" +
               "<span style=\"display:block;font-size:15px;color:#1e40af;margin-bottom:4px;\">" +
               "Apunta la c&aacute;mara al c&oacute;digo para completar el pago</span>" +
               "</div>" + htmlContenido;
    }

    // ── Convierte tabla de texto (String.format) a tabla HTML ────────────────
    public static String tablaHtml(String icono, String texto) {
        String[] lineas = texto.split("\r?\n");

        // Encontrar la línea separadora (----)
        int sepIdx = -1;
        for (int i = 0; i < lineas.length; i++) {
            String t = lineas[i].trim();
            if (!t.isEmpty() && t.matches("[\\-=]{3,}.*")) {
                sepIdx = i;
                break;
            }
        }

        // Fallback: no es tabla reconocible → pre estilizado
        if (sepIdx < 1) {
            return cabeceraTbl(icono) +
                   "<div style=\"border:2px solid #e2e8f0;border-top:none;border-radius:0 0 10px 10px;" +
                   "overflow-x:auto;margin-bottom:20px;\">" +
                   "<pre style=\"font-family:'Courier New',monospace;font-size:12px;background:#f8fafc;" +
                   "padding:16px;white-space:pre;color:#1e293b;line-height:1.6;margin:0;\">" +
                   escape(texto) + "</pre></div>";
        }

        // Detectar posiciones de inicio de columna en la línea de cabecera
        String headerLine = lineas[sepIdx - 1];
        List<Integer> colStarts = new ArrayList<>();
        colStarts.add(0);
        Matcher m = Pattern.compile("\\s{2,}(\\S)").matcher(headerLine);
        while (m.find()) {
            colStarts.add(m.start(1));
        }

        // Extraer nombres de columnas
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < colStarts.size(); i++) {
            int start = colStarts.get(i);
            int end = (i + 1 < colStarts.size()) ? colStarts.get(i + 1) : headerLine.length();
            if (start < headerLine.length()) {
                headers.add(headerLine.substring(start, Math.min(end, headerLine.length())).trim());
            }
        }

        // Construir tabla HTML
        StringBuilder sb = new StringBuilder();
        sb.append(cabeceraTbl(icono));
        sb.append("<div style=\"border:2px solid #e2e8f0;border-top:none;border-radius:0 0 10px 10px;" +
                  "overflow-x:auto;margin-bottom:22px;\">");
        sb.append("<table style=\"width:100%;border-collapse:collapse;font-size:13px;\">");

        // Fila de encabezados
        sb.append("<thead><tr>");
        for (String h : headers) {
            sb.append("<th style=\"background:#c0392b;color:#fff;padding:9px 12px;text-align:left;" +
                      "font-size:11px;font-weight:700;text-transform:uppercase;letter-spacing:0.4px;" +
                      "white-space:nowrap;border-right:1px solid rgba(255,255,255,0.15);\">")
              .append(h.isEmpty() ? "&nbsp;" : escape(h)).append("</th>");
        }
        sb.append("</tr></thead><tbody>");

        // Filas de datos
        boolean par = false;
        for (int i = sepIdx + 1; i < lineas.length; i++) {
            String row = lineas[i];
            if (row.trim().isEmpty()) continue;
            String rowBg = par ? "#f8fafc" : "#ffffff";
            sb.append("<tr style=\"background:").append(rowBg).append(";\">");

            for (int c = 0; c < colStarts.size(); c++) {
                int start = colStarts.get(c);
                int end = (c + 1 < colStarts.size()) ? colStarts.get(c + 1) : row.length() + 1;
                String val = "";
                if (start < row.length()) {
                    val = row.substring(start, Math.min(end, row.length())).trim();
                }
                sb.append("<td style=\"padding:9px 12px;border-bottom:1px solid #f1f5f9;" +
                          "color:#1e293b;white-space:nowrap;border-right:1px solid #f1f5f9;\">")
                  .append(badgeEstado(val))
                  .append("</td>");
            }
            sb.append("</tr>");
            par = !par;
        }

        sb.append("</tbody></table></div>");
        return sb.toString();
    }

    // ── Extrae (ID: N) de un texto ───────────────────────────────────────────
    public static String extraerId(String msg) {
        Matcher m = Pattern.compile("\\(ID:\\s*(\\d+)\\)").matcher(msg);
        return m.find() ? m.group(1) : null;
    }

    // ── Escapa HTML básico ───────────────────────────────────────────────────
    public static String escape(String txt) {
        if (txt == null) return "";
        return txt.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // ── Helpers privados ─────────────────────────────────────────────────────
    private static String cabeceraTbl(String icono) {
        return "<div style=\"background:#4a5568;color:#fff;padding:10px 18px;" +
               "border-radius:10px 10px 0 0;font-size:14px;font-weight:700;" +
               "letter-spacing:0.4px;\">" + icono + " Resultados</div>";
    }

    private static String badgeEstado(String val) {
        if (val == null || val.isEmpty()) return "<span style=\"color:#9ca3af;\">—</span>";
        switch (val) {
            case "PENDIENTE":
                return "<span style=\"background:#fef3c7;color:#92400e;padding:2px 8px;" +
                       "border-radius:4px;font-size:11px;font-weight:700;\">PENDIENTE</span>";
            case "PAGADO":
                return "<span style=\"background:#dcfce7;color:#166534;padding:2px 8px;" +
                       "border-radius:4px;font-size:11px;font-weight:700;\">PAGADO</span>";
            case "CANCELADO":
                return "<span style=\"background:#dcfce7;color:#166534;padding:2px 8px;" +
                       "border-radius:4px;font-size:11px;font-weight:700;\">CANCELADO</span>";
            case "ACTIVO":
                return "<span style=\"background:#dcfce7;color:#166534;padding:2px 8px;" +
                       "border-radius:4px;font-size:11px;font-weight:700;\">ACTIVO</span>";
            case "ANULADO":
            case "INACTIVO":
                return "<span style=\"background:#fee2e2;color:#991b1b;padding:2px 8px;" +
                       "border-radius:4px;font-size:11px;font-weight:700;\">" + val + "</span>";
            case "SOLICITADO":
            case "DESPACHADO":
            case "PROCESADO":
                return "<span style=\"background:#dbeafe;color:#1d4ed8;padding:2px 8px;" +
                       "border-radius:4px;font-size:11px;font-weight:700;\">" + val + "</span>";
            case "VIGENTE":
                return "<span style=\"background:#f3e8ff;color:#7c3aed;padding:2px 8px;" +
                       "border-radius:4px;font-size:11px;font-weight:700;\">VIGENTE</span>";
            default:
                return escape(val);
        }
    }
}
