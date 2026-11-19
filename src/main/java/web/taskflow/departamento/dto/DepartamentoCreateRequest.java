package web.taskflow.departamento.dto;

import jakarta.validation.constraints.NotBlank;

public record DepartamentoCreateRequest(
        @NotBlank(message = "Nome do departamento é obrigatório")
        String nome,
        @NotBlank(message = "Empresa é obrigatório")
        String nome_empresa,
        @NotBlank(message = "Funcionário coordenador é obrigatório")
        String nome_funcionario_coordenador,
        String descricao
){}
