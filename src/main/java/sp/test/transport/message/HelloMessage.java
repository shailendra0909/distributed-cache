package sp.test.transport.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HelloMessage {
    private MessageType messageType = MessageType.HELLO;
    private String nodeId;
    private String host;
    private Integer port;
    private long heartbeat;
    private long incarnation;

    public HelloMessage(String nodeId, String host, Integer port, long heartbeat, long incarnation) {
        this.nodeId = nodeId;
        this.host = host;
        this.port = port;
        this.heartbeat = heartbeat;
        this.incarnation = incarnation;
    }
}
