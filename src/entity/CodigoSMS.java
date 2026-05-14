package entity;

import java.time.LocalDateTime;
import java.util.Random;

public class CodigoSMS {
    private String celular;
    private String codigo;
    private LocalDateTime expiracao;
    private boolean usado;

    public CodigoSMS(String celular) {
        this.celular = celular;
        this.codigo = gerarCodigo();
        this.expiracao = LocalDateTime.now().plusMinutes(5);
        this.usado = false;
    }

    private String gerarCodigo() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    public boolean validar(String codigoInformado) {
        if (usado) return false;
        if (LocalDateTime.now().isAfter(expiracao)) return false;
        if (this.codigo.equals(codigoInformado)) {
            usado = true;
            return true;
        }
        return false;
    }

    public String getCodigo() { return codigo; }
    public String getCelular() { return celular; }
}
