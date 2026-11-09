package web.taskflow.funcionario;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import web.taskflow.common.enums.Status;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "funcionario")
@DynamicUpdate
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Funcionario implements Serializable {

    private static final long serialVersionUID = 1L;

    public Funcionario () {};

    @Id
	@SequenceGenerator(name="gerador_funcionario", sequenceName="funcionario_codigo_seq", allocationSize=1)
	@GeneratedValue(generator="gerador_funcionario", strategy=GenerationType.SEQUENCE)
    @EqualsAndHashCode.Include
	@ToString.Include
    private Long codigo;

    @ToString.Include
    private String nome;

    @ToString.Include
    private String cpf;

    @ToString.Include
    private String email;

    @ToString.Include
    private String telefone;

    @ToString.Include
    private String cargo;

    @Column(name = "data_admissao")
    @ToString.Include
    private LocalDate dataAdmissao;
    /*
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_empresa")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_departamento")
    private Departamento departamento;
    */
    @Enumerated(EnumType.STRING)
    @ToString.Include
    private Status status = Status.ATIVO;
}
