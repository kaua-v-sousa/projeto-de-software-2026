package entity;

public class Assinante {
    private int id;
    private String nome;
    private String celular;
    private String email;

    public Assinante(int id, String nome, String celular, String email) {
        this.id = id;
        this.nome = nome;
        this.celular = celular;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public String getCelular() { return celular; }
    public String getEmail() { return email; }

    public String toCsv() {
        return id + ";" + nome + ";" + celular + ";" + email;
    }

    public static Assinante fromCsv(String line) {
        String[] p = line.split(";");
        return new Assinante(
            Integer.parseInt(p[0].trim()),
            p[1].trim(),
            p[2].trim(),
            p[3].trim()
        );
    }

    @Override
    public String toString() {
        return "Assinante{id=" + id + ", nome='" + nome + "', celular='" + celular + "'}";
    }
}
