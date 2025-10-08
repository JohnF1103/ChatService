package flightIQ_microservices.LLM_SVC.Controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Value("${checkwx.api.url}")
    private String checkwxApiUrl;

    @Value("${checkwx.api.key}")
    private String apiKey;

    @GetMapping
    public ResponseEntity<String> getWeather(@RequestParam String station) {
        try {
            // Build the request
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-api-key", apiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = checkwxApiUrl.replace("{station}", station);


            ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching weather data: " + e.getMessage());
        }
    }
}
