package org.mailgrupo02.presentacion.email;

import java.util.regex.*;

public class PPagos {

    private static final String C1 = "#1e3a8a";
    private static final String C2 = "#1e40af";

    public static String generarHtml(String comando, String resultado) {
        StringBuilder body = new StringBuilder();
        body.append("<h2 class=\"card-title\">").append(describir(comando)).append("</h2>");

        boolean esError = resultado.trim().toLowerCase().startsWith("error");

        if (resultado.contains("<img") || resultado.contains("<div style")) {
            // Pasarela QR de PagoFácil
            body.append("<div class=\"qr-card\">")
                .append("<span class=\"qr-icon\">&#128179;</span>")
                .append("<span class=\"qr-tit\">ESCANEA EL C&Oacute;DIGO QR PARA PAGAR</span>")
                .append("<span class=\"qr-msg\">Apunta la cámara al código para completar el pago</span>")
                .append("</div>")
                .append(resultado);
        } else if (resultado.contains("---") || resultado.contains("===")) {
            body.append("<div class=\"lista-hdr\">&#128184; Resultados</div>")
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
        if (cmd == null) return "Pagos";
        switch (cmd.toUpperCase()) {
            case "LISTARCREDITOS":  return "Créditos Activos";
            case "VERCUOTAS":       return "Cuotas del Crédito";
            case "PAGARCUOTA":      return "Pago de Cuota";
            case "REGISTRARPAGO":   return "Registrar Pago";
            default:                return "Gestión de Pagos y Créditos";
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
               ".header{background:linear-gradient(135deg," + C1 + "," + C2 + ");padding:30px 20px;text-align:center;color:#fff;}\n" +
               ".header h1{margin:0;font-size:26px;font-weight:800;letter-spacing:3px;text-transform:uppercase;}\n" +
               ".header p{margin:4px 0 0;font-size:13px;letter-spacing:0.5px;opacity:0.75;}\n" +
               ".content{padding:30px 28px;}\n" +
               ".card-title{font-size:22px;font-weight:700;margin-top:0;margin-bottom:18px;color:" + C1 + ";border-bottom:2px solid #bfdbfe;padding-bottom:8px;}\n" +
               ".ok-card{background:linear-gradient(145deg,#f0f9ff,#e0f2fe);border:1px solid #bae6fd;border-top:5px solid " + C1 + ";border-radius:14px;padding:26px 28px;text-align:center;margin-bottom:20px;}\n" +
               ".ok-icon{display:block;font-size:52px;color:" + C1 + ";line-height:1.1;margin-bottom:10px;}\n" +
               ".ok-tit{display:block;font-size:22px;font-weight:800;color:" + C1 + ";letter-spacing:3px;text-transform:uppercase;margin-bottom:12px;}\n" +
               ".ok-msg{display:block;font-size:18px;color:#374151;line-height:1.65;margin-bottom:4px;}\n" +
               ".id-badge{display:inline-block;background:" + C1 + ";color:#fff;padding:8px 24px;border-radius:24px;font-size:22px;font-weight:700;margin-top:14px;letter-spacing:1px;}\n" +
               ".qr-card{background:linear-gradient(145deg,#dbeafe,#bfdbfe);border:1px solid #93c5fd;border-top:5px solid " + C1 + ";border-radius:14px;padding:20px 28px 16px;text-align:center;margin-bottom:16px;}\n" +
               ".qr-icon{display:block;font-size:44px;line-height:1.1;margin-bottom:8px;}\n" +
               ".qr-tit{display:block;font-size:20px;font-weight:800;color:" + C1 + ";letter-spacing:2px;text-transform:uppercase;margin-bottom:8px;}\n" +
               ".qr-msg{display:block;font-size:16px;color:#1e40af;}\n" +
               ".err-card{background:linear-gradient(145deg,#fef2f2,#fee2e2);border:1px solid #fecaca;border-top:5px solid #991b1b;border-radius:14px;padding:26px 28px;text-align:center;margin-bottom:20px;}\n" +
               ".err-icon{display:block;font-size:52px;color:#991b1b;line-height:1.1;margin-bottom:10px;}\n" +
               ".err-tit{display:block;font-size:22px;font-weight:800;color:#991b1b;letter-spacing:3px;text-transform:uppercase;margin-bottom:12px;}\n" +
               ".err-msg{display:block;font-size:17px;color:#7f1d1d;line-height:1.6;}\n" +
               ".lista-hdr{background:#374151;color:#fff;padding:11px 18px;border-radius:8px 8px 0 0;font-size:14px;font-weight:700;text-transform:uppercase;letter-spacing:0.5px;margin-top:4px;}\n" +
               ".lista-wrap{border:1px solid #e2e8f0;border-top:none;border-radius:0 0 10px 10px;overflow-x:auto;margin-bottom:20px;}\n" +
               ".table-pre{font-family:'Courier New',Courier,monospace;font-size:16px;background:#eff6ff;padding:18px;white-space:pre;color:#1e293b;line-height:1.6;margin:0;}\n" +
               ".footer{background:#f8fafc;padding:20px;text-align:center;font-size:14px;color:#64748b;border-top:1px solid #e2e8f0;}\n" +
               "</style>\n</head>\n<body>\n" +
               "<div class=\"container\">\n" +
               "<div class=\"header\"><h1>RAO MOTOS</h1><div style=\"width:40px;height:2px;background:rgba(255,255,255,0.30);margin:10px auto 8px;border-radius:1px;\"></div><p>Pagos y Cr&eacute;ditos</p></div>\n" +
               "<div class=\"content\">" + contenido + "</div>\n" +
               "<div class=\"footer\"><strong>Grupo 02 &mdash; Tecnolog&iacute;a Web (UAGRM)</strong><br>Correo autom&aacute;tico &mdash; no responder directamente.</div>\n" +
               "</div>\n</body>\n</html>";
    }
}
