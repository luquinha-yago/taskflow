package web.taskflow.projeto;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.taskflow.common.enums.Status;
import web.taskflow.common.exception.EntidadeNaoEncontradaException;
import web.taskflow.departamento.Departamento;
import web.taskflow.departamento.DepartamentoRepository;
import web.taskflow.projeto.dto.ProjetoCreateRequest;
import web.taskflow.projeto.dto.ProjetoResponse;
import web.taskflow.projeto.dto.ProjetoSearchRequest;
import web.taskflow.projeto.dto.ProjetoUpdateRequest;

@Service
@Transactional(readOnly = true)
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final DepartamentoRepository departamentoRepository;

    public ProjetoService(ProjetoRepository projetoRepository,
                          DepartamentoRepository departamentoRepository
    ) {
        this.projetoRepository = projetoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    @Transactional
    public void salvar(ProjetoCreateRequest form) {
        projetoRepository.save(toEntity(form));
    }

    @Transactional(readOnly = true)
    public Page<ProjetoResponse> pesquisar(
            ProjetoSearchRequest filtro,
            Pageable pageable) {

        Specification<Projeto> specification =
                ProjetoSpecifications.comFiltros(filtro);

        return projetoRepository
                .findAll(specification, pageable)
                .map(this::toResponse);
    }

    public ProjetoResponse buscarParaVisualizar(Long codigo) {
        Projeto projeto = projetoRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Projeto não encontrado."));
        return toResponse(projeto);
    }

    public ProjetoUpdateRequest buscarParaEdicao(Long codigo) {
        Projeto projeto = projetoRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Projeto não encontrado."));
        return toUpdateRequest(projeto);
    }

    @Transactional
    public void atualizar(Long codigo, ProjetoUpdateRequest request) {
        Projeto projeto = projetoRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Projeto não encontrado."));
        projeto.setNome(request.nome());
        projeto.setDescricao(request.descricao());
        projeto.setDataCriacao(LocalDate.now());
        projeto.setDataInicio(request.dataInicio());
        projeto.setDataFim(request.dataFim());
        projeto.setStatusProjeto(request.statusProjeto());
        projeto.setPrioridade(request.prioridade());
        Departamento departamento = departamentoRepository.findByCodigoAndStatus(request.codigoDepartamento(),Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Departamento não encotrado."));
        projeto.setDepartamento(departamento);
        projetoRepository.save(projeto);
    }

    @Transactional
    public void remover(Long codigo) {
        Projeto projeto = projetoRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Projeto não encontrado."));
        projeto.setStatus(Status.INATIVO);
        projetoRepository.save(projeto);
    }

    private Projeto toEntity(ProjetoCreateRequest request) {
        Projeto projeto = new Projeto();
        projeto.setNome(request.nome());
        projeto.setDescricao(request.descricao());
        projeto.setDataCriacao(LocalDate.now());
        projeto.setDataInicio(request.dataInicio());
        projeto.setDataFim(request.dataFim());
        projeto.setStatusProjeto(request.statusProjeto());
        projeto.setPrioridade(request.prioridade());
        Departamento departamento = departamentoRepository.findByCodigoAndStatus(request.codigoDepartamento(),Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Departamento não encotrado."));
        projeto.setDepartamento(departamento);
        return projeto;
    }

    private ProjetoResponse toResponse(Projeto projeto) {

        Departamento departamento = projeto.getDepartamento();

        return new ProjetoResponse(
            projeto.getCodigo(),
            projeto.getNome(),
            projeto.getDescricao(),
            projeto.getDataCriacao(),
            projeto.getDataInicio(),
            projeto.getDataFim(),
            projeto.getDataConclusao(),
            projeto.getStatusProjeto(),
            projeto.getPrioridade(),
            departamento != null ? departamento.getCodigo() : null,
            departamento != null ? departamento.getNome() : null
        );
    }

    private ProjetoUpdateRequest toUpdateRequest(Projeto projeto) {

        Departamento departamento = projeto.getDepartamento();

        return new ProjetoUpdateRequest(
            projeto.getNome(),
            projeto.getDescricao(),
            projeto.getDataInicio(),
            projeto.getDataFim(),
            projeto.getStatusProjeto(),
            projeto.getPrioridade(),
            departamento != null ? departamento.getCodigo() : null,
            departamento != null ? departamento.getNome() : null
        );
    }
}