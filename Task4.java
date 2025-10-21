import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Task4 {

    public static int collatzSteps(long n) {
        int steps = 0;
        while (n != 1) {
            if ((n & 1) == 0) {
                n /= 2;
            } else {
                n = 3 * n + 1;
            }
            steps++;
        }
        return steps;
    }

    public static void runTask3(int n, int threadCount) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicLong totalSteps = new AtomicLong(0);
        long start = System.currentTimeMillis();

        for (int i = 1; i <= n; i++) {
            final int num = i;
            executor.submit(() -> {
                int steps = collatzSteps(num);
                totalSteps.addAndGet(steps);
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        double avgSteps = totalSteps.doubleValue() / n;
        long end = System.currentTimeMillis();

        System.out.println("=== Task3 (ExecutorService) ===");
        System.out.println("Середня кiлькiсть крокiв: " + avgSteps);
        System.out.println("Час виконання: " + (end - start) / 1000.0 + " сек\n");
    }

    
    public static void runTask4(int n, int threadCount) throws InterruptedException {
        AtomicInteger current = new AtomicInteger(1);
        AtomicLong totalSteps = new AtomicLong(0);
        Thread[] threads = new Thread[threadCount];

        long start = System.currentTimeMillis();

        for (int t = 0; t < threadCount; t++) {
            threads[t] = new Thread(() -> {
                while (true) {
                    int num = current.getAndIncrement();
                    if (num > n) break;
                    int steps = collatzSteps(num);
                    totalSteps.addAndGet(steps);
                }
            });
            threads[t].start();
        }

        for (Thread thread : threads) thread.join();

        long end = System.currentTimeMillis();
        double avgSteps = totalSteps.doubleValue() / n;

        System.out.println("=== Task4 (Атомарна синхронiзацiя без пулу) ===");
        System.out.println("Середня кiлькiсть крокiв: " + avgSteps);
        System.out.println("Час виконання: " + (end - start) / 1000.0 + " сек\n");
    }

    public static void main(String[] args) throws InterruptedException {
        final int n = 1_000_000;     // можна зменшити для тестів
        final int threadCount = 8;
        runTask3(n, threadCount);
        runTask4(n, threadCount);
    }
}