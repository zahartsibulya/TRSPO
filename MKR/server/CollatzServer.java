import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CollatzServer {

    public static int collatzSteps(long n) {
        int steps = 0;
        while (n != 1) {
            if ((n & 1L) == 0L) {
                n /= 2;
            } else {
                n = 3 * n + 1;
            }
            steps++;
        }
        return steps;
    }

    public static double averageStepsParallel(int N, int threadCount) throws InterruptedException {
        if (N <= 0) throw new IllegalArgumentException("N must be positive");
        if (threadCount <= 0) throw new IllegalArgumentException("threadCount must be positive");

        AtomicInteger current = new AtomicInteger(1);
        AtomicLong totalSteps = new AtomicLong(0);
        Thread[] threads = new Thread[threadCount];

        for (int t = 0; t < threadCount; t++) {
            threads[t] = new Thread(() -> {
                while (true) {
                    int num = current.getAndIncrement();
                    if (num > N) break;
                    int steps = collatzSteps(num);
                    totalSteps.addAndGet(steps);
                }
            });
            threads[t].start();
        }

        for (Thread thread : threads) thread.join();

        return totalSteps.doubleValue() / N;
    }

    private static int envIntOrDefault(String name, int def) {
        String v = System.getenv(name);
        if (v == null || v.trim().isEmpty()) return def;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static void main(String[] args) {
        final int port = 9000; // за умовою
        final int threadCount = envIntOrDefault("THREAD_COUNT",
                Math.max(1, Runtime.getRuntime().availableProcessors()));

        System.out.println("[SERVER] Starting on port " + port + ", THREAD_COUNT=" + threadCount);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // достатньо одного клієнта
            try (Socket client = serverSocket.accept()) {
                System.out.println("[SERVER] Client connected: " + client.getRemoteSocketAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8));

                String line = in.readLine();
                if (line == null || line.trim().isEmpty()) {
                    out.write("ERROR: empty input\n");
                    out.flush();
                    return;
                }

                int N;
                try {
                    N = Integer.parseInt(line.trim());
                    if (N <= 0) throw new NumberFormatException("N <= 0");
                } catch (NumberFormatException ex) {
                    out.write("ERROR: N must be a positive integer\n");
                    out.flush();
                    return;
                }

                System.out.println("[SERVER] Received N=" + N + ". Computing...");
                long start = System.currentTimeMillis();

                double avg = averageStepsParallel(N, threadCount);

                long end = System.currentTimeMillis();
                System.out.println("[SERVER] Done in " + ((end - start) / 1000.0) + "s, avg=" + avg);

                out.write(Double.toString(avg));
                out.write("\n");
                out.flush();

                System.out.println("[SERVER] Sent ответ и закрываю соединение.");
            }
        } catch (IOException e) {
            System.err.println("[SERVER] IO Error: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("[SERVER] Interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
