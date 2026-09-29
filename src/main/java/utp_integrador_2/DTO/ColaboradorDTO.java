package utp_integrador_2.DTO;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColaboradorDTO {
    private int idColaborador;
    private String nombres;
    private String apellidos;
    private String email;
    private String rol;  // analista, diseñador, programador, coordinador
}
