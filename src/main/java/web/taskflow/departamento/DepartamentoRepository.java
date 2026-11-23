package web.taskflow.departamento;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import web.taskflow.common.enums.Status;

public interface DepartamentoRepository
        extends JpaRepository<Departamento, Long>, JpaSpecificationExecutor<Departamento> {
    public Optional<Departamento> findByCodigoAndStatus(Long codigo, Status status);
    public List<Departamento> findTop10ByNomeContainingIgnoreCaseAndStatusOrderByNomeAsc(String nome, Status status);
}
