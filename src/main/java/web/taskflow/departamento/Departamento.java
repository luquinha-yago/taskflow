package web.taskflow.departamento;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import web.taskflow.common.enums.Status;
import web.taskflow.empresa.Empresa;
import web.taskflow.funcionario.Funcionario;

import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Table(name = "departamento")
@DynamicUpdate
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Departamento implements Serializable {
    
    private static final long serialVersionUID = 1L;

    public Departamento () {};
    
    @Id
	@SequenceGenerator(name="gerador_departamento", sequenceName="departamento_codigo_seq", allocationSize=1)
	@GeneratedValue(generator="gerador_departamento", strategy=GenerationType.SEQUENCE)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_coordenador_id")
    private Funcionario funcionarioCoordenador;

    @Enumerated(EnumType.STRING)
    @ToString.Include
    private Status status = Status.ATIVO;
}
