package web.taskflow.empresa.dto;

import jakarta.validation.constraints.NotNull;

public record EmpresaUpdateRequest(
    @NotNull(message =  "A razão social é obrigatória.")
    String razaoSocial,
    @NotNull(message =  "O nome fantasia é obrigatório.")
    String nomeFantasia,
    @NotNull(message =  "O CNPJ é obrigatório.")
    String cnpj,
    String email,
    String telefone,
    String endereco
) {}
