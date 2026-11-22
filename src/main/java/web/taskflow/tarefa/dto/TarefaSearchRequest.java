package web.taskflow.tarefa.dto;

import java.time.LocalDate;
import web.taskflow.common.enums.Prioridade;
import web.taskflow.common.enums.StatusTarefa;

public record TarefaSearchRequest(
    String titulo,
    LocalDate dataCriacaoInicial,
    LocalDate dataCriacaoFinal,
    LocalDate dataPrazoInicial,
    LocalDate dataPrazoFinal,
    LocalDate dataConclusaoInicial,
    LocalDate dataConclusaoFinal,
    StatusTarefa statusTarefa,
    Prioridade prioridade,
    Long codigoProjeto,
    String nomeProjeto,
    Long codigoFuncionarioResponsavel,
    String nomeFuncionarioResponsavel
) {
    public boolean isEmpty() {
        return (titulo == null || titulo.isBlank())
            && statusTarefa == null
            && prioridade == null
            && codigoProjeto == null
            && (nomeProjeto == null || nomeProjeto.isBlank())
            && codigoFuncionarioResponsavel == null
            && (nomeFuncionarioResponsavel == null || nomeFuncionarioResponsavel.isBlank())
            && dataCriacaoInicial == null
            && dataCriacaoFinal == null
            && dataPrazoInicial == null
            && dataPrazoFinal == null
            && dataConclusaoInicial == null
            && dataConclusaoFinal == null;
    }
}
