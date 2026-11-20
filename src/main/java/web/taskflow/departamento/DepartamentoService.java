package web.taskflow.departamento;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import web.taskflow.empresa.dto.EmpresaResponse;
import web.taskflow.funcionario.Funcionario;
import web.taskflow.funcionario.FuncionarioRepository;
import web.taskflow.funcionario.dto.FuncionarioResponse;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartamentoService {

    private static final int LIMITE_OPCOES = 10;

    private final DepartamentoRepository repository;
    private final EmpresaRepository empresaRepository;
    private final FuncionarioRepository funcionarioRepository;

    @Transactional
    public void salvar(DepartamentoCreateRequest form) {
        repository.save(toEntity(form));
    }

    @Transactional(readOnly = true)
    public Page<DepartamentoResponse> pesquisar(DepartamentoSearchRequest filtro, Pageable pageable) {
        return repository
                .findAll(DepartamentoSpecifications.filtrar(filtro), pageable)
                .map(DepartamentoService::toResponse);
    }

    @Transactional(readOnly = true)
    public DepartamentoUpdateRequest buscarParaEdicao(Long codigo) {
        return toUpdateRequest(buscarAtivo(codigo));
    }

    @Transactional
    public void atualizar(Long codigo, DepartamentoUpdateRequest request) {
        Departamento departamento = buscarAtivo(codigo);
        departamento.setNome(request.nome());
        departamento.setDescricao(request.descricao());
        departamento.setEmpresa(buscarEmpresa(request.codigo_empresa()));
        departamento.setFuncionarioCoordenador(buscarFuncionario(request.codigo_funcionario_coordenador()));
        repository.save(departamento);
    }

    @Transactional
    public void remover(Long codigo) {
        Departamento departamento = buscarAtivo(codigo);
        departamento.setStatus(Status.INATIVO);
        repository.save(departamento);
    }

    @Transactional(readOnly = true)
    public List<EmpresaResponse> buscarOpcoesEmpresa(String termo) {
        if (!termoValido(termo)) {
            return List.of();
        }
        return empresaRepository
                .findAll(DepartamentoSpecifications.empresasAtivasPorNome(termo),
                        PageRequest.of(0, LIMITE_OPCOES, Sort.by("nomeFantasia")))
                .map(empresa -> new EmpresaResponse(
                        empresa.getCodigo(),
                        empresa.getRazaoSocial(),
                        empresa.getNomeFantasia(),
                        empresa.getCnpj(),
                        empresa.getEmail(),
                        empresa.getTelefone(),
                        empresa.getEndereco(),
                        empresa.getDataCadastro()))
                .getContent();
    }

    @Transactional(readOnly = true)
    public List<FuncionarioResponse> buscarOpcoesFuncionario(String termo) {
        if (!termoValido(termo)) {
            return List.of();
        }
        return funcionarioRepository
                .findAll(DepartamentoSpecifications.funcionariosAtivosPorNome(termo),
                        PageRequest.of(0, LIMITE_OPCOES, Sort.by("nome")))
                .map(funcionario -> new FuncionarioResponse(
                        funcionario.getCodigo(),
                        funcionario.getNome(),
                        funcionario.getCpf(),
                        funcionario.getEmail(),
                        funcionario.getTelefone(),
                        funcionario.getCargo(),
                        funcionario.getDataAdmissao()))
                .getContent();
    }

    private static boolean termoValido(String termo) {
        return termo != null && termo.trim().length() >= 3;
    }

    private Departamento buscarAtivo(Long codigo) {
        return repository.findByCodigoAndStatus(codigo, Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Departamento não encontrado: " + codigo));
    }

    private Empresa buscarEmpresa(Long codigo) {
        return empresaRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Empresa não encontrada: " + codigo));
    }

    private Funcionario buscarFuncionario(Long codigo) {
        return funcionarioRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado: " + codigo));
    }

    private Departamento toEntity(DepartamentoCreateRequest request) {
        Departamento departamento = new Departamento();
        departamento.setNome(request.nome());
        departamento.setDescricao(request.descricao());
        departamento.setDataCriacao(LocalDate.now());
        departamento.setEmpresa(buscarEmpresa(request.codigo_empresa()));
        departamento.setFuncionarioCoordenador(buscarFuncionario(request.codigo_funcionario_coordenador()));
        return departamento;
    }

    static DepartamentoResponse toResponse(Departamento departamento) {
        Empresa empresa = departamento.getEmpresa();
        Funcionario coordenador = departamento.getFuncionarioCoordenador();

        return new DepartamentoResponse(
                departamento.getCodigo(),
                departamento.getNome(),
                departamento.getDescricao(),
                coordenador != null ? coordenador.getNome() : null,
                empresa != null ? nomeExibicao(empresa) : null,
                departamento.getStatus(),
                departamento.getDataCriacao()
        );
    }

    private static DepartamentoUpdateRequest toUpdateRequest(Departamento departamento) {
        Empresa empresa = departamento.getEmpresa();
        Funcionario coordenador = departamento.getFuncionarioCoordenador();

        return new DepartamentoUpdateRequest(
                departamento.getNome(),
                empresa != null ? empresa.getCodigo() : null,
                empresa != null ? nomeExibicao(empresa) : null,
                coordenador != null ? coordenador.getCodigo() : null,
                coordenador != null ? coordenador.getNome() : null,
                departamento.getDescricao()
        );
    }

    private static String nomeExibicao(Empresa empresa) {
        String fantasia = empresa.getNomeFantasia();
        return (fantasia != null && !fantasia.isBlank()) ? fantasia : empresa.getRazaoSocial();
    }
}
