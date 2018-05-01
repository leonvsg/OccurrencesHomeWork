import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

public class OccurrencesSelector implements Runnable, Selector<String> {

    private final static Logger logger = Logger.getLogger(OccurrencesSelector.class);
    private String sentence;
    private String[] words;
    private BlockingQueue<String> sentences;
    private final static String PUNCTUATIONS_DELETE_REGEXP = "\\pP";
    private final static String WORD_REGEXP = ".*\\b%s\\b.*";

    public OccurrencesSelector(String[] words, BlockingQueue<String> sentences) {
        this.words = words;
        this.sentences = sentences;
    }

    public OccurrencesSelector(String sentence, String[] words, BlockingQueue<String> sentences) {
        this.words = words;
        this.sentences = sentences;
        this.sentence = sentence;
    }

    @Override
    public synchronized boolean select(String sentence) {
        /*for (String word : words) {
            String regexp = String.format(WORD_REGEXP, word);
            if (sentence.matches(regexp)){
                try {
                    sentences.put(Thread.currentThread().getName() + ": " + sentence + "\r\n");
                    return;
                } catch (InterruptedException e) {
                    e.printStackTrace(System.out);
                }
            }
        }*/

        //TODO оптимизировать
        String[] buf = sentence.replaceAll(PUNCTUATIONS_DELETE_REGEXP, "").toLowerCase().split(" ");
        for (String word : words)
            for (String s : buf)
                if (s.equals(word.toLowerCase())){
                    try {
                        sentences.put(sentence);
                        logger.debug("Предложение \"" + sentence + "\" содержит слово: " + word);
                        logger.debug("В шину положено предложение: " + sentence);
                        return true;
                    } catch (InterruptedException e) {
                        logger.error("Выполнение потока " + Thread.currentThread().getName() + " прервано\n" + e.getMessage());
                    }
                }
        return false;
    }

    @Override
    public void run() {
        select(sentence);
    }
}
