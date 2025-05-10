package org.example;

import java.text.SimpleDateFormat;
import java.util.*;

public class Chef extends User {
    private static Scanner scanner = new Scanner(System.in);

    public Chef(String name, String email, String password) {
        super(name, email, password, "Chef");
    }

    @Override
    public void showMenu() {
        int choice;
        do {
            System.out.println("\n----- Chef Menu -----");
            System.out.println("1. Customize Meals");
            System.out.println("2. View Customer Orders");
            System.out.println("3. View Assigned Tasks");
            System.out.println("4. Review Replacement Requests");
            System.out.println("5. View Customer Preferences and order history");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> customizeMealAsChef();
                case 2 -> viewCustomerOrders();
                case 3 -> showAssignedTasks();
                case 4 -> reviewReplacementRequests();
                case 5 -> showAllOrdersAndSelectForDetails();
                case 0 -> System.out.println("Logged out successfully.");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    public void showAllOrdersAndSelectForDetails() {
        List<String> orders = FileUtils.readFile("orders.txt");

        if (orders.isEmpty()) {
            System.out.println("📭 No orders currently available.");
            return;
        }

        System.out.println("📦 All Orders:");
        for (String order : orders) {
            String[] parts = order.split(":");
            if (parts.length >= 3) {
                String orderId = parts[0].trim();
                String email = parts[1].trim();
                String mealName = parts[2].trim();
                System.out.println("🔢 Order ID: " + orderId + " | 👤 Customer: " + email + " | 🍽️ Meal: " + mealName);
            }
        }

        System.out.print("\n📝 Enter order ID to view details: ");
        String chosenOrderId = scanner.nextLine().trim();

        viewFullCustomerOrderDetailsByOrderNumber(chosenOrderId);
    }

    public void viewFullCustomerOrderDetailsByOrderNumber(String orderId) {
        List<String> orders = FileUtils.readFile("orders.txt");
        List<String> customerPrefs = FileUtils.readFile("customer_preferences.txt");

        String customerEmail = null;

        for (String order : orders) {
            if (order.startsWith(orderId + ":")) {
                String[] parts = order.split(":");
                if (parts.length >= 2) {
                    customerEmail = parts[1].trim();
                }
                break;
            }
        }

        if (customerEmail == null) {
            System.out.println("❌ No email found for this order.");
            return;
        }

        System.out.println("\n📧 Customer Email: " + customerEmail);

        for (String line : customerPrefs) {
            if (line.startsWith(customerEmail)) {
                String[] prefParts = line.split(":");
                if (prefParts.length >= 3) {
                    System.out.println("✅ Preferences: " + prefParts[1].trim());
                    System.out.println("⚠️ Allergies: " + prefParts[2].trim());
                }
                break;
            }
        }

        System.out.println("\n📜 Customer Order History:");
        for (String order : orders) {
            if (order.contains(customerEmail)) {
                String[] parts = order.split(":");
                if (parts.length >= 6) {
                    System.out.println("----------------------------");
                    System.out.println("🔢 Order ID: " + parts[0].trim());
                    System.out.println("🍽️ Meal: " + parts[2].trim());
                    System.out.println("💰 Price: " + parts[3].trim());
                    System.out.println("📅 Date: " + parts[4].trim());
                    System.out.println("📌 Status: " + parts[5].trim());

                    if (parts.length >= 9 && parts[7].trim().equalsIgnoreCase("change")) {
                        System.out.println("♻️ Replacement: " + parts[8].trim());
                    }
                }
            }
        }
    }

    public void customizeMealAsChef() {
        List<String> meals = FileUtils.readFile("meals.txt");
        Scanner scanner = new Scanner(System.in);

        if (meals.isEmpty()) {
            System.out.println("❌ No meals available.");
            return;
        }

        System.out.println("🍽️ Available Meals:");
        for (String meal : meals) {
            if (!meal.startsWith("M:")) // Skip ingredient lines
                System.out.println(meal);
        }

        System.out.print("🔢 Enter the meal ID you want to customize: ");
        String chosenId = scanner.nextLine().trim();

        String selectedMeal = null;
        for (String meal : meals) {
            if (meal.startsWith(chosenId + ":")) {
                selectedMeal = meal;
                break;
            }
        }

        if (selectedMeal == null) {
            System.out.println("❌ No meal found with this ID.");
            return;
        }

        String[] parts = selectedMeal.split(":");
        if (parts.length < 5) {
            System.out.println("⚠️ Invalid meal format.");
            return;
        }

        String oldMealName = parts[1].trim();
        String oldIngredients = parts[2].trim();
        String oldPrice = parts[3].trim();
        String oldType = parts[4].trim();

        System.out.println("🔧 Customize Meal:");
        System.out.println("🔤 Current Name: " + oldMealName);
        System.out.print("➡️ Enter new name: ");
        String newMealName = scanner.nextLine().trim();

        System.out.println("🌿 Current Ingredients: " + oldIngredients);
        System.out.print("➡️ Enter new ingredients (comma separated): ");
        String newIngredients = scanner.nextLine().trim();

        // Generate new ID
        int newId = meals.size() + 1;
        String newMealId = String.format("%03d", newId); // e.g. "001"

        // Create new meal line
        String customizedMeal = newMealId + ":" + newMealName + ":" + newIngredients + ":" + oldPrice + ":" + oldType;
        FileUtils.appendToFile("meals.txt", customizedMeal);

        // Register quantities for each ingredient
        String[] ingredientList = newIngredients.split(",");
        for (String ing : ingredientList) {
            ing = ing.trim();
            System.out.print("📦 Enter required quantity of " + ing + " per meal: ");
            int qty = Integer.parseInt(scanner.nextLine().trim());

            String ingLine = "M:" + ing + ":" + qty;
            FileUtils.appendToFile("meals.txt", ingLine);
        }

        System.out.println("✅ Meal customized successfully.");
        System.out.println("🆕 " + customizedMeal);
    }

    public void viewCustomerOrders() {
        List<String> orders = FileUtils.readFile("orders.txt");
        if (orders.isEmpty()) {
            System.out.println("❌ No orders currently available.");
            return;
        }

        List<String> waitingOrders = new ArrayList<>();
        System.out.println("📦 Pending Orders:");
        int index = 1;
        for (String order : orders) {
            if (order.contains(": waiting")) {
                System.out.println(index + ". " + order);
                waitingOrders.add(order);
                index++;
            }
        }

        if (waitingOrders.isEmpty()) {
            System.out.println("✅ No pending orders.");
            return;
        }

        System.out.print("🔢 Select order number to review (or 0 to exit): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Clear line

        if (choice < 1 || choice > waitingOrders.size()) {
            System.out.println("❌ Invalid number. Operation cancelled.");
            return;
        }

        String selectedOrder = waitingOrders.get(choice - 1);
        String[] parts = selectedOrder.split(":");
        if (parts.length < 9) {
            System.out.println("⚠️ Invalid order format.");
            return;
        }

        String orderId = parts[0].trim();
        String email = parts[1].trim();
        String mealName = parts[2].trim();
        String price = parts[3].trim();
        String timestamp = parts[4].trim();
        String status = parts[5].trim();
        String amount = parts[6].trim();
        int qtyRequested = Integer.parseInt(amount);

        System.out.println("\n📋 Order Details:");
        System.out.println("Order ID: " + orderId);
        System.out.println("Email: " + email);
        System.out.println("Meal: " + mealName);
        System.out.println("Quantity: " + amount);
        System.out.println("Price: " + price + " SAR");
        System.out.println("Delivery Time: " + timestamp);

        // Read meal data from meals.txt
        List<String> mealsData = FileUtils.readFile("meals.txt");
        Map<String, Integer> requiredIngredients = new HashMap<>();
        boolean mealFound = false;

        for (int i = 0; i < mealsData.size(); i++) {
            String line = mealsData.get(i).trim();
            if (line.contains(mealName)) {
                mealFound = true;
                for (int j = i + 1; j < mealsData.size(); j++) {
                    String ingLine = mealsData.get(j).trim();
                    if (!ingLine.startsWith("M:")) break;
                    String[] ingParts = ingLine.substring(2).split(":");
                    if (ingParts.length == 2) {
                        String ingName = ingParts[0].trim();
                        int ingQtyPerMeal = Integer.parseInt(ingParts[1].trim());
                        requiredIngredients.put(ingName, ingQtyPerMeal * qtyRequested);
                    }
                }
                break;
            }
        }

        if (!mealFound) {
            System.out.println("⚠️ Meal details not found in meals.txt.");
            return;
        }

        // Ask user if they want to approve the order
        System.out.print("✅ Do you want to approve this order? (yes / no): ");
        String decision = scanner.nextLine().trim().toLowerCase();

        if (decision.equals("yes")) {
            // Read inventory from inventory.txt
            List<String> inventoryData = FileUtils.readFile("inventory.txt");
            Map<String, Integer> inventory = new HashMap<>();
            for (String line : inventoryData) {
                String[] invParts = line.split(":");
                if (invParts.length == 2) {
                    inventory.put(invParts[0].trim(), Integer.parseInt(invParts[1].trim()));
                }
            }

            // Deduct quantities from inventory after approval
            for (String ing : requiredIngredients.keySet()) {
                int newQty = inventory.getOrDefault(ing, 0) - requiredIngredients.get(ing);
                inventory.put(ing, newQty);
            }

            // Update inventory file
            List<String> updatedInventory = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                updatedInventory.add(entry.getKey() + ":" + entry.getValue());
            }
            FileUtils.writeToFile("inventory.txt", updatedInventory);

            // Calculate delivery time
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 45);
            String deliveryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());

            // Update order status
            List<String> allOrders = FileUtils.readFile("orders.txt");
            List<String> updatedOrders = new ArrayList<>();
            for (String order : allOrders) {
                parts = order.split(":");
                if (parts.length >= 9 && parts[6].trim().equals(orderId)) {
                    parts[7] = "yes"; // Update order status to "approved"
                    updatedOrders.add(String.join(":", parts));
                } else {
                    updatedOrders.add(order);
                }
            }
            FileUtils.writeToFile("orders.txt", updatedOrders);

            // Add order to approval and invoice files
            String approvedEntry = email + ":" + mealName + ":" + amount + ":" + price + ":" + deliveryDate + ":" + orderId;
            FileUtils.appendToFile("approved_orders.txt", approvedEntry);

            String invoiceEntry = email + ":" + mealName + ":" + price + ":" + deliveryDate;
            FileUtils.appendToFile("invoices.txt", invoiceEntry);

            System.out.println("✅ Order approved successfully.");
            System.out.println("📅 Delivery Time: " + deliveryDate);
        } else {
            System.out.println("❌ Order rejected. (Not automatically deleted from orders file)");
        }
    }

