package com.devsuperior.demo.controllers;

import com.devsuperior.demo.dto.EmailDTO;
import com.devsuperior.demo.services.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping(value = "/email")
public class EmailController {

	private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
	public ResponseEntity<Void> sendEmail(@RequestBody @Valid EmailDTO obj) {
		emailService.sendEmail(obj);
		return ResponseEntity.noContent().build();
	}
}
