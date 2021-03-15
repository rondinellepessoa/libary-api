package com.cursotddrsilva.libraryapi;

import com.cursotddrsilva.libraryapi.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

	/**
	 * Usado somente para o teste do envio de emails
	 */
	/*@Autowired
	private EmailService emailService;*/

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	/**
	 * Usado somente para o teste do envio de email
	 * @param args
	 */
	/*@Bean
	public CommandLineRunner runner(){
		return args -> {
			List<String> emails = Arrays.asList("library-api-7cc317@inbox.mailtrap.io");
			this.emailService.sendMails("Testando servico de emails.", emails);
			System.out.println("EMAILS ENVIADOS");
		};
	}*/

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
