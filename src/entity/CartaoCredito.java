package entity;

import java.time.LocalDate;

public class CartaoCredito {
    private String numero;
    private String nomeTitular;
    private String validade;
    private String cvv;

    public CartaoCredito(String numero, String nomeTitular, String validade, String cvv) {
        this.numero = numero.replaceAll("[\\s\\-]", "");
        this.nomeTitular = nomeTitular;
        this.validade = validade;
        this.cvv = cvv;
    }

    public boolean isNumeroValido() {
        String num = numero.replaceAll("[^0-9]", "");
        return num.length() >= 13 && num.length() <= 19;
    }

    public boolean isCvvValido() {
        return cvv != null && cvv.matches("\\d{3,4}");
    }

    public boolean isValidadeValida() {
        if (validade == null || !validade.matches("\\d{2}/\\d{2}")) return false;
        String[] parts = validade.split("/");
        int mes = Integer.parseInt(parts[0]);
        int ano = Integer.parseInt("20" + parts[1]);
        LocalDate now = LocalDate.now();
        return mes >= 1 && mes <= 12 &&
               (ano > now.getYear() || (ano == now.getYear() && mes >= now.getMonthValue()));
    }

    public String getNumeroMascarado() {
        if (numero.length() < 4) return "****";
        return "**** **** **** " + numero.substring(numero.length() - 4);
    }

    public String getNumero() { return numero; }
    public String getNomeTitular() { return nomeTitular; }
    public String getValidade() { return validade; }
}
