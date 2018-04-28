import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

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
        for (File file : files) {
            sources.add("text/" + file.getName());
        }
        String[] sourcesArr = new String[sources.size()];
        sources.toArray(sourcesArr);
        long startTime = System.nanoTime();
        new OccurrencesImpl().getOccurencies(
                sourcesArr,
                new String[] {"starter", "ffdf", "wfrrf", "cdcd","dc"},
                //wordsArr,
                "output.txt");

        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Времени потрачено на обработку файлов: " + estimatedTime);
    }
}
