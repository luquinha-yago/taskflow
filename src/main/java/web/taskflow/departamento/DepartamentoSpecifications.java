package web.taskflow.departamento;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.Specification;
import web.taskflow.common.enums.Status;
import web.taskflow.departamento.dto.DepartamentoSearchRequest;
import web.taskflow.empresa.Empresa;
import web.taskflow.empresa.Empresa_;
import web.taskflow.funcionario.Funcionario;
import web.taskflow.funcionario.Funcionario_;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class DepartamentoSpecifications {

    private static final char ESCAPE = '\\';

    private static final DepartamentoSearchRequest FILTRO_VAZIO =
            new DepartamentoSearchRequest(
                    null, null, null, null,
                    null, null, null, null
            );

    private DepartamentoSpecifications() {
    }

    public static Specification<Departamento> filtrar(DepartamentoSearchRequest filtro) {

        final DepartamentoSearchRequest f =
                filtro != null ? filtro : FILTRO_VAZIO;

        return (root, query, cb) -> {

            boolean contagem = isConsultaDeContagem(query);

            boolean filtrarEmpresa =
                    f.codigoEmpresa() != null || temTexto(f.nomeEmpresa());

            boolean filtrarCoordenador =
                    f.codigoFuncionarioCoordenador() != null
                            || temTexto(f.nomeFuncionarioCoordenador());

            // Carrega os relacionamentos na consulta principal.
            // Na consulta de contagem, utiliza JOIN somente quando necessário.
            Join<Departamento, Empresa> empresa =
                    juntar(root, Departamento_.empresa, contagem, filtrarEmpresa);

            Join<Departamento, Funcionario> coordenador =
                    juntar(root, Departamento_.funcionarioCoordenador,
                            contagem, filtrarCoordenador);

            List<Predicate> predicados = new ArrayList<>();

            // Filtro fixo: somente departamentos ativos.
            predicados.add(
                    cb.equal(root.get(Departamento_.status), Status.ATIVO)
            );

            // Filtro por código.
            if (f.codigo() != null) {
                predicados.add(
                        cb.equal(root.get(Departamento_.codigo), f.codigo())
                );
            }

            // Filtro por nome (case-insensitive).
            if (temTexto(f.nome())) {
                predicados.add(
                        contem(cb, root.get(Departamento_.nome), f.nome())
                );
            }

            // Filtro por código da empresa.
            if (f.codigoEmpresa() != null) {
                predicados.add(
                        cb.equal(
                                empresa.get(Empresa_.codigo),
                                f.codigoEmpresa()
                        )
                );
            }

            // Filtro por nome da empresa.
            // Pesquisa tanto pelo nome fantasia quanto pela razão social.
            if (temTexto(f.nomeEmpresa())) {
                predicados.add(
                        cb.or(
                                contem(cb,
                                        empresa.get(Empresa_.nomeFantasia),
                                        f.nomeEmpresa()),

                                contem(cb,
                                        empresa.get(Empresa_.razaoSocial),
                                        f.nomeEmpresa())
                        )
                );
            }

            // Filtro por código do funcionário coordenador.
            if (f.codigoFuncionarioCoordenador() != null) {
                predicados.add(
                        cb.equal(
                                coordenador.get(Funcionario_.codigo),
                                f.codigoFuncionarioCoordenador()
                        )
                );
            }

            // Filtro por nome do funcionário coordenador.
            if (temTexto(f.nomeFuncionarioCoordenador())) {
                predicados.add(
                        contem(
                                cb,
                                coordenador.get(Funcionario_.nome),
                                f.nomeFuncionarioCoordenador()
                        )
                );
            }

            // Data de criação inicial (inclusiva).
            if (f.dataCriacaoInicial() != null) {
                predicados.add(
                        cb.greaterThanOrEqualTo(
                                root.get(Departamento_.dataCriacao),
                                f.dataCriacaoInicial()
                        )
                );
            }

            // Data de criação final (inclusiva).
            if (f.dataCriacaoFinal() != null) {
                predicados.add(
                        cb.lessThanOrEqualTo(
                                root.get(Departamento_.dataCriacao),
                                f.dataCriacaoFinal()
                        )
                );
            }

            return cb.and(predicados.toArray(Predicate[]::new));
        };
    }

    /**
     * Empresas ativas cujo nome fantasia ou razão social contém o termo.
     * Utilizado no inputBuscavel.
     */
    public static Specification<Empresa> empresasAtivasPorNome(String termo) {

        return (root, query, cb) -> cb.and(
                cb.equal(root.get(Empresa_.status), Status.ATIVO),

                cb.or(
                        contem(cb, root.get(Empresa_.nomeFantasia), termo),
                        contem(cb, root.get(Empresa_.razaoSocial), termo)
                )
        );
    }

    /**
     * Funcionários ativos cujo nome contém o termo.
     * Utilizado no inputBuscavel.
     */
    public static Specification<Funcionario> funcionariosAtivosPorNome(String termo) {

        return (root, query, cb) -> cb.and(
                cb.equal(root.get(Funcionario_.status), Status.ATIVO),
                contem(cb, root.get(Funcionario_.nome), termo)
        );
    }

    /**
     * Na consulta principal faz FETCH para evitar N+1.
     * Na consulta de COUNT utiliza JOIN simples somente quando o filtro
     * precisa acessar o relacionamento.
     *
     * O cast é compatível com a implementação do Hibernate utilizada
     * neste projeto, mas não é garantido pela especificação JPA.
     */
    @SuppressWarnings("unchecked")
    private static <Y> Join<Departamento, Y> juntar(
            Root<Departamento> root,
            SingularAttribute<? super Departamento, Y> atributo,
            boolean contagem,
            boolean usadoNoFiltro) {

        if (!contagem) {
            return (Join<Departamento, Y>) root.fetch(atributo, JoinType.LEFT);
        }

        return usadoNoFiltro
                ? root.join(atributo, JoinType.LEFT)
                : null;
    }

    private static boolean isConsultaDeContagem(CriteriaQuery<?> query) {

        Class<?> tipo = query.getResultType();

        return Long.class.equals(tipo) || long.class.equals(tipo);
    }

    /**
     * LIKE '%termo%' case-insensitive,
     * escapando curingas digitados pelo usuário.
     */
    private static Predicate contem(
            CriteriaBuilder cb,
            Expression<String> campo,
            String termo) {

        String padrao = "%"
                + escaparLike(termo.trim().toLowerCase(Locale.ROOT))
                + "%";

        return cb.like(cb.lower(campo), padrao, ESCAPE);
    }

    private static String escaparLike(String valor) {

        return valor
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    private static boolean temTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}
