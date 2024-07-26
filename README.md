# Credit Billing System

## Overview
This command-line application simulates a credit-based billing system for a SaaS product with various services and credit packages using Spring Boot.

## How to Run
1. Clone the repository.
2. Navigate to the project directory.
3. Build and run the application:
   ```bash
   mvn spring-boot:run

3. Ensure the input files (pricing.txt, purchases.txt, usage.txt) are located in the src/main/resources/ directory.
# Input Files
1. **pricing.txt**: Contains creditService pricing and credit package information.
2. **purchases.txt**: Contains customer purchase information.
3. **usage.txt**: Contains customer usage information for services.
# Output File
1. The application generates a **transaction_history.txt** file in the src/main/resources/ directory with the transaction history and remaining credits for each customer.