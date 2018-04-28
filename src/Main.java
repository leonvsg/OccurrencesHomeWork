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
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner = new Scanner(new File("words.txt"));
        List<String> words = new ArrayList<>();
        while (scanner.hasNext())
            words.add(scanner.next());
        String[] wordsArr = new String[words.size()];
        words.toArray(wordsArr);
        File folder = new File("text");
        File[] files = folder.listFiles();
        List<String> sources = new ArrayList<>();

        sources.add("sggfjjd");

        for (File file : files) {
            sources.add("text/" + file.getName());
        }
        String[] sourcesArr = new String[sources.size()];
        sources.toArray(sourcesArr);
        logger.info("Подготовительный этап пройден, начинаем парсинг");
        long startTime = System.nanoTime();
        new OccurrencesImpl().getOccurencies(
                sourcesArr,
                wordsArr,
                "output.txt");

        long estimatedTime = System.nanoTime() - startTime;
        logger.info("Парсинг окончен");
        logger.info("Времени потрачено на обработку файлов: " + estimatedTime);
    }
}
