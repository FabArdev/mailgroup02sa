package org.mailgrupo02.presentacion.email;

public class PInventario {

    private static final String COLOR1 = "#b91c1c";
    private static final String COLOR2 = "#7f1d1d";

    public static String generarHtml(String comando, String resultado) {
        StringBuilder body = new StringBuilder();
        body.append("<h2 class=\"card-title\">").append(describir(comando)).append("</h2>");

        boolean esError = resultado.trim().toLowerCase().startsWith("error");

        if (resultado.contains("---") || resultado.contains("===")) {
            body.append("<div class=\"table-container\"><pre class=\"table-pre\">")
                .append(escapar(resultado))
                .append("</pre></div>");
        } else {
            String cls = esError ? "alert-error" : "alert-success";
            String titulo = esError ? "ERROR EN LA OPERACIÓN" : "OPERACIÓN EXITOSA";
            body.append("<div class=\"alert ").append(cls).append("\">")
                .append("<strong>").append(titulo).append("</strong><br>")
                .append(resultado.replace("\r\n", "<br>").replace("\n", "<br>"))
                .append("</div>");
        }

        return construirPlantillaBase(body.toString());
    }

    private static String describir(String cmd) {
        if (cmd == null) return "Inventario";
        switch (cmd.toUpperCase()) {
            case "VERINVENTARIO":     return "Estado del Inventario";
            case "REGISTRARINGRESO":  return "Registrar Ingreso de Stock";
            case "REGISTRAREGRESO":   return "Registrar Egreso de Stock";
            default:                  return "Gestión de Inventario";
        }
    }

    private static String escapar(String txt) {
        return txt.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String construirPlantillaBase(String contenido) {
        return "<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"utf-8\">\n<style>\n" +
               "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;background:#f1f5f9;color:#1e293b;margin:0;padding:0;}\n" +
               ".container{max-width:680px;margin:30px auto;background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 8px 30px rgba(0,0,0,0.10);border:1px solid #e2e8f0;}\n" +
               ".header{background:linear-gradient(135deg," + COLOR1 + "," + COLOR2 + ");padding:30px 20px;text-align:center;color:#fff;}\n" +
               ".header h1{margin:0;font-size:26px;font-weight:800;letter-spacing:3px;text-transform:uppercase;}\n" +
               ".header p{margin:0;font-size:12px;letter-spacing:0.5px;opacity:0.75;}\n" +
               ".content{padding:30px 28px;}\n" +
               ".card-title{font-size:20px;font-weight:700;margin-top:0;margin-bottom:16px;color:" + COLOR1 + ";border-bottom:2px solid #fee2e2;padding-bottom:8px;}\n" +
               ".alert{padding:16px;border-radius:12px;margin-bottom:20px;font-size:16px;line-height:1.6;}\n" +
               ".alert-success{background:#f0fdf4;border:1px solid #bbf7d0;color:#166534;}\n" +
               ".alert-error{background:#fef2f2;border:1px solid #fecaca;color:#991b1b;}\n" +
               ".table-container{overflow-x:auto;margin-top:8px;}\n" +
               ".table-pre{font-family:'Courier New',Courier,monospace;font-size:14px;background:#f8fafc;padding:16px;border-radius:10px;border:1px solid #e2e8f0;white-space:pre;color:#1e293b;line-height:1.55;margin:0;}\n" +
               ".footer{background:#f8fafc;padding:20px;text-align:center;font-size:12px;color:#64748b;border-top:1px solid #e2e8f0;}\n" +
               "</style>\n</head>\n<body>\n" +
               "<div class=\"container\">\n" +
               "<div class=\"header\"><h1>RAO MOTOS</h1><div style=\"width:40px;height:2px;background:rgba(255,255,255,0.30);margin:10px auto 8px;border-radius:1px;\"></div><p>Control de Inventario</p></div>\n" +
               "<div class=\"content\">" + contenido + "</div>\n" +
               "<div class=\"footer\"><strong>Grupo 02 &mdash; Tecnología Web (UAGRM)</strong><br>Correo automático &mdash; no responder directamente.</div>\n" +
               "</div>\n</body>\n</html>";
    }
}
