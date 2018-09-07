package com.gopersist.demo.account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AccountServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountServerApplication.class, args);
	}

	@Value("${account.title}")
	private String accountTitle;

	@Value("${account.secret}")
	private String accountSecret;

	@GetMapping("/account/title")
	public String getAccountTitle() {
		return this.accountTitle;
	}

	@GetMapping("/account/secret")
	public String getBlogSecret() {
		return this.accountSecret;
	}
}
