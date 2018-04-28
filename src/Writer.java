import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Writer implements AutoCloseable, Runnable{

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
        fileWriter.write(text);
        fileWriter.flush();
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
                write(str);
                System.out.print("В файл записано: " + str);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
