package flightIQ_microservices.LLM_SVC.Controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient.Builder chatClientBuilder;

    public ChatController(ChatClient.Builder builder) {
        this.chatClientBuilder = builder;
    }

    @GetMapping("/")
    public String home(@RequestParam String systemPrompt, @RequestParam String userMessage) {
        ChatClient chatClient = chatClientBuilder.defaultSystem(systemPrompt).build();
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }

}