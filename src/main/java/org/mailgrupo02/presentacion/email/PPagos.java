package org.mailgrupo02.presentacion.email;

public class PPagos {

    public static String generarHtml(String comando, String resultado) {
        StringBuilder body = new StringBuilder();
        body.append(PlantillaBase.titulo(describir(comando)));

        boolean esError = resultado.trim().toLowerCase().startsWith("error");

        if (resultado.contains("<img") || resultado.contains("<div style")) {
            // Respuesta con QR generado
            body.append(PlantillaBase.qrCard(resultado));
        } else if (resultado.contains("---") || resultado.contains("===")) {
            body.append(PlantillaBase.tablaHtml("&#128184;", resultado));
        } else if (esError) {
            body.append(PlantillaBase.errCard(resultado));
        } else {
            String idStr = PlantillaBase.extraerId(resultado);
            String limpio = idStr != null
                ? resultado.replaceAll("\\s*\\(ID:\\s*\\d+\\)", "").trim()
                : resultado;
            body.append(PlantillaBase.okCard(limpio, idStr));
        }

        return PlantillaBase.envolver("Pagos y Cr&eacute;ditos", body.toString());
    }

    private static String describir(String cmd) {
        if (cmd == null) return "Pagos";
        switch (cmd.toUpperCase()) {
            case "LISTARCREDITOS":  return "Créditos Activos";
            case "VERCUOTAS":       return "Cuotas del Crédito";
            case "MISCREDITOS":     return "Mis Créditos";
            case "MISCUOTAS":       return "Mis Cuotas";
            case "PAGARCUOTA":      return "Pago de Cuota";
            case "REGISTRARPAGO":   return "Registrar Pago";
            default:                return "Gestión de Pagos y Créditos";
        }
    }
}
