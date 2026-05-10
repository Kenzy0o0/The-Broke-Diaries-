# The Broke Diaries

Project: The Broke Diaries — Personal Budgeting Software
Course: CS251 Introduction to Software Engineering
Team:

- Rawan Ahmed Said
- Kenzy Khalil
- Ramy Zaher

# Budget App File Structure
```
src/
└── main/
    ├── java/com/budgetapp/
    │   ├── Main.java                 # Entry point of the application
    │   ├── controller/               # Business logic and coordination
    │   │   ├── AuthManager.java
    │   │   ├── BudgetManager.java
    │   │   ├── NotificationManager.java
    │   │   ├── ReportGenerator.java
    │   │   └── TransactionManager.java
    │   ├── factory/                  # Factory design pattern implementations
    │   │   └── TransactionFactory.java
    │   ├── infrastructure/           # Database and external service connections
    │   │   └── DatabaseManager.java
    │   ├── model/                    # Data models and entities
    │   │   ├── Account.java
    │   │   ├── Budget.java
    │   │   ├── Category.java
    │   │   ├── Expense.java
    │   │   ├── Income.java
    │   │   ├── Notification.java
    │   │   ├── Transaction.java
    │   │   └── User.java
    │   ├── observer/                 # Observer design pattern interfaces
    │   │   └── IBudgetObserver.java
    │   └── UI/                       # UI Controllers (JavaFX)
    │       ├── BudgetController.java
    │       ├── DashboardController.java
    │       ├── LoginController.java
    │       ├── ProfileController.java
    │       ├── RegisterController.java
    │       ├── ReportController.java
    │       ├── TransactionController.java
    │       └── UIManager.java
    └── resources/                    # Static assets and UI layouts
        ├── css/
        │   └── style.css             # Application styling
        └── fxml/                     # JavaFX view templates
            ├── budget.fxml
            ├── dashboard.fxml
            ├── login.fxml
            ├── profile.fxml
            ├── register.fxml
            ├── report.fxml
            └── transaction.fxml
```
Tools used:

- Java 17
- JavaFX 17
- SQLite via JDBC (sqlite-jdbc)
- Maven for build management
- IntelliJ IDEA
- Scene Builder for FXML
- draw.io for diagrams
- MockFlow for wireframes

To run:

1. Open project in IntelliJ
2. Ensure Maven dependencies are loaded (pom.xml)
3. Run Main.java
[img alt](https://github.com/Kenzy0o0/The-Broke-Diaries-/blob/913ea129b38c1b474d919b878af4a631a5e824f6/image.png)
