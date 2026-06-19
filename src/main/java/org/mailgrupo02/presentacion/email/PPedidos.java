package org.mailgrupo02.presentacion.email;

public class PPedidos {

    public static String generarHtml(String comando, String resultado) {
        StringBuilder body = new StringBuilder();
        body.append(PlantillaBase.titulo(describir(comando)));

        boolean esError = resultado.trim().toLowerCase().startsWith("error");

        if (resultado.contains("---") || resultado.contains("===")) {
            body.append(PlantillaBase.tablaHtml("&#128221;", resultado));
        } else if (esError) {
            body.append(PlantillaBase.errCard(resultado));
        } else {
            String idStr = PlantillaBase.extraerId(resultado);
            String limpio = idStr != null
                ? resultado.replaceAll("\\s*\\(ID:\\s*\\d+\\)", "").trim()
                : resultado;
            body.append(PlantillaBase.okCard(limpio, idStr));
        }

        return PlantillaBase.envolver("Gesti&oacute;n de Pedidos", body.toString());
    }

    private static String describir(String cmd) {
        if (cmd == null) return "Pedidos";
        switch (cmd.toUpperCase()) {
            case "LISTARPEDIDOS": case "LISTARPEDIDO": return "Registro de Pedidos";
            case "PEDIDO":            return "Crear Pedido";
            case "MISPEDIDOS":        return "Mis Pedidos";
            case "MIPEDIDO":          return "Detalle de Mi Pedido";
            case "CANCELARPEDIDO":    return "Cancelar Pedido";
            case "PROCESARPEDIDO":    return "Procesar Pedido";
            case "DESPACHARPEDIDO":   return "Despachar Pedido";
            case "ANULARPEDIDO":      return "Anular Pedido";
            case "GETPEDIDO":         return "Detalle de Pedido";
            default:                  return "Gestión de Pedidos";
        }
    }
}
