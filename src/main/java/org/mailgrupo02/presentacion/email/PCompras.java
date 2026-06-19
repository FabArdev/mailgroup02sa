package org.mailgrupo02.presentacion.email;

public class PCompras {

    public static String generarHtml(String comando, String resultado) {
        StringBuilder body = new StringBuilder();
        body.append(PlantillaBase.titulo(describir(comando)));

        boolean esError = resultado.trim().toLowerCase().startsWith("error");

        if (resultado.contains("---") || resultado.contains("===")) {
            body.append(PlantillaBase.tablaHtml("&#128666;", resultado));
        } else if (esError) {
            body.append(PlantillaBase.errCard(resultado));
        } else {
            String idStr = PlantillaBase.extraerId(resultado);
            String limpio = idStr != null
                ? resultado.replaceAll("\\s*\\(ID:\\s*\\d+\\)", "").trim()
                : resultado;
            body.append(PlantillaBase.okCard(limpio, idStr));
        }

        return PlantillaBase.envolver("Gesti&oacute;n de Compras", body.toString());
    }

    private static String describir(String cmd) {
        if (cmd == null) return "Compras";
        switch (cmd.toUpperCase()) {
            case "LISTARCOMPRAS": case "LISTARCOMPRA": return "Registro de Compras";
            case "CREARCOMPRA":   return "Registrar Compra a Proveedor";
            case "ANULARCOMPRA":  return "Anular Compra";
            case "GETCOMPRA":     return "Detalle de Compra";
            default:              return "Gestión de Compras";
        }
    }
}
