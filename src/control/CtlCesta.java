package control;

import entity.Assinatura;
import entity.Catalogo;
import entity.Cesta;
import entity.Produto;
import persistence.AssinaturaDAO;
import persistence.CestaDAO;
import java.io.*;

/**
 * «control»
 * Coordena o fluxo do caso de uso UC2 – Montar Cesta Semanal.
 * Metodos nomeados conforme o diagrama de sequencia:
 *   carregarCatalogo()         → passo 2 (inclui passo 3 buscarProdutos e passo 5 criar Cesta)
 *   addItem(produto, qtde)     → passo 7
 *   salvar(cesta)              → passo 12
 */
public class CtlCesta {

    private Assinatura assinatura;
    private Catalogo catalogo;
    private Cesta cesta;

    private static final String CATALOGO_ARQUIVO = "data/catalogo.csv";

    private final AssinaturaDAO assinaturaDAO = new AssinaturaDAO();
    private final CestaDAO cestaDAO = new CestaDAO();

    /** Carrega a assinatura do assinante para verificar plano e limite. */
    public Assinatura carregarAssinatura(int id) throws IOException {
        assinatura = assinaturaDAO.buscarPorId(id);
        return assinatura;
    }

    /**
     * Passo 2 do diagrama: carregarCatalogo().
     * Internamente executa passo 3 (Catalogo.buscarProdutos) e
     * passo 5 («create» Cesta.criar(assinatura)).
     * Retorna o catalogo para exibicao (passo 4: exibirCatalogo).
     */
    public Catalogo carregarCatalogo() throws IOException {
        // Passo 3: Catalogo.buscarProdutos()
        catalogo = new Catalogo();
        File f = new File(CATALOGO_ARQUIVO);
        if (!f.exists()) throw new IOException("Arquivo de catalogo nao encontrado: " + CATALOGO_ARQUIVO);

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    catalogo.adicionar(Produto.fromCsv(line));
                }
            }
        }

        // Passo 5: «create» Cesta.criar(assinatura)
        int id = cestaDAO.proximoId();
        cesta = new Cesta(id, assinatura.getId());

        return catalogo;
    }

    /**
     * Passo 7 do diagrama: addItem(produto, qtde).
     * Delega para passo 8: Cesta.addItem(produto, qtde),
     * que internamente executa passo 9: «create» ItemCesta.criar(prod, qtde).
     * Retorna o subtotal atualizado ou mensagem de erro.
     */
    public String addItem(int produtoId, int quantidade) {
        if (cesta == null) return "ERRO:Nenhuma cesta ativa.";
        if (catalogo == null) return "ERRO:Catalogo nao carregado.";

        Produto produto = catalogo.buscarPorId(produtoId);
        if (produto == null) return "ERRO:Produto nao encontrado no catalogo.";

        int totalAtual = cesta.getTotalQuantidade();
        int limite = assinatura.getPlano().getLimiteItens();

        if (totalAtual + quantidade > limite) {
            int disponivel = limite - totalAtual;
            if (disponivel <= 0) return "ERRO:Limite do plano atingido.";
            return "ERRO:Limite excedido! Maximo " + disponivel + " unidade(s) disponivel(is).";
        }

        // Passo 8-9: Cesta.addItem → ItemCesta.criar
        cesta.adicionarItem(produto, quantidade);

        // Retorna subtotal atualizado (passo 10: exibirSubtotal)
        return "OK:" + String.format("%.2f", cesta.getValorTotal());
    }

    /** Remove um produto da cesta pelo ID. */
    public boolean removerItem(int produtoId) {
        return cesta != null && cesta.removerItem(produtoId);
    }

    /**
     * Passo 12 do diagrama: salvar(cesta).
     * Delega para passo 13: Cesta.salvar() → ok.
     */
    public Cesta salvar() throws IOException {
        if (cesta == null) throw new IllegalStateException("Nenhuma cesta ativa.");
        cesta.confirmar();
        cestaDAO.salvar(cesta);
        return cesta;
    }

    public Cesta getCesta() { return cesta; }
    public Catalogo getCatalogo() { return catalogo; }
    public Assinatura getAssinatura() { return assinatura; }
}
