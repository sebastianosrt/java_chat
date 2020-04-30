package Client.Models;

public class Messaggio {
    public int id;
    public String testo;
    public String mittente;
    public String destinatario;
    public String type;

    public Messaggio(int id, String testo, String mittente, String destinatario, String type) {
        this.id = id;
        this.testo = testo;
        this.mittente = mittente;
        this.destinatario = destinatario;
        this.type = type;
    }
}
