package Server;

/**
 * @author Sebastiano Sartor
 */
class Main {
    public static void main(String[] args) {
        // apri il server
        new Thread(new Server()).start();
    }
}