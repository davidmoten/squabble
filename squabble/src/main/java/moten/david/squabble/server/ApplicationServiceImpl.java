package moten.david.squabble.server;

import java.util.List;

import moten.david.squabble.DataManagerImpl;
import moten.david.squabble.Engine;
import moten.david.squabble.Letters;
import moten.david.squabble.Service;
import moten.david.squabble.Engine.WordStatus;
import moten.david.squabble.client.ApplicationService;
import moten.david.squabble.client.Versioned;
import moten.david.util.words.DictionarySowpods;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ApplicationServiceImpl extends RemoteServiceServlet implements
        ApplicationService {

    public static boolean keepGoing = true;

    private static final long serialVersionUID = 8564374631589515374L;

    private static final long LONG_POLL_SLEEP_MS = 100;

    private Chat chat = new Chat(1, ImmutableList.of("Welcome to Squabble!"));
    private Game game;

    private Service service;

    public ApplicationServiceImpl() {
        service = createService();
        updateGame();
    }

    private Service createService() {
        Service service = new Service(new Engine(new DictionarySowpods(),
                new Letters("eng")), new DataManagerImpl());
        service.turnLetter();
        service.turnLetter();
        return service;
    }

    private void updateGame() {
        if (game == null)
            game = new Game(1, service.getData());
        else
            game = new Game(game.getVersion() + 1, service.getData());
    }

    private Versioned getChat(Chat chat) {
        synchronized (chat) {
            int start = Math.max(0, chat.getLines().size() - 10);
            StringBuffer s = new StringBuffer();
            for (int i = start; i < chat.getLines().size(); i++) {
                if (s.length() > 0)
                    s.append("\n");
                s.append(chat.getLines().get(i));
            }
            Versioned v = new Versioned(s.toString(), chat.getVersion());
            return v;
        }
    }

    @Override
    public Versioned getChat(Long version) {
        try {
            while (version == chat.getVersion() && keepGoing)
                try {
                    Thread.sleep(LONG_POLL_SLEEP_MS);
                } catch (InterruptedException e) {
                    // do nothing
                }
            return getChat(chat);
        } catch (RuntimeException e) {
            reportError(e);
            throw e;
        }
    }

    @Override
    public Versioned getGame(Long version) {
        try {
            while (version == game.getVersion() && keepGoing)
                try {
                    Thread.sleep(LONG_POLL_SLEEP_MS);
                } catch (InterruptedException e) {
                    // do nothing
                }

            return new Versioned(game.getData().toString(), game.getVersion());
        } catch (RuntimeException e) {
            reportError(e);
            throw e;
        }
    }

    @Override
    public boolean turnLetter(String user) {
        try {
            boolean letterTurned = service.turnLetter();
            updateGame();
            if (letterTurned)
                submitChatLine(user + " turned a letter");
            else
                submitChatLine("No more letters left! Game Over!");
            return letterTurned;
        } catch (RuntimeException e) {
            reportError(e);
            throw e;
        }
    }

    private void reportError(RuntimeException e) {
        e.printStackTrace();
    }

    @Override
    public synchronized String submitWord(String user, String words) {
        String[] items = words.split(" ");
        boolean success = false;
        for (String word : items) {
            WordStatus result = service.addWord(user, word);
            updateGame();
            String message = user + " submitted " + word;
            if (result.equals(WordStatus.OK)) {
                success = true;
                message += " - ACCEPTED";
            } else
                message += " - REJECTED - " + result;
            submitChatLine(message);
        }
        if (success)
            return WordStatus.OK.toString();
        else
            return null;
    }

    private void submitChatLine(String message) {
        List<String> lines = Lists.newArrayList(chat.getLines());
        lines.add(message);
        chat = new Chat(chat.getVersion() + 1, lines);
    }

    @Override
    public void submitMessage(String user, String message) {
        try {
            submitChatLine(user + ": " + message);
        } catch (RuntimeException e) {
            reportError(e);
            throw e;
        }
    }

    @Override
    public synchronized void restart(String user) {
        try {
            submitChatLine(user + " requested a restart");
            service = createService();
            updateGame();
            chat = new Chat(1, ImmutableList.of("Welcome to Squabble!"));
        } catch (RuntimeException e) {
            reportError(e);
            throw e;
        }
    }
}