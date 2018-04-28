import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class SentenceCompiler {

    private final static Logger logger = Logger.getLogger(SentenceCompiler.class);

    public static void main(String[] args) {
        List<String> words = getVocabulary();
        makeWordsFile(words);
        makeSourcesFiles(words);
    }

    private static void makeSourcesFiles(List<String> words) {
        logger.debug("Создаем список файлов с предложениями");
        for (int i = 0; i < Settings.SOURCES_AMOUNT; i++) {
            try(FileWriter fileWriter = new FileWriter("text\\" + String.valueOf(i))){
                logger.debug("Формируем файл №" + i);
                int sentenceAmount = new Random().nextInt(Settings.SENTENCES_MAX_AMOUNT);
                for (int j = 0; j < sentenceAmount; j++) {
                    StringBuilder sentence = new StringBuilder();
                    Character[] chars = new Character[]{'.', '!', '?'};
                    int sentenceLength = new Random().nextInt(Settings.SENTENCE_MAX_LENGTH);
                    String firstWord = words.get(new Random().nextInt(words.size()));
                    sentence.append(firstWord.replaceFirst(firstWord.substring(0, 1), firstWord.substring(0, 1).toUpperCase()));
                    for (int k = 0; k < sentenceLength; k++) {
                        sentence.append(" ");
                        sentence.append(words.get(new Random().nextInt(words.size())));
                    }
                    sentence.append(chars[new Random().nextInt(3)]);
                    sentence.append(" ");
                    fileWriter.write(sentence.toString());
                    fileWriter.flush();
                    logger.debug("Следующее предложение записано в файл: " + sentence.toString());
                }
                logger.debug("Файл №" + i + " создан");
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        logger.debug("Создание файлов с предложениями закончено");
    }

    private static void makeWordsFile(List<String> words) {
        logger.debug("Создаем файл со словами");
        try(FileWriter fileWriter = new FileWriter("words.txt")){
            for (int i = 0; i < Settings.WORDS_AMOUNT; i++) {
                fileWriter.write(words.get(new Random().nextInt(words.size())) + "\r\n");
                fileWriter.flush();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        logger.debug("Файл со словами создан");
    }

    private static List<String> getVocabulary() {
        logger.debug("Формируем словарь");
        List<String> words = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("en.txt"))) {
            while (scanner.hasNext()) {
                String word = scanner.next()
                        .replaceAll("^\\d+$", "")
                        .replaceAll(" ", "")
                        .replaceAll("\\pP", "")
                        .replaceAll("\n", "");
                words.add(word);
                logger.debug("Слово \"" + word + "\" добавлено в список");
                scanner.next();
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }
        logger.debug("Словарь сформирован");
        return words;
    }

}
