package server;

import java.net.Socket;

public class ConnectionTask {

    protected Socket connection;

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
