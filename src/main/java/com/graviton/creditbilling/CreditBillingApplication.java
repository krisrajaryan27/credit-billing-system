package com.graviton.creditbilling;

import com.graviton.creditbilling.service.CreditBillingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CreditBillingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CreditBillingApplication.class, args);
	}

	@Bean
	CommandLineRunner run(CreditBillingService creditBillingService) {
		return args -> {
			creditBillingService.loadPricingInformation("src/main/resources/pricing.txt");
			creditBillingService.loadPurchaseInformation("src/main/resources/purchases.txt");
			creditBillingService.loadUsageInformation("src/main/resources/usage.txt");
			creditBillingService.generateTransactionHistory("src/main/resources/transaction_history.txt");
		};
	}
}
