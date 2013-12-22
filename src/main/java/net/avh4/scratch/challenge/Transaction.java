package net.avh4.scratch.challenge;

public class Transaction {
    public final int index;
    public final String key;
    public final String value;

    public Transaction(int index, String key, String value) {
        this.index = index;
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "<TXN-" + index +
                ':' + key +
                ':' + value
                + '>';
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (index != that.index) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

}
