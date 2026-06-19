package org.mailgrupo02.presentacion.email.controladores;

import org.mailgrupo02.datos.modelo.CreditoM;
import org.mailgrupo02.datos.modelo.VentaM;
import org.mailgrupo02.negocio.pedidos.PedidoService;
import org.mailgrupo02.negocio.usuarios.UsuarioService;
import org.mailgrupo02.negocio.ventas.VentaService;
import org.mailgrupo02.presentacion.email.PPedidos;

import java.util.List;

public class PedidoControlador {

    public static boolean canHandle(String cmd) {
        if (cmd == null) return false;
        switch (cmd.toUpperCase()) {
            case "LISTARPEDIDOS": case "LISTARPEDIDO":
            case "CREARPEDIDO":
            case "DESPACHARPEDIDO":
            case "ANULARPEDIDO":
            case "GETPEDIDO":
            case "PEDIDO":
            case "MISPEDIDOS":
            case "MIPEDIDO":
            case "CANCELARPEDIDO":
            case "PROCESARPEDIDO":
                return true;
            default:
                return false;
        }
    }

    public static String handle(String cmd, List<String> params, String emailRemitente) {
        try {
            PedidoService service = new PedidoService();
            UsuarioService usuarioService = new UsuarioService(null);
            String rawResult;

            switch (cmd.toUpperCase()) {

                // ── Comandos ADMIN ─────────────────────────────────────────────

                case "LISTARPEDIDOS":
                case "LISTARPEDIDO":
                    rawResult = service.obtenerPedidos();
                    break;

                case "GETPEDIDO": {
                    if (params.isEmpty()) return PPedidos.generarHtml(cmd, "Error: se requiere el ID del pedido.");
                    rawResult = formatearPedido(service, Integer.parseInt(params.get(0).trim()));
                    break;
                }

                case "CREARPEDIDO":
                    if (params.isEmpty()) return PPedidos.generarHtml(cmd, "Error: se requiere el ID del cliente.");
                    rawResult = service.crearPedido(Integer.parseInt(params.get(0).trim()));
                    break;

                case "DESPACHARPEDIDO":
                    if (params.isEmpty()) return PPedidos.generarHtml(cmd, "Error: se requiere el ID del pedido.");
                    rawResult = service.despacharPedido(Integer.parseInt(params.get(0).trim()));
                    break;

                case "ANULARPEDIDO":
                    if (params.isEmpty()) return PPedidos.generarHtml(cmd, "Error: se requiere el ID del pedido.");
                    rawResult = service.anularPedido(Integer.parseInt(params.get(0).trim()));
                    break;

                case "PROCESARPEDIDO": {
                    if (params.size() < 3) return PPedidos.generarHtml(cmd,
                        "Error: se requieren mínimo 3 parámetros.\n" +
                        "Contado: PROCESARPEDIDO[pedidoId,CONTADO,metodoPago]\n" +
                        "Crédito: PROCESARPEDIDO[pedidoId,CREDITO,cuotas,tasaInteres,metodoPago]");
                    int pedidoId = Integer.parseInt(params.get(0).trim());
                    String tipo  = params.get(1).trim().toUpperCase();
                    VentaService vs = new VentaService(new VentaM(), new CreditoM());
                    if ("CONTADO".equals(tipo)) {
                        String metodo = params.get(2).trim().toUpperCase();
                        rawResult = vs.procesarDesdePedido(pedidoId, tipo, metodo, 0, 0);
                    } else if ("CREDITO".equals(tipo)) {
                        if (params.size() < 5) return PPedidos.generarHtml(cmd,
                            "Error: crédito requiere 5 parámetros: PROCESARPEDIDO[pedidoId,CREDITO,cuotas,tasaInteres,metodoPago]");
                        int cuotas     = Integer.parseInt(params.get(2).trim());
                        double tasa    = Double.parseDouble(params.get(3).trim());
                        String metodo  = params.get(4).trim().toUpperCase();
                        rawResult = vs.procesarDesdePedido(pedidoId, tipo, metodo, cuotas, tasa);
                    } else {
                        rawResult = "Error: tipo de venta inválido. Use CONTADO o CREDITO.";
                    }
                    break;
                }

                // ── Comandos CLIENTE ───────────────────────────────────────────

                case "PEDIDO": {
                    int clienteId = usuarioService.buscarIdPorEmail(emailRemitente);
                    if (clienteId < 0) return PPedidos.generarHtml(cmd, msgNoRegistrado(emailRemitente));
                    if (params.isEmpty()) return PPedidos.generarHtml(cmd,
                        "Error: especifica los productos.\nEjemplo: PEDIDO[1:2,3:1] (productoId:cantidad)");

                    int[][] items = new int[params.size()][2];
                    for (int i = 0; i < params.size(); i++) {
                        String[] partes = params.get(i).trim().split(":");
                        if (partes.length != 2) return PPedidos.generarHtml(cmd,
                            "Error: formato incorrecto en '" + params.get(i) + "'. Use productoId:cantidad");
                        items[i][0] = Integer.parseInt(partes[0].trim());
                        items[i][1] = Integer.parseInt(partes[1].trim());
                    }
                    rawResult = service.crearConProductos(clienteId, items);
                    break;
                }

                case "MISPEDIDOS": {
                    int clienteId = usuarioService.buscarIdPorEmail(emailRemitente);
                    if (clienteId < 0) return PPedidos.generarHtml(cmd, msgNoRegistrado(emailRemitente));
                    rawResult = service.obtenerPorCliente(clienteId);
                    break;
                }

                case "MIPEDIDO": {
                    if (params.isEmpty()) return PPedidos.generarHtml(cmd, "Error: se requiere el ID del pedido.");
                    int clienteId = usuarioService.buscarIdPorEmail(emailRemitente);
                    if (clienteId < 0) return PPedidos.generarHtml(cmd, msgNoRegistrado(emailRemitente));
                    rawResult = service.leerPedidoCliente(Integer.parseInt(params.get(0).trim()), clienteId);
                    break;
                }

                case "CANCELARPEDIDO": {
                    if (params.isEmpty()) return PPedidos.generarHtml(cmd, "Error: se requiere el ID del pedido.");
                    int clienteId = usuarioService.buscarIdPorEmail(emailRemitente);
                    if (clienteId < 0) return PPedidos.generarHtml(cmd, msgNoRegistrado(emailRemitente));
                    rawResult = service.cancelarPorCliente(Integer.parseInt(params.get(0).trim()), clienteId);
                    break;
                }

                default:
                    rawResult = "Error: Comando de pedidos no soportado.";
            }

            return PPedidos.generarHtml(cmd, rawResult);
        } catch (NumberFormatException e) {
            return PPedidos.generarHtml(cmd, "Error: parámetro numérico inválido — " + e.getMessage());
        } catch (Exception e) {
            return PPedidos.generarHtml(cmd, "Error: " + e.getMessage());
        }
    }

    private static String formatearPedido(PedidoService service, int id) throws Exception {
        var n = service.leerPedido(id);
        return n.toString();
    }

    static String msgNoRegistrado(String email) {
        return "Tu correo <strong>" + email + "</strong> no est&aacute; registrado en el sistema.<br><br>" +
               "Para registrarte env&iacute;a este comando en el asunto:<br>" +
               "<code>CREATEUSUARIO[TuNombre," + email + ",TuContrase&ntilde;a,CLIENTE,TuTel&eacute;fono,TuDirecci&oacute;n]</code><br><br>" +
               "Ejemplo:<br>" +
               "<code>CREATEUSUARIO[Juan Perez," + email + ",clave123,CLIENTE,70000000,Av. Principal 123]</code>";
    }
}
