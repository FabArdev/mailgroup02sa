package org.mailgrupo02.presentacion.email;

public class PProductos {

    public static String generarHtml(String comando, String resultado) {
        StringBuilder body = new StringBuilder();
        body.append(PlantillaBase.titulo(describir(comando)));

        boolean esError = resultado.trim().toLowerCase().startsWith("error");

        if (resultado.startsWith("<div style=\"border:1px solid #e2e8f0")) {
            body.append(resultado);
        } else if (resultado.contains("---") || resultado.contains("===")) {
            body.append(PlantillaBase.tablaHtml("&#128230;", resultado));
        } else if (esError) {
            body.append(PlantillaBase.errCard(resultado));
        } else {
            String idStr = PlantillaBase.extraerId(resultado);
            String limpio = idStr != null
                ? resultado.replaceAll("\\s*\\(ID:\\s*\\d+\\)", "").trim()
                : resultado;
            body.append(PlantillaBase.okCard(limpio, idStr));
        }

        return PlantillaBase.envolver("Cat&aacute;logo de Productos", body.toString());
    }

    private static String describir(String cmd) {
        if (cmd == null) return "Productos";
        switch (cmd.toUpperCase()) {
            case "LISTARPRODUCTOS": case "LISTARPRODUCTO": return "Catálogo de Productos";
            case "CREATEPRODUCTO":  return "Registrar Producto";
            case "UPDATEPRODUCTO":  return "Actualizar Producto";
            case "DELETEPRODUCTO":  return "Eliminar Producto";
            case "GETPRODUCTO":     return "Detalle de Producto";
            default:                return "Gestión de Productos";
        }
    }
}
