package net.avh4.data.log;

import com.fasterxml.jackson.core.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ServerTransactionLog implements TransactionLog, TransactionLogCommands {
    private static final JsonEncoding ENCODING_JSON = JsonEncoding.UTF8;
    private static final Charset ENCODING_CHARSET = Charset.forName("UTF-8");
    private final String url;
    private final CloseableHttpClient client;
    private final JsonFactory factory = new JsonFactory();
    private final String userId;

    public ServerTransactionLog(String url, String appId, String userId) {
        this.url = url + "/apps/" + appId;
        this.userId = userId;
        client = HttpClients.createDefault();
    }

    @Override
    public int count() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Transaction> get(int last) {
        String uri = url + "?last=" + last;
        HttpGet get = new HttpGet(uri);
        get.addHeader("X-User-ID", userId);
        try {
            CloseableHttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException(uri + ": " + response.getStatusLine());
            }

            ArrayList<Transaction> result = new ArrayList<>();

            JsonParser parser = factory.createParser(response.getEntity().getContent());
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
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(String key, String value) {
        HttpPost post = new HttpPost(url);
        post.addHeader("X-User-ID", userId);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            JsonGenerator generator = factory.createGenerator(os, ENCODING_JSON);
            generator.writeStartArray();
            generator.writeStartArray();
            generator.writeString(key);
            generator.writeString(value);
            generator.writeEndArray();
            generator.writeEndArray();
            generator.close();
            post.setEntity(new StringEntity(new String(os.toByteArray(), ENCODING_CHARSET), ContentType.APPLICATION_JSON));
            CloseableHttpResponse response = client.execute(post);
            response.getEntity().getContent().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
