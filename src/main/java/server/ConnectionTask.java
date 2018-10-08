package server;

import communication.Connection;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ConnectionTask {

    protected Connection connection;

    ConnectionTask(Connection connection) throws IOException {
        this.connection = connection;
    }

    public void sendStateSignal(int state) throws IOException {
        ObjectOutputStream os = connection.getOs();
        os.writeInt(state);
        os.flush();
    }

    public synchronized void doWait(long millis) {
        try {
            if (millis == 0)
                wait();
            else
                wait(millis);
        } catch (InterruptedException ex) {
        }
    }
}
