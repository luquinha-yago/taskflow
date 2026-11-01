package web.taskflow.projeto;


import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import web.taskflow.common.enums.Prioridade;
import web.taskflow.common.enums.Status;
import web.taskflow.common.enums.StatusProjeto;
import web.taskflow.departamento.Departamento;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "projeto")
@DynamicUpdate
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Projeto implements Serializable {

    private static final long serialVersionUID = 1L;

    public Projeto() {}

    @Id
    @SequenceGenerator(name="gerador_projeto", sequenceName="comentario_codigo_seq", allocationSize=1)
    @GeneratedValue(generator="gerador_projeto", strategy= GenerationType.SEQUENCE)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long codigo;

    @ToString.Include
    private String nome;

    @ToString.Include
    private String descricao;

    @Column(name = "data_criacao")
    @ToString.Include
    private LocalDate dataCriacao;

    @Column(name = "data_inicio")
    @ToString.Include
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    @ToString.Include
    private LocalDate dataFim;

    @Column(name = "data_conclusao")
    @ToString.Include
    private LocalDate dataConclusao;

    @Column(name = "status_projeto")
    @ToString.Include
    @Enumerated(EnumType.STRING)
    private StatusProjeto statusProjeto;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    @ToString.Include
    @Enumerated(EnumType.STRING)
    private Status status = Status.ATIVO;

    @JoinColumn(name = "codigo_departamento")
    @ManyToOne(fetch = FetchType.LAZY)
    private Departamento departamento;
}
