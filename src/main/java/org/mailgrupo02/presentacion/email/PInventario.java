package org.mailgrupo02.presentacion.email;

public class PInventario {

    public static String generarHtml(String comando, String resultado) {
        StringBuilder body = new StringBuilder();
        body.append(PlantillaBase.titulo(describir(comando)));

        boolean esError = resultado.trim().toLowerCase().startsWith("error");

        if (resultado.contains("---") || resultado.contains("===")) {
            body.append(PlantillaBase.tablaHtml("&#128200;", resultado));
        } else if (esError) {
            body.append(PlantillaBase.errCard(resultado));
        } else {
            body.append(PlantillaBase.okCard(resultado, null));
        }

        return PlantillaBase.envolver("Control de Inventario", body.toString());
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
}
