package moten.david.squabble;

public class DataManagerImpl implements DataManager {

    private Data data;

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public Transaction getTransaction() {
        return new DummyTransaction();
    }

}
