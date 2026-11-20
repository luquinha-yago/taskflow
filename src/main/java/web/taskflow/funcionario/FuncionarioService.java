package web.taskflow.funcionario;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.taskflow.common.enums.Status;
import web.taskflow.common.exception.EntidadeNaoEncontradaException;
import web.taskflow.departamento.Departamento;
import web.taskflow.departamento.DepartamentoRepository;
import web.taskflow.empresa.Empresa;
import web.taskflow.empresa.EmpresaRepository;
import web.taskflow.funcionario.dto.FuncionarioCreateRequest;
import web.taskflow.funcionario.dto.FuncionarioResponse;
import web.taskflow.funcionario.dto.FuncionarioSearchRequest;
import web.taskflow.funcionario.dto.FuncionarioUpdateRequest;

@Service
@Transactional(readOnly = true)
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final EmpresaRepository empresaRepository;
    private final DepartamentoRepository departamentoRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository,
                              EmpresaRepository empresaRepository,
                              DepartamentoRepository departamentoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.empresaRepository = empresaRepository;
        this.departamentoRepository = departamentoRepository;
    }

    @Transactional
    public void salvar(FuncionarioCreateRequest form) {
        funcionarioRepository.save(toEntity(form));
    }

    public Page<FuncionarioResponse> pesquisar(
            FuncionarioSearchRequest filtro,
            Pageable pageable) {

        validarPeriodo(filtro);

        return funcionarioRepository
                .findAll(
                    FuncionarioSpecifications.comFiltros(filtro),
                    pageable
                )
                .map(this::toResponse);
    }

    private void validarPeriodo(FuncionarioSearchRequest filtro) {

        if (filtro == null) {
            return;
        }

        if (filtro.dataAdmissaoInicial() != null
                && filtro.dataAdmissaoFinal() != null
                && filtro.dataAdmissaoInicial()
                    .isAfter(filtro.dataAdmissaoFinal())) {

            throw new IllegalArgumentException(
                "A data inicial não pode ser posterior à data final."
            );
        }
    }

    public List<FuncionarioResponse> buscarPeloNome(String nome) {
        return funcionarioRepository.findTop10ByNomeContainingIgnoreCaseAndStatusOrderByNomeAsc(nome, Status.ATIVO).stream()
            .map(this::toResponse)
            .toList();
    }

    public FuncionarioUpdateRequest buscarParaEdicao(Long codigo) {
        Funcionario funcionario = funcionarioRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado"));
        return toUpdateRequest(funcionario);
    }

    @Transactional
    public void atualizar(Long codigo, FuncionarioUpdateRequest request) {
        Funcionario funcionario = funcionarioRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado"));
        funcionario.setNome(request.nome());
        funcionario.setCpf(request.cpf());
        funcionario.setEmail(request.email());
        funcionario.setTelefone(request.telefone());
        funcionario.setCargo(request.cargo());
        funcionario.setDataAdmissao(request.dataAdmissao());
        Empresa empresa = empresaRepository.findByCodigoAndStatus(request.codigoEmpresa(),Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Empresa não encotrada."));
        funcionario.setEmpresa(empresa);
        Departamento departamento = departamentoRepository.findByCodigoAndStatus(request.codigoDepartamento(),Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Departamento não encotrado."));
        funcionario.setDepartamento(departamento);
        funcionarioRepository.save(funcionario);
    }

    @Transactional
    public void remover(Long codigo) {
        Funcionario funcionario = funcionarioRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado"));
        funcionario.setStatus(Status.INATIVO);
        funcionarioRepository.save(funcionario);
    }

    private Funcionario toEntity(FuncionarioCreateRequest request) {
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(request.nome());
        funcionario.setCpf(request.cpf());
        funcionario.setEmail(request.email());
        funcionario.setTelefone(request.telefone());
        funcionario.setCargo(request.cargo());
        funcionario.setDataAdmissao(request.dataAdmissao());
        Empresa empresa = empresaRepository.findByCodigoAndStatus(request.codigoEmpresa(),Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Empresa não encotrada."));
        funcionario.setEmpresa(empresa);
        Departamento departamento = departamentoRepository.findByCodigoAndStatus(request.codigoDepartamento(),Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Departamento não encotrado."));
        funcionario.setDepartamento(departamento);
        return funcionario;
    }

    private FuncionarioResponse toResponse(Funcionario funcionario) {
        return new FuncionarioResponse(
            funcionario.getCodigo(),
            funcionario.getNome(),
            funcionario.getCpf(),
            funcionario.getEmail(),
            funcionario.getTelefone(),
            funcionario.getCargo(),
            funcionario.getDataAdmissao(),
            funcionario.getEmpresa() != null ? funcionario.getEmpresa().getCodigo() : null,
            funcionario.getEmpresa() != null ? funcionario.getEmpresa().getNomeFantasia() : null,
            funcionario.getDepartamento() != null ? funcionario.getDepartamento().getCodigo() : null,
            funcionario.getDepartamento() != null ? funcionario.getDepartamento().getNome() : null
        );
    }

    private FuncionarioUpdateRequest toUpdateRequest(Funcionario funcionario) {
        return new FuncionarioUpdateRequest(
            funcionario.getNome(),
            funcionario.getCpf(),
            funcionario.getEmail(),
            funcionario.getTelefone(),
            funcionario.getCargo(),
            funcionario.getDataAdmissao(),
            funcionario.getEmpresa() != null ? funcionario.getEmpresa().getCodigo() : null,
            funcionario.getEmpresa() != null ? funcionario.getEmpresa().getNomeFantasia() : null,
            funcionario.getDepartamento() != null ? funcionario.getDepartamento().getCodigo() : null,
            funcionario.getDepartamento() != null ? funcionario.getDepartamento().getNome() : null
        );
    }
}