package org.mailgrupo02.presentacion.email;

public class PUsuarios {

    public static String generarHtml(String comando, String resultado) {
        StringBuilder body = new StringBuilder();
        body.append(PlantillaBase.titulo(describir(comando)));

        boolean esError = resultado.trim().toLowerCase().startsWith("error");

        if (resultado.startsWith("<div style=\"border:1px solid #e2e8f0")) {
            body.append(resultado);
        } else if (resultado.contains("---") || resultado.contains("===")) {
            body.append(PlantillaBase.tablaHtml("&#128101;", resultado));
        } else if (esError) {
            body.append(PlantillaBase.errCard(resultado));
        } else {
            String idStr = PlantillaBase.extraerId(resultado);
            String limpio = idStr != null
                ? resultado.replaceAll("\\s*\\(ID:\\s*\\d+\\)", "").trim()
                : resultado;
            body.append(PlantillaBase.okCard(limpio, idStr));
        }

        return PlantillaBase.envolver("Gesti&oacute;n de Usuarios", body.toString());
    }

    private static String describir(String cmd) {
        if (cmd == null) return "Usuarios";
        switch (cmd.toUpperCase()) {
            case "LISTARUSUARIOS": case "LISTARUSUARIO": return "Listado de Usuarios";
            case "CREATEUSUARIO":   return "Crear Usuario";
            case "UPDATEUSUARIO":   return "Actualizar Usuario";
            case "UPDATECLIENTE":   return "Actualizar Datos de Cliente";
            case "DELETEUSUARIO":   return "Eliminar Usuario";
            case "GETUSUARIO":      return "Detalle de Usuario";
            case "WHOAMI":          return "Mi Perfil";
            case "CAMBIARROL":      return "Cambiar Rol de Usuario";
            default:                return "Gestión de Usuarios";
        }
    }
}
