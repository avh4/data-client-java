package net.avh4.data.log.follower;

public interface DataModel<T extends DataModel> {
    T apply(int txn, String key, String value);
}
