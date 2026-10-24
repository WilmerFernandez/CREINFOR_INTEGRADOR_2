package utp_integrador_2.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionDTO {

    private int idSolicitud;
    private LocalDate fechaVencimiento;
    private String observaciones;
    private List<DetalleCotizacionDTO> detalles;
}