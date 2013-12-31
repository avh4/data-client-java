package net.avh4.data.log.follower.fields;

import net.avh4.data.log.follower.Field;
import org.pcollections.ConsPStack;
import org.pcollections.IntTreePMap;

import java.util.List;

public class StringListField implements Field<List<String>> {
    private final IntTreePMap<String> list;

    public StringListField() {
        this(IntTreePMap.<String>empty());
    }

    private StringListField(IntTreePMap<String> list) {
        this.list = list;
    }

    @Override
    public Field<List<String>> apply(int txn, String id, String key, String value) {
        int index = Integer.parseInt(id);
        if (value == null) {
            return new StringListField(list.minus(index));
        } else {
            return new StringListField(list.plus(index, value));
        }
    }

    public List<String> list() {
        return ConsPStack.from(list.values());
    }
}
