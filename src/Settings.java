import java.util.concurrent.TimeUnit;

public class Settings {

    private Settings(){}

    public final static int WRITER_TIMEOUT = 1;
    public final static TimeUnit WRITER_TIMEOUT_TIMEUNIT = TimeUnit.SECONDS;
    public final static int THREAD_TIMEOUT = 10;
    public final static int SELECTOR_THREADS_AMOUNT = 3;
    public final static int READER_THREADS_AMOUNT = 3;
    public final static int MESSAGE_QUEUE_MAX_AMOUNT = 50_000;
    public final static String SOURCE_FORMAT_EXCEPTION_MESSAGE = "Неизвестный тип ресурса или ресурс недоступен\n";
    public final static String OBJECT_FORMAT_EXCEPTION_MESSAGE = "Значение аргумента не может быть null или пустым\n";

    //Sentence Compiler Settings
    public static int SENTENCES_MAX_AMOUNT = 300_000;
    public static int WORDS_AMOUNT = 100;
    public static int SOURCES_AMOUNT = 200;
    public static int SENTENCE_MAX_LENGTH = 15;
}
