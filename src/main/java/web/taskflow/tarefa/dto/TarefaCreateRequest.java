package web.taskflow.tarefa.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import web.taskflow.common.enums.Prioridade;
import web.taskflow.common.enums.StatusTarefa;

public record TarefaCreateRequest(
    @NotBlank(message = "O título da tarefa é obrigatório.")
    String titulo,
    String descricao,
    @NotNull(message = "O início da tarefa é obrigatório.")
    LocalDate dataInicio,
    @NotNull(message = "O prazo da tarefa é obrigatório.")
    LocalDate dataPrazo,
    StatusTarefa statusTarefa,
    Prioridade prioridade,
    @NotNull(message = "O projeto dessa tarefa é obrigatório.")
    Long codigoProjeto,
    String nomeProjeto,
    Long codigoFuncionarioResponsavel,
    String nomeFuncionarioResponsavel
) {}
