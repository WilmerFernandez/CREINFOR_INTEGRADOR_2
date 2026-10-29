package utp_integrador_2.DTO;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudDTO {
    private int idUsuario;
    private String tipoSolicitud;
    private String motivo;
}
