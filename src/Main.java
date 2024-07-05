import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    public static final char R = 'R';
    public static int maxCount = 0, maxInterval;

    public static void main(String[] args) {

        long startTs = System.currentTimeMillis(); // start time
        ExecutorService executor = Executors.newFixedThreadPool(1000);

        for (int i = 0; i < 1000; i++) { //цикл для потоков

            executor.execute(() -> {
                String route = generateRoute("RLRFR", 100);
                //System.out.println (route);
                int interval, count;
                int startInterval = -1;

                for (int j = 0; j < 100; j++) { //цикл найти первую букву R
                    if (route.charAt(j) == R && startInterval == -1) {
                        startInterval = j;
                        continue;
                    }
                    if (route.charAt(j) != R && startInterval != -1) { //найти интервал
                        interval = j - startInterval;
                        startInterval = -1;

                        //добавить интервал и его повторение в map
                        synchronized (sizeToFreq) {
                            if (sizeToFreq.containsKey(interval)) {
                                count = sizeToFreq.get(interval) + 1;
                            } else {
                                count = 1;
                            }
                            if (maxCount < count) {
                                maxCount = count;
                                maxInterval = interval;
                            }
                            sizeToFreq.put(interval, count);
                        }
                    }
                }
            });//конец лямбды
        }
        executor.shutdown();

        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");

        System.out.println("Самое частое количество повторений " + maxInterval + " (встретилось " + maxCount + " раз)\nДругие размеры:");
        for (Map.Entry entry : sizeToFreq.entrySet()) {
            if (maxInterval == (int) entry.getKey()) {
                continue;
            }
            System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)");
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

//Time: 67ms