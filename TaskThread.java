import java.util.Random;

class MonteCarlo implements Runnable{
    private int pointInCircle;
    private Random random;
    private int numPoint;

    public MonteCarlo(int numPoint){
        this.pointInCircle = 0;
        this.random = new Random();
        this.numPoint = numPoint;
    }

    public void run(){
        for(int i=0; i<numPoint; i++){
            double x = random.nextDouble();
            double y = random.nextDouble();
            if((x*x + y*y)<=1.0){
                pointInCircle++;
            }
        }
    }

    public int getPointInCircle(){
        return pointInCircle;
    }
}

public class TaskThread{
    public static void main(String[] args) throws InterruptedException{
        int n = 1_000_000;
        int numOfThreads=64; //можемо змінити на інше задане число(2,4,8,16,32,64)
        int pointsThread = n/numOfThreads;

        MonteCarlo[] tasks = new MonteCarlo[numOfThreads];
        Thread[] threads = new Thread[numOfThreads];

        long start = System.currentTimeMillis();

        for(int i = 0; i<numOfThreads; i++){
            tasks[i]=new MonteCarlo(pointsThread);
            threads[i]=new Thread(tasks[i]);
            threads[i].start();
        }
        for(int i = 0; i<numOfThreads; i++){
            threads[i].join();
        }
        int result = 0;
        for(int i = 0; i<numOfThreads; i++){
            result += tasks[i].getPointInCircle();
        }

        double pi = 4.0*result/n;
        long end = System.currentTimeMillis();

        System.out.println("Threads: " + numOfThreads);
        System.out.println("PI = " + pi);
        System.out.println("Time: " + (end - start) + " ms");
    }
}



