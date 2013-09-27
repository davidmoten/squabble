package moten.david.squabble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import moten.david.squabble.Engine.Result;
import moten.david.squabble.Engine.WordStatus;
import moten.david.util.words.DictionaryAlwaysValid;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class Service {

    private static Logger log = Logger.getLogger(Service.class.getName());

    private static final int minimumChars = 3;
    private final Engine engine;
    private final User board = new User("board", 1);
    private static final boolean TURN_ON_OK_WORD = false;

    private final DataManager dataManager;

    public Service(Engine engine, DataManager dataManager) {
        this.engine = engine;
        this.dataManager = dataManager;
        ListMultimap<User, Word> map = ArrayListMultimap.create();
        Data data = new Data(map);
        data = engine.turnLetter(data, board);
        dataManager.getTransaction().begin();
        dataManager.setData(data);
        dataManager.getTransaction().commit();
    }

    public Data getData() {
        return dataManager.getData();
    }

    public WordStatus addWord(String user, String word) {
        dataManager.getTransaction().begin();
        Data data = dataManager.getData();
        Result result = engine.wordSubmitted(data,
                new User(user, minimumChars), word);
        log.info(word + " status:" + result.getStatus());
        dataManager.setData(result.getData());
        if (TURN_ON_OK_WORD && result.getStatus().equals(WordStatus.OK))
            turnLetter();
        dataManager.getTransaction().commit();
        return result.getStatus();
    }

    /**
     * Returns true if and only a letter was turned. If a letter was not turned
     * then the letters have run out and the game is over
     * 
     * @return
     */
    public boolean turnLetter() {
        dataManager.getTransaction().begin();
        Data data = dataManager.getData();
        Data newData = engine.turnLetter(dataManager.getData(), board);
        if (newData != data)
            dataManager.setData(newData);
        dataManager.getTransaction().commit();
        return newData != data;
    }

    public static void main(String[] args) throws IOException {
        Service service = new Service(new Engine(new DictionaryAlwaysValid(),
                new Letters("eng")), new DataManagerImpl());
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        System.out.println("data:\n" + service.dataManager.getData());
        while ((line = br.readLine()) != null) {
            if (line.trim().length() == 0)
                service.turnLetter();
            else if ("q".equals(line)) {
                log.info("exiting");
            } else {
                String[] items = line.trim().split(" ");
                if (items.length >= 2) {
                    String user = items[0];
                    for (int i = 1; i < items.length; i++) {
                        service.addWord(user, items[i]);
                    }
                }
            }
            System.out.println("data:\n" + service.dataManager.getData());
        }
    }
}
