
package web.taskflow.funcionario;

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
import web.taskflow.empresa.Empresa;
import web.taskflow.empresa.Empresa_;
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

            // Código do funcionário
            if (filtro.codigo() != null) {
                predicados.add(
                    criteriaBuilder.equal(
                        root.get(Funcionario_.codigo),
                        filtro.codigo()
                    )
                );
            }

            // Nome do funcionário: busca parcial e case-insensitive
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

            // Empresa: filtros por código e nome
            if (filtro.codigoEmpresa() != null
                    || (filtro.nomeEmpresa() != null
                    && !filtro.nomeEmpresa().isBlank())) {

                Join<Funcionario, Empresa> empresaJoin =
                        root.join(Funcionario_.empresa, JoinType.LEFT);

                // Código da empresa
                if (filtro.codigoEmpresa() != null) {
                    predicados.add(
                        criteriaBuilder.equal(
                            empresaJoin.get(Empresa_.codigo),
                            filtro.codigoEmpresa()
                        )
                    );
                }

                // Nome da empresa: nome fantasia ou razão social
                if (filtro.nomeEmpresa() != null
                        && !filtro.nomeEmpresa().isBlank()) {

                    String nomeEmpresa = "%"
                            + filtro.nomeEmpresa().trim()
                                    .toLowerCase(Locale.ROOT)
                            + "%";

                    predicados.add(
                        criteriaBuilder.or(
                            criteriaBuilder.like(
                                criteriaBuilder.lower(
                                    empresaJoin.get(Empresa_.nomeFantasia)
                                ),
                                nomeEmpresa
                            ),
                            criteriaBuilder.like(
                                criteriaBuilder.lower(
                                    empresaJoin.get(Empresa_.razaoSocial)
                                ),
                                nomeEmpresa
                            )
                        )
                    );
                }
            }

            // Departamento: filtros por código e nome
            if (filtro.codigoDepartamento() != null
                    || (filtro.nomeDepartamento() != null
                    && !filtro.nomeDepartamento().isBlank())) {

                Join<Funcionario, Departamento> departamentoJoin =
                        root.join(Funcionario_.departamento, JoinType.LEFT);

                // Código do departamento
                if (filtro.codigoDepartamento() != null) {
                    predicados.add(
                        criteriaBuilder.equal(
                            departamentoJoin.get(Departamento_.codigo),
                            filtro.codigoDepartamento()
                        )
                    );
                }

                // Nome do departamento: busca parcial e case-insensitive
                if (filtro.nomeDepartamento() != null
                        && !filtro.nomeDepartamento().isBlank()) {

                    String nomeDepartamento = "%"
                            + filtro.nomeDepartamento().trim()
                                    .toLowerCase(Locale.ROOT)
                            + "%";

                    predicados.add(
                        criteriaBuilder.like(
                            criteriaBuilder.lower(
                                departamentoJoin.get(Departamento_.nome)
                            ),
                            nomeDepartamento
                        )
                    );
                }
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