    public void viewInvoices() {
        List<String> invoices = FileUtils.readFile("invoices.txt");
        if (invoices.isEmpty()) {
            System.out.println("No invoices currently available.");
            return;
        }

        System.out.println("Customer Invoices:");
        for (String invoice : invoices) {
            System.out.println(invoice);
        }
    }

    public void manageIngredients() {
        List<String> ingredients = FileUtils.readFile("ingredients.txt");

        if (ingredients.isEmpty()) {
            System.out.println("No ingredients currently available.");
            return;
        }

        System.out.println("Available Ingredients:");
        for (String ingredient : ingredients) {
            System.out.println(ingredient);
        }

        System.out.println("\nDo you want to add new ingredients? (yes / no)");
        String addMore = scanner.nextLine();

        if (addMore.equalsIgnoreCase("yes")) {
            System.out.println("Enter new ingredient name:");
            String newIngredient = scanner.nextLine();
            FileUtils.appendToFile("ingredients.txt", newIngredient);
            System.out.println("Ingredient added successfully.");
        }
    }

    public void showAssignedTasks() {
        List<String> tasks = FileUtils.readFile("chef_tasks.txt");
        boolean found = false;
        System.out.println("\n📋 Assigned Tasks for " + getName() + ":");

        for (String task : tasks) {
            String[] parts = task.split(":");
            if (parts.length >= 2 && parts[0].trim().equalsIgnoreCase(this.getEmail())) {
                System.out.println("🧾 " + parts[1].trim());
                found = true;
            }
        }

        if (!found) {
            System.out.println("🚫 No tasks assigned to you yet.");
        }
    }

