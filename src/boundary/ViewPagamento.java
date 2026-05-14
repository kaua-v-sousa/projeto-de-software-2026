package boundary;

import control.CtlPagamento;
import entity.Assinatura;
import entity.CartaoCredito;
import entity.Pagamento;
import java.io.IOException;
import java.util.Scanner;

/**
 * «boundary»
 * Interface do usuario para UC3 – Processar Pagamento.
 * Sequencia conforme diagrama:
 *   1. abrirFormPagamento()
 *   2. iniciarPagamento(valor) → ctlPagamento  (inclui validarCartao)
 *   4. confirmarPagamento()
 *   5. processarCobranca(valor) → ctlPagamento
 *   alt [aprovado]  → 10. exibirComprovante()
 *   alt [recusado]  →  9. exibirErro()
 */
public class ViewPagamento {

    private final Scanner scanner;
    private final CtlPagamento ctl = new CtlPagamento();

    public ViewPagamento(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Passo 1: abrirFormPagamento() */
    public void executar() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║       UC3 – PROCESSAR PAGAMENTO          ║");
        System.out.println("╚══════════════════════════════════════════╝");

        System.out.print("\nID da assinatura: ");
        int assinaturaId;
        try {
            assinaturaId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[ERRO] ID invalido.");
            return;
        }

        try {
            Assinatura assinatura = ctl.carregarAssinatura(assinaturaId);
            if (assinatura == null) {
                System.out.println("[ERRO] Assinatura nao encontrada. Verifique o ID.");
                return;
            }

            // Exibe resumo da assinatura e valor a pagar
            System.out.println();
            System.out.println("  Assinatura : " + assinatura.getProtocolo());
            System.out.println("  Plano      : " + assinatura.getPlano().getDescricao());
            System.out.printf ("  Valor      : R$ %.2f/mes%n", assinatura.getPlano().getValor());
            System.out.println("  Status     : " + assinatura.getStatus());

            if (assinatura.getStatus() == Assinatura.Status.ATIVA) {
                System.out.println("\n  [INFO] Esta assinatura ja esta ATIVA.");
                return;
            }

            double valor = assinatura.getPlano().getValor();

            // Coleta dados do cartao de credito
            System.out.println();
            System.out.println("  -- Dados do Cartao de Credito " + "-".repeat(15));
            System.out.print("  Numero do cartao: ");
            String numero = scanner.nextLine().trim();

            System.out.print("  Nome do titular: ");
            String titular = scanner.nextLine().trim();

            System.out.print("  Validade (MM/AA): ");
            String validade = scanner.nextLine().trim();

            System.out.print("  CVV: ");
            String cvv = scanner.nextLine().trim();

            CartaoCredito cartao = new CartaoCredito(numero, titular, validade, cvv);

            // Passo 2: iniciarPagamento(valor) → ctlPagamento
            // Internamente: passo 3 CartaoCredito.validarCartao(dados) → cartaoValido
            String validacao = ctl.iniciarPagamento(cartao, valor);
            if (!"OK".equals(validacao)) {
                // Passo 8: exibirErro(motivo) [cartao invalido]
                System.out.println("\n  [ERRO] " + validacao);
                return;
            }
            System.out.println("\n  [OK] Cartao validado: " + cartao.getNumeroMascarado());

            // Passo 4: confirmarPagamento() — usuario confirma a cobrança
            System.out.println();
            System.out.printf ("  Cobranca a realizar: R$ %.2f%n", valor);
            System.out.print("  Confirmar pagamento? (S/N): ");
            String confirmacao = scanner.nextLine().trim();

            if (!confirmacao.equalsIgnoreCase("S")) {
                System.out.println("  Pagamento cancelado pelo usuario.");
                return;
            }

            // Passo 5: processarCobranca(valor) → ctlPagamento
            // alt [pagamento aprovado]: passo 6 criar Pagamento + passo 7 atualizarStatus(ativo)
            // alt [pagamento recusado]: passo 8 exibirErro(motivo)
            System.out.println("\n  Processando pagamento...");
            Pagamento pagamento = ctl.processarCobranca(valor);

            System.out.println();
            System.out.println("  " + "=".repeat(44));

            if (pagamento.getStatus() == Pagamento.Status.APROVADO) {
                // Passo 10: exibirComprovante()
                System.out.println("  [APROVADO] Pagamento realizado com sucesso!");
                System.out.println("  Codigo de transacao : " + pagamento.getCodigoTransacao());
                System.out.printf ("  Valor cobrado       : R$ %.2f%n", pagamento.getValor());
                System.out.println("  Status da assinatura: ATIVA");
            } else {
                // Passo 9: exibirErro()
                System.out.println("  [RECUSADO] Pagamento nao autorizado.");
                System.out.println("  Codigo de erro : " + pagamento.getCodigoTransacao());
                System.out.println("  Entre em contato com seu banco e tente novamente.");
                System.out.println();
                System.out.println("  [DICA] Para testar aprovacao, use cartao que nao");
                System.out.println("         termine em \"0000\" (ex: 4111111111111111).");
            }

            System.out.println("  " + "=".repeat(44));

        } catch (IOException e) {
            System.out.println("[ERRO] " + e.getMessage());
        }
    }
}
