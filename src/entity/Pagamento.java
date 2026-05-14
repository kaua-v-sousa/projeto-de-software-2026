package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Pagamento {

    public enum Status { PENDENTE, APROVADO, RECUSADO }

    private int id;
    private int assinaturaId;
    private double valor;
    private Status status;
    private LocalDateTime dataProcessamento;
    private String codigoTransacao;

    // Construtor para criacao nova
    public Pagamento(int id, int assinaturaId, double valor) {
        this.id = id;
        this.assinaturaId = assinaturaId;
        this.valor = valor;
        this.status = Status.PENDENTE;
        this.dataProcessamento = LocalDateTime.now();
        this.codigoTransacao = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Construtor para leitura do CSV
    public Pagamento(int id, int assinaturaId, double valor,
                     Status status, LocalDateTime dataProcessamento, String codigoTransacao) {
        this.id = id;
        this.assinaturaId = assinaturaId;
        this.valor = valor;
        this.status = status;
        this.dataProcessamento = dataProcessamento;
        this.codigoTransacao = codigoTransacao;
    }

    public void aprovar() { this.status = Status.APROVADO; }
    public void recusar() { this.status = Status.RECUSADO; }

    public String toCsv() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return id + ";" + assinaturaId + ";" + valor + ";" +
               status.name() + ";" + dataProcessamento.format(fmt) + ";" + codigoTransacao;
    }

    public static Pagamento fromCsv(String line) {
        String[] p = line.split(";");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return new Pagamento(
            Integer.parseInt(p[0].trim()),
            Integer.parseInt(p[1].trim()),
            Double.parseDouble(p[2].trim()),
            Status.valueOf(p[3].trim()),
            LocalDateTime.parse(p[4].trim(), fmt),
            p[5].trim()
        );
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAssinaturaId() { return assinaturaId; }
    public double getValor() { return valor; }
    public Status getStatus() { return status; }
    public LocalDateTime getDataProcessamento() { return dataProcessamento; }
    public String getCodigoTransacao() { return codigoTransacao; }

    @Override
    public String toString() {
        return "Pagamento{id=" + id + ", valor=R$ " + String.format("%.2f", valor) +
               ", status=" + status + ", transacao='" + codigoTransacao + "'}";
    }
}
