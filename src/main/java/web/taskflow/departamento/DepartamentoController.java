package web.taskflow.departamento;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import web.taskflow.core.web.notification.NotificacaoSweetAlert2;
import web.taskflow.core.web.notification.TipoNotificacaoSweetAlert2;
import web.taskflow.core.web.pagination.PageWrapper;
import web.taskflow.departamento.dto.DepartamentoCreateRequest;
import web.taskflow.departamento.dto.DepartamentoResponse;
import web.taskflow.departamento.dto.DepartamentoSearchRequest;
import web.taskflow.departamento.dto.DepartamentoUpdateRequest;

@Slf4j
@Controller
@RequestMapping("/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping("/cadastrar")
    public String abrirFormulario(DepartamentoCreateRequest request,
                                  HtmxRequest htmxRequest) {
        return htmxRequest.isHtmxRequest() ? "departamentos/create :: formulario" : "departamentos/create";
    }

    @PostMapping
    public String cadastrar(@Valid DepartamentoCreateRequest form,
                            BindingResult resultado,
                            RedirectAttributes redirect,
                            HtmxRequest htmxRequest) {
        if (resultado.hasErrors()) {
            log.info("O departamento para cadastrar não é válido.");
            logarErros(resultado);
            return htmxRequest.isHtmxRequest() ? "departamentos/create :: formulario" : "departamentos/create";
        } else {
            departamentoService.salvar(form);

            redirect.addFlashAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("Departamento criado com sucesso!",
                            TipoNotificacaoSweetAlert2.SUCCESS, 4000));

            return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:/departamentos" : "redirect:/departamentos";
        }
    }

    @GetMapping
    public String pesquisar(DepartamentoSearchRequest form, Model model,
                            @PageableDefault(size = 7) @SortDefault(sort = "codigo", direction = Sort.Direction.ASC) Pageable pageable,
                            HttpServletRequest request,
                            HtmxRequest htmxRequest) {
        Page<DepartamentoResponse> pagina = departamentoService.pesquisar(form, pageable);
        log.info("Departamentos pesquisados: {}", pagina.getContent());
        if (pagina.getContent().isEmpty()) {
            model.addAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("A busca não encontrou departamentos.",
                            TipoNotificacaoSweetAlert2.INFO, 4000));
        }
        PageWrapper<DepartamentoResponse> paginaWrapper = new PageWrapper<>(pagina, request);
        model.addAttribute("pagina", paginaWrapper);
        return htmxRequest.isHtmxRequest() ? "departamentos/lista :: conteudo" : "departamentos/lista";
    }

    @GetMapping("/{codigo}/editar")
    public String abrirEditar(@PathVariable("codigo") Long codigo, Model model,
                              HtmxRequest htmxRequest) {
        DepartamentoUpdateRequest request = departamentoService.buscarParaEdicao(codigo);
        model.addAttribute("departamentoUpdateRequest", request);
        model.addAttribute("codigo", codigo);
        return htmxRequest.isHtmxRequest() ? "departamentos/edit :: formulario" : "departamentos/edit";
    }

    @PutMapping("/{codigo}")
    public String editar(@PathVariable("codigo") Long codigo,
                         @Valid DepartamentoUpdateRequest request,
                         BindingResult resultado,
                         Model model,
                         RedirectAttributes redirect,
                         HtmxRequest htmxRequest) {
        if (resultado.hasErrors()) {
            log.info("O departamento recebido para editar não é válido.");
            logarErros(resultado);
            model.addAttribute("codigo", codigo);
            return htmxRequest.isHtmxRequest() ? "departamentos/edit :: formulario" : "departamentos/edit";
        } else {
            departamentoService.atualizar(codigo, request);
            redirect.addFlashAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("Departamento alterado com sucesso!",
                            TipoNotificacaoSweetAlert2.SUCCESS, 4000));
            return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:/departamentos" : "redirect:/departamentos";
        }
    }

    @DeleteMapping("/{codigo}")
    public String remover(@PathVariable("codigo") Long codigo,
                          RedirectAttributes redirect,
                          HtmxRequest htmxRequest) {
        departamentoService.remover(codigo);
        redirect.addFlashAttribute("notificacaoSA2",
                new NotificacaoSweetAlert2("Departamento removido com sucesso!",
                        TipoNotificacaoSweetAlert2.SUCCESS, 4000));
        return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:/departamentos" : "redirect:/departamentos";
    }

    /** Opções do campo "Empresa" (inputBuscavel) nos formulários de departamento. */
    @GetMapping("/opcoes/empresas")
    public String opcoesEmpresa(@RequestParam(name = "termoBusca", required = false) String termo,
                                Model model) {
        model.addAttribute("empresas", departamentoService.buscarOpcoesEmpresa(termo));
        return "departamentos/fragments/opcoes :: opcoesEmpresa";
    }

    /** Opções do campo "Coordenador" (inputBuscavel) nos formulários de departamento. */
    @GetMapping("/opcoes/funcionarios")
    public String opcoesFuncionario(@RequestParam(name = "termoBusca", required = false) String termo,
                                    Model model) {
        model.addAttribute("funcionarios", departamentoService.buscarOpcoesFuncionario(termo));
        return "departamentos/fragments/opcoes :: opcoesFuncionario";
    }

    private void logarErros(BindingResult resultado) {
        log.info("Erros encontrados:");
        for (FieldError erro : resultado.getFieldErrors()) {
            log.info("{}", erro);
        }
        for (ObjectError erro : resultado.getGlobalErrors()) {
            log.info("{}", erro);
        }
    }
}
