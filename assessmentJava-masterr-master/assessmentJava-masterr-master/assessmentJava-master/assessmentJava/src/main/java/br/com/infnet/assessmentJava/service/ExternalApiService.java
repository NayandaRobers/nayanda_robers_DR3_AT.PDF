package br.com.infnet.assessmentJava.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.infnet.assessmentJava.model.ArmorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import java.util.Arrays;
import java.util.List;

@Service
public class ExternalApiService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalApiService.class);
    public static final String EXTERNAL_API_URL = "https://mhw-db.com/armor";

    private final RestTemplate restTemplate;

    public ExternalApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ArmorResponse> fetchArmorDataFromExternalApi() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(EXTERNAL_API_URL))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            ObjectMapper objectMapper = new ObjectMapper();
            ArmorResponse[] armorResponses = objectMapper.readValue(responseBody, ArmorResponse[].class);

            logger.info("Data received from External API: {}", Arrays.toString(armorResponses));

            return Arrays.asList(armorResponses);
        } catch (Exception e) {
            logger.error("Error fetching data from External API: {}", e.getMessage());
            throw new RuntimeException("Error fetching data from External API", e);
        }
    }


    public ResponseEntity<String> sendArmorDataToExternalApi(ArmorResponse armorResponse) {
        if (dadosDaArmaduraSaoValidos(armorResponse)) {
            logger.info("Sending Armor data to External API: {}", armorResponse);
            return ResponseEntity.ok().body("Armor data sent successfully");
        } else {
            logger.error("Invalid Armor data: {}", armorResponse);
            return ResponseEntity.badRequest().body("Invalid Armor data");
        }
    }

    private boolean dadosDaArmaduraSaoValidos(ArmorResponse armorResponse) {
        return armorResponse != null && armorResponse.getType() != null;
    }

    public ResponseEntity<String> deleteArmorById(int id) {
        try {
            String url = EXTERNAL_API_URL + "/" + id;
            logger.info("Deleting Armor with ID: {}", id);
            logger.info("Calling External API at URL: {}", url);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok().body("Armor deleted successfully");
            } else {
                return ResponseEntity.status(responseEntity.getStatusCode()).body("Failed to delete armor");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    public ResponseEntity<String> updateArmorById(int id, ArmorResponse updatedArmor) {
        try {
            String apiUrl = EXTERNAL_API_URL + "/" + id;
            ResponseEntity<ArmorResponse> existingArmorResponseEntity = restTemplate.getForEntity(apiUrl, ArmorResponse.class);

            if (existingArmorResponseEntity.getStatusCode().is2xxSuccessful()) {
                ArmorResponse existingArmor = existingArmorResponseEntity.getBody();

                if (existingArmor != null) {
                    existingArmor.setType(updatedArmor.getType());
                    existingArmor.setPieces(updatedArmor.getPieces());

                    ResponseEntity<String> responseEntity = sendArmorDataToExternalApi(existingArmor);
                    logger.info("Status Code from External API: {}", responseEntity.getStatusCodeValue());

                    return ResponseEntity.ok().body("Armor updated successfully");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Armor not found");
                }
            } else {
                return ResponseEntity.status(existingArmorResponseEntity.getStatusCode()).body("Failed to get existing armor");
            }
        } catch (Exception e) {
            logger.error("Error updating armor with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
}
