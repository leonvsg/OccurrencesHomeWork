import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws FileNotFoundException {
        logger.info("Подготовительный этап");
        Scanner scanner = new Scanner(new File("words.txt"));
        List<String> buffer = new ArrayList<>();
        while (scanner.hasNext())
            buffer.add(scanner.next());
        String[] words = new String[buffer.size()];
        buffer.toArray(words);
        buffer.clear();
        File folder = new File("text");
        File[] files = folder.listFiles();
        for (File file : files) {
            buffer.add("text/" + file.getName());
        }
        String[] sources = new String[buffer.size()];
        buffer.toArray(sources);
        logger.info("Подготовительный этап пройден");
        long startTime = System.nanoTime();
        new OccurrencesImpl().getOccurencies(
                sources,
                words,
                "output.txt");
        long estimatedTime = System.nanoTime() - startTime;
        logger.info("Времени потрачено на обработку файлов: " + estimatedTime);
    }
}
