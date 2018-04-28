import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Writer implements AutoCloseable, Runnable{

    private final static Logger logger = Logger.getLogger(Writer.class);
    private FileWriter fileWriter;
    private BlockingQueue<String> sentences;
    private int queueTimeout;
    private TimeUnit queueTimeoutTimeUnit;

    public Writer(String fileName, BlockingQueue<String> sentences) throws IOException {
        fileWriter = new FileWriter(fileName);
        this.sentences = sentences;
        queueTimeout = Settings.WRITER_TIMEOUT;
        queueTimeoutTimeUnit = Settings.WRITER_TIMEOUT_TIMEUNIT;
    }

    public synchronized void write(String text) throws IOException {
        if (text == null) return;
        fileWriter.write(text + "\r\n");
        fileWriter.flush();
        logger.debug("Предложение \"" + text + "\" записано в файл");
    }

    @Override
    public void close() throws Exception {
        if (fileWriter != null) fileWriter.close();
    }

    @Override
    public void run() {
        String str = "";
        try {
            while (str != null){
                str = sentences.poll(queueTimeout, queueTimeoutTimeUnit);
                logger.debug("Из шины получено предложение: \"" + str + "\"");
                write(str);
            }
        } catch (InterruptedException | IOException e) {
            logger.error("Выполнение потока " + Thread.currentThread().getName() + " прервано\n" + e.getMessage());
        }
    }
}
