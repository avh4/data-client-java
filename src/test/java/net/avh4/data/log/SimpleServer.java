package net.avh4.data.log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;

public class SimpleServer implements Container {
    public static final int DEFAULT_PORT = 9876;

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new SimpleServer().connect(port);
    }

    private final HashMap<String, HashMap<String, TransientTransactionLog>> storage
            = new HashMap<>();

    public Connection connect(int port) throws IOException {
        Server server = new ContainerServer(this);
        SocketConnection connection = new SocketConnection(server);
        SocketAddress address = new InetSocketAddress(port);
        connection.connect(address);
        return connection;
    }

    @Override
    public void handle(Request request, Response response) {
        try {
            String[] segments = request.getPath().getSegments();
            boolean get = request.getMethod().equals("GET");
            boolean post = request.getMethod().equals("POST");
            if (segments.length < 2 || !segments[0].equals("apps")) {
                notFound(request, response);
            }
            String appId = segments[1];
            String userId = "USER";
            if (get && segments.length == 2) {
                handleAll(appId, userId, request, response);
            } else if (post && segments.length == 2) {
                handleAdd(appId, userId, request, response);
            } else {
                notFound(request, response);
            }
            response.close();
        } catch (Exception e) {
            response.setCode(500);
            response.setContentType("text/plain");
            try {
                PrintWriter writer = new PrintWriter(response.getOutputStream());
                e.printStackTrace(writer);
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                e.printStackTrace();
            }
        }
    }

    private void notFound(Request request, Response response) throws IOException {
        response.setStatus(Status.NOT_FOUND);
        response.setContentType("text/plain");
        PrintWriter writer = new PrintWriter(response.getOutputStream());
        writer.write(request.toString());
        writer.close();
        response.close();
    }

    private TransientTransactionLog getLog(String appId, String userId) {
        if (!storage.containsKey(appId)) {
            storage.put(appId, new HashMap<String, TransientTransactionLog>());
        }
        HashMap<String, TransientTransactionLog> appStorage = storage.get(appId);
        if (!appStorage.containsKey(userId)) {
            appStorage.put(userId, new TransientTransactionLog());
        }
        return appStorage.get(userId);
    }

    private void handleAll(String appId, String userId, Request request, Response response) throws IOException {
        String lastString = request.getParameter("last");
        int last = Integer.parseInt(lastString);
        JsonFactory factory = new JsonFactory();
        JsonGenerator generator = factory.createGenerator(response.getOutputStream());
        generator.writeStartArray();

        List<Transaction> transactions = getLog(appId, userId).get(last);
        for (Transaction txn : transactions) {
            generator.writeStartArray();
            generator.writeNumber(txn.index);
            generator.writeString(txn.key);
            generator.writeString(txn.value);
            generator.writeEndArray();
        }

        generator.writeEndArray();
        generator.close();
    }

    private void handleAdd(String appId, String userId, Request request, Response response) throws IOException {
        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(request.getInputStream());
        if (parser.nextToken() != JsonToken.START_ARRAY)
            throw new RuntimeException("Expected an array of key/value pairs");
        while (true) {
            JsonToken token = parser.nextToken();
            if (token == JsonToken.START_ARRAY) {
                parser.nextToken();
                String key = parser.getValueAsString();
                parser.nextToken();
                String value = parser.getValueAsString();
                if (parser.nextToken() != JsonToken.END_ARRAY)
                    throw new RuntimeException("Expected an array of key/value pairs");
                getLog(appId, userId).add(key, value);
            } else if (token == JsonToken.END_ARRAY) {
                if (parser.nextToken() != null)
                    throw new RuntimeException("Thought we finished parsing, but there are more tokens");
                break;
            } else {
                throw new RuntimeException("Unexpected JSON value");
            }
        }
    }
}
