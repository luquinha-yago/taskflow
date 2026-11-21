package web.taskflow.projeto.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import web.taskflow.common.enums.Prioridade;
import web.taskflow.common.enums.StatusProjeto;

public record ProjetoCreateRequest(
    @NotBlank(message = "O nome do projeto é obrigatório.")
    String nome,
    String descricao,
    @NotNull(message = "A data de ínicio é obrigatório.")
    LocalDate dataInicio,
    @NotNull(message = "A data de fim é obrigatório.")
    LocalDate dataFim,
    StatusProjeto statusProjeto,
    Prioridade prioridade,
    @NotNull(message = "O departamento desse projeto é obrigatório.")
    Long codigoDepartamento,
    String nomeDepartamento
) {}
