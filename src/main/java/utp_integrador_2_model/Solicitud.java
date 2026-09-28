package utp_integrador_2_model;

import lombok.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Solicitud {
    private int idSolicitud;
    private int idUsuario;
    private String tipoSolicitud;
    private String motivo;
    private Date fechaRegistro;
    private String estado;  // pendiente, en proceso, finalizada
}
