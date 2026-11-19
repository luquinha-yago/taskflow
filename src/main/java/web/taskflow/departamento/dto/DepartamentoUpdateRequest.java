package web.taskflow.departamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DepartamentoUpdateRequest(
        @NotBlank(message = "Nome do departamento é obrigatório")
        String nome,
        @NotNull(message = "Código da empresa é obrigatório")
        Long codigo_empresa,
        @NotBlank(message = "Empresa é obrigatório")
        String nome_empresa,
        @NotNull(message = "O código do funcionário coordenador é obrigatório")
        Long codigo_funcionario_coordenador,
        @NotBlank(message = "Funcionário coordenador é obrigatório")
        String nome_funcionario_coordenador,
        String descricao
){}
