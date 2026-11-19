package web.taskflow.departamento.dto;

import web.taskflow.common.enums.Status;

import java.time.LocalDate;

public record DepartamentoSearchRequest(
        Long codigo,
        String nome,
        String descricao,
        String funcionario_coordenador,
        String empresa,
        LocalDate data_criacao
){
    public boolean isEmpty() {
        return codigo == null && (nome == null || nome.isBlank())
                && (descricao == null || descricao.isBlank())
                && (funcionario_coordenador == null || funcionario_coordenador.isBlank())
                && (empresa == null || empresa.isBlank())
                && data_criacao == null;
    }
}
