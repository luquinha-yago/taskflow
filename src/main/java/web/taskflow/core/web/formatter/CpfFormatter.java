package web.taskflow.core.web.formatter;

import org.springframework.stereotype.Component;

@Component("cpf")
public class CpfFormatter {

    public String formatar(Object cpfObj) {
        if (cpfObj == null) {
            return "";
        }

        // Converte para String, remove espaços e mantém apenas os dígitos
        String digitos = String.valueOf(cpfObj).trim().replaceAll("\\D", "");

        if (digitos.isEmpty()) {
            return "";
        }

        // Se o CPF perdeu o zero à esquerda (ex: tem 10 dígitos), preenche com 0 até ter 11
        if (digitos.length() < 11) {
            digitos = String.format("%011d", Long.parseLong(digitos));
        }

        // Aplica a máscara se tiver exatamente 11 dígitos
        if (digitos.length() == 11) {
            return digitos.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }

        // Caso tenha mais de 11 dígitos ou formato inválido, devolve o original
        return String.valueOf(cpfObj);
    }
}