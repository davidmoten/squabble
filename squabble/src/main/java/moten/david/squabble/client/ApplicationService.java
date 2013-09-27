package moten.david.squabble.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service")
public interface ApplicationService extends RemoteService {

    /**
     * Designed for long-polling.
     * 
     * @param version
     *            is the last version received.
     * @return
     */
    Versioned getGame(Long version);

    /**
     * Designed for long-polling.
     * 
     * @param version
     *            is the last version received.
     * @return
     */
    Versioned getChat(Long version);

    void submitMessage(String user, String message);

    String submitWord(String user, String word);

    /**
     * Returns true if and only there was another letter available to turn.
     * 
     * @param user
     * @return
     */
    boolean turnLetter(String user);

    void restart(String user);
}
