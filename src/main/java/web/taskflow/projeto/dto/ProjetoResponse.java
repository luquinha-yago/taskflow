package web.taskflow.projeto.dto;

import java.time.LocalDate;
import web.taskflow.common.enums.Prioridade;
import web.taskflow.common.enums.StatusProjeto;

public record ProjetoResponse(
    Long codigo,
    String nome,
    String descricao,
    LocalDate dataCriacao,
    LocalDate dataInicio,
    LocalDate dataFim,
    LocalDate dataConclusao,
    StatusProjeto statusProjeto,
    Prioridade prioridade,
    Long codigoDepartamento,
    String nomeDepartamento
) {}
