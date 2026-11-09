package web.taskflow.funcionario.dto;

import java.time.LocalDate;

public record FuncionarioResponse(
    Long codigo, 
    String nome, 
    String cpf, 
    String email,
    String telefone, 
    String cargo, 
    LocalDate dataAdmissao
    /* 
    Long codigoEmpresa,
    Long codigoDepartamento
    */) {
}
