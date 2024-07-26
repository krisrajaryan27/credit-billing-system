package com.graviton.creditbilling.service;

import com.graviton.creditbilling.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CreditBillingCreditServiceTests {

    @InjectMocks
    private CreditBillingService creditBillingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadPricingInformation() throws IOException {
        // Create a temporary pricing file
        String pricingFile = "src/test/resources/pricing_test.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pricingFile))) {
            writer.write("S1,1\n");
            writer.write("S2,2\n");
            writer.write("Basic Package,100,100.00\n");
        }

        creditBillingService.loadPricingInformation(pricingFile);

        // Validate services and packages
        assertNotNull(creditBillingService.getServices().get("S1"));
        assertNotNull(creditBillingService.getPackages().get("Basic Package"));
        assertEquals(1, creditBillingService.getServices().get("S1").getCreditCost());
        assertEquals(100, creditBillingService.getPackages().get("Basic Package").getCredits());

    }

    @Test
    void testLoadPurchaseInformation() throws IOException {
        // Create temporary purchase file
        String purchaseFile = "src/test/resources/purchases_test.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(purchaseFile))) {
            writer.write("C1,Basic Package\n");
        }

        // Load pricing first
        creditBillingService.loadPricingInformation("src/test/resources/pricing_test.txt");
        creditBillingService.loadPurchaseInformation(purchaseFile);

        // Validate customer credits
        Customer customer = creditBillingService.getCustomers().get("C1");
        assertNotNull(customer);
        assertEquals(100, customer.getTotalCredits());

    }

    @Test
    void testLoadUsageInformation() throws IOException {
        // Create temporary usage file
        String usageFile = "src/test/resources/usage_test.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usageFile))) {
            writer.write("C1,S1,5\n");
        }

        // Load pricing and purchase first
        creditBillingService.loadPricingInformation("src/test/resources/pricing_test.txt");
        creditBillingService.loadPurchaseInformation("src/test/resources/purchases_test.txt");
        creditBillingService.loadUsageInformation(usageFile);

        // Validate customer credits after usage
        Customer customer = creditBillingService.getCustomers().get("C1");
        assertNotNull(customer);
        assertEquals(95, customer.getTotalCredits()); // 100 - 5 = 95

    }

    @Test
    void testGenerateTransactionHistory() throws IOException {
        // Prepare test data
        creditBillingService.loadPricingInformation("src/test/resources/pricing_test.txt");
        creditBillingService.loadPurchaseInformation("src/test/resources/purchases_test.txt");
        creditBillingService.loadUsageInformation("src/test/resources/usage_test.txt");

        // Generate transaction history
        creditBillingService.generateTransactionHistory("src/test/resources/transaction_history_test.txt");

        // Validate transaction history file
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/transaction_history_test.txt"));
        assertFalse(lines.isEmpty());
        assertTrue(lines.stream().anyMatch(line -> line.contains("C1,purchase,Basic Package,Success")));
        assertTrue(lines.stream().anyMatch(line -> line.contains("C1,usage,S1 x5,Success")));

        // Clean up
        Files.deleteIfExists(Paths.get("src/test/resources/transaction_history_test.txt"));
    }
}

