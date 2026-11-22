package web.taskflow.comentario;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import web.taskflow.comentario.dto.ComentarioSearchRequest;
import web.taskflow.common.enums.Status;
import web.taskflow.funcionario.Funcionario;
import web.taskflow.funcionario.Funcionario_;
import web.taskflow.tarefa.Tarefa_;

public class ComentarioSpecifications {

    private ComentarioSpecifications() {
    }

    public static Specification<Comentario> comFiltros(
            ComentarioSearchRequest filtro,
            Long codigoTarefa) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicados = new ArrayList<>();

            // Filtro fixo: somente comentários ativos.
            predicados.add(
                criteriaBuilder.equal(
                    root.get(Comentario_.status),
                    Status.ATIVO
                )
            );
        
            // Filtra somente os comentários da tarefa informada
            predicados.add(
                criteriaBuilder.equal(
                    root.get(Comentario_.tarefa).get(Tarefa_.codigo),
                    codigoTarefa
                )
            );
            
            // Filtro por data/hora de registro inicial.
            if (filtro.dataHoraRegistroInicial() != null) {
                predicados.add(
                    criteriaBuilder.greaterThanOrEqualTo(
                        root.get(Comentario_.dataHoraRegistro),
                        filtro.dataHoraRegistroInicial()
                    )
                );
            }

            // Filtro por data/hora de registro final.
            if (filtro.dataHoraRegistroFinal() != null) {
                predicados.add(
                    criteriaBuilder.lessThanOrEqualTo(
                        root.get(Comentario_.dataHoraRegistro),
                        filtro.dataHoraRegistroFinal()
                    )
                );
            }


            // Join com funcionário somente quando necessário
            // para aplicar os filtros relacionados.
            boolean filtrarFuncionario =
                    filtro.codigoFuncionario() != null ||
                    (filtro.nomeFuncionario() != null &&
                     !filtro.nomeFuncionario().isBlank());

            if (filtrarFuncionario) {

                Join<Comentario, Funcionario> funcionario =
                    root.join(
                        Comentario_.funcionario,
                        JoinType.LEFT
                    );

                // Código do funcionário.
                if (filtro.codigoFuncionario() != null) {
                    predicados.add(
                        criteriaBuilder.equal(
                            funcionario.get(Funcionario_.codigo),
                            filtro.codigoFuncionario()
                        )
                    );
                }

                // Nome do funcionário: parcial e case-insensitive.
                if (filtro.nomeFuncionario() != null &&
                    !filtro.nomeFuncionario().isBlank()) {

                    String nome = "%" +
                        filtro.nomeFuncionario().trim().toLowerCase() +
                        "%";

                    predicados.add(
                        criteriaBuilder.like(
                            criteriaBuilder.lower(
                                funcionario.get(Funcionario_.nome)
                            ),
                            nome
                        )
                    );
                }
            }

            /*
             * Carrega os relacionamentos na consulta principal.
             *
             * Não fazemos FETCH na consulta de contagem,
             * utilizada internamente pela paginação.
             */
            if (query.getResultType() != Long.class &&
                query.getResultType() != long.class) {

                root.fetch(
                    Comentario_.funcionario,
                    JoinType.LEFT
                );

                root.fetch(
                    Comentario_.tarefa,
                    JoinType.LEFT
                );
            }

            return criteriaBuilder.and(
                predicados.toArray(new Predicate[0])
            );
        };
    }
}