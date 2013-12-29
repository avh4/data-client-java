package net.avh4.data.log;

import com.fasterxml.jackson.core.*;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ServerTransactionLog implements TransactionLog, TransactionLogCommands, TransactionLogBulkCommands {
    private final String url;
    private final JsonFactory factory = new JsonFactory();
    private final String userId;

    public ServerTransactionLog(String url, String appId, String userId) {
        this.url = url + "/apps/" + appId;
        this.userId = userId;
    }

    @Override
    public PVector<Transaction> get(int last) {
        String uri = url + "?last=" + last;
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(uri).openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("X-User-ID", userId);
            c.connect();

            if (c.getResponseCode() != 200) {
                throw new RuntimeException(uri + ": " + c.getResponseMessage());
            }

            ArrayList<Transaction> result = new ArrayList<>();

            JsonParser parser = factory.createParser(c.getInputStream());
            if (parser.nextToken() != JsonToken.START_ARRAY)
                throw new RuntimeException("Expected an array of key/value pairs");
            while (true) {
                JsonToken token = parser.nextToken();
                if (token == JsonToken.START_ARRAY) {
                    parser.nextToken();
                    int index = parser.getIntValue();
                    parser.nextToken();
                    String key = parser.getValueAsString();
                    parser.nextToken();
                    String value = parser.getValueAsString();
                    if (parser.nextToken() != JsonToken.END_ARRAY)
                        throw new RuntimeException("Expected an array of key/value pairs");
                    result.add(new Transaction(index, key, value));
                } else if (token == JsonToken.END_ARRAY) {
                    if (parser.nextToken() != null)
                        throw new RuntimeException("Thought we finished parsing, but there are more tokens");
                    break;
                } else {
                    throw new RuntimeException("Unexpected JSON value: " + token);
                }
            }
            c.disconnect();
            return TreePVector.from(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(String key, String value) {
        ArrayList<Transaction> txns = new ArrayList<>();
        txns.add(new Transaction(-1, key, value));
        addAll(txns);
    }

    @Override
    public void addAll(List<Transaction> txns) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setRequestMethod("POST");
            c.setRequestProperty("X-User-ID", userId);
            c.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            c.setDoOutput(true);
            c.connect();

            JsonGenerator generator = factory.createGenerator(c.getOutputStream(), JsonEncoding.UTF8);
            generator.writeStartArray();
            for (Transaction txn : txns) {
                generator.writeStartArray();
                generator.writeString(txn.key);
                generator.writeString(txn.value);
                generator.writeEndArray();
            }
            generator.writeEndArray();
            generator.close();
            c.getOutputStream().flush();
            c.getOutputStream().close();

            if (c.getResponseCode() != 202) {
                throw new RuntimeException(url + ": " + c.getResponseMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
