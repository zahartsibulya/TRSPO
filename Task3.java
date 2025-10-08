import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Task3 {

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

    public static void main(String[] args) throws InterruptedException {
        final int n = 10_000_000;
        final int thread_count = 8;

        ExecutorService executor = Executors.newFixedThreadPool(thread_count);
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

        double avgSteps = totalSteps.doubleValue() /n;
        long end = System.currentTimeMillis();

        System.out.println("Середня кiлькiсть крокiв: " + avgSteps);
        System.out.println("Час виконання: " + (end - start) / 1000.0 + " сек");
    }
}