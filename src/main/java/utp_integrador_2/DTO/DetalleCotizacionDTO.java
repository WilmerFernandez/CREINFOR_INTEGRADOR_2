package utp_integrador_2.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCotizacionDTO {

    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
}