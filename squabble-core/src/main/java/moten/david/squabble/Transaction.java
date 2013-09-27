package moten.david.squabble;

public interface Transaction {

    void begin();

    void rollback();

    void commit();
}
