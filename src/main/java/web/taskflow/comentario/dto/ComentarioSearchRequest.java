package web.taskflow.comentario.dto;

import java.time.LocalDateTime;

public record ComentarioSearchRequest(
    LocalDateTime dataHoraRegistroInicial,
    LocalDateTime dataHoraRegistroFinal,
    Long codigoFuncionario,
    String nomeFuncionario
) {
    public boolean isEmpty() {
        return dataHoraRegistroInicial == null
            && dataHoraRegistroFinal == null
            && codigoFuncionario == null
            && (nomeFuncionario == null || nomeFuncionario.isBlank());
    }
}