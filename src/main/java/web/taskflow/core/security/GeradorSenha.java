package web.taskflow.core.security;

import java.util.Scanner;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class GeradorSenha {
    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            System.out.print("Digite a senha: ");
            String senha = entrada.nextLine();

            // var encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
            var encoder = new Argon2PasswordEncoder(16, 32, 1, 65536, 20);
            
            long tempoInicial = System.nanoTime();
            String hash = encoder.encode(senha);
            long tempoFinal = System.nanoTime();
            System.out.println("Hash para o BD:\n{argon2}" + hash);
            long tempoDecorridoNanos = tempoFinal - tempoInicial;

            double tempoDecorridoMs = tempoDecorridoNanos / 1_000_000.0;
            double tempoDecorridoSeg = tempoDecorridoNanos / 1_000_000_000.0;

            System.out.println("Tempo em nanossegundos: " + tempoDecorridoNanos + " ns");
            System.out.println("Tempo em milissegundos: " + tempoDecorridoMs + " ms");
            System.out.println("Tempo em segundos: " + tempoDecorridoSeg + " s");
        }
    }
}
