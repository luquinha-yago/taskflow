package web.taskflow.common.exception;

public class PrazoOperacaoComentarioExpiradoException
        extends RuntimeException {

    private final Long codigoTarefa;

    public PrazoOperacaoComentarioExpiradoException(
            String mensagem,
            Long codigoTarefa) {

        super(mensagem);
        this.codigoTarefa = codigoTarefa;
    }

    public Long getCodigoTarefa() {
        return codigoTarefa;
    }
}
