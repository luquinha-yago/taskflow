package web.taskflow.tarefa;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import web.taskflow.common.enums.Prioridade;
import web.taskflow.common.enums.StatusTarefa;
import web.taskflow.core.web.notification.NotificacaoSweetAlert2;
import web.taskflow.core.web.notification.TipoNotificacaoSweetAlert2;
import web.taskflow.funcionario.FuncionarioService;
import web.taskflow.funcionario.dto.FuncionarioResponse;
import web.taskflow.projeto.ProjetoService;
import web.taskflow.projeto.dto.ProjetoResponse;
import web.taskflow.tarefa.dto.TarefaCreateRequest;
import web.taskflow.tarefa.dto.TarefaResponse;
import web.taskflow.tarefa.dto.TarefaUpdateRequest;

@Slf4j
@Controller
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;
    private final ProjetoService projetoService;
    private final FuncionarioService funcionarioService;

    public TarefaController(TarefaService tarefaService,
                            ProjetoService projetoService,
                            FuncionarioService funcionarioService
    ) {
        this.tarefaService = tarefaService;
        this.projetoService = projetoService;
        this.funcionarioService = funcionarioService;
    }

    /*
     * A tarefa sempre é criada dentro de um projeto (botão "+" da raia do kanban).
     * O projeto e a situação vêm da raia que foi clicada.
     */
    @GetMapping("/cadastrar")
    public String abrirFormulario(@RequestParam("codigoProjeto") Long codigoProjeto,
                                  @RequestParam(value = "statusTarefa", required = false) StatusTarefa statusTarefa,
                                  Model model,
                                  HtmxRequest htmxRequest) {
        ProjetoResponse projeto = projetoService.buscarParaVisualizar(codigoProjeto);
        TarefaCreateRequest request = new TarefaCreateRequest(
                null, null, null, null,
                statusTarefa != null ? statusTarefa : StatusTarefa.A_FAZER,
                Prioridade.MEDIA,
                projeto.codigo(), projeto.nome(),
                null, null);
        model.addAttribute("tarefaCreateRequest", request);
        adicionarOpcoes(model);
        return htmxRequest.isHtmxRequest() ? "tarefas/create :: formulario" : "tarefas/create";
    }

    @PostMapping
    public String cadastrar(@Valid TarefaCreateRequest form,
                            BindingResult resultado,
                            Model model,
                            RedirectAttributes redirect,
                            HtmxRequest htmxRequest) {
        if (resultado.hasErrors()) {
            log.info("A tarefa para cadastrar não é válida.");
            log.info("Erros encontrados:");
            for (FieldError erro : resultado.getFieldErrors()) {
                log.info("{}", erro);
            }
            for (ObjectError erro : resultado.getGlobalErrors()) {
                log.info("{}", erro);
            }
            adicionarOpcoes(model);
            return htmxRequest.isHtmxRequest() ? "tarefas/create :: formulario" : "tarefas/create";
        } else {
            tarefaService.salvar(form);
            redirect.addFlashAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("Tarefa criada com sucesso!",
                            TipoNotificacaoSweetAlert2.SUCCESS, 4000));
            String urlProjeto = "/projetos/" + form.codigoProjeto();
            return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:" + urlProjeto : "redirect:" + urlProjeto;
        }
    }

    @GetMapping("/{codigo}")
    public String abrirTarefa(@PathVariable("codigo") Long codigo, Model model,
                              HtmxRequest htmxRequest) {
        TarefaResponse tarefa = tarefaService.buscarParaVisualizar(codigo);
        model.addAttribute("tarefa", tarefa);
        return htmxRequest.isHtmxRequest() ? "tarefas/visualizar :: conteudo" : "tarefas/visualizar";
    }

    @GetMapping("/{codigo}/editar")
    public String abrirEditar(@PathVariable("codigo") Long codigo, Model model,
                              HtmxRequest htmxRequest) {
        TarefaUpdateRequest request = tarefaService.buscarParaEdicao(codigo);
        model.addAttribute("tarefaUpdateRequest", request);
        model.addAttribute("codigo", codigo);
        adicionarOpcoes(model);
        return htmxRequest.isHtmxRequest() ? "tarefas/edit :: formulario" : "tarefas/edit";
    }

    @PutMapping("/{codigo}")
    public String editar(@PathVariable("codigo") Long codigo,
                         @Valid TarefaUpdateRequest request,
                         BindingResult resultado,
                         Model model,
                         RedirectAttributes redirect,
                         HtmxRequest htmxRequest) {
        if (resultado.hasErrors()) {
            log.info("A tarefa recebida para editar não é válida.");
            log.info("Erros encontrados:");
            for (FieldError erro : resultado.getFieldErrors()) {
                log.info("{}", erro);
            }
            for (ObjectError erro : resultado.getGlobalErrors()) {
                log.info("{}", erro);
            }
            model.addAttribute("codigo", codigo);
            adicionarOpcoes(model);
            return htmxRequest.isHtmxRequest() ? "tarefas/edit :: formulario" : "tarefas/edit";
        } else {
            tarefaService.atualizar(codigo, request);
            redirect.addFlashAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("Tarefa alterada com sucesso!",
                            TipoNotificacaoSweetAlert2.SUCCESS, 4000));
            String urlTarefa = "/tarefas/" + codigo;
            return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:" + urlTarefa : "redirect:" + urlTarefa;
        }
    }

    @DeleteMapping("/{codigo}")
    public String remover(@PathVariable("codigo") Long codigo,
                          RedirectAttributes redirect,
                          HtmxRequest htmxRequest) {
        Long codigoProjeto = tarefaService.remover(codigo);
        redirect.addFlashAttribute("notificacaoSA2",
                new NotificacaoSweetAlert2("Tarefa removida com sucesso!",
                        TipoNotificacaoSweetAlert2.SUCCESS, 4000));
        String urlProjeto = codigoProjeto != null ? "/projetos/" + codigoProjeto : "/projetos";
        return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:" + urlProjeto : "redirect:" + urlProjeto;
    }

    @GetMapping("/pesquisarfuncionario")
    public String pesquisarFuncionario(String termoBusca, Model model) {
        List<FuncionarioResponse> funcionarios = funcionarioService.buscarPeloNome(termoBusca);
        log.debug("Funcionários buscados: {}", funcionarios);
        model.addAttribute("funcionarios", funcionarios);
        return "funcionarios/list_li :: lista";
    }

    private void adicionarOpcoes(Model model) {
        model.addAttribute("statusTarefas", StatusTarefa.values());
        model.addAttribute("prioridades", Prioridade.values());
    }
}
