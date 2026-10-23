package utp_integrador_2.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionDetalleDTO {

    private int idAsignacion;
    private int idSolicitud;
    private int idColaborador;

    private String tipoSolicitud;
    private String motivo;
    private String estado;

    private String nombres;
    private String apellidos;
    private String rol;

    private boolean esCoordinador;
}