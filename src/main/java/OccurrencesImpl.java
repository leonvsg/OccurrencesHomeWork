import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OccurrencesImpl implements Occurrences {

    private final static Logger logger = Logger.getLogger(OccurrencesImpl.class);
    private final static int THREAD_TIMEOUT = Settings.THREAD_TIMEOUT;
    private final static int THREADS_AMOUNT = Settings.READER_THREADS_AMOUNT;
    private final static int QUEUE_MAX_AMOUNT = Settings.MESSAGE_QUEUE_MAX_AMOUNT;
    private final static boolean MULTI_THREAD_SELECTOR_FLAG = Settings.MULTI_THREAD_SELECTOR;
    private ExecutorService executor;
    private BlockingQueue<String> sentences;

    public OccurrencesImpl() {
        executor = Executors.newFixedThreadPool(THREADS_AMOUNT);
        sentences = new ArrayBlockingQueue<>(QUEUE_MAX_AMOUNT);
    }

    @Override
    public void getOccurencies(String[] sources, String[] words, String res) throws IllegalArgumentException {
        if (sources == null || words == null || res == null ||
                sources.length == 0 || words.length == 0 || res.isEmpty())
            throw new IllegalArgumentException(Settings.OBJECT_FORMAT_EXCEPTION_MESSAGE);

        logger.info("Начинаем парсинг файлов");
        try(Writer writer = new Writer(res, sentences)) {
            executor.submit(writer);
            for (String source : sources){
                executor.submit(new Reader(sentences, source, words, MULTI_THREAD_SELECTOR_FLAG));
            }
            executor.shutdown();
            while (!executor.isTerminated()) Thread.sleep(THREAD_TIMEOUT);
            logger.info("Парсинг окончен");
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка: " + e.getMessage());
        }
    }
}
