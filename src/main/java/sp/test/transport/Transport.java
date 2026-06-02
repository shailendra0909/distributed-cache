package sp.test.transport;

public interface Transport {
    void send(String host, int port, byte[] msg);
}
