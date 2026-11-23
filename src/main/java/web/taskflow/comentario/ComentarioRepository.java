package web.taskflow.comentario;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import web.taskflow.common.enums.Status;
import web.taskflow.tarefa.Tarefa;

public interface ComentarioRepository extends
        JpaRepository<Comentario, Long>,
        JpaSpecificationExecutor<Comentario> {
        public Optional<Tarefa> findByCodigoAndStatus(Long codigo, Status status);
}