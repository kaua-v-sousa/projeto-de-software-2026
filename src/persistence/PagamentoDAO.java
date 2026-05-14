package persistence;

import entity.Pagamento;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PagamentoDAO {
    private static final String ARQUIVO = "data/pagamentos.csv";

    public void salvar(Pagamento pagamento) throws IOException {
        garantirArquivo();
        List<Pagamento> lista = listarTodos();
        boolean encontrado = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == pagamento.getId()) {
                lista.set(i, pagamento);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) lista.add(pagamento);
        salvarTodos(lista);
    }

    public int proximoId() throws IOException {
        List<Pagamento> lista = listarTodos();
        return lista.stream().mapToInt(Pagamento::getId).max().orElse(0) + 1;
    }

    public List<Pagamento> listarTodos() throws IOException {
        garantirArquivo();
        List<Pagamento> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) lista.add(Pagamento.fromCsv(line));
            }
        }
        return lista;
    }

    private void salvarTodos(List<Pagamento> lista) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO))) {
            for (Pagamento p : lista) pw.println(p.toCsv());
        }
    }

    private void garantirArquivo() throws IOException {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        File f = new File(ARQUIVO);
        if (!f.exists()) f.createNewFile();
    }
}
