package org.mailgrupo02.presentacion.email;

public class PReportes {

    public static String generarHtml(String comando, String resultado) {
        StringBuilder body = new StringBuilder();
        body.append(PlantillaBase.titulo(describir(comando)));

        boolean esError = resultado.trim().toLowerCase().startsWith("error");

        if (resultado.contains("---") || resultado.contains("===")) {
            body.append(PlantillaBase.tablaHtml("&#128202;", resultado));
        } else if (esError) {
            body.append(PlantillaBase.errCard(resultado));
        } else {
            body.append(PlantillaBase.okCard(resultado, null));
        }

        return PlantillaBase.envolver("Reportes Gerenciales", body.toString());
    }

    private static String describir(String cmd) {
        if (cmd == null) return "Reportes";
        switch (cmd.toUpperCase()) {
            case "REPORT_VENTAS_POR_MES":     return "Reporte de Ventas por Mes";
            case "REPORT_VENTAS_POR_CLIENTE": return "Reporte de Ventas por Cliente";
            case "REPORT_MORAS_PENDIENTES":   return "Reporte de Moras Pendientes";
            default:                          return "Reportes Gerenciales";
        }
    }
}
