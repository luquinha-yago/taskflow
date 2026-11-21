package web.taskflow.projeto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import web.taskflow.common.enums.Status;
import web.taskflow.departamento.Departamento;
import web.taskflow.departamento.Departamento_;
import web.taskflow.projeto.dto.ProjetoSearchRequest;

public class ProjetoSpecifications {

    public static Specification<Projeto> comFiltros(
            ProjetoSearchRequest filtro) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicados = new ArrayList<>();

            // Filtro fixo: somente projetos ativos
            predicados.add(
                criteriaBuilder.equal(
                    root.get(Projeto_.status),
                    Status.ATIVO
                )
            );

            // Filtros dinâmicos
            if (filtro != null) {

                // Nome: busca parcial e case-insensitive
                if (filtro.nome() != null && !filtro.nome().isBlank()) {

                    String nome = "%"
                            + filtro.nome().trim().toLowerCase(Locale.ROOT)
                            + "%";

                    predicados.add(
                        criteriaBuilder.like(
                            criteriaBuilder.lower(
                                root.get(Projeto_.nome)
                            ),
                            nome
                        )
                    );
                }

                // Data de criação inicial: maior ou igual
                if (filtro.dataCriacaoInicial() != null) {

                    predicados.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                            root.get(Projeto_.dataCriacao),
                            filtro.dataCriacaoInicial()
                        )
                    );
                }

                // Data de criação final: menor ou igual
                if (filtro.dataCriacaoFinal() != null) {

                    predicados.add(
                        criteriaBuilder.lessThanOrEqualTo(
                            root.get(Projeto_.dataCriacao),
                            filtro.dataCriacaoFinal()
                        )
                    );
                }

                // Data de conclusão inicial: maior ou igual
                if (filtro.dataConclusaoInicial() != null) {

                    predicados.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                            root.get(Projeto_.dataConclusao),
                            filtro.dataConclusaoInicial()
                        )
                    );
                }

                // Data de conclusão final: menor ou igual
                if (filtro.dataConclusaoFinal() != null) {

                    predicados.add(
                        criteriaBuilder.lessThanOrEqualTo(
                            root.get(Projeto_.dataConclusao),
                            filtro.dataConclusaoFinal()
                        )
                    );
                }

                // Status do projeto
                if (filtro.statusProjeto() != null) {

                    predicados.add(
                        criteriaBuilder.equal(
                            root.get(Projeto_.statusProjeto),
                            filtro.statusProjeto()
                        )
                    );
                }

                // Prioridade
                if (filtro.prioridade() != null) {

                    predicados.add(
                        criteriaBuilder.equal(
                            root.get(Projeto_.prioridade),
                            filtro.prioridade()
                        )
                    );
                }

                // Filtros relacionados ao departamento
                if (filtro.codigoDepartamento() != null
                        || (filtro.nomeDepartamento() != null
                        && !filtro.nomeDepartamento().isBlank())) {

                    Join<Projeto, Departamento> departamentoJoin =
                            root.join(
                                Projeto_.departamento,
                                JoinType.LEFT
                            );

                    // Código do departamento
                    if (filtro.codigoDepartamento() != null) {

                        predicados.add(
                            criteriaBuilder.equal(
                                departamentoJoin.get(Departamento_.codigo),
                                filtro.codigoDepartamento()
                            )
                        );
                    }

                    // Nome do departamento
                    if (filtro.nomeDepartamento() != null
                            && !filtro.nomeDepartamento().isBlank()) {

                        String nomeDepartamento = "%"
                                + filtro.nomeDepartamento().trim()
                                    .toLowerCase(Locale.ROOT)
                                + "%";

                        predicados.add(
                            criteriaBuilder.like(
                                criteriaBuilder.lower(
                                    departamentoJoin.get(
                                        Departamento_.nome
                                    )
                                ),
                                nomeDepartamento
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
                    Projeto_.departamento,
                    JoinType.LEFT
                );
            }

            return criteriaBuilder.and(
                predicados.toArray(new Predicate[0])
            );
        };
    }
}
