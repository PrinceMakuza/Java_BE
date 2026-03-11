import models.*;
import managers.*;
import utils.InputValidator;

import java.util.Scanner;

public class Main {
    private static AccountManager accountManager;
    private static TransactionManager transactionManager;
    private static Scanner scanner;

    public static void main(String[] args) {
        initializeSystem();
        runApplication();
    }

    private static void initializeSystem() {     // initializes the system with empty data structures and welcomes the user
        accountManager = new AccountManager();
        transactionManager = new TransactionManager();
        scanner = new Scanner(System.in);

        // No hard-coded data - system starts empty
        System.out.println("Bank Account Management System initialized.");
        System.out.println("Start by creating accounts using option 1 from the menu.\n");
    }

    private static void runApplication() {
        boolean running = true;

        while (running) {
            displayMainMenu();
            int choice = InputValidator.getIntInput(scanner, "Enter choice: ", 1, 5);

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    viewAccounts();
                    break;
                case 3:
                    processTransaction();
                    break;
                case 4:
                    viewTransactionHistory();
                    break;
                case 5:
                    running = false;
                    exitApplication();
                    break;
            }

            if (running && choice != 5) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("BANK ACCOUNT MANAGEMENT SYSTEM");
        System.out.println("=".repeat(60));
        System.out.println("1. Create Account");
        System.out.println("2. View Accounts");
        System.out.println("3. Process Transaction");
        System.out.println("4. View Transaction History");
        System.out.println("5. Exit");
        System.out.println("-".repeat(60));
    }

    private static void createAccount() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("CREATE ACCOUNT");
        System.out.println("=".repeat(60));

        // Get customer details
        System.out.println("\n--- Customer Information ---");
        String name = InputValidator.getStringInput(scanner, "Enter customer name: ");
        int age = InputValidator.getIntInput(scanner, "Enter customer age: ", 18, 120);
        String contact = InputValidator.getStringInput(scanner, "Enter contact number: ");
        String address = InputValidator.getStringInput(scanner, "Enter address: ");

        // Select customer type
        System.out.println("\nCustomer type:");
        System.out.println("1. Regular Customer (Standard services)");
        System.out.println("2. Premium Customer (No fees, priority service, min balance $10,000)");
        int customerType = InputValidator.getIntInput(scanner, "Select type (1-2): ", 1, 2);

        Customer customer;
        if (customerType == 1) {
            customer = new RegularCustomer(name, age, contact, address);
        } else {
            customer = new PremiumCustomer(name, age, contact, address);
        }

        // Select account type
        System.out.println("\nAccount type:");
        System.out.println("1. Savings Account (3.5% interest, min balance $500)");
        System.out.println("2. Checking Account (Overdraft $1000, monthly fee $10)");
        int accountType = InputValidator.getIntInput(scanner, "Select type (1-2): ", 1, 2);

        // Get initial deposit
        double minDeposit = (accountType == 1) ? 500.0 : 0.0;
        double initialDeposit;

        while (true) {
            initialDeposit = InputValidator.getPositiveDoubleInput(scanner,
                    "Enter initial deposit amount: $");

            if (accountType == 1 && initialDeposit < minDeposit) {
                System.out.printf("Savings account requires minimum deposit of $%.2f%n", minDeposit);
            } else {
                break;
            }
        }

        // Create account
        Account account = null;
        try {
            if (accountType == 1) {
                account = new SavingsAccount(customer, initialDeposit);
            } else {
                account = new CheckingAccount(customer, initialDeposit);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        // Add account to manager
        if (accountManager.addAccount(account)) {
            System.out.println("\n✓ Account created successfully!");
            account.displayAccountDetails();

            // Record initial deposit transaction
            Transaction initialTxn = new Transaction(
                    account.getAccountNumber(),
                    "DEPOSIT",
                    initialDeposit,
                    initialDeposit
            );
            transactionManager.addTransaction(initialTxn);

            System.out.printf("%nTotal accounts in system: %d%n", accountManager.getAccountCount());
        } else {
            System.out.println("\n✗ Failed to create account. Maximum capacity reached.");
        }
    }

    private static void viewAccounts() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("VIEW ACCOUNTS");
        System.out.println("=".repeat(60));

        if (accountManager.getAccountCount() == 0) {
            System.out.println("No accounts registered yet. Use option 1 to create accounts.");
            return;
        }

        accountManager.viewAllAccounts();
    }

    private static void processTransaction() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PROCESS TRANSACTION");
        System.out.println("=".repeat(60));

