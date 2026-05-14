package control;

import entity.Assinante;
import entity.Assinatura;
import entity.CodigoSMS;
import persistence.AssinanteDAO;
import persistence.AssinaturaDAO;
import java.io.IOException;

/**
 * «control»
 * Coordena o fluxo do caso de uso UC1 – Realizar Assinatura.
 * Metodos nomeados conforme o diagrama de sequencia:
 *   solicitarValidacao(celular) → passo 3
 *   validarCodigo(sms)         → passo 7
 *   processarPlano(tipo)       → passo 10
 */
public class CtlAssinatura {

    private CodigoSMS codigoSMS;
    private Assinante assinanteAtual;
    private Assinatura.Plano planoSelecionado;

    private final AssinanteDAO assinanteDAO = new AssinanteDAO();
    private final AssinaturaDAO assinaturaDAO = new AssinaturaDAO();

    /**
     * Passo 3 do diagrama: solicitarValidacao(celular).
     * Internamente executa passo 4 (buscarAssinante) e passo 5 (enviarSMS).
     */
    public CodigoSMS solicitarValidacao(String celular) throws IOException {
        // Passo 4: buscarAssinante(celular) — verifica se ja existe cadastro
        assinanteAtual = assinanteDAO.buscarPorCelular(celular);

        // Passo 5: «create» enviarSMS(celular) — cria CodigoSMS (simulado)
        codigoSMS = new CodigoSMS(celular);
        return codigoSMS;
    }

    /**
     * Passo 7 do diagrama: validarCodigo(sms).
     * Delega para passo 8: CodigoSMS.verificar(codigo).
     */
    public boolean validarCodigo(String sms) {
        if (codigoSMS == null) return false;
        return codigoSMS.validar(sms);
    }

    /** Retorna os planos disponiveis para exibicao na viewAssinatura. */
    public Assinatura.Plano[] listarPlanos() {
        return Assinatura.Plano.values();
    }

    /**
     * Passo 10 do diagrama: processarPlano(tipo).
     * Registra o plano escolhido pelo assinante.
     */
    public boolean processarPlano(String nomePlano) {
        try {
            planoSelecionado = Assinatura.Plano.valueOf(nomePlano.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Passo 11 do diagrama: «create» criar(assinante, plano).
     * Persiste o Assinante (se novo) e cria a Assinatura com protocolo.
     * Retorna a Assinatura para que a view exiba o protocolo (passo 14).
     */
    public Assinatura confirmarAssinatura(String nome, String celular, String email) throws IOException {
        // Cria assinante se nao existia
        if (assinanteAtual == null) {
            int assinanteId = assinanteDAO.proximoId();
            assinanteAtual = new Assinante(assinanteId, nome, celular, email);
            assinanteDAO.salvar(assinanteAtual);
        }

        // Passo 11: «create» Assinatura.criar(assinante, plano)
        int assinaturaId = assinaturaDAO.proximoId();
        Assinatura assinatura = new Assinatura(assinaturaId, assinanteAtual.getId(), planoSelecionado);
        assinaturaDAO.salvar(assinatura);

        return assinatura;
    }

    public Assinante getAssinanteAtual() { return assinanteAtual; }
}
