package web.taskflow.comentario.dto;

import java.time.LocalDateTime;

public record ComentarioResponse(
    Long codigo,
    String comentario,
    LocalDateTime dataHoraRegistro,
    Long codigoFuncionario,
    String nomeFuncionario,
    Long codigoTarefa,
    String nomeTarefa
) {}
