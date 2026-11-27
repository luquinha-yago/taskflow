package web.taskflow.core.security.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequest;
import web.taskflow.core.web.notification.NotificacaoSweetAlert2;
import web.taskflow.core.web.notification.TipoNotificacaoSweetAlert2;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(HtmxRequest htmxRequest) {
        return htmxRequest.isHtmxRequest() ? "login :: conteudo" : "login";
    }

    @PostMapping("/login-error")
    public String loginError(String username, Model model) {
        model.addAttribute("username", username);
        model.addAttribute("notificacaoSA2", new NotificacaoSweetAlert2(
                "Usuário e/ou senha inválidos.", TipoNotificacaoSweetAlert2.ERROR));
        return "login :: formulario";
    }
}