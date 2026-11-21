package web.taskflow.common.enums;

import lombok.Getter;

@Getter
public enum Prioridade {

    BAIXA("Baixa"),
    MEDIA("Média"),
    ALTA("Alta");

    private final String descricao;

    Prioridade(String descricao) {
        this.descricao = descricao;
    }

    public String getValor() {
        return name();
    }
}
