package web.taskflow.tarefa;

import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import web.taskflow.common.enums.Prioridade;
import web.taskflow.common.enums.Status;
import web.taskflow.empresa.Empresa;
import web.taskflow.funcionario.Funcionario;

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
    @SequenceGenerator(name = "gerador3", sequenceName = "tarefa_codigo_seq", allocationSize = 1)
    @GeneratedValue(generator = "gerador3", strategy = GenerationType.SEQUENCE)
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

    @Enumerated(EnumType.STRING)
    @ToString.Include
    private Prioridade prioridade = Prioridade.MEDIA;

    @Enumerated(EnumType.STRING)
    @ToString.Include
    private Status status = Status.ATIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_responsavel_id")
    private Funcionario funcionarioResponsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;
}