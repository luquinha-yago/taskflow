package web.taskflow.empresa;

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
import web.taskflow.core.web.notification.NotificacaoSweetAlert2;
import web.taskflow.core.web.notification.TipoNotificacaoSweetAlert2;
import web.taskflow.core.web.pagination.PageWrapper;
import web.taskflow.empresa.dto.EmpresaCreateRequest;
import web.taskflow.empresa.dto.EmpresaResponse;
import web.taskflow.empresa.dto.EmpresaSearchRequest;
import web.taskflow.empresa.dto.EmpresaUpdateRequest;

@Slf4j
@Controller
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController (EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping("/cadastrar")
    public String abrirFormulario(EmpresaCreateRequest request,
            HtmxRequest htmxRequest) {
        return htmxRequest.isHtmxRequest() ? "empresas/create :: formulario" : "empresas/create";
    }

    @PostMapping
    public String cadastrar(@Valid EmpresaCreateRequest form,
            BindingResult resultado,
            RedirectAttributes redirect,
            HtmxRequest htmxRequest) {
        if (resultado.hasErrors()) {
            log.info("O empresa para cadastrar não é válida.");
            log.info("Erros encontrados:");
            for (FieldError erro : resultado.getFieldErrors()) {
                log.info("{}", erro);
            }
            for (ObjectError erro : resultado.getGlobalErrors()) {
                log.info("{}", erro);
            }
            return htmxRequest.isHtmxRequest() ? "empresas/create :: formulario" : "empresas/create";
        } else {
            empresaService.salvar(form);

            redirect.addFlashAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("Empresas criada com sucesso!",
                            TipoNotificacaoSweetAlert2.SUCCESS, 4000));

            return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:/empresas" : "redirect:/empresas";
        }
    }

    @GetMapping
    public String pesquisar(EmpresaSearchRequest form, Model model,
            @PageableDefault(size = 7) @SortDefault(sort = "codigo", direction = Sort.Direction.ASC) Pageable pageable,
            HttpServletRequest request,
            HtmxRequest htmxRequest) {
        Page<EmpresaResponse> pagina = empresaService.pesquisar(form, pageable);
        log.info("Empresas Pesquisadas: {}", pagina.getContent());
        if (pagina.getContent().isEmpty()) {
            model.addAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("A busca não encontrou empresas.",
                            TipoNotificacaoSweetAlert2.INFO, 4000));
        }
        PageWrapper<EmpresaResponse> paginaWrapper = new PageWrapper<>(pagina, request);
        model.addAttribute("pagina", paginaWrapper);
        return htmxRequest.isHtmxRequest() ? "empresas/lista :: conteudo" : "empresas/lista";
    }

    @GetMapping("/{codigo}/editar")
    public String abrirEditar(@PathVariable("codigo") Long codigo, Model model,
            HtmxRequest htmxRequest) {
        EmpresaUpdateRequest request = empresaService.buscarParaEdicao(codigo);
        model.addAttribute("empresaUpdateRequest", request);
        model.addAttribute("codigo", codigo);
        return htmxRequest.isHtmxRequest() ? "empresas/edit :: formulario" : "empresas/edit";
    }

    @PutMapping("/{codigo}")
    public String editar(@PathVariable("codigo") Long codigo,
            @Valid EmpresaUpdateRequest request,
            BindingResult resultado,
            RedirectAttributes redirect,
            HtmxRequest htmxRequest) {
        if (resultado.hasErrors()) {
            log.info("A empresa recebida para editar não é válida.");
            log.info("Erros encontrados:");
            for (FieldError erro : resultado.getFieldErrors()) {
                log.info("{}", erro);
            }
            for (ObjectError erro : resultado.getGlobalErrors()) {
                log.info("{}", erro);
            }
            return htmxRequest.isHtmxRequest() ? "empresas/edit :: formulario" : "empresas/edit";
        } else {
            empresaService.atualizar(codigo, request);
            redirect.addFlashAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("Empresa alterada com sucesso!",
                            TipoNotificacaoSweetAlert2.SUCCESS, 4000));
            return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:/empresas" : "redirect:/empresas";
        }
    }

    @DeleteMapping("/{codigo}")
    public String remover(@PathVariable("codigo") Long codigo,
            RedirectAttributes redirect,
            HtmxRequest htmxRequest) {
        empresaService.remover(codigo);
        redirect.addFlashAttribute("notificacaoSA2",
                new NotificacaoSweetAlert2("Empresa removida com sucesso!",
                        TipoNotificacaoSweetAlert2.SUCCESS, 4000));
        return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:/empresas" : "redirect:/empresas";
    }

}
