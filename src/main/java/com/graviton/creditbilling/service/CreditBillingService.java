package com.graviton.creditbilling.service;

import com.graviton.creditbilling.model.CreditService;
import com.graviton.creditbilling.model.Customer;
import com.graviton.creditbilling.model.Package;
import com.graviton.creditbilling.model.Transaction;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
public class CreditBillingService {
    private static final Logger logger = LoggerFactory.getLogger(CreditBillingService.class);

    private final Map<String, CreditService> services = new HashMap<>();
    private final Map<String, Package> packages = new HashMap<>();
    private final Map<String, Customer> customers = new HashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();

    public void loadPricingInformation(String filename) {
        logger.info("Loading pricing information from file: {}", filename);
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String packageName = parts[0];
                    int credits = Integer.parseInt(parts[1]);
                    double price = Double.parseDouble(parts[2]);
                    packages.put(packageName, new Package(packageName, credits, price));
                    logger.debug("Loaded package: {} with credits: {} and price: {}", packageName, credits, price);
                } else if (parts.length == 2) {
                    String serviceName = parts[0];
                    int creditCost = Integer.parseInt(parts[1]);
                    services.put(serviceName, new CreditService(serviceName, creditCost));
                    logger.debug("Loaded service: {} with credit cost: {}", serviceName, creditCost);
                } else {
                    logger.warn("Invalid line format: {}", line);
                }
            }
        } catch (IOException e) {
            logger.error("Error loading pricing information: {}", e.getMessage());
        }
    }

    public void loadPurchaseInformation(String filename) {
        logger.info("Loading purchase information from file: {}", filename);
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String customerName = parts[0];
                String packageName = parts[1];
                Package pkg = packages.get(packageName);
                if (pkg != null) {
                    customers.putIfAbsent(customerName, new Customer(customerName));
                    Customer customer = customers.get(customerName);
                    customer.addCredits(pkg.getCredits());
                    transactions.add(new Transaction(customerName, "purchase", packageName, true));
                    logger.info("Customer {} purchased package {}. Credits added: {}", customerName, packageName, pkg.getCredits());
                } else {
                    logger.warn("Package {} not found for customer {}", packageName, customerName);
                }
            }
        } catch (IOException e) {
            logger.error("Error loading purchase information: {}", e.getMessage());
        }
    }

    public void loadUsageInformation(String filename) {
        logger.info("Loading usage information from file: {}", filename);
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String customerName = parts[0];
                String serviceName = parts[1];
                int usageCount = Integer.parseInt(parts[2]);
                CreditService creditService = services.get(serviceName);
                Customer customer = customers.get(customerName);

                if (customer == null) {
                    logger.warn("Customer {} not found.", customerName);
                    continue; // Skip if customer does not exist
                }

                if (creditService != null) {
                    if (customer.getTotalCredits() <= 0) {
                        logger.warn("Customer {} has not purchased any package and cannot use service {}.", customerName, serviceName);
                        transactions.add(new Transaction(customerName, "usage", serviceName + " x" + usageCount, false));
                        continue; // Skip usage if no credits available
                    }

                    int totalCost = creditService.getCreditCost() * usageCount;
                    boolean success = customer.useCredits(totalCost);
                    transactions.add(new Transaction(customerName, "usage", serviceName + " x" + usageCount, success));
                    if (success) {
                        logger.info("Customer {} used service {} for {} times. Total cost: {}", customerName, serviceName, usageCount, totalCost);
                    } else {
                        logger.warn("Customer {} does not have enough credits for service {} usage.", customerName, serviceName);
                    }
                } else {
                    logger.warn("Service {} not found for customer {}", serviceName, customerName);
                }
            }
        } catch (IOException e) {
            logger.error("Error loading usage information: {}", e.getMessage());
        }
    }

    public void generateTransactionHistory(String filename) {
        logger.info("Generating transaction history to file: {}", filename);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Transaction transaction : transactions) {
                bw.write(transaction.toString());
                bw.newLine();
                logger.debug("Written transaction: {}", transaction);
            }
            bw.write("Customer Credits:");
            bw.newLine();
            for (Customer customer : customers.values()) {
                bw.write(customer.getName() + " has " + customer.getTotalCredits() + " credits remaining.");
                bw.newLine();
                logger.debug("Customer {} has {} credits remaining.", customer.getName(), customer.getTotalCredits());
            }
        } catch (IOException e) {
            logger.error("Error generating transaction history: {}", e.getMessage());
        }
    }
}
