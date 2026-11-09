package web.taskflow.funcionario;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import web.taskflow.common.enums.Status;
import web.taskflow.funcionario.dto.FuncionarioSearchRequest;

public class FuncionarioSpecifications {

    public static Specification<Funcionario> comFiltros(
            FuncionarioSearchRequest filtro) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicados = new ArrayList<>();

            // Filtro fixo: somente funcionários ativos
            predicados.add(
                criteriaBuilder.equal(
                    root.get(Funcionario_.status),
                    Status.ATIVO
                )
            );

            if (filtro == null) {
                return criteriaBuilder.and(
                    predicados.toArray(new Predicate[0])
                );
            }

            // Código: igualdade exata
            if (filtro.codigo() != null) {
                predicados.add(
                    criteriaBuilder.equal(
                        root.get(Funcionario_.codigo),
                        filtro.codigo()
                    )
                );
            }

            // Nome: busca parcial e case-insensitive
            if (filtro.nome() != null && !filtro.nome().isBlank()) {

                String nome = "%" + filtro.nome().trim()
                        .toLowerCase(Locale.ROOT) + "%";

                predicados.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(
                            root.get(Funcionario_.nome)
                        ),
                        nome
                    )
                );
            }

            // CPF: igualdade exata
            if (filtro.cpf() != null && !filtro.cpf().isBlank()) {
                predicados.add(
                    criteriaBuilder.equal(
                        root.get(Funcionario_.cpf),
                        filtro.cpf().trim()
                    )
                );
            }

            // Cargo: busca parcial e case-insensitive
            if (filtro.cargo() != null && !filtro.cargo().isBlank()) {

                String cargo = "%" + filtro.cargo().trim()
                        .toLowerCase(Locale.ROOT) + "%";

                predicados.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(
                            root.get(Funcionario_.cargo)
                        ),
                        cargo
                    )
                );
            }

            // Data de admissão inicial: maior ou igual
            if (filtro.dataAdmissaoInicial() != null) {
                predicados.add(
                    criteriaBuilder.greaterThanOrEqualTo(
                        root.get(Funcionario_.dataAdmissao),
                        filtro.dataAdmissaoInicial()
                    )
                );
            }

            // Data de admissão final: menor ou igual
            if (filtro.dataAdmissaoFinal() != null) {
                predicados.add(
                    criteriaBuilder.lessThanOrEqualTo(
                        root.get(Funcionario_.dataAdmissao),
                        filtro.dataAdmissaoFinal()
                    )
                );
            }

            return criteriaBuilder.and(
                predicados.toArray(new Predicate[0])
            );
        };
    }
}