package moten.david.squabble.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ApplicationServiceAsync {

    void getGame(Long version, AsyncCallback<Versioned> callback);

    void getChat(Long version, AsyncCallback<Versioned> callback);

    void submitWord(String user, String word, AsyncCallback<String> callback);

    void submitMessage(String user, String message, AsyncCallback<Void> callback);

    void turnLetter(String user, AsyncCallback<Boolean> callback);

    void restart(String user, AsyncCallback<Void> callback);
}
