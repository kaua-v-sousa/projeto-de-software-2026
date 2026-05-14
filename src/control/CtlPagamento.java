package control;

import entity.Assinatura;
import entity.CartaoCredito;
import entity.Pagamento;
import persistence.AssinaturaDAO;
import persistence.PagamentoDAO;
import java.io.IOException;

/**
 * «control»
 * Coordena o fluxo do caso de uso UC3 – Processar Pagamento.
 * Metodos nomeados conforme o diagrama de sequencia:
 *   iniciarPagamento(valor)    → passo 2 (inclui passo 3: validarCartao)
 *   processarCobranca(valor)   → passo 5
 *   atualizarStatus(ativo)     → passo 7 (chamado na Assinatura)
 */
public class CtlPagamento {

    private Assinatura assinatura;
    private CartaoCredito cartaoAtual;

    private final AssinaturaDAO assinaturaDAO = new AssinaturaDAO();
    private final PagamentoDAO pagamentoDAO = new PagamentoDAO();

    /** Carrega a assinatura para exibir protocolo, plano e valor na view. */
    public Assinatura carregarAssinatura(int id) throws IOException {
        assinatura = assinaturaDAO.buscarPorId(id);
        return assinatura;
    }

    /**
     * Passo 2 do diagrama: iniciarPagamento(valor).
     * Internamente executa passo 3: CartaoCredito.validarCartao(dados).
     * Armazena o cartao para uso posterior em processarCobranca.
     * Retorna "OK" se valido, ou mensagem de erro especifica.
     */
    public String iniciarPagamento(CartaoCredito cartao, double valor) {
        this.cartaoAtual = cartao;
        // Passo 3: validarCartao(dados) → cartaoValido
        return validarCartao(cartao);
    }

    /**
     * Passo 3 do diagrama: CartaoCredito.validarCartao(dados).
     * Valida numero, validade e CVV do cartao.
     */
    public String validarCartao(CartaoCredito cartao) {
        if (!cartao.isNumeroValido()) return "Numero do cartao invalido.";
        if (!cartao.isValidadeValida()) return "Validade invalida ou cartao expirado.";
        if (!cartao.isCvvValido()) return "CVV invalido.";
        return "OK";
    }

    /**
     * Passo 5 do diagrama: processarCobranca(valor).
     * alt [pagamento aprovado]:
     *   passo 6: «create» Pagamento.criar(assinatura, valor)
     *   passo 7: Assinatura.atualizarStatus(ativo)
     * alt [pagamento recusado]:
     *   passo 8: exibirErro(motivo) → tratado na view
     */
    public Pagamento processarCobranca(double valor) throws IOException {
        int id = pagamentoDAO.proximoId();
        Pagamento pagamento = new Pagamento(id, assinatura.getId(), valor);

        // Simulacao da autorizadora: numeros terminando em "0000" sao recusados
        boolean aprovado = simularAutorizacao(cartaoAtual);

        if (aprovado) {
            // Passo 6: «create» Pagamento.criar(assinatura, valor)
            pagamento.aprovar();
            // Passo 7: Assinatura.atualizarStatus(ativo)
            assinatura.atualizarStatus(Assinatura.Status.ATIVA);
            assinaturaDAO.salvar(assinatura);
        } else {
            pagamento.recusar();
        }

        pagamentoDAO.salvar(pagamento);
        return pagamento;
    }

    /** Simula autorizadora: numero terminando em "0000" = recusado (para testes). */
    private boolean simularAutorizacao(CartaoCredito cartao) {
        String num = cartao.getNumero().replaceAll("[^0-9]", "");
        return !num.endsWith("0000");
    }

    public Assinatura getAssinatura() { return assinatura; }
}
