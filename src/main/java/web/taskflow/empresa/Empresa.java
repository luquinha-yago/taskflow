package web.taskflow.empresa;

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
@Table(name = "empresa")
@DynamicUpdate
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Empresa implements Serializable {

    private static final long serialVersionUID = 1L;

    public Empresa () {};

    @Id
	@SequenceGenerator(name="gerador_empresa", sequenceName="empresa_codigo_seq", allocationSize=1)
	@GeneratedValue(generator="gerador_empresa", strategy=GenerationType.SEQUENCE)
    @EqualsAndHashCode.Include
	@ToString.Include
    private Long codigo;

    @Column(name = "razao_social")
    @ToString.Include
    private String razaoSocial;

    @Column(name = "nome_fantasia")
    @ToString.Include
    private String nomeFantasia;

    @ToString.Include
    private String cnpj;

    @ToString.Include
    private String email;

    @ToString.Include
    private String telefone;

    @ToString.Include
    private String endereco;

    @Column(name = "data_cadastro")
    @ToString.Include
    private LocalDate dataCadastro;
    
    @Enumerated(EnumType.STRING)
    @ToString.Include
    private Status status = Status.ATIVO;
}
