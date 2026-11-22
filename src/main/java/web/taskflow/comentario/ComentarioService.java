package web.taskflow.comentario;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import web.taskflow.comentario.dto.ComentarioCreateRequest;
import web.taskflow.comentario.dto.ComentarioResponse;
import web.taskflow.comentario.dto.ComentarioSearchRequest;
import web.taskflow.common.enums.Status;
import web.taskflow.common.exception.EntidadeNaoEncontradaException;
import web.taskflow.common.exception.PrazoOperacaoComentarioExpiradoException;
import web.taskflow.funcionario.Funcionario;
import web.taskflow.funcionario.FuncionarioRepository;
import web.taskflow.tarefa.Tarefa;
import web.taskflow.tarefa.TarefaRepository;

@Service
@Transactional(readOnly = true)
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final TarefaRepository tarefaRepository;
    private final FuncionarioRepository funcionarioRepository;

    public ComentarioService(ComentarioRepository comentarioRepository,
                             TarefaRepository tarefaRepository,
                             FuncionarioRepository funcionarioRepository
    ){
        this.comentarioRepository = comentarioRepository;
        this.tarefaRepository = tarefaRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Transactional
    public void salvar(ComentarioCreateRequest form) {
        comentarioRepository.save(toEntity(form));
    }

    public Page<ComentarioResponse> pesquisar(
            ComentarioSearchRequest filtro,
            Long codigoTarefa,
            Pageable pageable) {

        Specification<Comentario> specification =
                ComentarioSpecifications.comFiltros(filtro,codigoTarefa);

        return comentarioRepository
                .findAll(specification, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public Long editar(Long codigo, String novoComentario) {

        Comentario comentario = comentarioRepository.findById(codigo)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Comentário não encontrado."
                ));

        Long codigoTarefa = comentario.getTarefa().getCodigo();

        validarPrazoOperacao(
                comentario,
                Duration.ofMinutes(1),
                "excluir"
        );

        comentario.setComentario(novoComentario);
        comentarioRepository.save(comentario);
        return codigoTarefa;
    }

    @Transactional
    public Long excluir(Long codigo) {

        Comentario comentario = comentarioRepository.findById(codigo)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Comentário não encontrado."
                ));

        Long codigoTarefa = comentario.getTarefa().getCodigo();

        validarPrazoOperacao(
                comentario,
                Duration.ofMinutes(1),
                "excluir"
        );

        comentario.setStatus(Status.INATIVO);

        return codigoTarefa;
    }   

    private void validarPrazoOperacao(
        Comentario comentario,
        Duration prazo,
        String operacao) {

        LocalDateTime limite = comentario.getDataHoraRegistro().plus(prazo);

        if (LocalDateTime.now().isAfter(limite)) {
            throw new PrazoOperacaoComentarioExpiradoException(
                    "O prazo para " + operacao + " deste comentário expirou.",
                    comentario.getTarefa().getCodigo()
            );
        }
    }

    private Funcionario buscarFuncionario(Long codigoFuncionario) {
        if (codigoFuncionario == null) {
            return null;
        }
        return funcionarioRepository.findByCodigoAndStatus(codigoFuncionario, Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Funcionário não encontrado."));
    }
    
    public Long buscarCodigoTarefa(Long codigo) {

        Comentario comentario = comentarioRepository.findById(codigo)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Comentário não encontrado."
                ));

        return comentario.getTarefa().getCodigo();
    }

    private Tarefa buscarTarefa(Long codigoTarefa) {
        if (codigoTarefa == null) {
            return null;
        }
        return tarefaRepository.findByCodigoAndStatus(codigoTarefa, Status.ATIVO)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tarefa não encontrada."));
    }

    private Comentario toEntity(ComentarioCreateRequest request) {
        Comentario comentario = new Comentario();
        comentario.setComentario(request.comentario());
        comentario.setDataHoraRegistro(LocalDateTime.now());
        Funcionario funcionario = buscarFuncionario(request.codigoFuncionario());
        comentario.setFuncionario(funcionario);
        Tarefa tarefa = buscarTarefa(request.codigoTarefa());
        comentario.setTarefa(tarefa);
        return comentario;
    }

    private ComentarioResponse toResponse(Comentario comentario) {

        return new ComentarioResponse(
            comentario.getCodigo(),
            comentario.getComentario(),
            comentario.getDataHoraRegistro(),

            comentario.getFuncionario() != null
                ? comentario.getFuncionario().getCodigo()
                : null,

            comentario.getFuncionario() != null
                ? comentario.getFuncionario().getNome()
                : null,

            comentario.getTarefa() != null
                ? comentario.getTarefa().getCodigo()
                : null,

            comentario.getTarefa() != null
                ? comentario.getTarefa().getTitulo()
                : null
        );
    }

    
}