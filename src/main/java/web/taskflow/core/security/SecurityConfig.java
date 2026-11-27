package web.taskflow.core.security;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer
                // Qualquer um pode fazer requisições para essas URLs.
                // IMPORTANTE (HTMX): "/layout/**" precisa ser público porque os fragmentos do
                // cabeçalho (/layout/header-atualizado) e do menu (/layout/menu-atualizado)
                // são recarregados via HTMX tanto no login quanto no logout (quando o usuário
                // já não está mais autenticado).
                .requestMatchers("/favicon.ico", "/css/**", "/js/**", "/images/**", "/layout/**", "/components/**", "/",
                        "/index.html")
                .permitAll()
                // Um usuário autenticado e com o papel ADMIN pode fazer requisições para
                // essas URLs
                // .requestMatchers("/vacinas/nova").hasRole("ADMIN")
                .requestMatchers("/empresas/**").hasRole("ADMIN")
                .requestMatchers("/departamentos/**","/funcionarios/**","/relatorios/**").hasAnyRole("ADMIN","EMPRESA")
                // .requestMatchers("URL").hasAnyRole("ADMIN", // "USUARIO")
                // .anyRequest().permitAll()) // Qualquer um pode acessar
                .anyRequest().authenticated().requestMatchers("/projetos/**","/tarefas/**","/comentarios/**").permitAll()) // Tem que logar para acessar
                .formLogin(form -> form
                        // Uma página de login customizada
                        .loginPage("/login")
                        // Em aplicações tradicionais (sem HTMX), bastaria usar
                        // .defaultSuccessUrl("/index.html").
                        // Porém, com HTMX, usamos o successHandler() customizado abaixo para atualizar
                        // apenas o fragmento #main e avisar o cabeçalho e o menu que o login ocorreu.
                        .defaultSuccessUrl("/inicio.html")
                        .successHandler(successHandler())
                        // IMPORTANTE (HTMX): Usamos failureForwardUrl (encaminhamento interno via POST)
                        // em vez de failureUrl (redirecionamento 302 via GET) para que o
                        // LoginController
                        // devolva o fragmento do formulário de login mantendo o nome digitado e
                        // adicione
                        // a notificação do SweetAlert2 no model (que o NotificacaoHeaderInterceptor
                        // transforma no cabeçalho HX-Trigger para exibir o alerta na tela).
                        .failureForwardUrl("/login-error")
                        .permitAll())
                        .logout(logout -> logout.logoutUrl("/logout")
                        // Em uma aplicação tradicional (sem HTMX), o Spring Security faria apenas um
                        // redirecionamento 302 para "/login?logout" ou "/".
                        // Com HTMX, como o botão de Logout faz uma requisição parcial
                        // (hx-post="/logout"),
                        // precisamos responder com cabeçalhos específicos do HTMX:
                        // 1. "HX-Trigger: atualizarAuth" -> Dispara um evento no navegador para que o
                        // cabeçalho (botões Login/Logout) e o menu lateral se atualizem sozinhos.
                        // 2. "HX-Location" -> Pede para o HTMX carregar a página inicial ("/") apenas
                        // dentro da área principal (#main), sem recarregar a página inteira.
                        .logoutSuccessHandler((request, response, authentication) -> {
                            if ("true".equals(request.getHeader("HX-Request"))) {
                                response.addHeader("HX-Trigger", "atualizarAuth");
                                String hxLocationPayload = "{\"path\":\"/\", \"target\":\"#main\", \"swap\":\"outerHTML\"}";
                                response.addHeader("HX-Location", hxLocationPayload);
                                response.setStatus(HttpServletResponse.SC_OK);
                            } else {
                                response.sendRedirect("/");
                            }
                        }).invalidateHttpSession(true).deleteCookies("JSESSIONID"))
                // Garante que a sessão HTTP (e consequentemente o token CSRF) seja criada já no
                // primeiro acesso à aplicação, evitando erro 403 na primeira submissão via
                // HTMX.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                // Configura o handler customizado de CSRF necessário para manter o token
                // atualizado
                // nas navegações parciais do HTMX (veja explicação na classe
                // SpaCsrfTokenRequestHandler abaixo).
                .csrf((csrf) -> csrf
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()));
        return http.build();
    }

    /**
     * Handler executado quando o login é realizado com sucesso.
     *
     * Por que este código é necessário por causa do HTMX?
     * - Sem HTMX, o Spring Security responde ao POST /login com um redirecionamento
     * HTTP 302.
     * - Com HTMX, o formulário de login é enviado via fetch (AJAX) esperando
     * atualizar apenas o #main.
     * Se apenas trocássemos o #main, o cabeçalho continuaria mostrando o botão
     * "Login" e o menu
     * lateral não exibiria as opções restritas (como Relatórios e Usuários para
     * ADMIN).
     *
     * O que este handler faz:
     * 1. Verifica no RequestCache se o usuário tentou acessar alguma URL restrita
     * antes de logar
     * (ex.: clicou em "Relatórios -> Vacinas Completo" deslogado e caiu na tela de
     * login). Se sim,
     * recupera essa URL; caso contrário, usa "/" como destino padrão.
     * 2. Envia o cabeçalho "HX-Location" instruindo o HTMX a carregar a URL de
     * destino dentro de #main.
     * 3. Envia o cabeçalho "HX-Trigger: atualizarAuth", que faz o cabeçalho e o
     * menu lateral
     * buscarem suas versões atualizadas no servidor (agora com o usuário
     * autenticado).
     */
    private AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            String targetUrl = "/"; // Default para login padrão
            RequestCache requestCache = new HttpSessionRequestCache();
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                targetUrl = savedRequest.getRedirectUrl();
                requestCache.removeRequest(request, response); // Limpa o cache
            }
            if ("true".equals(request.getHeader("HX-Request"))) {
                String hxLocationPayload = String
                        .format("{\"path\":\"%s\", \"target\":\"#main\", \"swap\":\"outerHTML\"}", targetUrl);
                response.addHeader("HX-Location", hxLocationPayload);
                response.addHeader("HX-Trigger", "atualizarAuth");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendRedirect(targetUrl);
            }
        };
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery(
                "select nome_usuario, senha, ativo " + "from usuario " + "where nome_usuario = ?");
        manager.setAuthoritiesByUsernameQuery("SELECT tab.nome_usuario , papel.nome FROM"
                + "(SELECT usuario.nome_usuario , usuario.codigo FROM usuario WHERE nome_usuario = ?) as tab "
                + " INNER JOIN usuario_papel ON codigo_usuario = tab.codigo "
                + " INNER JOIN papel ON codigo_papel = papel.codigo;");
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Usar um PasswordEncoder que aceite todos os esquemas disponiveis no Spring
        // Security
        // mas escolhendo quais vamos aceitar. As senhas comecam dizendo qual o esquema
        // usado {noop}, {bcrypt}, {argon2}
        String idEnconder = "argon2";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("argon2", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        // encoders.put("noop", NoOpPasswordEncoder.getInstance());
        // encoders.put("bcrypt", new BCryptPasswordEncoder());
        // encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        // encoders.put("scrypt", new SCryptPasswordEncoder());
        // encoders.put("sha256", new StandardPasswordEncoder());
        PasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(idEnconder, encoders);
        return passwordEncoder;
    }
}

