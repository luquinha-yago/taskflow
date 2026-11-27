package web.taskflow.models.service;

import org.springframework.stereotype.Service;
import web.taskflow.models.usuario.UsuarioRepository;
import web.taskflow.models.usuario.dto.UsuarioCreateRequest;

@Service
public class NomeUsuarioUnicoServiceImpl implements NomeUsuarioUnicoService {

    private final UsuarioRepository repositorio;

    public NomeUsuarioUnicoServiceImpl(UsuarioRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean isValueUnique(Object value, String attributeName, Object rootObject) {
        if (value == null) {
            return true;
        }

        String nomeUsuario = (String) value;

        if (rootObject instanceof UsuarioCreateRequest) {
            return !repositorio.existsByNomeUsuarioIgnoreCase(nomeUsuario);
        }

        throw new UnsupportedOperationException(
                "Validação de Nome de Usuário Único não suportada para o Record/Classe: "
                        + rootObject.getClass().getName());
    }
}

