package moten.david.squabble.server;

import moten.david.squabble.Data;

public class Game {
    private final long version;
    private final Data data;

    public Game(long version, Data data) {
        this.version = version;
        this.data = data;
    }

    public long getVersion() {
        return version;
    }

    public Data getData() {
        return data;
    }

}
