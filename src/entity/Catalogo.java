package entity;

import java.util.ArrayList;
import java.util.List;

public class Catalogo {
    private List<Produto> produtos;

    public Catalogo() {
        this.produtos = new ArrayList<>();
    }

    public void adicionar(Produto p) {
        produtos.add(p);
    }

    public Produto buscarPorId(int id) {
        return produtos.stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElse(null);
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public boolean isEmpty() {
        return produtos.isEmpty();
    }
}
