package net.avh4.data.log.follower;

public interface DataProvider<T> {
    T create(int txn, String id);
}
