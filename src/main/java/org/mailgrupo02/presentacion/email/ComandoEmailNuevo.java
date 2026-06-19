package org.mailgrupo02.presentacion.email;

import org.mailgrupo02.datos.modelo.UsuarioM;
import org.mailgrupo02.presentacion.email.controladores.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ComandoEmailNuevo {

    public ComandoEmailNuevo() throws SQLException {}

    // ── Punto de entrada ─────────────────────────────────────────────────────

    public String evaluarYEjecutar(String asunto, String emailRemitente) {
        try {
            if (asunto == null || asunto.trim().isEmpty()) {
                return PAyuda.generarError("El asunto del correo está vacío.");
            }
            asunto = asunto.trim();

            if (asunto.equalsIgnoreCase("HELP")) {
                return PAyuda.generarHtml();
            }

            if (asunto.equalsIgnoreCase("WHOAMI")) {
                return whoAmI(emailRemitente);
            }

            String[] parsed = parsearAsunto(asunto);
            String cmd = parsed[0].toUpperCase();
            List<String> params = parsed[1].isEmpty()
                    ? Collections.emptyList()
                    : Arrays.asList(parsed[1].replaceAll("\"", "").split(",\\s*"));

            // Despachar al controlador correspondiente
            if (UsuarioControlador.canHandle(cmd))    return UsuarioControlador.handle(cmd, params);
            if (ProveedorControlador.canHandle(cmd))  return ProveedorControlador.handle(cmd, params);
            if (ProductoControlador.canHandle(cmd))   return ProductoControlador.handle(cmd, params);
            if (VentaControlador.canHandle(cmd))      return VentaControlador.handle(cmd, params);
            if (CompraControlador.canHandle(cmd))     return CompraControlador.handle(cmd, params);
            if (PedidoControlador.canHandle(cmd))     return PedidoControlador.handle(cmd, params, emailRemitente);
            if (InventarioControlador.canHandle(cmd)) return InventarioControlador.handle(cmd, params);
            if (PagoControlador.canHandle(cmd))       return PagoControlador.handle(cmd, params, emailRemitente);
            if (ReporteControlador.canHandle(cmd))    return ReporteControlador.handle(cmd, params);

            return PAyuda.generarError(
                "Comando no reconocido: <strong>" + asunto + "</strong><br>" +
                "Envía <strong>HELP</strong> en el asunto para ver todos los comandos disponibles.");

        } catch (Exception e) {
            return PAyuda.generarError("Error inesperado al procesar el comando: " + e.getMessage());
        }
    }

    private static String whoAmI(String emailRemitente) {
        try {
            UsuarioM u = UsuarioM.buscarPorEmail(emailRemitente);
            if (u == null) {
                String cuerpo =
                    "<h2 style=\"font-size:18px;font-weight:700;color:#111827;margin:0 0 14px 0;padding-bottom:10px;border-bottom:2px solid #e5e7eb;\">&#128100; &iquest;Qui&eacute;n eres?</h2>" +
                    "<div style=\"background-color:#fee2e2;border:1px solid #fca5a5;border-radius:8px;padding:16px 20px;color:#991b1b;font-size:15px;\">" +
                    "<strong>No est&aacute;s registrado</strong><br>" +
                    "El correo <strong>" + emailRemitente + "</strong> no tiene cuenta en el sistema.<br><br>" +
                    "Para registrarte env&iacute;a:<br>" +
                    "<code>CREATEUSUARIO[TuNombre," + emailRemitente + ",TuContrase&ntilde;a,CLIENTE,TuTel&eacute;fono,TuDirecci&oacute;n]</code>" +
                    "</div>";
                return PlantillaBase.envolver("Mi Perfil", cuerpo);
            }
            String cuerpo =
                "<h2 style=\"font-size:18px;font-weight:700;color:#111827;margin:0 0 14px 0;padding-bottom:10px;border-bottom:2px solid #e5e7eb;\">&#128100; Tu perfil</h2>" +
                "<table style=\"width:100%;border-collapse:collapse;font-size:17px;\">" +
                fila("ID",            String.valueOf(u.getId())) +
                fila("Nombre",        nvl(u.getNombre())) +
                fila("Email",         nvl(u.getEmail())) +
                fila("Rol",           nvl(u.getRol())) +
                fila("Tel&eacute;fono", nvl(u.getTelefono())) +
                fila("Direcci&oacute;n", nvl(u.getDireccion())) +
                fila("Estado",        u.isActivo() ? "&#10004; Activo" : "Inactivo") +
                fila("Registro",      u.getFechaReg() != null ? u.getFechaReg().toString() : "—") +
                "</table>";
            return PlantillaBase.envolver("Mi Perfil", cuerpo);
        } catch (Exception e) {
            return PAyuda.generarError("No se pudo obtener tu perfil: " + e.getMessage());
        }
    }

    private static String fila(String label, String val) {
        return "<tr style=\"border-bottom:1px solid #e5e7eb;\">" +
               "<td style=\"padding:10px 14px;color:#6b7280;font-weight:600;width:35%;\">" + label + "</td>" +
               "<td style=\"padding:10px 14px;color:#111827;\">" + val + "</td>" +
               "</tr>";
    }

    private static String nvl(String v) { return v != null ? v : "—"; }

    private static String[] parsearAsunto(String asunto) {
        int inicio = asunto.indexOf('[');
        if (inicio == -1) return new String[]{asunto, ""};
        String cmd = asunto.substring(0, inicio).trim();
        int fin = asunto.lastIndexOf(']');
        String params = (fin > inicio) ? asunto.substring(inicio + 1, fin) : "";
        return new String[]{cmd, params};
    }
}
