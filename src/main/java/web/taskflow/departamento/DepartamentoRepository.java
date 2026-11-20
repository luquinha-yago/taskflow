package web.taskflow.departamento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import web.taskflow.common.enums.Status;

import java.util.Optional;

public interface DepartamentoRepository
        extends JpaRepository<Departamento, Long>, JpaSpecificationExecutor<Departamento> {
    public Optional<Departamento> findByCodigoAndStatus(Long codigo, Status status);
}
