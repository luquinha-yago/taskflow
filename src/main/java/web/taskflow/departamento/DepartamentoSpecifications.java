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
            new DepartamentoSearchRequest(null, null, null, null, null, null);

    private DepartamentoSpecifications() {
    }

    public static Specification<Departamento> filtrar(DepartamentoSearchRequest filtro) {
        final DepartamentoSearchRequest f = filtro != null ? filtro : FILTRO_VAZIO;

        return (root, query, cb) -> {
            boolean contagem = isConsultaDeContagem(query);

            // Um único join por associação: serve para o FETCH (evita N+1) e para o filtro.
            Join<Departamento, Empresa> empresa =
                    juntar(root, Departamento_.empresa, contagem, temTexto(f.empresa()));
            Join<Departamento, Funcionario> coordenador =
                    juntar(root, Departamento_.funcionarioCoordenador, contagem, temTexto(f.funcionario_coordenador()));

            List<Predicate> predicados = new ArrayList<>();

            // Filtro fixo (obrigatório)
            predicados.add(cb.equal(root.get(Departamento_.status), Status.ATIVO));

            // Filtros dinâmicos (aplicados só quando preenchidos)
            if (f.codigo() != null) {
                predicados.add(cb.equal(root.get(Departamento_.codigo), f.codigo()));
            }
            if (temTexto(f.nome())) {
                predicados.add(contem(cb, root.get(Departamento_.nome), f.nome()));
            }
            if (temTexto(f.descricao())) {
                predicados.add(contem(cb, root.get(Departamento_.descricao), f.descricao()));
            }
            if (temTexto(f.empresa())) {
                predicados.add(cb.or(
                        contem(cb, empresa.get(Empresa_.nomeFantasia), f.empresa()),
                        contem(cb, empresa.get(Empresa_.razaoSocial), f.empresa())));
            }
            if (temTexto(f.funcionario_coordenador())) {
                predicados.add(contem(cb, coordenador.get(Funcionario_.nome), f.funcionario_coordenador()));
            }
            if (f.data_criacao() != null) {
                predicados.add(cb.equal(root.get(Departamento_.dataCriacao), f.data_criacao()));
            }

            return cb.and(predicados.toArray(Predicate[]::new));
        };
    }

    /** Empresas ativas cujo nome fantasia ou razão social contém o termo (usado no inputBuscavel). */
    public static Specification<Empresa> empresasAtivasPorNome(String termo) {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get(Empresa_.status), Status.ATIVO),
                cb.or(contem(cb, root.get(Empresa_.nomeFantasia), termo),
                        contem(cb, root.get(Empresa_.razaoSocial), termo)));
    }

    /** Funcionários ativos cujo nome contém o termo (usado no inputBuscavel). */
    public static Specification<Funcionario> funcionariosAtivosPorNome(String termo) {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get(Funcionario_.status), Status.ATIVO),
                contem(cb, root.get(Funcionario_.nome), termo));
    }

    /**
     * Na consulta principal faz FETCH (carrega a associação junto).
     * Na consulta de COUNT da paginação, fetch é proibido: usa JOIN simples e só se o filtro precisar.
     * No Hibernate, Fetch também implementa Join, por isso o cast é seguro e o mesmo alias filtra.
     */
    @SuppressWarnings("unchecked")
    private static <Y> Join<Departamento, Y> juntar(Root<Departamento> root,
                                                    SingularAttribute<? super Departamento, Y> atributo,
                                                    boolean contagem,
                                                    boolean usadoNoFiltro) {
        if (!contagem) {
            return (Join<Departamento, Y>) root.fetch(atributo, JoinType.LEFT);
        }
        return usadoNoFiltro ? root.join(atributo, JoinType.LEFT) : null;
    }

    private static boolean isConsultaDeContagem(CriteriaQuery<?> query) {
        Class<?> tipo = query.getResultType();
        return Long.class.equals(tipo) || long.class.equals(tipo);
    }

    /** LIKE '%termo%' case-insensitive, escapando curingas digitados pelo usuário. */
    private static Predicate contem(CriteriaBuilder cb, Expression<String> campo, String termo) {
        String padrao = "%" + escaparLike(termo.trim().toLowerCase(Locale.ROOT)) + "%";
        return cb.like(cb.lower(campo), padrao, ESCAPE);
    }

    private static String escaparLike(String valor) {
        return valor.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private static boolean temTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}
