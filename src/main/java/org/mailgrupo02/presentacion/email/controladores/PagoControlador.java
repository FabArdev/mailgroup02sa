package org.mailgrupo02.presentacion.email.controladores;

import org.mailgrupo02.negocio.pagos.PagoCuotaService;
import org.mailgrupo02.negocio.usuarios.UsuarioService;
import org.mailgrupo02.presentacion.email.PPagos;

import java.util.List;

public class PagoControlador {

    public static boolean canHandle(String cmd) {
        if (cmd == null) return false;
        switch (cmd.toUpperCase()) {
            case "LISTARCREDITOS":
            case "VERCUOTAS":
            case "PAGARCUOTA":
            case "REGISTRARPAGO":
            case "MISCREDITOS":
            case "MISCUOTAS":
                return true;
            default:
                return false;
        }
    }

    public static String handle(String cmd, List<String> params, String emailRemitente) {
        try {
            PagoCuotaService service = new PagoCuotaService();
            UsuarioService usuarioService = new UsuarioService(null);
            String rawResult;

            switch (cmd.toUpperCase()) {

                // ── Admin ────────────────────────────────────────────────────────
                case "LISTARCREDITOS":
                    rawResult = service.listarCreditos();
                    break;

                case "VERCUOTAS":
                    if (params.isEmpty()) return PPagos.generarHtml(cmd, "Error: se requiere el ID del crédito.");
                    rawResult = service.verCuotas(Integer.parseInt(params.get(0).trim()));
                    break;

                // ── Cliente ──────────────────────────────────────────────────────
                case "MISCREDITOS": {
                    int clienteId = usuarioService.buscarIdPorEmail(emailRemitente);
                    if (clienteId < 0) return PPagos.generarHtml(cmd, PedidoControlador.msgNoRegistrado(emailRemitente));
                    rawResult = service.listarCreditosPorCliente(clienteId);
                    break;
                }

                case "MISCUOTAS": {
                    if (params.isEmpty()) return PPagos.generarHtml(cmd, "Error: se requiere el ID del crédito.");
                    int clienteId = usuarioService.buscarIdPorEmail(emailRemitente);
                    if (clienteId < 0) return PPagos.generarHtml(cmd, PedidoControlador.msgNoRegistrado(emailRemitente));
                    rawResult = service.verCuotasCliente(Integer.parseInt(params.get(0).trim()), clienteId);
                    break;
                }

                // ── Ambos ────────────────────────────────────────────────────────
                case "PAGARCUOTA":
                case "REGISTRARPAGO":
                    if (params.size() < 2) return PPagos.generarHtml(cmd, "Error: se requieren 2 parámetros [creditoId,numeroCuota].");
                    rawResult = service.registrarPago(
                        Integer.parseInt(params.get(0).trim()),
                        Integer.parseInt(params.get(1).trim()));
                    break;

                default:
                    rawResult = "Error: Comando de pagos no soportado.";
            }

            return PPagos.generarHtml(cmd, rawResult);
        } catch (Exception e) {
            return PPagos.generarHtml(cmd, "Error: " + e.getMessage());
        }
    }
}
