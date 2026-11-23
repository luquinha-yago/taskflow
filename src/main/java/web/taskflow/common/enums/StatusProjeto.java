package web.taskflow.common.enums;

import lombok.Getter;

@Getter
public enum StatusProjeto {

    PLANEJADO("Planejado"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusProjeto(String descricao) {
        this.descricao = descricao;
    }

    public String getValor() {
        return name();
    }
}
