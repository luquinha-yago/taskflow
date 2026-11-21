package web.taskflow.common.enums;

import lombok.Getter;

@Getter
public enum StatusTarefa {

    A_FAZER("A fazer"),
    FAZENDO("Fazendo"),
    EM_REVISAO("Em revisão"),
    FEITO("Feito");

    private final String descricao;

    StatusTarefa(String descricao) {
        this.descricao = descricao;
    }

    public String getValor() {
        return name();
    }
}