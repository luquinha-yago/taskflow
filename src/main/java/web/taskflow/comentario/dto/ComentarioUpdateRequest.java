package web.taskflow.comentario.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;

public record ComentarioUpdateRequest(
    @NotBlank(message = "O comentário é obrigatório.")
    String comentario,
    LocalDateTime dataHoraRegistro,
    Long codigoFuncionario,
    String nomeFuncionario,
    Long codigoTarefa,
    String nomeTarefa
) {}
