package web.taskflow.projeto;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import web.taskflow.common.enums.Status;

public interface ProjetoRepository extends JpaRepository<Projeto, Long>,
        JpaSpecificationExecutor<Projeto> {
    public Optional<Projeto> findByCodigoAndStatus(Long codigo, Status status);
}