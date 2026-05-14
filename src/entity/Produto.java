package entity;

public class Produto {
    private int id;
    private String nome;
    private String categoria;
    private String unidade;
    private double preco;

    public Produto(int id, String nome, String categoria, String unidade, double preco) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.unidade = unidade;
        this.preco = preco;
    }

    public static Produto fromCsv(String line) {
        String[] p = line.split(";");
        return new Produto(
            Integer.parseInt(p[0].trim()),
            p[1].trim(),
            p[2].trim(),
            p[3].trim(),
            Double.parseDouble(p[4].trim())
        );
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public String getUnidade() { return unidade; }
    public double getPreco() { return preco; }

    @Override
    public String toString() {
        return String.format("[%2d] %-15s %-10s %-10s R$ %.2f", id, nome, categoria, unidade, preco);
    }
}
