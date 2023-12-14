package br.com.infnet.assessmentJava.controller;

import br.com.infnet.assessmentJava.model.ArmorResponse;
import br.com.infnet.assessmentJava.service.ExternalApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/armors")
public class ArmorController {

    private static final Logger logger = LoggerFactory.getLogger(ArmorController.class);

    private final ExternalApiService externalApiService;

    public ArmorController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping
    public ResponseEntity<List<ArmorResponse>> getArmors(
            @RequestParam(required = false) String param1,
            @RequestParam(required = false) String param2) {
        List<ArmorResponse> armors = externalApiService.fetchArmorDataFromExternalApi();

        if (armors != null && !armors.isEmpty()) {
            return new ResponseEntity<>(armors, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<String> createArmor(@RequestBody ArmorResponse armorResponse) {
        logger.info("Creating Armor: {}", armorResponse);

        if (armorResponse.getId() <= 0 || armorResponse.getType() == null || armorResponse.getPieces() == null) {
            return ResponseEntity.badRequest().body("Invalid armor data");
        }

        ResponseEntity<String> responseEntity = externalApiService.sendArmorDataToExternalApi(armorResponse);
        logger.info("Status Code from External API: {}", responseEntity.getStatusCodeValue());

        return ResponseEntity.ok().body("Armor created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateArmor(@PathVariable int id, @RequestBody ArmorResponse updatedArmor) {
        logger.info("Updating Armor with ID {}: {}", id, updatedArmor);

        if (id <= 0 || updatedArmor.getId() != 0 || updatedArmor.getType() == null || updatedArmor.getPieces() == null) {
            return ResponseEntity.badRequest().body("Invalid armor data");
        }

        ResponseEntity<String> responseEntity = externalApiService.updateArmorById(id, updatedArmor);
        logger.info("Status Code from External API: {}", responseEntity.getStatusCodeValue());

        return ResponseEntity.ok().body("Armor updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArmor(@PathVariable int id) {
        logger.info("Deleting Armor with ID: {}", id);
        ResponseEntity<String> responseEntity = externalApiService.deleteArmorById(id);
        logger.info("Status Code from External API: {}", responseEntity.getStatusCodeValue());

        return responseEntity;
    }
}
