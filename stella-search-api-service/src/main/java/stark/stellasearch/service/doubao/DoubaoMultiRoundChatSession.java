package stark.stellasearch.service.doubao;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionChoice;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class DoubaoMultiRoundChatSession
{
    private final String sessionId;
    private final List<ChatMessage> messages;
    private final ArkService arkService;
    private final ChatCompletionRequest chatCompletionRequest;
    private final String model;

    public DoubaoMultiRoundChatSession(ArkService arkService, String model)
    {
        this.arkService = arkService;
        this.model = model;
        this.sessionId = "MultiRoundSession-" + UUID.randomUUID();
        this.messages = new ArrayList<>();
        this.chatCompletionRequest = ChatCompletionRequest.builder()
            .model(model)
            .messages(messages)
            .build();
    }

    public String runChat(String prompt)
    {
        ChatMessage message = new ChatMessage();
        message.setRole(ChatMessageRole.USER);
        message.setContent(prompt);
        messages.add(message);

        List<ChatCompletionChoice> choices = arkService.createChatCompletion(chatCompletionRequest).getChoices();
        ChatCompletionChoice chatCompletionChoice = choices.get(0);
        String chatCompletionMessageContent = chatCompletionChoice.getMessage().stringContent();

        setModelResponse(chatCompletionMessageContent);
        return chatCompletionMessageContent;
    }

    private static void setModelResponse(String chatCompletionMessageContent)
    {
        ChatMessage chatCompletionMessage = new ChatMessage();
        chatCompletionMessage.setRole(ChatMessageRole.ASSISTANT);
        chatCompletionMessage.setContent(chatCompletionMessageContent);
    }
}
