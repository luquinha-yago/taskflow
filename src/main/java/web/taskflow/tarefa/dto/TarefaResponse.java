package web.taskflow.tarefa.dto;

import java.time.LocalDate;
import web.taskflow.common.enums.Prioridade;
import web.taskflow.common.enums.StatusTarefa;

public record TarefaResponse(
    Long id,
    String titulo,
    String descricao,
    LocalDate dataCriacao,
    LocalDate dataInicio,
    LocalDate dataPrazo,
    LocalDate dataConclusao,
    StatusTarefa statusTarefa,
    Prioridade prioridade,
    Long codigoEmpresa,
    String nomeEmpresa,
    Long codigoProjeto,
    String nomeProjeto,
    Long codigoFuncionarioResponsavel,
    String nomeFuncionarioResponsavel
) {}
