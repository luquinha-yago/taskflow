package web.taskflow.tarefa;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import web.taskflow.common.enums.Prioridade;
import web.taskflow.common.enums.Status;
import web.taskflow.common.enums.StatusTarefa;
import web.taskflow.empresa.Empresa;
import web.taskflow.funcionario.Funcionario;
import web.taskflow.projeto.Projeto;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "tarefa")
@DynamicUpdate
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tarefa implements Serializable {

    private static final long serialVersionUID = 1L;

    public Tarefa() {}

    @Id
    @SequenceGenerator(name = "gerador_tarefa", sequenceName = "tarefa_codigo_seq", allocationSize = 1)
    @GeneratedValue(generator = "gerador_tarefa", strategy = GenerationType.SEQUENCE)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ToString.Include
    private String titulo;

    @ToString.Include
    private String descricao;

    @Column(name = "data_criacao")
    @ToString.Include
    private LocalDate dataCriacao;

    @Column(name = "data_inicio")
    @ToString.Include
    private LocalDate dataInicio;

    @Column(name = "data_prazo")
    @ToString.Include
    private LocalDate dataPrazo;

    @Column(name = "data_conclusao")
    @ToString.Include
    private LocalDate dataConclusao;

    @Column(name = "status_tarefa")
    @Enumerated(EnumType.STRING)
    @ToString.Include
    private StatusTarefa statusTarefa = StatusTarefa.A_FAZER;

    @Enumerated(EnumType.STRING)
    @ToString.Include
    private Prioridade prioridade = Prioridade.MEDIA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_projeto")
    private Projeto projeto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_responsavel_id")
    private Funcionario funcionarioResponsavel;

    @Enumerated(EnumType.STRING)
    @ToString.Include
    private Status status = Status.ATIVO;
}