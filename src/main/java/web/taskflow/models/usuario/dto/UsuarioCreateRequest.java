package web.taskflow.models.usuario.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import web.taskflow.core.validation.StrongPassword;
import web.taskflow.core.validation.UniqueValueAttribute;
import web.taskflow.models.papel.Papel;
import web.taskflow.models.service.NomeUsuarioUnicoService;

@UniqueValueAttribute(attribute = "nomeUsuario", service = NomeUsuarioUnicoService.class, message = "Já existe um usuário cadastrado com este nome de usuário")
public record UsuarioCreateRequest(
        @NotBlank(message = "O nome é obrigatório") String nome,

        @NotBlank(message = "A senha é obrigatória") @StrongPassword String senha,

        @NotBlank(message = "O nome de usuário é obrigatório") String nomeUsuario,

        @NotEmpty(message = "Selecione pelo menos um papel") List<Papel> papeis) {
}
