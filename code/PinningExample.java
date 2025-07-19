public class PinningExample {
    public static void main(String[] args) {
        Thread.startVirtualThread(() -> {
            synchronized (PinningExample.class) {
                try {
                    Thread.sleep(1000); // Blocking call inside synchronized block
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }
}