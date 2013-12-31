package net.avh4.data.log.follower.fields;

import net.avh4.data.log.follower.Field;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateField implements Field<Date> {
    private final Date date;

    public DateField() {
        this(null);
    }

    public DateField(Date date) {
        this.date = date;
    }

    @Override
    public Field<Date> apply(int txn, String id, String key, String value) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(value);
            return new DateField(d);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Date value() {
        return date;
    }
}
