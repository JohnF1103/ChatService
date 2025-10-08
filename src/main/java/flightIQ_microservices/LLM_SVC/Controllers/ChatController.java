package flightIQ_microservices.LLM_SVC.Controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatClient chatClient;
    private final WeatherController weatherController;

    @Autowired
    public ChatController(ChatClient chatClient, WeatherController weatherController) {
        this.chatClient = chatClient;
        this.weatherController = weatherController;
    }

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> payload) {
        String defaultPrompt = """
            You are an expert aviation assistant. 
            You can analyze and interpret METAR and TAF reports, runway conditions, 
            and aircraft performance. 
            When given weather data, always analyze it directly. 
            Never respond with 'I don’t have real-time access.' 
            Instead, use the provided METAR or other data to answer accurately.
            """;

        String systemPrompt = payload.getOrDefault("systemPrompt", defaultPrompt);
        String userMessage  = payload.getOrDefault("userMessage", "");
        String enrichedMessage = userMessage;

        try {
            // Only handle weather questions simply
            if (userMessage.toLowerCase().contains("weather at")) {
                String[] parts = userMessage.split(" ");
                String icao = parts[parts.length - 1].toUpperCase().trim();

                ResponseEntity<String> response = weatherController.getWeather(icao);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    enrichedMessage = """
                        %s

                        You have been provided with live METAR weather data for %s.
                        Analyze and summarize the conditions in plain English for a pilot.

                        METAR data: %s
                        """.formatted(userMessage, icao, response.getBody());
                } else {
                    enrichedMessage = userMessage + "\n(Note: Unable to fetch weather for " + icao + ")";
                }
            }

            // Call LLM
            String result = chatClient.prompt()
                    .system(systemPrompt)
                    .user(enrichedMessage)
                    .call()
                    .content();

            return Map.of("response", result);

        } catch (Exception e) {
            return Map.of("response", "Error processing request: " + e.getMessage());
        }
    }
}
