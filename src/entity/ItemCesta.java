package entity;

public class ItemCesta {
    private Produto produto;
    private int quantidade;

    public ItemCesta(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getSubtotal() {
        return produto.getPreco() * quantidade;
    }

    public String toCsv() {
        return produto.getId() + ":" + produto.getNome() + ":" + quantidade;
    }

    @Override
    public String toString() {
        return String.format("%-15s %3d %-10s R$ %.2f",
            produto.getNome(), quantidade, produto.getUnidade(), getSubtotal());
    }
}