        if (accountManager.getAccountCount() == 0) {
            System.out.println("No accounts available. Please create an account first.");
            return;
        }

        String accountNumber = InputValidator.getStringInput(scanner, "Enter Account Number: ");
        Account account = accountManager.findAccount(accountNumber);

        if (account == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.println("\nAccount Details:");
        System.out.println("Customer: " + account.getCustomer().getName());
        System.out.println("Account Type: " + account.getAccountType());
        System.out.printf("Current Balance: $%.2f%n", account.getBalance());

        System.out.println("\nTransaction type:");
        System.out.println("1. Deposit");
        System.out.println("2. Withdrawal");
        int txnType = InputValidator.getIntInput(scanner, "Select type (1-2): ", 1, 2);

        double amount = InputValidator.getPositiveDoubleInput(scanner, "Enter amount: $");

        // Validate withdrawal based on account type
        boolean valid = true;
        String validationMessage = "";

        if (txnType == 2) { // Withdrawal
            if (account instanceof SavingsAccount) {
                SavingsAccount sa = (SavingsAccount) account;
                double newBalance = account.getBalance() - amount;
                if (newBalance < sa.getMinimumBalance()) {
                    valid = false;
                    validationMessage = String.format(
                            "Withdrawal would drop balance below minimum requirement of $%.2f",
                            sa.getMinimumBalance()
                    );
                }
            } else if (account instanceof CheckingAccount) {
                CheckingAccount ca = (CheckingAccount) account;
                double newBalance = account.getBalance() - amount;
                if (newBalance < -ca.getOverdraftLimit()) {
                    valid = false;
                    validationMessage = String.format(
                            "Withdrawal exceeds overdraft limit of $%.2f",
                            ca.getOverdraftLimit()
                    );
                }
            }
        }

        if (!valid) {
            System.out.println("\n✗ " + validationMessage);
            return;
        }

        // Show confirmation
        double previousBalance = account.getBalance();
        String type = (txnType == 1) ? "DEPOSIT" : "WITHDRAWAL";
        double newBalance = (txnType == 1) ? previousBalance + amount : previousBalance - amount;

        System.out.println("\n" + "=".repeat(60));
        System.out.println("TRANSACTION CONFIRMATION");
        System.out.println("=".repeat(60));

        // Display transaction details
        Transaction preview = new Transaction(accountNumber, type, amount, newBalance);
        System.out.printf("%-8s: %s%n", "TXN ID", preview.getTransactionId());
        System.out.printf("%-8s: %s%n", "Account", accountNumber);
        System.out.printf("%-8s: %s%n", "Type", type);
        System.out.printf("%-8s: $%.2f%n", "Amount", amount);
        System.out.printf("%-8s: $%.2f%n", "Previous", previousBalance);
        System.out.printf("%-8s: $%.2f%n", "New", newBalance);
        System.out.printf("%-8s: %s%n", "Date/Time", preview.getTimestamp());

        boolean confirm = InputValidator.getYesNoInput(scanner, "\nConfirm transaction?");

        if (confirm) {
            // Process transaction
            boolean success;
            if (txnType == 1) {
                success = account.deposit(amount);
            } else {
                success = account.withdraw(amount);
            }

            if (success) {
                Transaction transaction = new Transaction(
                        accountNumber,
                        type,
                        amount,
                        account.getBalance()
                );
                transactionManager.addTransaction(transaction);
                System.out.println("\n✓ Transaction completed successfully!");
            } else {
                System.out.println("\n✗ Transaction failed!");
            }
        } else {
            System.out.println("\nTransaction cancelled.");
        }
    }

    private static void viewTransactionHistory() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("VIEW TRANSACTION HISTORY");
        System.out.println("=".repeat(60));

        if (accountManager.getAccountCount() == 0) {
            System.out.println("No accounts available.");
            return;
        }

        String accountNumber = InputValidator.getStringInput(scanner, "Enter Account Number: ");
        Account account = accountManager.findAccount(accountNumber);

        if (account == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.printf("%nAccount: %s - %s%n",
                accountNumber, account.getCustomer().getName());
        System.out.println("Account Type: " + account.getAccountType());
        System.out.printf("Current Balance: $%.2f%n", account.getBalance());

        transactionManager.viewTransactionsByAccount(accountNumber);
    }

    private static void exitApplication() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Thank you for using Bank Account Management System!");
        System.out.printf("Session Summary: %d accounts created, %d transactions processed%n",
                accountManager.getAccountCount(),
                transactionManager.getTransactionCount());
        System.out.println("Goodbye!");
        System.out.println("=".repeat(60));
        scanner.close();
    }
}