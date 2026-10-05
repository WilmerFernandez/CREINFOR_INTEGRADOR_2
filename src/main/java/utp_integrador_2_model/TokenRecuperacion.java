package utp_integrador_2_model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TokenRecuperacion {

    private int idToken;
    private int idUsuario;
    private String token;
    private LocalDateTime fechaExpiracion;
    private boolean usado;
}