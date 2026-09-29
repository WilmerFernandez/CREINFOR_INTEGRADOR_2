package utp_integrador_2.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class SolicitudDetalleDTO {
    private int idSolicitud;
    private String tipoSolicitud;
    private String motivo;
    private LocalDateTime fechaRegistro;
    private String estado;
    private String nombreCliente;
    private String apellidoCliente;
    private String emailCliente;
    private String nombreColaboradorAsignado;
    private String apellidoColaboradorAsignado;
    private String rolColaboradorAsignado;
    
    // *** AGREGAR ESTE MÉTODO GETTER AUXILIAR PERSONALIZADO ***
    public Date getFechaRegistroAsUtilDate() {
        if (this.fechaRegistro == null) {
            return null;
        }
        // Convertir LocalDateTime a Instant y luego a java.util.Date
        // Usa tu zona horaria local o la zona horaria del servidor (systemDefault())
        return Date.from(this.fechaRegistro.atZone(ZoneId.systemDefault()).toInstant());
    }
}