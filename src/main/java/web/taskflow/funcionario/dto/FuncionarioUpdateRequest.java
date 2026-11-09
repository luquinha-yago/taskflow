package web.taskflow.funcionario.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;

public record FuncionarioUpdateRequest(
    @NotNull(message = "O nome é obrigatório")
    String nome,
    @NotNull(message = "O cpf é obrigatório")
    String cpf,
    String email,
    String telefone,
    @NotNull(message = "O cargo é obrigatório")
    String cargo,
    @NotNull(message = "A data é obrigatória")
    LocalDate dataAdmissao
    /* 
    @NotNull(message = "A empresa é obrigatória")
    Long codigoEmpresa,
    @NotNull(message = "O departamento é obrigatório")
    Long codigoDepartamento*/
) {}
