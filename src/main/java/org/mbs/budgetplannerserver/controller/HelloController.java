package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.repository.MunicipalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	private MunicipalityRepository municipalityRepository;

	@Autowired
	public HelloController(MunicipalityRepository municipalityRepository) {
		this.municipalityRepository = municipalityRepository;
	}


	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@GetMapping("/municipalities")
	public Iterable<Municipality> getMunicipalities() {
		return municipalityRepository.findAll();
	}


}
