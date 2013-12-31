package net.avh4.data.log.follower.fields;

import net.avh4.data.log.follower.Field;

public class StringField implements Field<String> {
    private final int txn;
    private final String value;

    public StringField() {
        this(0, null);
    }

    public StringField(int txn, String value) {
        this.txn = txn;
        this.value = value;
    }

    @Override
    public Field<String> apply(int txn, String id, String key, String value) {
        return new StringField(txn, value);
    }

    public String value() {
        return value;
    }
}
