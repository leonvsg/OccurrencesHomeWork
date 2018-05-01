import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Reader implements Runnable, AutoCloseable {

    private final static Logger logger = Logger.getLogger(Reader.class);
    private String source;
    private final static int THREAD_TIMEOUT = Settings.THREAD_TIMEOUT;
    private final static int THREAD_AMOUNT = Settings.SELECTOR_THREADS_AMOUNT;
    private volatile ExecutorService executor;
    private Selector<String> selector;
    private boolean multiThreadSelector;

    private enum SourceType{LOCAL_FILE, FTP, HTTP}

    public Reader(BlockingQueue<String> sentences, String source, String[] words) {
        this(sentences, source, words, false);
    }

    public Reader(BlockingQueue<String> sentences, String source, String[] words, boolean multiThreadSelector) {
        this.source = source;
        executor = Executors.newFixedThreadPool(THREAD_AMOUNT);
        selector = new OccurrencesSelector(words, sentences);
        this.multiThreadSelector = multiThreadSelector;
    }

    public Reader(BlockingQueue<String> sentences, String[] words) {
        this(sentences, words, false);
    }

    public Reader(BlockingQueue<String> sentences, String[] words, boolean multiThreadSelector) {
        executor = Executors.newFixedThreadPool(THREAD_AMOUNT);
        selector = new OccurrencesSelector(words, sentences);
        this.multiThreadSelector = multiThreadSelector;
    }

    public synchronized void read(String source) {
        logger.info("Начинаем парсить файл: " + source);
        //TODO переделать
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(source), "UTF-8"))){
            StringBuilder sentence = new StringBuilder();
            int i;
            while ((i = reader.read()) != -1){
                char nextChar = (char) i;
                if (nextChar == '\n') continue;
                if (nextChar == '.' || nextChar == '!' || nextChar == '?'){
                    sentence.append(nextChar);
                    nextChar = (char) reader.read();
                    if (nextChar != ' ' && nextChar != '\n') {
                        sentence.append(nextChar);
                        continue;
                    }
                    nextChar = (char) reader.read();
                    if (Character.isLowerCase(nextChar)){
                        sentence.append(nextChar);
                    } else {
                        select(sentence.toString());
                        sentence.delete(0, sentence.length());
                        sentence.append(nextChar);
                    }
                } else {
                    sentence.append(nextChar);
                }
            }
            logger.info("Парсинг файла : " + source + " окончен");
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (UnknownSourceType e) {
            logger.error("Неверно укказан источник данных (файл, http, ftp)\n" + e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
        while (!executor.isTerminated()) Thread.sleep(THREAD_TIMEOUT);
    }

    @Override
    public void run() {
        read(source);
    }

    private synchronized void select(String sentence) {
        if (multiThreadSelector) executor.submit(() -> selector.select(sentence));
        else selector.select(sentence);
    }

    private InputStream getInputStream(String source) throws UnknownSourceType, IOException {
        InputStream stream;
        switch (getSourceType(source)) {
            case LOCAL_FILE:
                stream = new FileInputStream(source);
                break;
            case FTP:
                stream = new URL(source).openConnection().getInputStream();
                break;
            case HTTP:
                stream = new URL(source).openConnection().getInputStream();
                break;
            default:
                throw new UnknownSourceType(Settings.SOURCE_FORMAT_EXCEPTION_MESSAGE + source);
        }
        return stream;
    }

    private SourceType getSourceType(String source){
        if (source == null || source.isEmpty()) return null;
        if (source.startsWith("http")) return SourceType.HTTP;
        if (source.startsWith("ftp")) return SourceType.FTP;
        return SourceType.LOCAL_FILE;
    }

    public class UnknownSourceType extends Exception{

        UnknownSourceType(String message){
            super(message);
        }

    }
}
