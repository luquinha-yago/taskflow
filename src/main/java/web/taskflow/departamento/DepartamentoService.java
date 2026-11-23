package web.taskflow.departamento;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.taskflow.common.enums.Status;
import web.taskflow.common.exception.EntidadeNaoEncontradaException;
import web.taskflow.departamento.dto.DepartamentoCreateRequest;
import web.taskflow.departamento.dto.DepartamentoResponse;
import web.taskflow.departamento.dto.DepartamentoSearchRequest;
import web.taskflow.departamento.dto.DepartamentoUpdateRequest;
import web.taskflow.empresa.Empresa;
import web.taskflow.empresa.EmpresaRepository;
import web.taskflow.funcionario.Funcionario;
import web.taskflow.funcionario.FuncionarioRepository;

@Service
@Transactional(readOnly = true)
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final EmpresaRepository empresaRepository;
    private final FuncionarioRepository funcionarioRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository,
                               EmpresaRepository empresaRepository,
                               FuncionarioRepository funcionarioRepository) {
        this.departamentoRepository = departamentoRepository;
        this.empresaRepository = empresaRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Transactional
    public void salvar(DepartamentoCreateRequest form) {
        departamentoRepository.save(toEntity(form));
    }

    public Page<DepartamentoResponse> pesquisar(
        DepartamentoSearchRequest filtro,
        Pageable pageable) {

        validarPeriodo(filtro);

        return departamentoRepository
                .findAll(
                    DepartamentoSpecifications.filtrar(filtro),
                    pageable
                )
                .map(this::toResponse);
    }

    private void validarPeriodo(DepartamentoSearchRequest filtro) {

        if (filtro == null) {
            return;
        }

        if (filtro.dataCriacaoInicial() != null
                && filtro.dataCriacaoFinal() != null
                && filtro.dataCriacaoInicial()
                    .isAfter(filtro.dataCriacaoFinal())) {

            throw new IllegalArgumentException(
                "A data inicial não pode ser posterior à data final."
            );
        }
    }

    public List<DepartamentoResponse> buscarPeloNome(String nome) {
        return departamentoRepository.findTop10ByNomeContainingIgnoreCaseAndStatusOrderByNomeAsc(nome, Status.ATIVO).stream()
            .map(this::toResponse)
            .toList();
    }

    public DepartamentoUpdateRequest buscarParaEdicao(Long codigo) {
        Departamento departamento = departamentoRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Departamento não encontrado."));
        return toUpdateRequest(departamento);
    }

    @Transactional
    public void atualizar(Long codigo, DepartamentoUpdateRequest request) {
        Departamento departamento = departamentoRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Departamento não encontrado."));
        departamento.setNome(request.nome());
        departamento.setDescricao(request.descricao());
        Empresa empresa = empresaRepository.findByCodigoAndStatus(request.codigoEmpresa(),Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Empresa não encotrada."));
        departamento.setEmpresa(empresa);
        if (request.codigoFuncionarioCoordenador() != null) {
            Funcionario funcionario = funcionarioRepository
                    .findByCodigoAndStatus(
                        request.codigoFuncionarioCoordenador(),
                        Status.ATIVO
                    )
                    .orElseThrow(() ->
                        new EntidadeNaoEncontradaException(
                            "Funcionário não encontrado."
                        )
                    );
            departamento.setFuncionarioCoordenador(funcionario);
        }
        departamentoRepository.save(departamento);
    }

    @Transactional
    public void remover(Long codigo) {
        Departamento departamento = departamentoRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Departamento não encontrado."));
        departamento.setStatus(Status.INATIVO);
        departamentoRepository.save(departamento);
    }

    private Departamento toEntity(DepartamentoCreateRequest request) {
        Departamento departamento = new Departamento();
        departamento.setNome(request.nome());
        departamento.setDescricao(request.descricao());
        departamento.setDataCriacao(LocalDate.now());
        Empresa empresa = empresaRepository.findByCodigoAndStatus(request.codigoEmpresa(),Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Empresa não encotrada."));
        departamento.setEmpresa(empresa);
        if (request.codigoFuncionarioCoordenador() != null) {
            Funcionario funcionario = funcionarioRepository
                    .findByCodigoAndStatus(
                        request.codigoFuncionarioCoordenador(),
                        Status.ATIVO
                    )
                    .orElseThrow(() ->
                        new EntidadeNaoEncontradaException(
                            "Funcionário não encontrado."
                        )
                    );
            departamento.setFuncionarioCoordenador(funcionario);
        }
        return departamento;
    }

    private DepartamentoResponse toResponse(Departamento departamento) {
        return new DepartamentoResponse(
                departamento.getCodigo(),
                departamento.getNome(),
                departamento.getDescricao(),
                departamento.getDataCriacao(),
                departamento.getEmpresa() != null ? departamento.getEmpresa().getCodigo() : null,
                departamento.getEmpresa() != null ? departamento.getEmpresa().getNomeFantasia() : null,
                departamento.getFuncionarioCoordenador() != null ? departamento.getFuncionarioCoordenador().getCodigo() : null,
                departamento.getFuncionarioCoordenador() != null ? departamento.getFuncionarioCoordenador().getNome() : null
        );
    }

    private DepartamentoUpdateRequest toUpdateRequest(Departamento departamento) {
        return new DepartamentoUpdateRequest(
                departamento.getNome(),
                departamento.getEmpresa() != null ? departamento.getEmpresa().getCodigo() : null,
                departamento.getEmpresa() != null ? departamento.getEmpresa().getNomeFantasia() : null,
                departamento.getFuncionarioCoordenador() != null ? departamento.getFuncionarioCoordenador().getCodigo() : null,
                departamento.getFuncionarioCoordenador() != null ? departamento.getFuncionarioCoordenador().getNome() : null,
                departamento.getDescricao()
        );
    }
}
