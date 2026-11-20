package web.taskflow.funcionario.dto;

import java.time.LocalDate;

public record FuncionarioSearchRequest(
    Long codigo,
    String nome,
    String cpf,
    Long codigoEmpresa,
    String nomeEmpresa,
    Long codigoDepartamento,
    String nomeDepartamento,
    String cargo,
    LocalDate dataAdmissaoInicial,
    LocalDate dataAdmissaoFinal) {
    public boolean isEmpty() {
        return codigo == null && (nome == null || nome.isBlank())
                && (cpf == null || cpf.isBlank())
                && (cargo == null || cargo.isBlank())
                && codigoEmpresa == null
                && (nomeEmpresa == null || nomeEmpresa.isBlank())
                && codigoDepartamento == null
                && (nomeDepartamento == null || nomeDepartamento.isBlank())
                && dataAdmissaoInicial == null
                && dataAdmissaoFinal == null;
    }
}