    public void reviewReplacementRequests() {
        List<String> orders = FileUtils.readFile("orders.txt");
        List<String> ingredients = FileUtils.readFile("ingredients.txt");
        List<String> customerPrefs = FileUtils.readFile("customer_preferences.txt"); // email:preference:allergy

        List<String> updatedOrders = new ArrayList<>();
        boolean found = false;

        for (String order : orders) {
            if (!order.contains(": change :")) {
                updatedOrders.add(order);
                continue;
            }

            String[] parts = order.split(":");
            if (parts.length < 9) {
                updatedOrders.add(order);
                continue;
            }

            String orderId = parts[0].trim();
            String email = parts[1].trim();
            String mealName = parts[2].trim();
            String status = parts[5].trim(); // waiting
            String changeDetails = parts[8].trim(); // milk/almondmilk

            String[] changeParts = changeDetails.split("/");
            if (changeParts.length != 2) {
                updatedOrders.add(order);
                continue;
            }

            String original = changeParts[0].trim();
            String substitute = changeParts[1].trim();

            boolean safe = true;
            for (String line : customerPrefs) {
                String[] userParts = line.split(":");
                if (userParts.length >= 3 && userParts[0].trim().equalsIgnoreCase(email)) {
                    String allergy = userParts[2].trim().toLowerCase();
                    if (substitute.equalsIgnoreCase(allergy)) {
                        System.out.println("❌ Allergy conflict for order " + orderId);
                        safe = false;
                    }
                    break;
                }
            }

            if (!ingredients.contains(substitute)) {
                System.out.println("❌ Substitute not available for ingredient " + substitute + " in order " + orderId);
                safe = false;
            }

            System.out.println("📋 Order: " + orderId + " | " + email + " | Replace " + original + " with " + substitute);
            if (!safe) {
                System.out.println("🚫 Cannot approve this order.");
                updatedOrders.add(order.replace(": waiting :", ": no :"));
                continue;
            }

            // Ask for chef's decision
            System.out.print("✅ Do you approve this replacement? (yes/no): ");
            String decision = scanner.nextLine().trim().toLowerCase();

            if (decision.equals("yes")) {
                System.out.println("✅ Order " + orderId + " approved");
                updatedOrders.add(order.replace(": waiting :", ": yes :"));
            } else {
                System.out.println("❌ Order " + orderId + " rejected");
                updatedOrders.add(order.replace(": waiting :", ": no :"));
            }

            found = true;
        }

        if (!found) {
            System.out.println("📭 No replacement requests currently available.");
        }

        // Update file
        FileUtils.writeToFile("orders.txt", updatedOrders);
    }

