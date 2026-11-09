package web.taskflow.funcionario;

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
import web.taskflow.funcionario.dto.FuncionarioCreateRequest;
import web.taskflow.funcionario.dto.FuncionarioResponse;
import web.taskflow.funcionario.dto.FuncionarioSearchRequest;
import web.taskflow.funcionario.dto.FuncionarioUpdateRequest;

@Slf4j
@Controller
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService){
        this.funcionarioService = funcionarioService;
    }

    @GetMapping("/cadastrar")
    public String abrirFormulario(FuncionarioCreateRequest request,
            HtmxRequest htmxRequest) {
        return htmxRequest.isHtmxRequest() ? "funcionarios/create :: formulario" : "funcionarios/create";
    }

    @PostMapping
    public String cadastrar(@Valid FuncionarioCreateRequest form,
            BindingResult resultado,
            RedirectAttributes redirect,
            HtmxRequest htmxRequest) {
        if (resultado.hasErrors()) {
            log.info("O funcionário para cadastrar não é válido.");
            log.info("Erros encontrados:");
            for (FieldError erro : resultado.getFieldErrors()) {
                log.info("{}", erro);
            }
            for (ObjectError erro : resultado.getGlobalErrors()) {
                log.info("{}", erro);
            }
            return htmxRequest.isHtmxRequest() ? "funcionarios/create :: formulario" : "funcionarios/create";
        } else {
            funcionarioService.salvar(form);

            redirect.addFlashAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("Funcionário salvo com sucesso!",
                            TipoNotificacaoSweetAlert2.SUCCESS, 4000));

            return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:/funcionarios" : "redirect:/funcionarios";
        }
    }

    @GetMapping
    public String pesquisar(FuncionarioSearchRequest form, Model model,
            @PageableDefault(size = 7) @SortDefault(sort = "codigo", direction = Sort.Direction.ASC) Pageable pageable,
            HttpServletRequest request,
            HtmxRequest htmxRequest) {
        Page<FuncionarioResponse> pagina = funcionarioService.pesquisar(form, pageable);
        log.info("Funcionários pesquisados: {}", pagina.getContent());
        if (pagina.getContent().isEmpty()) {
            model.addAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("A busca não encontrou funcionários",
                            TipoNotificacaoSweetAlert2.INFO, 4000));
        }
        PageWrapper<FuncionarioResponse> paginaWrapper = new PageWrapper<>(pagina, request);
        model.addAttribute("pagina", paginaWrapper);
        return htmxRequest.isHtmxRequest() ? "funcionarios/lista :: conteudo" : "funcionarios/lista";
    }

    @GetMapping("/{codigo}/editar")
    public String abrirEditar(@PathVariable("codigo") Long codigo, Model model,
            HtmxRequest htmxRequest) {
        FuncionarioUpdateRequest request = funcionarioService.buscarParaEdicao(codigo);
        model.addAttribute("funcionarioUpdateRequest", request);
        model.addAttribute("codigo", codigo);
        return htmxRequest.isHtmxRequest() ? "funcionarios/edit :: formulario" : "funcionarios/edit";
    }

    @PutMapping("/{codigo}")
    public String editar(@PathVariable("codigo") Long codigo,
            @Valid FuncionarioUpdateRequest request,
            BindingResult resultado,
            RedirectAttributes redirect,
            HtmxRequest htmxRequest) {
        if (resultado.hasErrors()) {
            log.info("O funcionário recebido para editar não é válido.");
            log.info("Erros encontrados:");
            for (FieldError erro : resultado.getFieldErrors()) {
                log.info("{}", erro);
            }
            for (ObjectError erro : resultado.getGlobalErrors()) {
                log.info("{}", erro);
            }
            return htmxRequest.isHtmxRequest() ? "funcionarios/edit :: formulario" : "funcionarios/edit";
        } else {
            funcionarioService.atualizar(codigo, request);
            redirect.addFlashAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("Funcionário alterada com sucesso!",
                            TipoNotificacaoSweetAlert2.SUCCESS, 4000));
            return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:/funcionarios" : "redirect:/funcionarios";
        }
    }

    @DeleteMapping("/{codigo}")
    public String remover(@PathVariable("codigo") Long codigo,
            RedirectAttributes redirect,
            HtmxRequest htmxRequest) {
        funcionarioService.remover(codigo);
        redirect.addFlashAttribute("notificacaoSA2",
                new NotificacaoSweetAlert2("Funcionário removido com sucesso!",
                        TipoNotificacaoSweetAlert2.SUCCESS, 4000));
        return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:/funcionarios" : "redirect:/funcionarios";
    }
}
