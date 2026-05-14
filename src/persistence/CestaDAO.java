package persistence;

import entity.Cesta;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CestaDAO {
    private static final String ARQUIVO = "data/cestas.csv";

    public void salvar(Cesta cesta) throws IOException {
        garantirArquivo();
        List<String> linhas = new ArrayList<>();
        boolean encontrado = false;
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    int id = Integer.parseInt(line.split(";")[0].trim());
                    if (id == cesta.getId()) {
                        linhas.add(cesta.toCsv());
                        encontrado = true;
                    } else {
                        linhas.add(line);
                    }
                }
            }
        }
        if (!encontrado) linhas.add(cesta.toCsv());
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO))) {
            for (String l : linhas) pw.println(l);
        }
    }

    public int proximoId() throws IOException {
        garantirArquivo();
        int max = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    int id = Integer.parseInt(line.split(";")[0].trim());
                    if (id > max) max = id;
                }
            }
        }
        return max + 1;
    }

    private void garantirArquivo() throws IOException {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        File f = new File(ARQUIVO);
        if (!f.exists()) f.createNewFile();
    }
}
