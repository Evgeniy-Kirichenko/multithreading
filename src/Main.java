import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {

        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }
        List<Future<Integer>> futures = new ArrayList<>();
        List<Callable<Integer>> callables = new ArrayList<>();
        List<Integer> res = new ArrayList<>();//для сбора результата

        ExecutorService ex = Executors.newFixedThreadPool(10);//создаем пул потоков

        long startTs = System.currentTimeMillis(); // start time
        for (String text : texts) {
            callables.add(() -> maxSizeText(text));//создаем список задач
        }
        futures.addAll(ex.invokeAll(callables));//запускаем пул задач от списка задач и ответы
        //кладем в коллекцию List<Future<Integer>> futures

        for (Future<Integer> future : futures) {//собираем ответы от Future
            res.add(future.get());
        }

        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Максимальное значение = " + res.stream().max(Comparator.naturalOrder()).get());

        System.out.println("Time: " + (endTs - startTs) + "ms");
        ex.shutdown();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Integer maxSizeText(String text) {
        int maxSize = 0;
        for (int i = 0; i < text.length(); i++) {
            for (int j = 0; j < text.length(); j++) {
                if (i >= j) {
                    continue;
                }
                boolean bFound = false;
                for (int k = i; k < j; k++) {
                    if (text.charAt(k) == 'b') {
                        bFound = true;
                        break;
                    }
                }
                if (!bFound && maxSize < j - i) {
                    maxSize = j - i;
                }
            }
        }
        System.out.println(text.substring(0, 100) + " -> " + maxSize);
        return (Integer) maxSize;
    }
}
