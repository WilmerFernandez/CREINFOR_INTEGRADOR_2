package utp_integrador_2_model;

import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Actividad {
    private int idActividad;
    private int idAsignacion;
    private Date fecha;
    private String descripcion;
    private double tiempoHoras;
}
