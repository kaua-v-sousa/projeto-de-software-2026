package boundary;

import control.CtlAssinatura;
import entity.Assinatura;
import entity.CodigoSMS;
import java.io.IOException;
import java.util.Scanner;

/**
 * «boundary»
 * Interface do usuario para UC1 – Realizar Assinatura.
 * Sequencia conforme diagrama:
 *   1. iniciar()
 *   2. inserirDados(celular)
 *   3. solicitarValidacao(celular) → ctlAssinatura
 *   6. confirmarCodigo(sms)
 *   7. validarCodigo(sms) → ctlAssinatura
 *   9. selecionarPlano(tipo)
 *  10. processarPlano(tipo) → ctlAssinatura
 *  14. exibirConfirmacao(protocolo)
 */
public class ViewAssinatura {

    private final Scanner scanner;
    private final CtlAssinatura ctl = new CtlAssinatura();

    public ViewAssinatura(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Passo 1: iniciar() */
    public void executar() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║      UC1 – REALIZAR ASSINATURA           ║");
        System.out.println("╚══════════════════════════════════════════╝");

        // Passo 2: inserirDados(celular) — coleta dados do assinante
        System.out.print("\nNome completo: ");
        String nome = scanner.nextLine().trim();

        System.out.print("E-mail: ");
        String email = scanner.nextLine().trim();

        System.out.print("Celular (somente numeros, ex: 11987654321): ");
        String celular = scanner.nextLine().trim();

        // Passo 3: solicitarValidacao(celular) → ctlAssinatura
        // Internamente: passo 4 buscarAssinante + passo 5 enviarSMS
        try {
            CodigoSMS sms = ctl.solicitarValidacao(celular);
            System.out.println();
            System.out.println("  [SMS SIMULADO] Codigo enviado para " + celular + ": " + sms.getCodigo());

            // Passo 6: confirmarCodigo(sms) — usuario informa o codigo
            System.out.print("Digite o codigo SMS recebido: ");
            String codigoDigitado = scanner.nextLine().trim();

            // Passo 7: validarCodigo(sms) → ctlAssinatura
            // Internamente: passo 8 CodigoSMS.verificar(codigo)
            if (!ctl.validarCodigo(codigoDigitado)) {
                // opt [codigo invalido] exibirErro()
                System.out.println("[ERRO] Codigo SMS invalido ou expirado.");
                return;
            }
            System.out.println("[OK] Celular verificado com sucesso!");

        } catch (IOException e) {
            System.out.println("[ERRO] Falha ao verificar celular: " + e.getMessage());
            return;
        }

        // Exibe planos disponiveis para selecao
        System.out.println();
        System.out.println("  Planos disponiveis:");
        System.out.println("  " + "-".repeat(50));
        Assinatura.Plano[] planos = ctl.listarPlanos();
        for (int i = 0; i < planos.length; i++) {
            System.out.printf("  %d. %-35s R$ %.2f/mes%n",
                i + 1, planos[i].getDescricao(), planos[i].getValor());
        }
        System.out.println("  " + "-".repeat(50));

        // Passo 9: selecionarPlano(tipo) — usuario escolhe o plano
        System.out.print("Escolha o plano (1-" + planos.length + "): ");
        int opcao;
        try {
            opcao = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (opcao < 0 || opcao >= planos.length) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("[ERRO] Opcao invalida.");
            return;
        }

        // Passo 10: processarPlano(tipo) → ctlAssinatura
        ctl.processarPlano(planos[opcao].name());
        System.out.println("\nPlano selecionado: " + planos[opcao].getDescricao());
        System.out.print("Confirmar assinatura? (S/N): ");
        String confirmacao = scanner.nextLine().trim();

        if (!confirmacao.equalsIgnoreCase("S")) {
            System.out.println("Assinatura cancelada pelo usuario.");
            return;
        }

        // Passo 11: «create» Assinatura.criar(assinante, plano)
        // Passo 14: exibirConfirmacao(protocolo)
        try {
            Assinatura assinatura = ctl.confirmarAssinatura(nome, celular, email);

            System.out.println();
            System.out.println("  [OK] Assinatura realizada com sucesso!");
            System.out.println("  " + "=".repeat(44));
            System.out.println("  Protocolo    : " + assinatura.getProtocolo());
            System.out.println("  Plano        : " + assinatura.getPlano().getDescricao());
            System.out.printf ("  Valor        : R$ %.2f/mes%n", assinatura.getPlano().getValor());
            System.out.println("  Status       : " + assinatura.getStatus());
            System.out.println("  ID Assinatura: " + assinatura.getId());
            System.out.println("  " + "=".repeat(44));
            System.out.println("  IMPORTANTE: Guarde seu ID de assinatura: " + assinatura.getId());
            System.out.println("  Use-o para montar sua cesta e efetuar pagamento.");

        } catch (IOException e) {
            System.out.println("[ERRO] Falha ao salvar assinatura: " + e.getMessage());
        }
    }
}
