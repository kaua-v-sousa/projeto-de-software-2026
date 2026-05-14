import boundary.ViewAssinatura;
import boundary.ViewCesta;
import boundary.ViewPagamento;
import java.util.Scanner;

/**
 * FeiraNaBox – Implementacao dos Cenarios de Caso de Uso
 *
 * Atividade N2 PRJ3 – Implementacao do Cenario do Caso de Uso
 * Disciplina: Projeto de Software | Universidade Mackenzie 2026
 * Aluno: Kaua Sousa – RA: 10444362
 *
 * Arquitetura Boundary-Control-Entity (BCE):
 *   Boundary   : ViewAssinatura, ViewCesta, ViewPagamento
 *   Control    : CtlAssinatura, CtlCesta, CtlPagamento
 *   Entity     : Assinante, CodigoSMS, Assinatura, Catalogo, Cesta, ItemCesta,
 *                CartaoCredito, Pagamento
 *   Persistencia: CSV em data/ (AssinanteDAO, AssinaturaDAO, CestaDAO, PagamentoDAO)
 *
 * Fluxo recomendado:
 *   1. Execute UC1 para criar uma assinatura e obter seu ID.
 *   2. Execute UC2 com o ID obtido para montar a cesta semanal.
 *   3. Execute UC3 com o mesmo ID para ativar a assinatura via pagamento.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║         FeiraNaBox – Sistema de Assinaturas  ║");
        System.out.println("║    Projeto de Software · Mackenzie 2026      ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("  Fluxo recomendado: UC1 → UC2 → UC3");

        boolean executando = true;
        while (executando) {
            System.out.println();
            System.out.println("  -- Menu Principal " + "-".repeat(28));
            System.out.println("  1. UC1 – Realizar Assinatura");
            System.out.println("  2. UC2 – Montar Cesta Semanal");
            System.out.println("  3. UC3 – Processar Pagamento");
            System.out.println("  0. Sair");
            System.out.println("  " + "-".repeat(46));
            System.out.print("  Escolha: ");

            String op = scanner.nextLine().trim();
            switch (op) {
                case "1":
                    new ViewAssinatura(scanner).executar();
                    break;
                case "2":
                    new ViewCesta(scanner).executar();
                    break;
                case "3":
                    new ViewPagamento(scanner).executar();
                    break;
                case "0":
                    executando = false;
                    System.out.println("\nAte logo!");
                    break;
                default:
                    System.out.println("  [ERRO] Opcao invalida. Escolha entre 0 e 3.");
            }
        }
        scanner.close();
    }
}
