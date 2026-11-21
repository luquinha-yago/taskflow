package web.taskflow.tarefa;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import web.taskflow.common.enums.Status;
import web.taskflow.funcionario.Funcionario;
import web.taskflow.funcionario.Funcionario_;
import web.taskflow.projeto.Projeto;
import web.taskflow.projeto.Projeto_;
import web.taskflow.tarefa.dto.TarefaSearchRequest;

public class TarefaSpecifications {

    public static Specification<Tarefa> comFiltros(
            TarefaSearchRequest filtro) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicados = new ArrayList<>();

            // Filtro fixo: somente tarefas ativas
            predicados.add(
                criteriaBuilder.equal(
                    root.get(Tarefa_.status),
                    Status.ATIVO
                )
            );

            // Filtros dinâmicos
            if (filtro != null) {

                // Título: busca parcial e case-insensitive
                if (filtro.titulo() != null && !filtro.titulo().isBlank()) {

                    String titulo = "%"
                            + filtro.titulo().trim().toLowerCase(Locale.ROOT)
                            + "%";

                    predicados.add(
                        criteriaBuilder.like(
                            criteriaBuilder.lower(
                                root.get(Tarefa_.titulo)
                            ),
                            titulo
                        )
                    );
                }

                // Data de criação inicial: maior ou igual
                if (filtro.dataCriacaoInicial() != null) {

                    predicados.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                            root.get(Tarefa_.dataCriacao),
                            filtro.dataCriacaoInicial()
                        )
                    );
                }

                // Data de criação final: menor ou igual
                if (filtro.dataCriacaoFinal() != null) {

                    predicados.add(
                        criteriaBuilder.lessThanOrEqualTo(
                            root.get(Tarefa_.dataCriacao),
                            filtro.dataCriacaoFinal()
                        )
                    );
                }

                // Data de prazo inicial: maior ou igual
                if (filtro.dataPrazoInicial() != null) {

                    predicados.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                            root.get(Tarefa_.dataPrazo),
                            filtro.dataPrazoInicial()
                        )
                    );
                }

                // Data de prazo final: menor ou igual
                if (filtro.dataPrazoFinal() != null) {

                    predicados.add(
                        criteriaBuilder.lessThanOrEqualTo(
                            root.get(Tarefa_.dataPrazo),
                            filtro.dataPrazoFinal()
                        )
                    );
                }

                // Data de conclusão inicial: maior ou igual
                if (filtro.dataConclusaoInicial() != null) {

                    predicados.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                            root.get(Tarefa_.dataConclusao),
                            filtro.dataConclusaoInicial()
                        )
                    );
                }

                // Data de conclusão final: menor ou igual
                if (filtro.dataConclusaoFinal() != null) {

                    predicados.add(
                        criteriaBuilder.lessThanOrEqualTo(
                            root.get(Tarefa_.dataConclusao),
                            filtro.dataConclusaoFinal()
                        )
                    );
                }

                // Status da tarefa
                if (filtro.statusTarefa() != null) {

                    predicados.add(
                        criteriaBuilder.equal(
                            root.get(Tarefa_.statusTarefa),
                            filtro.statusTarefa()
                        )
                    );
                }

                // Prioridade
                if (filtro.prioridade() != null) {

                    predicados.add(
                        criteriaBuilder.equal(
                            root.get(Tarefa_.prioridade),
                            filtro.prioridade()
                        )
                    );
                }

                // Filtros relacionados ao projeto
                if (filtro.codigoProjeto() != null
                        || (filtro.nomeProjeto() != null
                        && !filtro.nomeProjeto().isBlank())) {

                    Join<Tarefa, Projeto> projetoJoin =
                            root.join(
                                Tarefa_.projeto,
                                JoinType.LEFT
                            );

                    // Código do projeto
                    if (filtro.codigoProjeto() != null) {

                        predicados.add(
                            criteriaBuilder.equal(
                                projetoJoin.get(Projeto_.codigo),
                                filtro.codigoProjeto()
                            )
                        );
                    }

                    // Nome do projeto
                    if (filtro.nomeProjeto() != null
                            && !filtro.nomeProjeto().isBlank()) {

                        String nomeProjeto = "%"
                                + filtro.nomeProjeto().trim()
                                    .toLowerCase(Locale.ROOT)
                                + "%";

                        predicados.add(
                            criteriaBuilder.like(
                                criteriaBuilder.lower(
                                    projetoJoin.get(Projeto_.nome)
                                ),
                                nomeProjeto
                            )
                        );
                    }
                }

                // Filtros relacionados ao funcionário responsável
                if (filtro.codigoFuncionarioResponsavel() != null
                        || (filtro.nomeFuncionarioResponsavel() != null
                        && !filtro.nomeFuncionarioResponsavel().isBlank())) {

                    Join<Tarefa, Funcionario> funcionarioJoin =
                            root.join(
                                Tarefa_.funcionarioResponsavel,
                                JoinType.LEFT
                            );

                    // Código do funcionário responsável
                    if (filtro.codigoFuncionarioResponsavel() != null) {

                        predicados.add(
                            criteriaBuilder.equal(
                                funcionarioJoin.get(Funcionario_.codigo),
                                filtro.codigoFuncionarioResponsavel()
                            )
                        );
                    }

                    // Nome do funcionário responsável
                    if (filtro.nomeFuncionarioResponsavel() != null
                            && !filtro.nomeFuncionarioResponsavel().isBlank()) {

                        String nomeFuncionario = "%"
                                + filtro.nomeFuncionarioResponsavel().trim()
                                    .toLowerCase(Locale.ROOT)
                                + "%";

                        predicados.add(
                            criteriaBuilder.like(
                                criteriaBuilder.lower(
                                    funcionarioJoin.get(Funcionario_.nome)
                                ),
                                nomeFuncionario
                            )
                        );
                    }
                }
            }

            /*
             * JOIN FETCH somente na consulta de resultados.
             * A consulta de COUNT utilizada pela paginação
             * não deve receber FETCH JOIN.
             */
            if (query.getResultType() != Long.class
                    && query.getResultType() != long.class) {

                root.fetch(
                    Tarefa_.projeto,
                    JoinType.LEFT
                );

                root.fetch(
                    Tarefa_.funcionarioResponsavel,
                    JoinType.LEFT
                );

                root.fetch(
                    Tarefa_.empresa,
                    JoinType.LEFT
                );
            }

            return criteriaBuilder.and(
                predicados.toArray(new Predicate[0])
            );
        };
    }
}
