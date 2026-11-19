package web.taskflow.departamento.dto;

import web.taskflow.common.enums.Status;

import java.time.LocalDate;

public record DepartamentoResponse(
        Long codigo,
        String nome,
        String descricao,
        String funcionario_coordenador,
        String empresa,
        Status status,
        LocalDate data_criacao
){}
