Feature: Chef Functionality
  As a chef
  I want to manage meal preparation and orders
  So I can efficiently fulfill customer requests

  Background:
    Given the chef is logged in with email "chef@restaurant.com"

  Scenario: View pending orders
    Given there are 3 pending orders in the system
    When the chef selects "View Customer Orders"
    Then the chef should see a list of all pending orders
    And each order should display ID, customer email, meal name, and quantity

  Scenario: Approve a valid order
    Given order "ORD-001" is in pending status
    And inventory has sufficient ingredients for the order
    When the chef approves order "ORD-001"
    Then the order status should change to "approved"
    And inventory quantities should be updated
    And an invoice should be generated

  Scenario: Reject an order with insufficient inventory
    Given order "ORD-002" is in pending status
    And inventory doesn't have sufficient ingredients for the order
    When the chef reviews order "ORD-002"
    Then the chef should see an insufficient inventory warning
    And the order should remain in pending status

  Scenario: Customize a meal
    Given meal "M001" exists in the system
    When the chef customizes meal "M001" with new name "Special Pasta" and new ingredients "pasta,tomato,cheese"
    Then a new meal entry should be created
    And the new meal should be available in the meals list

  Scenario: Review replacement requests
    Given customer "user@example.com" has no allergies to "almond milk"
    And ingredient "almond milk" exists in inventory
    And there's a replacement request to change "milk" to "almond milk" in order "ORD-003"
    When the chef reviews the replacement request
    Then the chef should approve the replacement
    And the order should be updated with the new ingredient

  Scenario: View customer preferences
    Given customer "user@example.com" has preferences "vegetarian" and allergy "peanuts"
    And order "ORD-004" belongs to this customer
    When the chef views details for order "ORD-004"
    Then the chef should see the customer's preferences and allergies

  Scenario: View assigned tasks
    Given the chef has 2 assigned tasks
    When the chef selects "View Assigned Tasks"
    Then the chef should see both tasks listed