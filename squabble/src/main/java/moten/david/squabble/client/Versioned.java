package moten.david.squabble.client;

import java.io.Serializable;

public class Versioned implements Serializable {

    private String value;
    private Long version;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * Zero argument constructor required for serialization to gwt client.
     */
    public Versioned() {

    }

    public Versioned(String value, Long version) {
        super();
        this.value = value;
        this.version = version;
    }
}
