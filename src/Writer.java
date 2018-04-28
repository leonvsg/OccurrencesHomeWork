import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Writer implements AutoCloseable, Runnable{

    private FileWriter fileWriter;
    private BlockingQueue<String> sentences;

    public Writer(String fileName, BlockingQueue<String> sentences) throws IOException {
        fileWriter = new FileWriter(fileName);
        this.sentences = sentences;
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
        String str;
        try {
            while (!(str = sentences.take()).equals("-1")){
                write(str);
                System.out.print("В файл записано: " + str);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
