package web.taskflow.tarefa;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.taskflow.common.enums.Prioridade;
import web.taskflow.common.enums.Status;
import web.taskflow.common.enums.StatusTarefa;
import web.taskflow.common.exception.EntidadeNaoEncontradaException;
import web.taskflow.departamento.Departamento;
import web.taskflow.empresa.Empresa;
import web.taskflow.funcionario.Funcionario;
import web.taskflow.funcionario.FuncionarioRepository;
import web.taskflow.projeto.Projeto;
import web.taskflow.projeto.ProjetoRepository;
import web.taskflow.tarefa.dto.TarefaCreateRequest;
import web.taskflow.tarefa.dto.TarefaResponse;
import web.taskflow.tarefa.dto.TarefaSearchRequest;
import web.taskflow.tarefa.dto.TarefaUpdateRequest;

@Service
@Transactional(readOnly = true)
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final ProjetoRepository projetoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public TarefaService(TarefaRepository tarefaRepository,
                         ProjetoRepository projetoRepository,
                         FuncionarioRepository funcionarioRepository
    ) {
        this.tarefaRepository = tarefaRepository;
        this.projetoRepository = projetoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Transactional
    public void salvar(TarefaCreateRequest form) {
        tarefaRepository.save(toEntity(form));
    }

    @Transactional(readOnly = true)
    public Page<TarefaResponse> pesquisar(
            TarefaSearchRequest filtro,
            Pageable pageable) {

        Specification<Tarefa> specification =
                TarefaSpecifications.comFiltros(filtro);

        return tarefaRepository
                .findAll(specification, pageable)
                .map(this::toResponse);
    }

    public TarefaResponse buscarParaVisualizar(Long codigo) {
        Tarefa tarefa = tarefaRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada."));
        return toResponse(tarefa);
    }

    public TarefaUpdateRequest buscarParaEdicao(Long codigo) {
        Tarefa tarefa = tarefaRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada."));
        return toUpdateRequest(tarefa);
    }

    @Transactional
    public void atualizar(Long codigo, TarefaUpdateRequest request) {
        Tarefa tarefa = tarefaRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada."));
        tarefa.setTitulo(request.titulo());
        tarefa.setDescricao(request.descricao());
        tarefa.setDataInicio(request.dataInicio());
        tarefa.setDataPrazo(request.dataPrazo());
        tarefa.setStatusTarefa(request.statusTarefa() != null ? request.statusTarefa() : StatusTarefa.A_FAZER);
        tarefa.setPrioridade(request.prioridade() != null ? request.prioridade() : Prioridade.MEDIA);
        atualizarDataConclusao(tarefa);
        Projeto projeto = buscarProjeto(request.codigoProjeto());
        tarefa.setProjeto(projeto);
        tarefa.setEmpresa(buscarEmpresaDoProjeto(projeto));
        tarefa.setFuncionarioResponsavel(buscarFuncionario(request.codigoFuncionarioResponsavel()));
        tarefaRepository.save(tarefa);
    }

    /**
     * Remove (logicamente) a tarefa e devolve o código do projeto
     * dela, para que o controller possa voltar ao quadro do projeto.
     */
    @Transactional
    public Long remover(Long codigo) {
        Tarefa tarefa = tarefaRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada."));
        tarefa.setStatus(Status.INATIVO);
        tarefaRepository.save(tarefa);
        return tarefa.getProjeto() != null ? tarefa.getProjeto().getCodigo() : null;
    }

    private Tarefa toEntity(TarefaCreateRequest request) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(request.titulo());
        tarefa.setDescricao(request.descricao());
        tarefa.setDataCriacao(LocalDate.now());
        tarefa.setDataInicio(request.dataInicio());
        tarefa.setDataPrazo(request.dataPrazo());
        tarefa.setStatusTarefa(request.statusTarefa() != null ? request.statusTarefa() : StatusTarefa.A_FAZER);
        tarefa.setPrioridade(request.prioridade() != null ? request.prioridade() : Prioridade.MEDIA);
        atualizarDataConclusao(tarefa);
        Projeto projeto = buscarProjeto(request.codigoProjeto());
        tarefa.setProjeto(projeto);
        tarefa.setEmpresa(buscarEmpresaDoProjeto(projeto));
        tarefa.setFuncionarioResponsavel(buscarFuncionario(request.codigoFuncionarioResponsavel()));
        return tarefa;
    }

    private Projeto buscarProjeto(Long codigoProjeto) {
        return projetoRepository.findByCodigoAndStatus(codigoProjeto, Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Projeto não encontrado."));
    }

    // A empresa da tarefa é a mesma do departamento do projeto
    private Empresa buscarEmpresaDoProjeto(Projeto projeto) {
        Departamento departamento = projeto.getDepartamento();
        return departamento != null ? departamento.getEmpresa() : null;
    }

    // O funcionário responsável é opcional
    private Funcionario buscarFuncionario(Long codigoFuncionario) {
        if (codigoFuncionario == null) {
            return null;
        }
        return funcionarioRepository.findByCodigoAndStatus(codigoFuncionario, Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado."));
    }

    // Tarefa concluída recebe a data de conclusão; ao sair de "Feito", ela é limpa
    private void atualizarDataConclusao(Tarefa tarefa) {
        if (tarefa.getStatusTarefa() == StatusTarefa.FEITO) {
            if (tarefa.getDataConclusao() == null) {
                tarefa.setDataConclusao(LocalDate.now());
            }
        } else {
            tarefa.setDataConclusao(null);
        }
    }

    private TarefaResponse toResponse(Tarefa tarefa) {

        Empresa empresa = tarefa.getEmpresa();
        Projeto projeto = tarefa.getProjeto();
        Funcionario funcionario = tarefa.getFuncionarioResponsavel();

        return new TarefaResponse(
            tarefa.getCodigo(),
            tarefa.getTitulo(),
            tarefa.getDescricao(),
            tarefa.getDataCriacao(),
            tarefa.getDataInicio(),
            tarefa.getDataPrazo(),
            tarefa.getDataConclusao(),
            tarefa.getStatusTarefa(),
            tarefa.getPrioridade(),
            empresa != null ? empresa.getCodigo() : null,
            empresa != null ? empresa.getNomeFantasia() : null,
            projeto != null ? projeto.getCodigo() : null,
            projeto != null ? projeto.getNome() : null,
            funcionario != null ? funcionario.getCodigo() : null,
            funcionario != null ? funcionario.getNome() : null
        );
    }

    private TarefaUpdateRequest toUpdateRequest(Tarefa tarefa) {

        Projeto projeto = tarefa.getProjeto();
        Funcionario funcionario = tarefa.getFuncionarioResponsavel();

        return new TarefaUpdateRequest(
            tarefa.getTitulo(),
            tarefa.getDescricao(),
            tarefa.getDataInicio(),
            tarefa.getDataPrazo(),
            tarefa.getStatusTarefa(),
            tarefa.getPrioridade(),
            projeto != null ? projeto.getCodigo() : null,
            projeto != null ? projeto.getNome() : null,
            funcionario != null ? funcionario.getCodigo() : null,
            funcionario != null ? funcionario.getNome() : null
        );
    }
}
