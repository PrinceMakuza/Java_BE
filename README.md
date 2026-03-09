# Bank Account Management System

A comprehensive console-based Java application for managing bank accounts, customers, and transactions with full OOP principles and DSA implementations.

## Features

### Account Management
- **Savings Accounts**: 3.5% annual interest, $500 minimum balance
- **Checking Accounts**: $1000 overdraft limit, $10 monthly fee (waived for premium customers)

### Customer Types
- **Regular Customer**: Standard banking services
- **Premium Customer**: No monthly fees, priority service, $10,000 minimum balance

### Core Functionality
- Create accounts with automatic ID generation (ACC001, ACC002, etc.)
- Process deposits and withdrawals with validation
- View all accounts with real-time balances
- View transaction history with summaries
- Input validation for all operations

## OOP Principles Implemented

- **Encapsulation**: Private fields with public getters/setters
- **Inheritance**: Account and Customer hierarchies
- **Polymorphism**: Method overriding and interface implementation
- **Abstraction**: Abstract classes and Transactable interface
- **Composition**: Manager classes contain arrays of objects
- **Static Members**: For ID generation counters

## Data Structures & Algorithms

- **Arrays**: Fixed-size collections (accounts: 50, transactions: 200)
- **Linear Search**: O(n) for finding accounts and transactions
- **Reverse Chronological Sorting**: Display newest transactions first
- **Time Complexity Awareness**: Optimized search implementations

## How to Run

1. Compile all Java files:
   ```bash
   javac src/**/*.java
