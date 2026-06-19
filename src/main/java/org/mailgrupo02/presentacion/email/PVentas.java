package org.mailgrupo02.presentacion.email;

public class PVentas {

    public static String generarHtml(String comando, String resultado) {
        StringBuilder body = new StringBuilder();
        body.append(PlantillaBase.titulo(describir(comando)));

        boolean esError = resultado.trim().toLowerCase().startsWith("error");

        if (resultado.contains("---") || resultado.contains("===")) {
            body.append(PlantillaBase.tablaHtml("&#128202;", resultado));
        } else if (esError) {
            body.append(PlantillaBase.errCard(resultado));
        } else {
            String idStr = PlantillaBase.extraerId(resultado);
            String limpio = idStr != null
                ? resultado.replaceAll("\\s*\\(ID:\\s*\\d+\\)", "").trim()
                : resultado;
            body.append(PlantillaBase.okCard(limpio, idStr));
        }

        return PlantillaBase.envolver("Registro de Ventas", body.toString());
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
}
