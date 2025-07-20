import java.io.BufferedReader;  // Line-by-line input read karne ke liye
import java.io.InputStreamReader;  // System.in ko reader me convert karne ke liye

public class GuaranteedPinningExample {

    public static void main(String[] args) {
        //  Console pe virtual thread start hone ka message
        System.out.println("Virtual thread starting...");

        //  Ek virtual thread start karo (Java 21+ feature)
        Thread.startVirtualThread(() -> {

            //  Synchronized block (class-level lock) — pinning scenario me kaafi critical
            synchronized (GuaranteedPinningExample.class) {
                try {
                    //  User se input lene ke liye prompt
                    System.out.println("Reading from input stream (waiting for input)... [Enter something]");

                    // ⚠ Legacy I/O (System.in) read karna — yeh blocking call hai
                    // ➤ Isse virtual thread pin ho jaata hai platform thread pe
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    String input = reader.readLine(); // 🔴 Blocking call

                    //  Input echo back to console
                    System.out.println("You entered: " + input);

                } catch (Exception e) {
                    //  Agar koi error aaye to stack trace print karo
                    e.printStackTrace();
                }
            }
        });
    }
}
