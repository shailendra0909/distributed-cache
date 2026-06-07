package sp.test.transport.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sp.test.membership.MembershipInfo;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
public class MembershipMessage {
    MessageType messageType = MessageType.MEMBERSHIP;
    List<MembershipInfo> membershipInfos;
}
