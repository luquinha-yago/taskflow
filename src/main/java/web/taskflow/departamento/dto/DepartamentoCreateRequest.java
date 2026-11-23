package web.taskflow.departamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DepartamentoCreateRequest(
        @NotBlank(message = "Nome do departamento é obrigatório")
        String nome,
        @NotNull(message = "A empresa desse departamento é obrigatória")
        Long codigoEmpresa,
        String nomeEmpresa,
        Long codigoFuncionarioCoordenador,
        String nomeFuncionarioCoordenador,
        String descricao
){}
