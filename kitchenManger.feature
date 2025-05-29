Feature: Kitchen Manager Functionalities

  Background:
    Given a Kitchen Manager "Alice" is logged in with email "alice@cook.com" and password "secure123"

  Scenario: View available chefs and assign a task
    Given chefs are listed in "chefs.txt"
    When the Kitchen Manager selects a chef and assigns a task "Prepare vegan lasagna"
    Then the task should be saved in "chef_tasks.txt"

  Scenario: View current inventory
    Given inventory data is available in "inventory.txt"
    When the Kitchen Manager views the inventory
    Then the system should display all items and flag items with quantity less than 50

  Scenario: Review supplier purchase orders
    Given supplier data is present in "suppliers.txt" and "inventory.txt"
    When the Kitchen Manager orders 100 units of "Tomato" from supplier "FreshFarms"
    Then the purchase should be added to the invoice and inventory should be updated

  Scenario: Get low stock alerts
    Given inventory data is available in "inventory.txt"
    When the Kitchen Manager requests stock alerts
    Then items with quantity less than 50 should be listed as low stock

