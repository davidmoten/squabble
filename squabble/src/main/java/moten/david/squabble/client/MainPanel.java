package moten.david.squabble.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends Composite {
    private static final String COOKIE_SQUABBLE_NAME = "squabble.name";

    interface MyUiBinder extends UiBinder<Widget, MainPanel> {
    };

    @UiField
    TextBox name;

    @UiField
    VerticalPanel game;

    @UiField
    TextArea chat;

    @UiField
    TextArea command;

    @UiField
    Button submit;

    @UiField
    Button turnLetter;

    @UiField
    Button restart;

    /**
     * Create a remote service proxy to talk to the server-side service.
     */
    private final ApplicationServiceAsync applicationService = GWT
            .create(ApplicationService.class);

    /**
     * Extra service for long-polling.
     */
    private final ApplicationServiceAsync gameService = GWT
            .create(ApplicationService.class);
    /**
     * Extra service for long-polling.
     */
    private final ApplicationServiceAsync chatService = GWT
            .create(ApplicationService.class);

    // Callbacks

    private final AsyncCallback<Void> submitMessageCallback;
    private final AsyncCallback<Versioned> getChatCallback;
    private final AsyncCallback<Versioned> getGameCallback;

    private final AsyncCallback<String> submitWordCallback;
    private final AsyncCallback<Boolean> turnLetterCallback;
    private final AsyncCallback<Void> restartCallback;
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    private long gameVersion = 0;
    private long chatVersion = 0;

    public MainPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        command.setText("");
        submitMessageCallback = createSubmitMessageCallback();
        getChatCallback = createGetChatCallback();
        submitWordCallback = createSubmitWordCallback();
        getGameCallback = createGetGameCallback();
        turnLetterCallback = createTurnLetterCallback();
        command.addKeyPressHandler(createCommandKeyPressHandler());
        turnLetter.addClickHandler(createTurnLetterClickHandler());
        command.setFocus(true);
        String nameCookie = Cookies.getCookie(COOKIE_SQUABBLE_NAME);
        if (nameCookie != null && nameCookie.trim().length() > 0)
            name.setText(nameCookie);
        name.addChangeHandler(createNameChangeHandler());
        restartCallback = createRestartCallback();
        restart.addClickHandler(createRestartClickHandler());
        // start streaming game and chat via long-polling
        gameService.getGame(gameVersion, getGameCallback);
        chatService.getChat(chatVersion, getChatCallback);
    }

    private AsyncCallback<Void> createRestartCallback() {
        return new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable t) {
                reportError(t);
            }

            @Override
            public void onSuccess(Void v) {

            }
        };
    }

    private ClickHandler createRestartClickHandler() {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                applicationService.restart(name.getText(), restartCallback);
                // restart.setEnabled(false);
            }
        };
    }

    private void updateCookies() {
        Cookies.removeCookie(COOKIE_SQUABBLE_NAME);
        Cookies.setCookie(COOKIE_SQUABBLE_NAME, name.getText(), new Date(System
                .currentTimeMillis()
                + 60 * 24 * 60 * 60 * 1000));
    }

    private ChangeHandler createNameChangeHandler() {
        return new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                updateCookies();
                command.setEnabled(true);
                command.setText("");
                turnLetter.setEnabled(true);
                submit.setEnabled(true);
            }
        };
    }

    private AsyncCallback<Boolean> createTurnLetterCallback() {
        return new AsyncCallback<Boolean>() {

            @Override
            public void onFailure(Throwable t) {
                reportError(t);
            }

            @Override
            public void onSuccess(Boolean v) {
                turnLetter.setEnabled(true);
                Timer timer = new Timer() {
                    @Override
                    public void run() {
                        turnLetter.setEnabled(true);
                    }
                };
                // timer.schedule(3000);
                restart.setEnabled(true);
            }
        };
    }

    private ClickHandler createTurnLetterClickHandler() {
        return new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                applicationService.turnLetter(name.getText(),
                        turnLetterCallback);
                command.setText("");
            }
        };
    }

    private KeyPressHandler createCommandKeyPressHandler() {
        return new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode()) {
                    submit.click();
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
        };
    }

    private AsyncCallback<Versioned> createGetGameCallback() {
        return new AsyncCallback<Versioned>() {

            @Override
            public void onFailure(Throwable t) {
                reportError(t);
                Timer timer = new Timer() {

                    @Override
                    public void run() {
                        gameService.getGame(gameVersion, getGameCallback);
                    }
                };
                timer.schedule(5000);
            }

            @Override
            public void onSuccess(Versioned v) {
                gameVersion = v.getVersion();
                game.clear();
                game.add(new HTMLPanel(gameAsHtml(v.getValue())));
                // game.add(new HTMLPanel(gameAsSvg(v.getValue())));
                gameService.getGame(gameVersion, getGameCallback);
            }

            private String gameAsHtml(String value) {
                StringBuffer s = new StringBuffer();
                if (value != null && value.trim().length() > 0) {
                    String board = "";
                    String[] lines = value.split("\n");
                    for (String line : lines) {
                        String[] items = line.split(":");
                        String name = items[0].trim();
                        String[] words = items[1].trim().split(" ");
                        StringBuffer h = new StringBuffer();
                        h.append("<p style=\"margin:0px;padding:0px;\">" + name
                                + ":&nbsp;");
                        for (String word : words) {
                            h.append("&nbsp;");
                            h.append("<span style=\"background:#f0e4a2;\">"
                                    + word.toUpperCase() + "</span>");
                        }
                        h.append("</p>");
                        if (name.equalsIgnoreCase("board"))
                            board = h.toString();
                        else
                            s.append(h.toString());
                    }
                    s.insert(0, board + "<br/>");
                }
                return s.toString();
            }
        };
    }

    private String gameAsSvg(String value) {
        StringBuffer h = new StringBuffer();
        if (value != null && value.trim().length() > 0) {
            String[] lines = value.split("\n");
            for (String line : lines) {
                String[] items = line.split(":");
                String name = items[0].trim();
                String[] words = items[1].trim().split(" ");
                h.append("<p style=\"margin-top:0px;margin-bottom:0px;\">"
                        + name + ":&nbsp;&nbsp;");
                for (String word : words) {
                    for (char ch : word.toCharArray())
                        h.append("<img src=\"../letter.jsp?val="
                                + Character.toUpperCase(ch) + "\"/>");
                    h.append("&nbsp;&nbsp;");
                }
                h.append("</p>");
            }
        }
        return h.toString();
    }

    private AsyncCallback<String> createSubmitWordCallback() {
        return new AsyncCallback<String>() {

            public void onFailure(Throwable t) {
                reportError(t);
                submit.setEnabled(true);
            }

            public void onSuccess(String result) {
                command.setFocus(true);
                submit.setEnabled(true);
                if (!result.equals(null))
                    turnLetter.setEnabled(true);
            }
        };
    }

    private void reportError(Throwable t) {
        chat.setText(chat.getText() + "\n" + t.getMessage());
    }

    private AsyncCallback<Versioned> createGetChatCallback() {
        return new AsyncCallback<Versioned>() {

            public void onFailure(Throwable t) {
                reportError(t);
                Timer timer = new Timer() {

                    @Override
                    public void run() {
                        chatService.getChat(chatVersion, getChatCallback);
                    }
                };
                timer.schedule(5000);
            }

            public void onSuccess(Versioned v) {
                chatVersion = v.getVersion();
                chat.setText(v.getValue());
                chatService.getChat(chatVersion, getChatCallback);
            }
        };
    }

    private AsyncCallback<Void> createSubmitMessageCallback() {
        return new AsyncCallback<Void>() {

            public void onFailure(Throwable t) {
                reportError(t);
            }

            public void onSuccess(Void v) {
                command.setFocus(true);
            }
        };
    }

    @UiHandler("submit")
    void handleClick(ClickEvent e) {
        if (command.getText().trim().equals(""))
            turnLetter.click();
        else if (command.getText().startsWith(" ")) {
            applicationService.submitMessage(name.getText(), command.getText()
                    .substring(1), submitMessageCallback);
            command.setText("");
        } else {
            applicationService.submitWord(name.getText(), command.getText(),
                    submitWordCallback);
            command.setText("");
        }
    }

}
