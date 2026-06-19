package org.mailgrupo02.presentacion.email;

import java.util.regex.*;

public class PVentas {

    private static final String COLOR1 = "#b91c1c";
    private static final String COLOR2 = "#7f1d1d";

    public static String generarHtml(String comando, String resultado) {
        StringBuilder body = new StringBuilder();
        body.append("<h2 class=\"card-title\">").append(describir(comando)).append("</h2>");

        boolean esError = resultado.trim().toLowerCase().startsWith("error");

        if (resultado.contains("---") || resultado.contains("===")) {
            body.append("<div class=\"lista-hdr\">&#128202; Resultados</div>")
                .append("<div class=\"lista-wrap\"><pre class=\"table-pre\">")
                .append(escapar(resultado))
                .append("</pre></div>");
        } else if (esError) {
            body.append("<div class=\"err-card\">")
                .append("<span class=\"err-icon\">&#10007;</span>")
                .append("<span class=\"err-tit\">ERROR EN LA OPERACI&Oacute;N</span>")
                .append("<span class=\"err-msg\">")
                .append(resultado.replace("\r\n", "<br>").replace("\n", "<br>"))
                .append("</span></div>");
        } else {
            String idStr = extraerIdTexto(resultado);
            String limpio = idStr != null
                ? resultado.replaceAll("\\s*\\(ID:\\s*\\d+\\)", "").trim()
                : resultado;
            body.append("<div class=\"ok-card\">")
                .append("<span class=\"ok-icon\">&#10003;</span>")
                .append("<span class=\"ok-tit\">OPERACI&Oacute;N EXITOSA</span>")
                .append("<span class=\"ok-msg\">")
                .append(limpio.replace("\r\n", "<br>").replace("\n", "<br>"))
                .append("</span>");
            if (idStr != null)
                body.append("<span class=\"id-badge\">ID: ").append(idStr).append("</span>");
            body.append("</div>");
        }

        return construirPlantillaBase(body.toString());
    }

    private static String describir(String cmd) {
        if (cmd == null) return "Ventas";
        switch (cmd.toUpperCase()) {
            case "LISTARVENTAS": case "LISTARVENTA": return "Registro de Ventas";
            case "CREARVENTA_CONTADO":  return "Registrar Venta al Contado";
            case "CREARVENTA_CREDITO":  return "Registrar Venta a Crédito";
            case "GETVENTA":            return "Detalle de Venta";
            case "DELETEVENTA":         return "Eliminar Venta";
            default:                    return "Gestión de Ventas";
        }
    }

    private static String escapar(String txt) {
        return txt.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String extraerIdTexto(String msg) {
        Matcher m = Pattern.compile("\\(ID:\\s*(\\d+)\\)").matcher(msg);
        return m.find() ? m.group(1) : null;
    }

    private static String construirPlantillaBase(String contenido) {
        return "<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"utf-8\">\n<style>\n" +
               "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;background:#f1f5f9;color:#1e293b;margin:0;padding:0;}\n" +
               ".container{max-width:680px;margin:30px auto;background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 8px 30px rgba(0,0,0,0.10);border:1px solid #e2e8f0;}\n" +
               ".header{background:linear-gradient(135deg," + COLOR1 + "," + COLOR2 + ");padding:30px 20px;text-align:center;color:#fff;}\n" +
               ".header h1{margin:0;font-size:26px;font-weight:800;letter-spacing:3px;text-transform:uppercase;}\n" +
               ".header p{margin:4px 0 0;font-size:13px;letter-spacing:0.5px;opacity:0.75;}\n" +
               ".content{padding:30px 28px;}\n" +
               ".card-title{font-size:22px;font-weight:700;margin-top:0;margin-bottom:18px;color:" + COLOR1 + ";border-bottom:2px solid #fee2e2;padding-bottom:8px;}\n" +
               ".ok-card{background:linear-gradient(145deg,#f8fafc,#e8edf2);border:1px solid #cbd5e1;border-top:5px solid " + COLOR1 + ";border-radius:14px;padding:26px 28px;text-align:center;margin-bottom:20px;}\n" +
               ".ok-icon{display:block;font-size:52px;color:" + COLOR1 + ";line-height:1.1;margin-bottom:10px;}\n" +
               ".ok-tit{display:block;font-size:22px;font-weight:800;color:" + COLOR1 + ";letter-spacing:3px;text-transform:uppercase;margin-bottom:12px;}\n" +
               ".ok-msg{display:block;font-size:18px;color:#374151;line-height:1.65;margin-bottom:4px;}\n" +
               ".id-badge{display:inline-block;background:" + COLOR1 + ";color:#fff;padding:8px 24px;border-radius:24px;font-size:22px;font-weight:700;margin-top:14px;letter-spacing:1px;}\n" +
               ".err-card{background:linear-gradient(145deg,#fef2f2,#fee2e2);border:1px solid #fecaca;border-top:5px solid #991b1b;border-radius:14px;padding:26px 28px;text-align:center;margin-bottom:20px;}\n" +
               ".err-icon{display:block;font-size:52px;color:#991b1b;line-height:1.1;margin-bottom:10px;}\n" +
               ".err-tit{display:block;font-size:22px;font-weight:800;color:#991b1b;letter-spacing:3px;text-transform:uppercase;margin-bottom:12px;}\n" +
               ".err-msg{display:block;font-size:17px;color:#7f1d1d;line-height:1.6;}\n" +
               ".lista-hdr{background:#374151;color:#fff;padding:11px 18px;border-radius:8px 8px 0 0;font-size:14px;font-weight:700;text-transform:uppercase;letter-spacing:0.5px;margin-top:4px;}\n" +
               ".lista-wrap{border:1px solid #e2e8f0;border-top:none;border-radius:0 0 10px 10px;overflow-x:auto;margin-bottom:20px;}\n" +
               ".table-pre{font-family:'Courier New',Courier,monospace;font-size:16px;background:#f8fafc;padding:18px;white-space:pre;color:#1e293b;line-height:1.6;margin:0;}\n" +
               ".footer{background:#f8fafc;padding:20px;text-align:center;font-size:14px;color:#64748b;border-top:1px solid #e2e8f0;}\n" +
               "</style>\n</head>\n<body>\n" +
               "<div class=\"container\">\n" +
               "<div class=\"header\"><h1>RAO MOTOS</h1><div style=\"width:40px;height:2px;background:rgba(255,255,255,0.30);margin:10px auto 8px;border-radius:1px;\"></div><p>Registro de Ventas</p></div>\n" +
               "<div class=\"content\">" + contenido + "</div>\n" +
               "<div class=\"footer\"><strong>Grupo 02 &mdash; Tecnolog&iacute;a Web (UAGRM)</strong><br>Correo autom&aacute;tico &mdash; no responder directamente.</div>\n" +
               "</div>\n</body>\n</html>";
    }
}
