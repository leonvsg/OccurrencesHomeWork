import java.io.*;
import java.util.concurrent.*;

public class OccurrencesImpl implements Occurrences {

    private final static int THREAD_TIMEOUT = Settings.THREAD_TIMEOUT;
    private final static int THREADS_AMOUNT = Settings.READER_THREADS_AMOUNT;
    private final static int QUEUE_MAX_AMOUNT = Settings.MESSAGE_QUEUE_MAX_AMOUNT;

    @Override
    public void getOccurencies(String[] sources, String[] words, String res) throws IllegalArgumentException {
        if (sources == null || words == null || res == null ||
                sources.length == 0 || words.length == 0 || res.isEmpty())
            throw new IllegalArgumentException(Settings.OBJECT_FORMAT_EXCEPTION_MESSAGE);

        ExecutorService executor = Executors.newFixedThreadPool(THREADS_AMOUNT);
        BlockingQueue<String> sentences = new ArrayBlockingQueue<>(QUEUE_MAX_AMOUNT);
        try(Writer writer = new Writer(res, sentences)) {
            executor.submit(writer);
            for (String source : sources){
                executor.submit(new Reader(sentences, source, words));
            }
            executor.shutdown();
            while (!executor.isTerminated()) Thread.sleep(THREAD_TIMEOUT);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(System.out);
        } catch (Exception e) {
            System.out.println("Непредвиденная ошибка: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }
}
