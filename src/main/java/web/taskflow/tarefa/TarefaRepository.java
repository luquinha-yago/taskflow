package web.taskflow.tarefa;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import web.taskflow.common.enums.Status;

public interface TarefaRepository extends JpaRepository<Tarefa, Long>,
        JpaSpecificationExecutor<Tarefa> {

    @EntityGraph(attributePaths = {"projeto", "funcionarioResponsavel", "empresa"})
    public Optional<Tarefa> findByIdAndStatus(Long id, Status status);
}
