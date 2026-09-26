package utp_integrador_2_model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asignacion {
    private int idAsignacion;
    private int idSolicitud;
    private int idColaborador;
    private boolean esCoordinador;
}
