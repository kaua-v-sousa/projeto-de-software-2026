package entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Assinatura {

    public enum Plano {
        BASICO(49.90, 5, "Basico - ate 5 itens/semana"),
        STANDARD(79.90, 10, "Standard - ate 10 itens/semana"),
        PREMIUM(119.90, 20, "Premium - ate 20 itens/semana");

        private final double valor;
        private final int limiteItens;
        private final String descricao;

        Plano(double valor, int limiteItens, String descricao) {
            this.valor = valor;
            this.limiteItens = limiteItens;
            this.descricao = descricao;
        }

        public double getValor() { return valor; }
        public int getLimiteItens() { return limiteItens; }
        public String getDescricao() { return descricao; }
    }

    public enum Status { PENDENTE, ATIVA, CANCELADA }

    private int id;
    private int assinanteId;
    private Plano plano;
    private Status status;
    private String protocolo;
    private LocalDate dataCriacao;

    // Construtor para criacao nova
    public Assinatura(int id, int assinanteId, Plano plano) {
        this.id = id;
        this.assinanteId = assinanteId;
        this.plano = plano;
        this.status = Status.PENDENTE;
        this.protocolo = gerarProtocolo(id);
        this.dataCriacao = LocalDate.now();
    }

    // Construtor para leitura do CSV
    public Assinatura(int id, int assinanteId, Plano plano,
                      Status status, String protocolo, LocalDate dataCriacao) {
        this.id = id;
        this.assinanteId = assinanteId;
        this.plano = plano;
        this.status = status;
        this.protocolo = protocolo;
        this.dataCriacao = dataCriacao;
    }

    private String gerarProtocolo(int id) {
        return "FNB-" + LocalDate.now().getYear() + "-" + String.format("%06d", id);
    }

    public String toCsv() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return id + ";" + assinanteId + ";" + plano.name() + ";" +
               status.name() + ";" + protocolo + ";" + dataCriacao.format(fmt);
    }

    public static Assinatura fromCsv(String line) {
        String[] p = line.split(";");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return new Assinatura(
            Integer.parseInt(p[0].trim()),
            Integer.parseInt(p[1].trim()),
            Plano.valueOf(p[2].trim()),
            Status.valueOf(p[3].trim()),
            p[4].trim(),
            LocalDate.parse(p[5].trim(), fmt)
        );
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAssinanteId() { return assinanteId; }
    public Plano getPlano() { return plano; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    /** Corresponde ao metodo atualizarStatus(ativo) do diagrama de sequencia UC3. */
    public void atualizarStatus(Status novoStatus) { this.status = novoStatus; }
    public String getProtocolo() { return protocolo; }
    public LocalDate getDataCriacao() { return dataCriacao; }

    @Override
    public String toString() {
        return "Assinatura{protocolo='" + protocolo + "', plano=" + plano.getDescricao() +
               ", status=" + status + ", valor=R$ " + String.format("%.2f", plano.getValor()) + "}";
    }
}