/**
 * Handler customizado para gerenciamento do Token CSRF em aplicações que usam
 * HTMX / SPA.
 *
 * Por que esta classe é necessária por causa do HTMX?
 * - Em aplicações tradicionais, cada clique ou submissão recarrega a página
 * inteira (<html> completo),
 * então a tag <meta name="_csrf" ...> no <head> do layoutpadrao.html é sempre
 * recriada com o token novo.
 * - Com HTMX, apenas o conteúdo do <main id="main"> é trocado nas navegações; o
 * <head> da página
 * NUNCA é recarregado.
 * - Porém, por segurança, o Spring Security invalida e gera um NOVO token CSRF
 * toda vez que o
 * usuário faz Login ou Logout. Se o JavaScript continuasse lendo o token antigo
 * do <head>,
 * qualquer POST/PUT/DELETE seguinte (como clicar em Logout ou cadastrar algo)
 * falharia com erro 403 (Forbidden).
 *
 * Como ela resolve isso:
 * - Usa o XorCsrfTokenRequestAttributeHandler padrão do Spring Security (que
 * protege o token com máscara XOR).
 * - Adicionalmente, injeta o token CSRF atualizado no cabeçalho HTTP da
 * resposta (X-CSRF-TOKEN).
 * - No front-end, o arquivo app.js escuta o evento "htmx:after:request", lê
 * esse cabeçalho da resposta
 * e atualiza o token em memória para usá-lo nas próximas requisições.
 */
final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

    private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            Supplier<CsrfToken> csrfToken) {
        // Delega ao XorCsrfTokenRequestAttributeHandler padrão para aplicar a proteção
        // BREACH
        // e disponibilizar o CsrfToken como atributo da requisição.
        this.xor.handle(request, response, csrfToken);
        // Força a leitura do token (que é "lazy" no Spring Security) e o envia no
        // cabeçalho
        // da resposta HTTP para que o JavaScript (app.js) possa atualizar o token do
        // HTMX.
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token != null) {
            response.setHeader(token.getHeaderName(), token.getToken());
        }
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        return this.xor.resolveCsrfTokenValue(request, csrfToken);
    }
}