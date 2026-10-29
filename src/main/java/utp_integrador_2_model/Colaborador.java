package utp_integrador_2_model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Colaborador {
    private int idColaborador;
    private String nombres;
    private String apellidos;
    private String email;
    private String rol;  // analista, diseñador, programador, coordinador
}
