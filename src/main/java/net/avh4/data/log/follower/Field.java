package net.avh4.data.log.follower;

public interface Field<T> {
    public Field<T> apply(int txn, String id, String key, String value);
}
