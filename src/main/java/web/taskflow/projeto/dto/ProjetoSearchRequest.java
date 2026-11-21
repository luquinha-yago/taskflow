package web.taskflow.projeto.dto;

import java.time.LocalDate;
import web.taskflow.common.enums.Prioridade;
import web.taskflow.common.enums.StatusProjeto;

public record ProjetoSearchRequest(
    String nome,
    LocalDate dataCriacaoInicial,
    LocalDate dataCriacaoFinal,
    LocalDate dataConclusaoInicial,
    LocalDate dataConclusaoFinal,
    StatusProjeto statusProjeto,
    Prioridade prioridade,
    Long codigoDepartamento,
    String nomeDepartamento
) {
    public boolean isEmpty() {
            return (nome == null || nome.isBlank())
            && statusProjeto == null
            && prioridade == null
            && codigoDepartamento == null
            && (nomeDepartamento == null || nomeDepartamento.isBlank())
            && dataCriacaoInicial == null
            && dataCriacaoFinal == null
            && dataConclusaoInicial == null
            && dataConclusaoFinal == null;
    }
}
