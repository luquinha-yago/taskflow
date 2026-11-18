package web.taskflow.empresa.dto;

import java.time.LocalDate;

public record EmpresaSearchRequest(
    Long codigo,
    String razaoSocial, 
    String nomeFantasia, 
    String cnpj,
    LocalDate dataCadastroInicial, 
    LocalDate dataCadastroFinal) 
    {
    public boolean isEmpty() {
        return codigo == null && (razaoSocial == null || razaoSocial.isBlank())
                && (nomeFantasia == null || nomeFantasia.isBlank())
                && (cnpj == null || cnpj.isBlank()) && dataCadastroInicial == null
                && dataCadastroFinal == null;
    }
}
