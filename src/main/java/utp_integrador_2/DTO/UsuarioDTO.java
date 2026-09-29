package utp_integrador_2.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private int idUsuario;
    private String nombres;
    private String apellidos;
    private String email;
    private String contrasena;   
    private String tipoUsuario;  // cliente, colaborador, admin
}

