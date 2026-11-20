package web.taskflow.departamento.dto;

import java.time.LocalDate;

public record DepartamentoSearchRequest(
        Long codigo,
        String nome,
        Long codigoEmpresa,
        String nomeEmpresa,
        Long codigoFuncionarioCoordenador,
        String nomeFuncionarioCoordenador,
        LocalDate dataCriacaoInicial,
        LocalDate dataCriacaoFinal
){
    public boolean isEmpty() {
        return codigo == null && (nome == null || nome.isBlank())
                && codigoEmpresa == null
                && codigoFuncionarioCoordenador == null
                && dataCriacaoInicial == null
                && dataCriacaoFinal == null;
    }
}