    public void viewPreferencesByOrderNumber() {
        System.out.print("Enter order ID: ");
        String orderId = scanner.nextLine().trim();
        List<String> orders = FileUtils.readFile("orders.txt");

        String customerEmail = null;
        for (String order : orders) {
            if (order.startsWith(orderId + ":")) {
                String[] parts = order.split(":");
                if (parts.length >= 2) {
                    customerEmail = parts[1].trim();
                }
                break;
            }
        }

        if (customerEmail == null) {
            System.out.println("No email found for this order.");
            return;
        }

        List<String> preferences = FileUtils.readFile("customer_preferences.txt");
        for (String line : preferences) {
            if (line.startsWith(customerEmail)) {
                System.out.println("Customer preferences (" + customerEmail + "): " + line.substring(line.indexOf(":") + 1).trim());
                return;
            }
        }

        System.out.println("No preferences registered for this customer.");
    }

    public void viewOrderHistoryByOrderNumber() {
        System.out.print("Enter order ID: ");
        String orderId = scanner.nextLine().trim();
        List<String> orders = FileUtils.readFile("orders.txt");

        String customerEmail = null;
        for (String order : orders) {
            if (order.startsWith(orderId + ":")) {
                String[] parts = order.split(":");
                if (parts.length >= 2) {
                    customerEmail = parts[1].trim();
                }
                break;
            }
        }

        if (customerEmail == null) {
            System.out.println("No email found for this order.");
            return;
        }

        System.out.println("Order history for customer (" + customerEmail + "):");
        for (String order : orders) {
            if (order.contains(customerEmail)) {
                System.out.println(order);
            }
        }
    }

    public void viewOrderHistory() {
        System.out.println("Your order history:");
        List<String> orders = FileUtils.readFile("orders.txt");
        String email = getEmail();

        boolean found = false;
        for (String order : orders) {
            if (order.contains(email)) {
                String[] orderDetails = order.split(":");
                String orderId = orderDetails[0]; // Order ID
                String mealName = orderDetails[2]; // Meal name
                String status = order.contains("(waiting for replacement approval)") ? "Pending approval" : "Completed";

                System.out.println("Order ID: " + orderId);
                System.out.println("Customer Name: " + email);
                System.out.println("Meal: " + mealName);
                System.out.println("Status: " + status);
                System.out.println("--------------------------");
                found = true;
            }
        }

        if (!found) {
            System.out.println("No saved orders for this account.");
        }
    }
}