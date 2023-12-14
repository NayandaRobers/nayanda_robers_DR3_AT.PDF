package br.com.infnet.assessmentJava;

import br.com.infnet.assessmentJava.controller.ArmorController;
import br.com.infnet.assessmentJava.model.ArmorResponse;
import br.com.infnet.assessmentJava.service.ExternalApiService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;


@SpringBootTest
class AssessmentJavaApplicationTests {

	@Autowired
	private ExternalApiService externalApiService;

	@Autowired
	private ArmorController armorController;

	@Test
	void testGetArmors() {
		ResponseEntity<List<ArmorResponse>> responseEntity = armorController.getArmors(null, null);
		assertNotNull(responseEntity);

		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			List<ArmorResponse> armors = responseEntity.getBody();
			assertNotNull(armors, "A lista de armaduras não deve ser nula");
			assertFalse(armors.isEmpty(), "A lista de armaduras está vazia.");
		} else {
			fail("A requisição para obter armaduras falhou. Status: " + responseEntity.getStatusCode());
		}
	}

	@Test
	void testSendInvalidArmorData() {
		ArmorResponse invalidArmor = new ArmorResponse();
		invalidArmor.setType(null);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			externalApiService.sendArmorDataToExternalApi(invalidArmor);
		});

		assertNotNull(exception.getMessage());
	}



	@Test
	void testCreateArmor() {
		ArmorResponse armorResponse = new ArmorResponse();
		armorResponse.setId(1);
		armorResponse.setType("Type Example");

		ResponseEntity<String> response = armorController.createArmor(armorResponse);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}



	@Test
	void testDeleteArmor() {
		ResponseEntity<String> response = armorController.deleteArmor(1);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Armor deleted successfully", response.getBody());
	}

	@Test
	void testUpdateArmor() {
		ArmorResponse armorResponse = new ArmorResponse();
		armorResponse.setId(1);
		armorResponse.setType("Type Updated");

		ResponseEntity<String> response = armorController.updateArmor(1, armorResponse);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Armor updated successfully", response.getBody());
	}

	@Test
	void testCreateArmorInvalid() {
		ArmorResponse armorResponse = new ArmorResponse();
		armorResponse.setType("Type Example");

		ResponseEntity<String> response = armorController.createArmor(armorResponse);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

	}


}
