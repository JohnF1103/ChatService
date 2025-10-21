package flightIQ_microservices.LLM_SVC;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Value("${spring.ai.ollama.base-url:http://host.docker.internal:11434}")
    private String ollamaBaseUrl;

    @Value("${LLM_API_URL:}")
    private String llmApiUrl;

    @Value("${OPENAI_API_KEY:}")
    private String openAiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("userMessage");
        Map<String, String> response = new HashMap<>();

        try {
            // If OPENAI_API_KEY is set, use OpenAI endpoint
            if (openAiApiKey != null && !openAiApiKey.isEmpty()) {
                String apiUrl = (llmApiUrl != null && !llmApiUrl.isEmpty())
                        ? llmApiUrl
                        : "https://api.openai.com/v1/chat/completions";

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(openAiApiKey);

                String requestBody = """
                        {
                          "model": "gpt-4o-mini",
                          "messages": [{"role": "user", "content": "%s"}]
                        }
                        """.formatted(userMessage);

                HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
                ResponseEntity<Map> aiResponse = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

                Map<String, Object> body = aiResponse.getBody();
                if (body != null && body.containsKey("choices")) {
                    var choices = (java.util.List<Map<String, Object>>) body.get("choices");
                    if (!choices.isEmpty()) {
                        Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
                        response.put("response", msg.get("content").toString());
                        return response;
                    }
                }

                response.put("response", "No content received from LLM.");
                return response;
            }

            // Otherwise, fallback to local Ollama
            String requestBody = """
                    {
                      "model": "llama3.2",
                      "messages": [{"role": "user", "content": "%s"}]
                    }
                    """.formatted(userMessage);

            String url = ollamaBaseUrl + "/api/chat";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> ollamaResponse = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            Map<String, Object> body = ollamaResponse.getBody();
            if (body != null && body.containsKey("message")) {
                response.put("response", body.get("message").toString());
            } else {
                response.put("response", "No content received from Ollama backend.");
            }

        } catch (Exception e) {
            response.put("response", "Error processing request: " + e.getMessage());
        }

        return response;
    }
}
