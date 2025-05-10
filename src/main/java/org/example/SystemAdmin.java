package org.example;

import java.util.List;

public class SystemAdmin extends User {
    public SystemAdmin(String name, String email, String password) {
        super(name, email, password, "SystemAdmin");
    }

    @Override
    public void showMenu() {
        System.out.println("1. Analyze customer data");
        System.out.println("2. Generate financial reports");
        System.out.println("3. System supervision");
    }
    // In SystemAdmin.java
    public void analyzeCustomerData() {
        System.out.println("Customer data analysis:");
        List<String> customerData = FileUtils.readFile("customer_data.txt");
        for (String data : customerData) {
            System.out.println(data);
        }

        // Data analysis (simple example of report generation)
        System.out.println("Generating report of most common orders...");
        // Data analysis (example)
        // Can analyze the most common orders here based on data.
    }

    public void generateFinancialReport() {
        System.out.println("Generate financial report:");
        List<String> financialData = FileUtils.readFile("financial_reports.txt");
        for (String report : financialData) {
            System.out.println(report);
        }
    }

    public void generateFinancialReports() {}
    public void monitorSystem() {}
}