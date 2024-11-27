package stark.stellasearch.dto.results;

import lombok.Data;

import java.util.Date;

@Data
public class UserChatSessionInfo
{
    private long id;
    private long user1Id;
    private long user2Id;
    private int state;
    private long lastMessageSenderId;
    private String lastMessage;
    private Date lastMessageTime;
}
