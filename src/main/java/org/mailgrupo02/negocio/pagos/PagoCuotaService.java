package org.mailgrupo02.negocio.pagos;

import org.mailgrupo02.datos.conexion.Conexion;
import org.mailgrupo02.datos.modelo.CreditoM;
import org.mailgrupo02.datos.modelo.PagoCuotaM;
import org.mailgrupo02.datos.modelo.VentaM;
import org.mailgrupo02.datos.modelo.UsuarioM;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PagoCuotaService {

    public String registrarPago(int creditoId, int numeroCuota) throws SQLException {
        PagoCuotaM cuota = new PagoCuotaM().obtenerPorCreditoYNumero(creditoId, numeroCuota);
        if (cuota == null) {
            return "Error: No se encontró la cuota " + numeroCuota + " del crédito " + creditoId;
        }
        if (!"PENDIENTE".equals(cuota.getEstado())) {
            return "La cuota " + numeroCuota + " del crédito " + creditoId + " ya está " + cuota.getEstado();
        }

        String clienteNombre = "";
        String clienteEmail  = "";
        String clienteTelefono = "";

        try {
            CreditoM credito = CreditoM.leer(creditoId);
            VentaM venta = VentaM.leer(credito.getVentaId());
            UsuarioM usuario = UsuarioM.leer(venta.getClienteId());
            clienteNombre    = usuario.getNombre() != null ? usuario.getNombre() : "";
            clienteEmail     = usuario.getEmail()   != null ? usuario.getEmail()   : "";
            clienteTelefono  = usuario.getTelefono() != null ? usuario.getTelefono() : "";
        } catch (Exception e) {
            System.err.println("[PagoCuotaService] Advertencia al obtener datos de cliente: " + e.getMessage());
        }

        String companyTxId = "CUO-" + creditoId + "-" + numeroCuota;
        String descripcion = "Cuota " + numeroCuota + " de credito #" + creditoId;
        double montoReal   = cuota.getMontoCuota();

        String[] qrResult = PagoFacilService.generarQR(
            clienteNombre,
            clienteTelefono,
            clienteEmail,
            companyTxId,
            montoReal,
            descripcion
        );

        if (qrResult == null) {
            return "Error: No se pudo generar el codigo QR de PagoFacil. Intente de nuevo.";
        }

        String pfTxId   = qrResult[0];
        String qrBase64 = qrResult[1];

        PagoFacilService.registrarTransaccion(companyTxId, clienteEmail, montoReal, "cuota;" + pfTxId);

        StringBuilder sb = new StringBuilder();
        sb.append("Solicitud de pago para la Cuota ").append(numeroCuota)
          .append(" del Credito #").append(creditoId).append(" procesada correctamente.<br><br>");
        sb.append("<div style=\"text-align: center; margin: 15px 0;\">");
        sb.append("<strong style=\"color: #1d4ed8; font-size: 15px;\">")
          .append("ESCANEA EL SIGUIENTE QR PARA PAGAR TU CUOTA:")
          .append("</strong><br><br>");
        sb.append("<img src=\"data:image/png;base64,")
          .append(qrBase64.replace("\r", "").replace("\n", "").trim())
          .append("\" style=\"max-width: 250px; border: 4px solid #1d4ed8; border-radius: 12px; ")
          .append("box-shadow: 0 4px 12px rgba(0,0,0,0.1);\"><br><br>");
        sb.append("<span style=\"font-weight: bold; font-size: 16px; color: #1d4ed8;\">")
          .append("Monto a Pagar: ").append(String.format("%.2f", montoReal)).append(" Bs.")
          .append("</span><br>");
        sb.append("<span style=\"color: #6b7280; font-size: 12px;\">")
          .append("Transaccion ID: ").append(companyTxId)
          .append("</span>");
        sb.append("</div>");

        return sb.toString();
    }

    public String confirmarPago(int creditoId, int numeroCuota) throws SQLException {
        PagoCuotaM cuota = new PagoCuotaM().obtenerPorCreditoYNumero(creditoId, numeroCuota);
        if (cuota == null) {
            return "Error: No se encontro la cuota " + numeroCuota + " del credito " + creditoId;
        }
        if (!"PENDIENTE".equals(cuota.getEstado())) {
            return "La cuota " + numeroCuota + " ya esta " + cuota.getEstado();
        }

        cuota.setFechaPago(new Date(System.currentTimeMillis()));
        cuota.setEstado("PAGADO");
        String resultado = cuota.actualizar();

        String companyTxId = "CUO-" + creditoId + "-" + numeroCuota;
        PagoFacilService.removerTransaccion(companyTxId);

        actualizarEstadoCredito(creditoId);

        return resultado;
    }

    private void actualizarEstadoCredito(int creditoId) {
        String sql = "SELECT COUNT(*) FROM pago_cuota WHERE credito_id = ? AND estado = 'PENDIENTE'";
        try (Connection conn = Conexion.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, creditoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String updateSql = "UPDATE credito SET estado = 'CANCELADO', saldo_pendiente = 0 WHERE id = ?";
                    try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
                        ps2.setInt(1, creditoId);
                        ps2.executeUpdate();
                        System.out.println("[PagoCuotaService] Credito #" + creditoId + " marcado como CANCELADO (todas las cuotas pagadas).");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[PagoCuotaService] Error al actualizar estado del credito: " + e.getMessage());
        }
    }

    public String verCuotas(int creditoId) throws SQLException {
        List<PagoCuotaM> lista = new PagoCuotaM().obtenerPorCredito(creditoId);
        return mapear(lista);
    }

    public String listarCreditos() throws SQLException {
        List<CreditoM> lista = CreditoM.obtenerTodos();
        return mapearCreditos(lista);
    }

    /** Cliente: lista sus propios créditos. */
    public String listarCreditosPorCliente(int clienteId) throws SQLException {
        List<CreditoM> lista = CreditoM.obtenerPorCliente(clienteId);
        if (lista.isEmpty()) return "No tienes créditos activos.";
        return mapearCreditos(lista);
    }

    /** Cliente: ve las cuotas de un crédito verificando que le pertenezca. */
    public String verCuotasCliente(int creditoId, int clienteId) throws SQLException {
        CreditoM c = CreditoM.leer(creditoId);
        VentaM v = VentaM.leer(c.getVentaId());
        if (v.getClienteId() != clienteId) return "Error: el crédito no pertenece a tu cuenta.";
        return verCuotas(creditoId);
    }

    private String mapear(List<PagoCuotaM> lista) {
        StringBuilder sb = new StringBuilder();
        String format = "%-5s %-10s %-12s %-12s %-15s %-15s %-10s %-10s%n";
        sb.append(String.format(format, "ID", "Credito", "Nro Cuota", "Monto", "Fecha Venc", "Fecha Pago", "Mora", "Estado"));
        sb.append("----------------------------------------------------------------------------------------------------\r\n");
        for (PagoCuotaM p : lista) {
            sb.append(String.format(format,
                    p.getId(),
                    p.getCreditoId(),
                    p.getNumeroCuota(),
                    String.format("%.2f", p.getMontoCuota()),
                    p.getFechaVencimiento() != null ? p.getFechaVencimiento().toString() : "N/A",
                    p.getFechaPago() != null ? p.getFechaPago().toString() : "N/A",
                    String.format("%.2f", p.getMora()),
                    p.getEstado() != null ? p.getEstado() : "N/A"));
        }
        return sb.toString();
    }

    private String mapearCreditos(List<CreditoM> lista) {
        StringBuilder sb = new StringBuilder();
        String format = "%-5s %-8s %-12s %-12s %-18s %-10s%n";
        sb.append(String.format(format, "ID", "Venta", "Nro Cuotas", "Interes", "Saldo Pendiente", "Estado"));
        sb.append("-----------------------------------------------------------------\r\n");
        for (CreditoM c : lista) {
            sb.append(String.format(format,
                    c.getId(),
                    c.getVentaId(),
                    c.getNumeroCuotas(),
                    String.format("%.2f", c.getTasaInteres()),
                    String.format("%.2f", c.getSaldoPendiente()),
                    c.getEstado() != null ? c.getEstado() : "N/A"));
        }
        return sb.toString();
    }
}
