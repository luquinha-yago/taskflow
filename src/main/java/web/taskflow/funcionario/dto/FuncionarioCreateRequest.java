package web.taskflow.funcionario.dto;

import java.time.LocalDate;
import org.hibernate.validator.constraints.br.CPF;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FuncionarioCreateRequest(
    @NotBlank (message = "O nome é obrigatório")
    String nome,
    @NotNull(message = "O cpf é obrigatório")
    @CPF(message = "O cpf deve ser válido.")
    String cpf,
    String email,
    String telefone,
    @NotNull(message = "O cargo é obrigatório")
    String cargo,
    @NotNull(message = "A data é obrigatória")
    LocalDate dataAdmissao,
    @NotNull(message = "A empresa desse funcionário é obrigatória")
    Long codigoEmpresa,
    String nomeEmpresa,
    @NotNull(message = "O departamento desse funcionário é obrigatório")
    Long codigoDepartamento,
    String nomeDepartamento) {}
