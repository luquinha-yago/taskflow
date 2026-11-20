package web.taskflow.empresa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import web.taskflow.common.enums.Status;

public interface EmpresaRepository extends JpaRepository<Empresa, Long>,
        JpaSpecificationExecutor<Empresa> {
    public Optional<Empresa> findByCodigoAndStatus(Long codigo, Status status);
    public List<Empresa> findTop10ByNomeFantasiaContainingIgnoreCaseAndStatusOrderByNomeFantasiaAsc(String nomeFantasia, Status status);
}
