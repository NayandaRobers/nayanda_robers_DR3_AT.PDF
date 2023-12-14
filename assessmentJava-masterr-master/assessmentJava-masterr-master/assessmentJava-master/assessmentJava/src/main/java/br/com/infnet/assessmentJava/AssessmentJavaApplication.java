package br.com.infnet.assessmentJava;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class AssessmentJavaApplication implements CommandLineRunner {


	private static final Logger LOGGER = LoggerFactory.getLogger(AssessmentJavaApplication.class);

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(AssessmentJavaApplication.class, args);
	}

	@Override
	public void run(String... args) {
		LOGGER.debug("Iniciando aplicação...");
	}
}