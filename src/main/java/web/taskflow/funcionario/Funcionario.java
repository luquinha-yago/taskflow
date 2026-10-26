package web.taskflow.funcionario;

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
import web.taskflow.common.enums.Status;
import web.taskflow.empresa.Empresa;

@Entity
@Table(name = "funcinario")
@DynamicUpdate
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Funcionario implements Serializable {

    private static final long serialVersionUID = 1L;

    public Funcionario () {};

    @Id
	@SequenceGenerator(name="gerador2", sequenceName="empresa_codigo_seq", allocationSize=1)
	@GeneratedValue(generator="gerador2", strategy=GenerationType.SEQUENCE)
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
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codigo_empresa")
    private Empresa empresa;
    @Enumerated(EnumType.STRING)
    @ToString.Include
    private Status status = Status.ATIVO;
}
