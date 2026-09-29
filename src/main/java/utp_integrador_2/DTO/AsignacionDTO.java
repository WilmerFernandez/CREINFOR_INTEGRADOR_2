package utp_integrador_2.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionDTO {
    private int idSolicitud;
    private int idColaborador;
    private boolean esCoordinador;
}
