package persistence;

import entity.Assinatura;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AssinaturaDAO {
    private static final String ARQUIVO = "data/assinaturas.csv";

    public void salvar(Assinatura assinatura) throws IOException {
        garantirArquivo();
        List<Assinatura> lista = listarTodos();
        boolean encontrado = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == assinatura.getId()) {
                lista.set(i, assinatura);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) lista.add(assinatura);
        salvarTodos(lista);
    }

    public int proximoId() throws IOException {
        List<Assinatura> lista = listarTodos();
        return lista.stream().mapToInt(Assinatura::getId).max().orElse(0) + 1;
    }

    public Assinatura buscarPorId(int id) throws IOException {
        return listarTodos().stream()
            .filter(a -> a.getId() == id)
            .findFirst().orElse(null);
    }

    public List<Assinatura> listarTodos() throws IOException {
        garantirArquivo();
        List<Assinatura> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) lista.add(Assinatura.fromCsv(line));
            }
        }
        return lista;
    }

    private void salvarTodos(List<Assinatura> lista) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO))) {
            for (Assinatura a : lista) pw.println(a.toCsv());
        }
    }

    private void garantirArquivo() throws IOException {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        File f = new File(ARQUIVO);
        if (!f.exists()) f.createNewFile();
    }
}
