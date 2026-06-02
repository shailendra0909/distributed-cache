package sp.test.transport;

import lombok.extern.slf4j.Slf4j;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.function.Consumer;

@Slf4j
public class UDPListener {
    private int port;
    private final Consumer<String> handler;
    private volatile boolean running = true;

    public UDPListener(int port, Consumer<String> handler) {
        this.port = port;
        this.handler = handler;
    }

    public void start() {
        Thread listener = new Thread(this::listen);
        listener.setDaemon(true);
        listener.start();
    }

    private void listen() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
           while (true){
               byte[] buffer = new byte[4096];
               DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
               socket.receive(packet);
               String msg = new String(packet.getData(), 0, packet.getLength());
               log.info("received msg: "+ msg);
               this.handler.accept(msg);
           }
        } catch (Exception e) {
            log.error("error in getting UDP data:", e);
        }
    }
}
