package boundary;

import control.CtlCesta;
import entity.Assinatura;
import entity.Catalogo;
import entity.Cesta;
import entity.ItemCesta;
import entity.Produto;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * «boundary»
 * Interface do usuario para UC2 – Montar Cesta Semanal.
 * Sequencia conforme diagrama:
 *   1. abrirCesta()
 *   2. carregarCatalogo() → ctlCesta  (inclui buscarProdutos + criar Cesta)
 *   4. exibirCatalogo(lista)
 *   6. adicionarItem(produto, qtde)
 *   7. addItem(produto, qtde) → ctlCesta
 *  10. exibirSubtotal()
 *  11. confirmarCesta()
 *  12. salvar(cesta) → ctlCesta
 *  14. exibirSucesso()
 */
public class ViewCesta {

    private final Scanner scanner;
    private final CtlCesta ctl = new CtlCesta();

    public ViewCesta(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Passo 1: abrirCesta() */
    public void executar() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     UC2 – MONTAR CESTA SEMANAL           ║");
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
            System.out.println("[OK] Assinatura: " + assinatura.getPlano().getDescricao() +
                               " | Limite: " + assinatura.getPlano().getLimiteItens() + " itens/semana");

            // Passo 2: carregarCatalogo() → ctlCesta
            // Internamente: passo 3 Catalogo.buscarProdutos() + passo 5 «create» Cesta.criar(assinatura)
            Catalogo catalogo = ctl.carregarCatalogo();

            // Passo 4: exibirCatalogo(lista)
            exibirCatalogo(catalogo.getProdutos());

            // Loop: [enquanto houver itens a adicionar]
            boolean continuar = true;
            while (continuar) {
                exibirCesta(ctl.getCesta(), assinatura.getPlano().getLimiteItens());

                System.out.println();
                System.out.println("  Opcoes: [A] Adicionar item  [R] Remover item");
                System.out.println("          [C] Confirmar cesta [S] Sair sem salvar");
                System.out.print("  Escolha: ");
                String op = scanner.nextLine().trim().toUpperCase();

                switch (op) {
                    case "A": adicionarItem(); break;
                    case "R": removerItem(); break;
                    case "C":
                        if (confirmarCesta()) continuar = false;
                        break;
                    case "S":
                        System.out.println("Montagem cancelada sem salvar.");
                        continuar = false;
                        break;
                    default:
                        System.out.println("[ERRO] Opcao invalida.");
                }
            }

        } catch (IOException e) {
            System.out.println("[ERRO] " + e.getMessage());
        }
    }

    /** Passo 4: exibirCatalogo(lista) */
    private void exibirCatalogo(List<Produto> produtos) {
        System.out.println();
        System.out.println("  -- Catalogo de Produtos " + "-".repeat(38));
        System.out.printf("  %-4s %-16s %-10s %-11s %s%n", "ID", "Nome", "Categoria", "Unidade", "Preco");
        System.out.println("  " + "-".repeat(58));
        for (Produto p : produtos) {
            System.out.println("  " + p);
        }
        System.out.println("  " + "-".repeat(58));
    }

    private void exibirCesta(Cesta cesta, int limite) {
        System.out.println();
        System.out.println("  -- Sua Cesta (" + cesta.getSemana() + ") " + "-".repeat(28));
        List<ItemCesta> itens = cesta.getItens();
        if (itens.isEmpty()) {
            System.out.println("  (cesta vazia)");
        } else {
            System.out.printf("  %-16s %5s %-11s %10s%n", "Produto", "Qtd", "Unidade", "Subtotal");
            System.out.println("  " + "-".repeat(47));
            for (ItemCesta item : itens) {
                System.out.println("  " + item);
            }
            System.out.printf("  %34s R$ %.2f%n", "TOTAL:", cesta.getValorTotal());
        }
        System.out.println("  Itens usados: " + cesta.getTotalQuantidade() + " / " + limite);
        System.out.println("  " + "-".repeat(47));
    }

    /** Passo 6: adicionarItem(produto, qtde) — usuario informa o produto */
    private void adicionarItem() {
        System.out.print("  ID do produto: ");
        int prodId;
        try {
            prodId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [ERRO] ID invalido.");
            return;
        }
        System.out.print("  Quantidade: ");
        int qtd;
        try {
            qtd = Integer.parseInt(scanner.nextLine().trim());
            if (qtd <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("  [ERRO] Quantidade deve ser maior que zero.");
            return;
        }

        // Passo 7: addItem(produto, qtde) → ctlCesta
        // Internamente: passo 8 Cesta.addItem + passo 9 «create» ItemCesta.criar
        String resultado = ctl.addItem(prodId, qtd);

        if (resultado.startsWith("OK:")) {
            // Passo 10: exibirSubtotal()
            String subtotal = resultado.substring(3);
            System.out.println("  [OK] Item adicionado. Subtotal da cesta: R$ " + subtotal);
        } else {
            String erro = resultado.startsWith("ERRO:") ? resultado.substring(5) : resultado;
            System.out.println("  [ERRO] " + erro);
        }
    }

    private void removerItem() {
        System.out.print("  ID do produto a remover: ");
        try {
            int prodId = Integer.parseInt(scanner.nextLine().trim());
            if (ctl.removerItem(prodId)) {
                System.out.println("  [OK] Item removido da cesta.");
            } else {
                System.out.println("  [ERRO] Produto nao encontrado na cesta.");
            }
        } catch (NumberFormatException e) {
            System.out.println("  [ERRO] ID invalido.");
        }
    }

    /** Passo 11: confirmarCesta() */
    private boolean confirmarCesta() throws IOException {
        Cesta cesta = ctl.getCesta();
        if (cesta.getItens().isEmpty()) {
            System.out.println("  [ERRO] A cesta esta vazia. Adicione ao menos um item.");
            return false;
        }
        System.out.print("  Confirmar cesta? (S/N): ");
        String confirmacao = scanner.nextLine().trim();
        if (!confirmacao.equalsIgnoreCase("S")) {
            System.out.println("  Confirmacao cancelada.");
            return false;
        }

        // Passo 12: salvar(cesta) → ctlCesta → passo 13: Cesta.salvar() → ok
        Cesta confirmada = ctl.salvar();

        // Passo 14: exibirSucesso()
        System.out.println();
        System.out.println("  [OK] Cesta confirmada com sucesso!");
        System.out.println("  " + "=".repeat(44));
        System.out.println("  ID da Cesta : " + confirmada.getId());
        System.out.println("  Semana      : " + confirmada.getSemana());
        System.out.println("  Total itens : " + confirmada.getTotalQuantidade());
        System.out.printf ("  Valor total : R$ %.2f%n", confirmada.getValorTotal());
        System.out.println("  " + "=".repeat(44));
        return true;
    }
}
