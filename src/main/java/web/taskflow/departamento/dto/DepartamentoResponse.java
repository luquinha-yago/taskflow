package web.taskflow.departamento.dto;

import java.time.LocalDate;

public record DepartamentoResponse(
        Long codigo,
        String nome,
        String descricao,
        LocalDate data_criacao,
        Long codigoEmpresa,
        String nomeEmpresa,
        Long codigoFuncionarioCoordenador,
        String nomeFuncionarioCoordenador
){}
