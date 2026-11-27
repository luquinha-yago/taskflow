package web.taskflow.models.usuario;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	boolean existsByNomeUsuario(String nomeUsuario);

	boolean existsByNomeUsuarioIgnoreCase(String nomeUsuario);

	Optional<Usuario> findByNomeUsuario(String nomeUsuario);

	Optional<Usuario> findByNomeUsuarioIgnoreCase(String nomeUsuario);

}

