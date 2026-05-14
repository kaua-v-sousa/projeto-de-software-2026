package entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Cesta {

    public enum Status { ABERTA, CONFIRMADA, CANCELADA }

    private int id;
    private int assinaturaId;
    private String semana;
    private List<ItemCesta> itens;
    private Status status;
    private LocalDate dataCriacao;

    public Cesta(int id, int assinaturaId) {
        this.id = id;
        this.assinaturaId = assinaturaId;
        this.semana = calcularSemana();
        this.itens = new ArrayList<>();
        this.status = Status.ABERTA;
        this.dataCriacao = LocalDate.now();
    }

    private String calcularSemana() {
        return "Semana de " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void adicionarItem(Produto produto, int quantidade) {
        for (ItemCesta item : itens) {
            if (item.getProduto().getId() == produto.getId()) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                return;
            }
        }
        itens.add(new ItemCesta(produto, quantidade));
    }

    public boolean removerItem(int produtoId) {
        return itens.removeIf(item -> item.getProduto().getId() == produtoId);
    }

    public int getTotalQuantidade() {
        return itens.stream().mapToInt(ItemCesta::getQuantidade).sum();
    }

    public double getValorTotal() {
        return itens.stream().mapToDouble(ItemCesta::getSubtotal).sum();
    }

    public void confirmar() {
        this.status = Status.CONFIRMADA;
    }

    public String toCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(";")
          .append(assinaturaId).append(";")
          .append(semana).append(";")
          .append(status.name()).append(";")
          .append(dataCriacao.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append(";");

        StringBuilder itensStr = new StringBuilder();
        for (int i = 0; i < itens.size(); i++) {
            if (i > 0) itensStr.append("|");
            itensStr.append(itens.get(i).toCsv());
        }
        sb.append(itensStr);
        return sb.toString();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAssinaturaId() { return assinaturaId; }
    public String getSemana() { return semana; }
    public List<ItemCesta> getItens() { return itens; }
    public Status getStatus() { return status; }
    public LocalDate getDataCriacao() { return dataCriacao; }
}
