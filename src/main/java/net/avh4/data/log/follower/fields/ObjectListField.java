package net.avh4.data.log.follower.fields;

import net.avh4.data.log.follower.DataModelBase;
import net.avh4.data.log.follower.DataProvider;
import net.avh4.data.log.follower.Field;
import org.pcollections.ConsPStack;
import org.pcollections.IntTreePMap;

import java.util.List;

public class ObjectListField<T extends DataModelBase<T>> implements Field<List<T>> {
    private final DataProvider<T> provider;
    private final IntTreePMap<T> list;

    public ObjectListField(DataProvider<T> provider) {
        this(provider, IntTreePMap.<T>empty());
    }

    private ObjectListField(DataProvider<T> provider, IntTreePMap<T> list) {
        this.provider = provider;
        this.list = list;
    }

    public T findOrCreate(int txn, int id) {
        T object = list.get(id);
        if (object == null)
            return provider.create(txn, id + "");
        else return object;
    }

    @Override
    public Field<List<T>> apply(int txn, String id, String key, String value) {
        int index = Integer.parseInt(id);
        if (value == null) {
            return new ObjectListField<>(provider, list.minus(index));
        } else {
            T updated = findOrCreate(txn, index);
            updated = updated.apply(txn, key, value);
            return new ObjectListField<>(provider, list.plus(index, updated));
        }
    }

    public List<T> list() {
        return ConsPStack.from(list.values());
    }
}
