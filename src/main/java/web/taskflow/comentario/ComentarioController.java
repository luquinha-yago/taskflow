package web.taskflow.comentario;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import web.taskflow.comentario.dto.ComentarioCreateRequest;
import web.taskflow.comentario.dto.ComentarioUpdateRequest;
import web.taskflow.common.exception.PrazoOperacaoComentarioExpiradoException;
import web.taskflow.core.web.notification.NotificacaoSweetAlert2;
import web.taskflow.core.web.notification.TipoNotificacaoSweetAlert2;
import web.taskflow.funcionario.FuncionarioService;
import web.taskflow.funcionario.dto.FuncionarioResponse;

@Slf4j
@Controller
@RequestMapping("/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final FuncionarioService funcionarioService;

    public ComentarioController(ComentarioService comentarioService,
                                FuncionarioService funcionarioService
    ) {
        this.comentarioService = comentarioService;
        this.funcionarioService = funcionarioService;
    }

    @PostMapping
    public String cadastrar(@Valid ComentarioCreateRequest form,
                            BindingResult resultado,
                            Model model,
                            RedirectAttributes redirect,
                            HtmxRequest htmxRequest) {
        if (resultado.hasErrors()) {
            log.info("O comentário não é válido.");
            log.info("Erros encontrados:");
            for (FieldError erro : resultado.getFieldErrors()) {
                log.info("{}", erro);
            }
            for (ObjectError erro : resultado.getGlobalErrors()) {
                log.info("{}", erro);
            }
            String urlTarefa = "/tarefas/" + form.codigoTarefa();
            redirect.addFlashAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("Comente ao menos alguma coisa!",
                            TipoNotificacaoSweetAlert2.ERROR, 4000));
            return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:" + urlTarefa : "redirect:" + urlTarefa;
        } else {
            comentarioService.salvar(form);
            redirect.addFlashAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("Comentário postado com sucesso!",
                            TipoNotificacaoSweetAlert2.SUCCESS, 4000));
            String urlTarefa = "/tarefas/" + form.codigoTarefa();
            return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:" + urlTarefa : "redirect:" + urlTarefa;
        }
    }

    @PutMapping("/{codigo}")
    public String editar(
            @PathVariable("codigo") Long codigo,
            @Valid ComentarioUpdateRequest request,
            BindingResult resultado,
            Model model,
            RedirectAttributes redirect,
            HtmxRequest htmxRequest) {

        if (resultado.hasErrors()) {
            log.info("O comentário recebido para editar não é válido.");
            log.info("Erros encontrados:");
            for (FieldError erro : resultado.getFieldErrors()) {
                log.info("{}", erro);
            }
            for (ObjectError erro : resultado.getGlobalErrors()) {
                log.info("{}", erro);
            }
            Long codigoTarefa = comentarioService.buscarCodigoTarefa(codigo);
            redirect.addFlashAttribute(
                    "notificacaoSA2",
                    new NotificacaoSweetAlert2(
                            "Não foi possível atualizar o comentário. Verifique os dados informados.",
                            TipoNotificacaoSweetAlert2.WARNING,
                            4000
                    )
            );
            String urlTarefa = "/tarefas/" + codigoTarefa;
            return htmxRequest.isHtmxRequest()
                    ? "redirect:htmx:location:" + urlTarefa
                    : "redirect:" + urlTarefa;

        } else {
            try {
                Long codigoTarefa = comentarioService.editar(codigo, request.comentario());
                redirect.addFlashAttribute(
                        "notificacaoSA2",
                        new NotificacaoSweetAlert2(
                                "Comentário alterado com sucesso!",
                                TipoNotificacaoSweetAlert2.SUCCESS,
                                4000
                        )
                );
                String urlTarefa = "/tarefas/" + codigoTarefa;
                return htmxRequest.isHtmxRequest()
                        ? "redirect:htmx:location:" + urlTarefa
                        : "redirect:" + urlTarefa;
            } catch (PrazoOperacaoComentarioExpiradoException e) {
                redirect.addFlashAttribute(
                        "notificacaoSA2",
                        new NotificacaoSweetAlert2(
                                "Não é possível editar comentários com mais de 5 minutos de publicação!",
                                TipoNotificacaoSweetAlert2.WARNING,
                                4000
                        )
                );
                String urlTarefa = "/tarefas/" + e.getCodigoTarefa();
                return htmxRequest.isHtmxRequest()
                        ? "redirect:htmx:location:" + urlTarefa
                        : "redirect:" + urlTarefa;
            }
        }
    }

    @DeleteMapping("/{codigo}")
    public String remover(
            @PathVariable("codigo") Long codigo,
            RedirectAttributes redirect,
            HtmxRequest htmxRequest) {

        try {

            Long codigoTarefa = comentarioService.excluir(codigo);

            redirect.addFlashAttribute(
                    "notificacaoSA2",
                    new NotificacaoSweetAlert2(
                            "Comentário removido com sucesso!",
                            TipoNotificacaoSweetAlert2.SUCCESS,
                            4000
                    )
            );

            String urlTarefa = "/tarefas/" + codigoTarefa;

            return htmxRequest.isHtmxRequest()
                    ? "redirect:htmx:location:" + urlTarefa
                    : "redirect:" + urlTarefa;

        } catch (PrazoOperacaoComentarioExpiradoException e) {

            redirect.addFlashAttribute(
                    "notificacaoSA2",
                    new NotificacaoSweetAlert2(
                            "Não é possível remover comentários com mais de 1 minuto de publicação!",
                            TipoNotificacaoSweetAlert2.WARNING,
                            4000
                    )
            );

            String urlTarefa = "/tarefas/" + e.getCodigoTarefa();

            return htmxRequest.isHtmxRequest()
                    ? "redirect:htmx:location:" + urlTarefa
                    : "redirect:" + urlTarefa;
        }
    }

    @GetMapping("/pesquisarfuncionario")
    public String pesquisarFuncionario(String termoBusca, Model model) {
        List<FuncionarioResponse> funcionarios = funcionarioService.buscarPeloNome(termoBusca);
        log.debug("Funcionários buscados: {}", funcionarios);
        model.addAttribute("funcionarios", funcionarios);
        return "funcionarios/list_li :: lista";
    }
}
