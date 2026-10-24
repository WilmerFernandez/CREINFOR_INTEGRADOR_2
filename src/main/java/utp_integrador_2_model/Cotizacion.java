package utp_integrador_2_model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cotizacion {

    private int idCotizacion;
    private int idSolicitud;
    private LocalDateTime fechaEmision;
    private LocalDate fechaVencimiento;
    private String estado;
    private String moneda;
    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal total;
    private String observaciones;
    private LocalDateTime fechaRespuesta;
}