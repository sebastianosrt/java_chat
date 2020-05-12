package Server;

/**
 * Apre il server
 *
 * @author Sebastiano Sartor
 */
class Main {
    public static void main(String[] args) {
        new Thread(new Server()).start();
    }
}