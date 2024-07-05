import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static final char R = 'R';
    public static int maxInterval, maxCount;

    public static void main(String[] args) throws InterruptedException {

        long startTs = System.currentTimeMillis(); // start time
        List<Thread> threads = new ArrayList<>();
        Thread thread = new Thread();

        thread = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    synchronized (sizeToFreq) {
                        sizeToFreq.wait();
                        for (Map.Entry entry : sizeToFreq.entrySet()) {
                            if (maxCount < (int) entry.getValue()) {
                                maxCount = (int) entry.getValue();
                                maxInterval = (int) entry.getKey();
                            }
                            System.out.println("Самое частое количество повторений "
                                    + maxInterval
                                    + " (встретилось "
                                    + maxCount
                                    + " раз)");
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread.start();


        for (int i = 0; i < 1000; i++) { //цикл для потоков
            Thread thread1000 = new Thread(() -> {
                String route = generateRoute("RLRFR", 100);
                //System.out.println(route);
                int interval, count;
                int startInterval = -1;
                synchronized (sizeToFreq) {
                    for (int j = 0; j < 100; j++) { //цикл найти первую букву R
                        if (route.charAt(j) == R && startInterval == -1) {
                            startInterval = j;
                            continue;
                        }
                        if (route.charAt(j) != R && startInterval != -1) { //найти интервал
                            interval = j - startInterval;
                            startInterval = -1;

                            //добавить интервал и его повторение в map
                            if (sizeToFreq.containsKey(interval)) {
                                count = sizeToFreq.get(interval) + 1;
                            } else {
                                count = 1;
                            }
                            sizeToFreq.put(interval, count);
                            sizeToFreq.notify();
                        }
                    }
                }
            });
            threads.add(thread1000);
            thread1000.start();
        }//конец циклов 1000

        for (Thread t : threads) {
            t.join();
        }

        thread.interrupt();

        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
        synchronized (sizeToFreq) {
            System.out.println("Другие размеры:");
            for (Map.Entry entry : sizeToFreq.entrySet()) {
                if (maxInterval == (int) entry.getKey()) {
                    continue;
                }
                System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)");
            }
        }

    }//конец main

    //Для генерации маршрутов
    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}

//Время в main: 67 ms
//Время в sizeToFreq: 76 ms