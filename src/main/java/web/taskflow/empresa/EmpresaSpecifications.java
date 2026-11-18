package web.taskflow.empresa;

import jakarta.persistence.criteria.Predicate;
import web.taskflow.common.enums.Status;
import web.taskflow.empresa.dto.EmpresaSearchRequest;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmpresaSpecifications {

    public static Specification<Empresa> comFiltros(EmpresaSearchRequest filtro) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Filtro fixo: somente empresas ATIVAS
            predicates.add(
                criteriaBuilder.equal(
                    root.get(Empresa_.status),
                    Status.ATIVO
                )
            );

            // Filtro por código
            if (filtro.codigo() != null) {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get(Empresa_.codigo),
                        filtro.codigo()
                    )
                );
            }

            // Filtro por razão social (case-insensitive)
            if (filtro.razaoSocial() != null
                    && !filtro.razaoSocial().isBlank()) {

                String razaoSocial = "%" +
                    filtro.razaoSocial().trim().toLowerCase(Locale.ROOT) + "%";

                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(Empresa_.razaoSocial)),
                        razaoSocial
                    )
                );
            }

            // Filtro por nome fantasia (case-insensitive)
            if (filtro.nomeFantasia() != null
                    && !filtro.nomeFantasia().isBlank()) {

                String nomeFantasia = "%" +
                    filtro.nomeFantasia().trim().toLowerCase(Locale.ROOT) + "%";

                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(Empresa_.nomeFantasia)),
                        nomeFantasia
                    )
                );
            }

            // Filtro por CNPJ
            if (filtro.cnpj() != null && !filtro.cnpj().isBlank()) {

                String cnpj = "%" + filtro.cnpj().trim() + "%";

                predicates.add(
                    criteriaBuilder.like(
                        root.get(Empresa_.cnpj),
                        cnpj
                    )
                );
            }

            // Data de cadastro inicial (inclusiva)
            if (filtro.dataCadastroInicial() != null) {

                predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(
                        root.get(Empresa_.dataCadastro),
                        filtro.dataCadastroInicial()
                    )
                );
            }

            // Data de cadastro final (inclusiva)
            if (filtro.dataCadastroFinal() != null) {

                predicates.add(
                    criteriaBuilder.lessThanOrEqualTo(
                        root.get(Empresa_.dataCadastro),
                        filtro.dataCadastroFinal()
                    )
                );
            }

            return criteriaBuilder.and(
                predicates.toArray(new Predicate[0])
            );
        };
    }
}
