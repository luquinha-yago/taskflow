package web.taskflow.funcionario;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import web.taskflow.common.enums.Status;

public interface FuncionarioRepository
                extends JpaRepository<Funcionario, Long>, JpaSpecificationExecutor<Funcionario> {
        public Optional<Funcionario> findByCodigoAndStatus(Long codigo, Status status);
}
