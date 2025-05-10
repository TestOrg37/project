Feature: Customer Menu Functionalities
  As a registered customer
  I want to use all menu options
  So that I can manage my preferences, view history, customize meals, and more

  Background:
    Given the customer "laila@example.com" is logged in with password "1234"

  Scenario: Manage Profile Preferences and Allergies
    When the customer updates preferences to "Vegetarian" and allergy to "Gluten"
    Then the preferences file should contain "laila@example.com: Vegetarian,Gluten"

  Scenario: View Order History
    Given orders file contains "ORD001:laila@example.com: Pasta: 25.00: 2024-05-10 12.00: waiting: 1: no-change : no"
    When the customer views order history
    Then the system should show orders for "laila@example.com"

  Scenario: View Invoices
    Given invoices file contains "laila@example.com:Pasta:25.00:2024-05-10 12.00"
    When the customer views invoices
    Then the system should display invoice for "Pasta"

  Scenario: View Preferences
    Given preferences file contains "laila@example.com: Vegetarian,Gluten"
    When the customer views preferences
    Then the system should display "Vegetarian" and "Gluten"

  Scenario: Check Approved Orders
    Given approved orders file contains "laila@example.com:Pasta:1:25.00:2024-05-11 12.00:ORD002"
    When the customer checks approved orders
    Then the system should display approved order with id "ORD002"

