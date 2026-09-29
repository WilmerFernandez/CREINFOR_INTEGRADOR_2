package utp_integrador_2.DTO;


import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActividadDTO {
    private int idAsignacion;
    private Date fecha;
    private String descripcion;
    private double tiempoHoras;
}
