package net.avh4.data.log.follower;

import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.util.Map;

public abstract class GenericModel<T extends GenericModel<T>> extends DataModelBase<T> {
    private final PMap<String, Field> fields;

    public GenericModel(Map<String, Field> fields) {
        this.fields = HashTreePMap.from(fields);
    }

    public GenericModel(PMap<String, Field> fields) {
        this.fields = fields;
    }

    public Field get(String field) {
        return fields.get(field);
    }

    @Override
    public T process(int txn, String field, String value) {
        Field f = fields.get(field);
        if (f == null) {
            System.out.println("Unrecognized field: " + field);
            return (T) this;
        }
        return newInstance(fields.plus(field, f.apply(txn, null, null, value)));
    }

    @Override
    public T process(int txn, String field, String id, String key, String value) {
        Field f = fields.get(field);
        if (f == null) {
            System.out.println("Unrecognized field: " + field);
            return (T) this;
        }
        return newInstance(fields.plus(field, f.apply(txn, id, key, value)));
    }

    protected abstract T newInstance(PMap<String, Field> fields);
}
