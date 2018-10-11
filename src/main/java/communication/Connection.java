package communication;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {

    private Socket connection;
    private ObjectOutputStream os;
    private ObjectInputStream is;

    public Connection(Socket connection) throws IOException {
        this.connection = connection;
        this.os = new ObjectOutputStream(connection.getOutputStream());
        this.is = new ObjectInputStream(new BufferedInputStream(connection.getInputStream()));
    }

    public ObjectOutputStream getOs() {
        return os;
    }

    public ObjectInputStream getIs() {
        return is;
    }

    public void close() throws IOException {
        connection.close();
        os = null;
        is = null;
        connection = null;
    }

    public boolean isConnected() {

        if (connection != null) return connection.isConnected();

        return false;
    }
}
