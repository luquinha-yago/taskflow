package web.taskflow.comentario;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import web.taskflow.funcionario.Funcionario;
import web.taskflow.tarefa.Tarefa;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentario")
@DynamicUpdate
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Comentario implements Serializable {

    private static final long serialVersionUID = 1L;

    public Comentario () {}

    @Id
    @SequenceGenerator(name="gerador_comentario", sequenceName="comentario_codigo_seq", allocationSize=1)
    @GeneratedValue(generator="gerador_comentario", strategy=GenerationType.SEQUENCE)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long codigo;

    @ToString.Include
    private String comentario;

    @Column(name = "hora_registro")
    @ToString.Include
    private LocalDateTime hora_registro;

    @ToString.Include
    private Funcionario funcionario;

    @ToString.Include
    private Tarefa tarefa;
}
