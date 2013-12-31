package net.avh4.data.log.follower.fields;

import net.avh4.data.log.follower.Field;

public class IntegerField implements Field<Integer> {
    private final int txn;
    private final int value;

    public IntegerField() {
        this(0, 0);
    }

    public IntegerField(int txn, int value) {
        this.txn = txn;
        this.value = value;
    }

    @Override
    public Field<Integer> apply(int txn, String id, String key, String value) {
        if (value == null) {
            return new IntegerField(txn, 0);
        }
        try {
            int luckyNumber = Integer.parseInt(value);
            return new IntegerField(txn, luckyNumber);
        } catch (NumberFormatException e) {
            // ignore
        }
        return this;
    }

    public int value() {
        return value;
    }
}
