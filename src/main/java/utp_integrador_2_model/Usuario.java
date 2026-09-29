package utp_integrador_2_model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    private int idUsuario;
    private String nombres;
    private String apellidos;
    private String email;
    private String contrasena;   
    private String tipoUsuario;
}
