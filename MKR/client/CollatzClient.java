import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class CollatzClient {

    private static String requireEnv(String name) {
        String v = System.getenv(name);
        if (v == null || v.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing required env var: " + name);
        }
        return v.trim();
    }

    public static void main(String[] args) {
        String host = requireEnv("SERVER_HOST");
        int port = Integer.parseInt(requireEnv("SERVER_PORT"));
        int N = Integer.parseInt(requireEnv("COLLATZ_COUNT"));
        if (N <= 0) throw new IllegalArgumentException("COLLATZ_COUNT must be positive");

        System.out.println("[CLIENT] Connecting to " + host + ":" + port);

        int attempts = 20;
        int delayMs = 500;

        for (int a = 1; a <= attempts; a++) {
            try (Socket socket = new Socket(host, port)) {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                out.write(Integer.toString(N));
                out.write("\n");
                out.flush();

                String resp = in.readLine();
                System.out.println("[CLIENT] Average Collatz steps for [1.." + N + "] = " + resp);
                return;
            } catch (IOException ex) {
                System.out.println("[CLIENT] Attempt " + a + "/" + attempts + " failed: " + ex.getMessage());
                try { Thread.sleep(delayMs); } catch (InterruptedException ignored) {}
            }
        }

        System.err.println("[CLIENT] Could not connect to server after retries.");
        System.exit(1);
    }
}