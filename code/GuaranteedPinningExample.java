import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GuaranteedPinningExample {
    public static void main(String[] args) {
        System.out.println("Virtual thread starting...");

        Thread.startVirtualThread(() -> {
            synchronized (GuaranteedPinningExample.class) {
                try {
                    System.out.println("Reading from input stream (waiting for input)... [Enter something]");
                    // Legacy blocking I/O — causes pinning
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    String input = reader.readLine(); // Blocking call

                    System.out.println("You entered: " + input);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}