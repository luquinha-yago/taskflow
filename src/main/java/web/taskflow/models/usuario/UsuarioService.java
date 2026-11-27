package web.taskflow.models.usuario;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.taskflow.models.usuario.dto.UsuarioCreateRequest;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void salvar(UsuarioCreateRequest form) {
        usuarioRepository.save(toEntity(form));
    }

    private Usuario toEntity(UsuarioCreateRequest request) {
        Usuario usuario = new Usuario();
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setNomeUsuario(request.nomeUsuario());
        usuario.setAtivo(true);
        usuario.setPapeis(request.papeis());
        return usuario;
    }

}

