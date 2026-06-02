package sp.test.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import sp.test.CommonUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@Slf4j
public class UDPSender {

    public void send(String host, int port, Object message) {
        try (DatagramSocket datagramSocket = new DatagramSocket()) {
            byte[] payload = CommonUtils.objectMapper.writeValueAsBytes(message);
            DatagramPacket datagramPacket = new DatagramPacket(payload, payload.length, InetAddress.getByName(host), port);
            datagramSocket.send(datagramPacket);
        } catch (Exception e){
         log.error("error in sending payload:", e);
        }
    }

}
