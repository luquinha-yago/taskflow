package web.taskflow.models.usuario;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import web.taskflow.core.web.notification.NotificacaoSweetAlert2;
import web.taskflow.core.web.notification.TipoNotificacaoSweetAlert2;
import web.taskflow.models.papel.Papel;
import web.taskflow.models.papel.PapelRepository;
import web.taskflow.models.usuario.dto.UsuarioCreateRequest;

@Slf4j
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PapelRepository papelRepository;

    public UsuarioController(UsuarioService usuarioService, PapelRepository papelRepository) {
        this.usuarioService = usuarioService;
        this.papelRepository = papelRepository;
    }

    @GetMapping("/novo")
    public String abrirFormulario(UsuarioCreateRequest request,
                                  Model model,
                                  HtmxRequest htmxRequest) {
        List<Papel> papeis = papelRepository.findAll();
        model.addAttribute("todosPapeis", papeis);
        return htmxRequest.isHtmxRequest() ? "usuarios/create :: formulario" : "usuarios/create";
    }

    @PostMapping
    public String cadastrar(@Valid UsuarioCreateRequest form,
                            BindingResult resultado,
                            Model model,
                            RedirectAttributes redirect,
                            HtmxRequest htmxRequest) {
        if (resultado.hasErrors()) {
            log.info("O usuário recebido para cadastrar não é válido.");
            log.info("Erros encontrados:");
            for (FieldError erro : resultado.getFieldErrors()) {
                log.info("{}", erro);
            }
            for (ObjectError erro : resultado.getGlobalErrors()) {
                log.info("{}", erro);
            }
            List<Papel> papeis = papelRepository.findAll();
            model.addAttribute("todosPapeis", papeis);
            return htmxRequest.isHtmxRequest() ? "usuarios/create :: formulario" :
                                                 "usuarios/create";
        } else {
            usuarioService.salvar(form);
            redirect.addFlashAttribute("notificacaoSA2",
                    new NotificacaoSweetAlert2("Usuário cadastrado com sucesso!",
                           TipoNotificacaoSweetAlert2.SUCCESS, 4000));
            return htmxRequest.isHtmxRequest() ? "redirect:htmx:location:/usuarios/novo" :
                                                 "redirect:/usuarios/novo";
        }
    }

}

