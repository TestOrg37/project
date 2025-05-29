Feature: Generate Financial Reports
  As a System Admin
  I want to generate financial reports from invoice data
  So that I can analyze revenue and save the report to a file

  Scenario: Generate financial report with valid invoice data
    Given there is a file "invoices.txt" with content:
      """
      user1@example.com:Burger:10.50:2025-05-01
      user2@example.com:Pizza:15.75:2025-05-02
      user1@example.com:Burger:10.50:2025-06-01
      """
    When the System Admin generates the financial report
    Then the total revenue should be 36.75 JOD
    And the report should contain invoice details:
      | Email             | Meal   | Price  | Date       |
      | user1@example.com | Burger | 10.50  | 2025-05-01 |
      | user2@example.com | Pizza  | 15.75  | 2025-05-02 |
      | user1@example.com | Burger | 10.50  | 2025-06-01 |
    And the report should show revenue by meal:
      | Meal   | Revenue |
      | Burger | 21.00   |
      | Pizza  | 15.75   |
    And the report should show monthly revenue:
      | Month   | Revenue |
      | 2025-05 | 26.25   |
      | 2025-06 | 10.50   |
    And the report should be saved to "financial_report.txt"
    And the file "financial_report.txt" does not exist

  Scenario: Generate financial report with empty invoice file
    Given there is a file "invoices.txt" with content:
      """
      """
    And the file "financial_report.txt" does not exist
    When the System Admin generates the financial report
    Then the system should display "❌ No financial data available in invoices.txt."
    And no report should be saved