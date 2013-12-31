package net.avh4.data.log.follower;

public abstract class DataModelBase<T extends DataModel<T>> implements DataModel<T> {
    public abstract T process(int txn, String field, String value);

    public abstract T process(int txn, String field, String id, String key, String value);

    @Override
    public T apply(int txn, String key, String value) {
        String[] keys = key.split("/", 3);
        String field = keys[0];
        if (keys.length == 1)
            return process(txn, field, value);

        else if (keys.length > 1) {
            String innerId = keys[1];
            String innerField = null;
            if (keys.length > 2) innerField = keys[2];
            return process(txn, field, innerId, innerField, value);
        }

        return (T) this; // TODO: Is this an error condition?
    }
}
