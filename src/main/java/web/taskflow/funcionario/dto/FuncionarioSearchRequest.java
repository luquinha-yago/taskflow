package web.taskflow.funcionario.dto;

import java.time.LocalDate;

public record FuncionarioSearchRequest(
    Long codigo,
    String nome,
    String cpf,
    String cargo,
    LocalDate dataAdmissaoInicial,
    LocalDate dataAdmissaoFinal
    /* 
    Empresa empresa,
    Departamento departamento,*/) {
    public boolean isEmpty() {
        return codigo == null && (nome == null || nome.isBlank())
                && (cpf == null || cpf.isBlank())
                && (cargo == null || cargo.isBlank())
                && dataAdmissaoInicial == null
                && dataAdmissaoFinal == null;
    }
}
