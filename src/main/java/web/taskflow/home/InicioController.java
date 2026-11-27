package web.taskflow.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxRequest;

@Controller
@RequestMapping("/inicio")
public class InicioController {

    @GetMapping
    public String inicio(HtmxRequest htmxRequest) {
        return htmxRequest.isHtmxRequest() ? "home/inicio :: conteudo" : "home/inicio";
    }
}
