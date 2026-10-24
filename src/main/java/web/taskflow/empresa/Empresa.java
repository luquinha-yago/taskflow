package web.taskflow.empresa;

import java.time.LocalDate;
import org.hibernate.annotations.DynamicUpdate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import web.taskflow.common.enums.Status;

@Entity
@Table(name = "empresa")
@DynamicUpdate
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Empresa {

    private static final long serialVersionUID = 1L;

    public Empresa () {};

    @Id
	@SequenceGenerator(name="gerador1", sequenceName="empresa_codigo_seq", allocationSize=1)
	@GeneratedValue(generator="gerador1", strategy=GenerationType.SEQUENCE)
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
