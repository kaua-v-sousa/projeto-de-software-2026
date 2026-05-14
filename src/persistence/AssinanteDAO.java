package persistence;

import entity.Assinante;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AssinanteDAO {
    private static final String ARQUIVO = "data/assinantes.csv";

    public void salvar(Assinante assinante) throws IOException {
        garantirArquivo();
        List<Assinante> lista = listarTodos();
        boolean encontrado = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == assinante.getId()) {
                lista.set(i, assinante);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) lista.add(assinante);
        salvarTodos(lista);
    }

    public int proximoId() throws IOException {
        List<Assinante> lista = listarTodos();
        return lista.stream().mapToInt(Assinante::getId).max().orElse(0) + 1;
    }

    public Assinante buscarPorId(int id) throws IOException {
        return listarTodos().stream()
            .filter(a -> a.getId() == id)
            .findFirst().orElse(null);
    }

    /** Corresponde ao buscarAssinante(celular) do diagrama UC1 (passo 4). */
    public Assinante buscarPorCelular(String celular) throws IOException {
        return listarTodos().stream()
            .filter(a -> a.getCelular().equals(celular))
            .findFirst().orElse(null);
    }

    public List<Assinante> listarTodos() throws IOException {
        garantirArquivo();
        List<Assinante> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) lista.add(Assinante.fromCsv(line));
            }
        }
        return lista;
    }

    private void salvarTodos(List<Assinante> lista) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO))) {
            for (Assinante a : lista) pw.println(a.toCsv());
        }
    }

    private void garantirArquivo() throws IOException {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        File f = new File(ARQUIVO);
        if (!f.exists()) f.createNewFile();
    }
}
