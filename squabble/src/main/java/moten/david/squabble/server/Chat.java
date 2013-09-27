package moten.david.squabble.server;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class Chat {
    private final long version;
    private final List<String> lines;

    public Chat(long version, List<String> lines) {
        this.version = version;
        this.lines = ImmutableList.copyOf(lines);
    }

    public long getVersion() {
        return version;
    }

    public List<String> getLines() {
        return lines;
    }
}
