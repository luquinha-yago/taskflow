package web.taskflow.projeto;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import web.taskflow.common.enums.Prioridade;
import web.taskflow.common.enums.StatusProjeto;
import web.taskflow.core.web.notification.NotificacaoSweetAlert2;
import web.taskflow.core.web.notification.TipoNotificacaoSweetAlert2;
import web.taskflow.core.web.pagination.PageWrapper;
import web.taskflow.departamento.DepartamentoService;
import web.taskflow.departamento.dto.DepartamentoResponse;
import web.taskflow.projeto.dto.ProjetoCreateRequest;
import web.taskflow.projeto.dto.ProjetoResponse;
import web.taskflow.projeto.dto.ProjetoSearchRequest;
import web.taskflow.projeto.dto.ProjetoUpdateRequest;

@Slf4j
@Controller
@RequestMapping("/projetos")
public class ProjetoController {

    private final ProjetoService projetoService;
    private final DepartamentoService departamentoService;

    public ProjetoController(ProjetoService projetoService,
                             DepartamentoService departamentoService
    ) {
        this.projetoService = projetoService;
        this.departamentoService = departamentoService;
    }

    @GetMapping("/cadastrar")
    public String abrirFormulario(ProjetoCreateRequest request,
                                  Model model,
                                  HtmxRequest htmxRequest) {
        model.addAttribute("statusProjetos", StatusProjeto.values());
        model.addAttribute("prioridades", Prioridade.values());
        return htmxRequest.isHtmxRequest() ? "projetos/create :: formulario" : "projetos/create";
    }

    @PostMapping
    public String cadastrar(@Valid ProjetoCreateRequest form,
                            BindingResult resultado,
                            RedirectAttributes redirect,
                            HtmxRequest htmxRequest) {
        if (resultado.hasErrors()) {
            log.info("O projeto para cadastrar não é válido.");
            log.info("Erros encontrados:");
            for (FieldError erro : resultado.getFieldErrors()) {
                log.info("{}", erro);
            }
            for (ObjectError erro : resultado.getGlobalErrors()) {
                log.info("{}", erro);
            }
            return htmxRequest.isHtmxRequest() ? "projetos/create :: formulario" : "projetos/create";
        } else {
            projetoService.salvar(form);
            redirect.addFlashAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("Projeto criado com sucesso!",
                            TipoNotificacaoSweetAlert2.SUCCESS, 4000));

            return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:/projetos" : "redirect:/projetos";
        }
    }

    @GetMapping
    public String pesquisar(ProjetoSearchRequest form, Model model,
                            @PageableDefault(size = 7) @SortDefault(sort = "codigo", direction = Sort.Direction.ASC) Pageable pageable,
                            HttpServletRequest request,
                            HtmxRequest htmxRequest) {
        Page<ProjetoResponse> pagina = projetoService.pesquisar(form, pageable);
        log.info("Projetos pesquisados: {}", pagina.getContent());
        if (pagina.getContent().isEmpty()) {
            model.addAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("A busca não encontrou projetos.",
                            TipoNotificacaoSweetAlert2.INFO, 4000));
        }
        PageWrapper<ProjetoResponse> paginaWrapper = new PageWrapper<>(pagina, request);
        model.addAttribute("statusProjetos", StatusProjeto.values());
        model.addAttribute("prioridades", Prioridade.values());
        model.addAttribute("pagina", paginaWrapper);
        return htmxRequest.isHtmxRequest() ? "projetos/lista :: conteudo" : "projetos/lista";
    }

    @GetMapping("/{codigo}/editar")
    public String abrirEditar(@PathVariable("codigo") Long codigo, Model model,
                              HtmxRequest htmxRequest) {
        ProjetoUpdateRequest request = projetoService.buscarParaEdicao(codigo);
        model.addAttribute("projetoUpdateRequest", request);
        model.addAttribute("codigo", codigo);
        model.addAttribute("statusProjetos", StatusProjeto.values());
        model.addAttribute("prioridades", Prioridade.values());
        return htmxRequest.isHtmxRequest() ? "projetos/edit :: formulario" : "projetos/edit";
    }

    @PutMapping("/{codigo}")
    public String editar(@PathVariable("codigo") Long codigo,
                         @Valid ProjetoUpdateRequest request,
                         BindingResult resultado,
                         Model model,
                         RedirectAttributes redirect,
                         HtmxRequest htmxRequest) {
        if (resultado.hasErrors()) {
            log.info("O projeto recebido para editar não é válido.");
            log.info("Erros encontrados:");
            for (FieldError erro : resultado.getFieldErrors()) {
                log.info("{}", erro);
            }
            for (ObjectError erro : resultado.getGlobalErrors()) {
                log.info("{}", erro);
            }
            model.addAttribute("codigo", codigo);
            return htmxRequest.isHtmxRequest() ? "projetos/edit :: formulario" : "projetos/edit";
        } else {
            projetoService.atualizar(codigo, request);
            redirect.addFlashAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("Projeto alterado com sucesso!",
                            TipoNotificacaoSweetAlert2.SUCCESS, 4000));
            return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:/projetos" : "redirect:/projetos";
        }
    }

    @DeleteMapping("/{codigo}")
    public String remover(@PathVariable("codigo") Long codigo,
                          RedirectAttributes redirect,
                          HtmxRequest htmxRequest) {
        projetoService.remover(codigo);
        redirect.addFlashAttribute("notificacaoSA2",
                new NotificacaoSweetAlert2("Projeto removido com sucesso!",
                        TipoNotificacaoSweetAlert2.SUCCESS, 4000));
        return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:/projetos" : "redirect:/projetos";
    }

    @GetMapping("/{codigo}")
    public String abrirProjeto(@PathVariable("codigo") Long codigo, Model model,
                              HtmxRequest htmxRequest){
        ProjetoResponse projeto = projetoService.buscarParaVisualizar(codigo);
        model.addAttribute("projeto", projeto);
        return htmxRequest.isHtmxRequest() ? "projetos/visualizar :: conteudo" : "projetos/visualizar";
    }

    @GetMapping("/pesquisardepartamento")
    public String pesquisarDepartamento(String termoBusca, Model model) {
        List<DepartamentoResponse> departamento = departamentoService.buscarPeloNome(termoBusca);
        log.debug("Departamentos buscados: {}", departamento);
        model.addAttribute("departamentos", departamento);
        return "departamentos/list_li :: lista";
    }

}
