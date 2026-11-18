package web.taskflow.empresa.dto;

import java.time.LocalDate;

public record EmpresaResponse(
    Long codigo, 
    String razaoSocial,
    String nomeFantasia,
    String cnpj,
    String email,
    String telefone,
    String endereco,
    LocalDate dataCadastro
) {}
