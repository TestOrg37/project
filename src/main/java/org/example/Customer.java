package org.example;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.Main.*;

public class Customer extends User {
    public static Scanner scanner = new Scanner(System.in);
    private String preferences = "";
    private String allergies = "";
    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    public Customer(String name, String email, String password) {
        super(name, email, password, "Customer");
        loadPreferencesFromFile(); // Load preferences when logging in
    }

    @Override
    public void showMenu() {
        int choice;
        do {
            System.out.println("\n----- Customer Menu -----");
            System.out.println("1. Manage Profile (Preferences & Allergies)");
            System.out.println("2. View Order History");
            System.out.println("3. Customize Meal");
            System.out.println("4. View Invoices");
            System.out.println("5. Notifications");
            System.out.println("6. View Dietary Preferences");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    inputPreferences();
                    break;
                case 2:
                    viewOrderHistory();
                    break;
                case 3:
                    customizeMeal();
                    break;
                case 4:
                    viewInvoices();
                    break;
                case 5:
                    checkApprovedOrders();
                    break;
                case 6:
                    viewPreferences();
                    break;
                case 0:
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (choice != 0);
    }

    public void viewOrderHistory() {
        System.out.println("Your Order History:");
        List<String> orders = FileUtils.readFile("orders.txt");
        String email = getEmail();

        boolean found = false;
        for (String order : orders) {
            if (order.contains(email)) {
                System.out.println(order);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No saved orders for this account.");
        }
    }

    public void viewInvoices() {
        List<String> invoices = FileUtils.readFile("invoices.txt");
        String email = getEmail();
        boolean found = false;

        System.out.println("📄 Your Invoices:\n");

        for (String line : invoices) {
            if (line.startsWith(email + ":")) {
                // Invoice format: email:mealName:price:timestamp
                String[] parts = line.split(":");
                if (parts.length >= 4) {
                    String id = parts[0];
                    String mealName = parts[1];
                    String price = parts[2];
                    String timestamp = parts[3];
                    System.out.println("🔢OrderId: " + id);
                    System.out.println("🍽️ Meal Name: " + mealName);
                    System.out.println("💰 Price: " + price + " SAR");
                    System.out.println("🗓️ Date: " + timestamp);
                    System.out.println("-----------------------------");
                    found = true;
                }
            }
        }

        if (!found) {
            System.out.println("❌ No saved invoices for this account.");
        }
    }

    public void sendDeliveryReminder() {
        System.out.println("\uD83D\uDCE6 Reminder: Your meal will be delivered tomorrow.");
    }

    public void viewPreferences() {
        List<String> preferencesList = FileUtils.readFile("customer_preferences.txt");
        String email = getEmail();
        boolean found = false;

        for (String line : preferencesList) {
            if (line.startsWith(email + ":")) {
                String data = line.substring(line.indexOf(":") + 1).trim();
                String[] parts = data.split(",");
                System.out.println("Dietary Preferences: " + parts[0]);
                System.out.println("Allergies: " + (parts.length > 1 ? parts[1] : "None"));
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("No saved preferences for this account.");
        }
    }

    private void loadPreferencesFromFile() {
        List<String> preferencesList = FileUtils.readFile("customer_preferences.txt");
        String email = getEmail();
        for (String line : preferencesList) {
            if (line.startsWith(email + ":")) {
                String data = line.substring(line.indexOf(":") + 1).trim();
                String[] parts = data.split(",");
                this.preferences = parts[0].trim();
                this.allergies = (parts.length > 1) ? parts[1].trim() : "";
                break;
            }
        }
    }

    public void inputPreferences() {
        if (this.preferences == null || this.preferences.isEmpty()) {
            this.preferences = "None";
        }
        if (this.allergies == null || this.allergies.isEmpty()) {
            this.allergies = "None";
        }

        System.out.println("📋 Your current preferences: " + this.preferences);
        System.out.println("⚠️ Your current allergies: " + this.allergies);

        System.out.println("Would you like to modify them? (yes / no): ");
        String answer = scanner.nextLine().trim();

        if (!answer.equalsIgnoreCase("yes")) {
            System.out.println("✅ Preferences were not modified.");
            return;
        }

        System.out.println("\nChoose your dietary preferences: ");
        System.out.println("1. Vegetarian");
        System.out.println("2. Asian");
        System.out.println("3. Keto");
        System.out.println("4. Italian");
        System.out.println("5. No preferences");
        System.out.print("Choose your preference (1-5): ");
        int preferenceChoice = scanner.nextInt();
        scanner.nextLine();
        String preference = "";

        switch (preferenceChoice) {
            case 1: preference = "Vegetarian"; break;
            case 2: preference = "Asian"; break;
            case 3: preference = "KETO"; break;
            case 4: preference = "ITALIAN"; break;
            case 5: preference = "None"; break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        System.out.println("Selected preference: " + preference);

        System.out.println("\nChoose your allergy: ");
        System.out.println("1. No allergies");
        System.out.println("2. Gluten allergy");
        System.out.println("3. Nut allergy");
        System.out.print("Choose allergy (1-3): ");
        int allergyChoice = scanner.nextInt();
        scanner.nextLine();
        String allergy = "";

        switch (allergyChoice) {
            case 1: allergy = "None"; break;
            case 2: allergy = "Gluten"; break;
            case 3: allergy = "Nuts"; break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        System.out.println("Selected allergy: " + allergy);

        this.preferences = preference;
        this.allergies = allergy;

        String entry = getEmail() + ": " + preference + "," + allergy;
        FileUtils.appendToFile("customer_preferences.txt", entry);
        System.out.println("✅ Preferences and allergies saved successfully.");
    }
    // To programmatically set preferences and allergies (useful for testing)
    public void inputPreferences(String preference, String allergy) {
        this.preferences = preference;
        this.allergies = allergy;

        String entry = getEmail() + ": " + preference + "," + allergy;
        FileUtils.appendToFile("customer_preferences.txt", entry);
    }
    public String getPreferences() {
        return preferences;
    }

    public String getAllergies() {
        return allergies;
    }

    public void checkApprovedOrders() {
        List<String> approvedOrders = FileUtils.readFile("approved_orders.txt");
        String email = getEmail();
        boolean found = false;

        for (String line : approvedOrders) {
            if (line.startsWith(email + ":")) {
                String[] parts = line.split(":");
                if (parts.length >= 5) {
                    String id = parts[5];
                    String mealName = parts[1];
                    String amount = parts[2];
                    String price = parts[3];
                    String deliveryDate = parts[4];
                    System.out.println("🔢OrderId: " + id);
                    System.out.println("✅ Your meal has been approved: " + mealName);
                    System.out.println("📦 Quantity: " + amount);
                    System.out.println("💰 Price: " + price + " SAR");
                    System.out.println("📅 Delivery Date-Time: " + deliveryDate);
                    System.out.println("-----------------------------");

                    String invoiceEntry = email + ":" + mealName + ":" + price + ":" + deliveryDate;
                    List<String> invoices = FileUtils.readFile("invoices.txt");
                    if (!invoices.contains(invoiceEntry)) {
                        FileUtils.appendToFile("invoices.txt", invoiceEntry);
                    }

                    found = true;
                }
            }
        }

        if (!found) {
            System.out.println("📭 No approved orders yet.");
        }
    }

    public void customizeMeal() {
        List<String> lines = FileUtils.readFile("meals.txt");
        String preference = this.preferences.toLowerCase();
        String allergy = this.allergies.toLowerCase();

        Map<String, String> mealMap = new LinkedHashMap<>();
        Map<String, List<String>> mealIngredientsMap = new HashMap<>();

        if (lines.isEmpty()) {
            System.out.println("❌ No meals available.");
            return;
        }

        System.out.println("🍽️ Meals suitable for you:");
        System.out.println("Your preferences: " + preference);
        System.out.println("Your allergies: " + allergy);

        int i = 0;
        while (i < lines.size()) {
            String line = lines.get(i);

            if (!line.contains(":") || !Character.isDigit(line.charAt(0))) {
                i++;
                continue;
            }

            String[] mainParts = line.split(":");
            if (mainParts.length < 5) {
                i++;
                continue;
            }

            String mealId = mainParts[0];
            String mealName = mainParts[1];
            String ingredientsRaw = mainParts[2];
            String mealPrice = mainParts[3];
            String mealType = mainParts[4];

            List<String> ingredientList = Arrays.stream(ingredientsRaw.split(","))
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            // Read required ingredient details (M:...)
            List<String> mLines = new ArrayList<>();
            i++;
            while (i < lines.size() && lines.get(i).startsWith("M:")) {
                mLines.add(lines.get(i));
                i++;
            }

            // Filter conditions:
            boolean exclude = false;

            if (!allergy.equals("none")) {
                for (String ing : ingredientList) {
                    if (ing.equalsIgnoreCase(allergy)) {
                        exclude = true;
                        break;
                    }
                }
            }

            if (!preference.equals("none")) {
                boolean matchesPref = mealName.toLowerCase().contains(preference)
                        || mealType.toLowerCase().contains(preference)
                        || ingredientList.stream().anyMatch(i2 -> i2.contains(preference));
                if (!matchesPref) {
                    exclude = true;
                }
            }

            if (!exclude) {
                mealMap.put(mealId, line);
                mealIngredientsMap.put(mealId, mLines);
                System.out.println("ID: " + mealId + " | " + mealName + " | Ingredients: " + ingredientsRaw + " | Price: " + mealPrice);
            }
        }

        if (mealMap.isEmpty()) {
            System.out.println("❌ No meals match your preferences.");
            return;
        }

        System.out.print("🔢 Enter the meal number you want to order: ");
        String chosenId = scanner.nextLine().trim();

        if (!mealMap.containsKey(chosenId)) {
            System.out.println("❌ No meal with this number.");
            return;
        }

        // Retrieve selected meal data
        String selectedMeal = mealMap.get(chosenId);
        List<String> selectedMIngredients = mealIngredientsMap.get(chosenId);

        String[] selectedParts = selectedMeal.split(":");
        String mealName = selectedParts[1];
        String mealPrice = selectedParts[3];
        String ingredientsRaw = selectedParts[2];

        System.out.print("🔢 Enter quantity: ");
        int amount = scanner.nextInt();
        scanner.nextLine(); // Clear line

        double pricePerUnit = Double.parseDouble(mealPrice);
        double totalPrice = pricePerUnit * amount;
        String formattedPrice = String.format("%.2f", totalPrice);

        List<String> orders = FileUtils.readFile("orders.txt");
        int orderNumber = orders.size() + 1;
        String orderId = String.format("ORD%03d", orderNumber);

        String timestamp = java.time.LocalDateTime.now()
                .withSecond(0)
                .withNano(0)
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm"));
        String changeStatus = "no-change";
        String changeDetails = "no";

        System.out.println("Would you like to substitute any ingredient? (yes / no)");
        String change = scanner.nextLine().trim();

        if (change.equalsIgnoreCase("yes")) {
            System.out.print("Which ingredient would you like to replace? ");
            String originalIngredient = scanner.nextLine().trim().toLowerCase();
            System.out.print("What is the substitute? ");
            String substitute = scanner.nextLine().trim().toLowerCase();

            changeStatus = "change";
            changeDetails = originalIngredient + "/" + substitute;

            System.out.println("✅ Replacement request included in the order.");
        } else {
            System.out.println("✅ Meal selected without substitutions.");
        }

        String orderEntry = orderId + ":" + getEmail() + ": " + mealName + ": " + formattedPrice + ": " +
                timestamp + ": waiting: " + amount + ": " + changeStatus + " :" + changeDetails;

        FileUtils.appendToFile("orders.txt", orderEntry);
        System.out.println("🧾 Order submitted successfully. Order ID: " + orderId);
    }
    // Example of a method for handling ingredient replacement
    public void requestIngredientReplacement(String mealId, String originalIngredient, String substituteIngredient) {
        // Logic for handling ingredient replacement
        System.out.println("Replaced " + originalIngredient + " with " + substituteIngredient + " in meal " + mealId);
    }

    // Example of a method for handling the order
    public void placeOrder(String mealId, int quantity) {
        // Logic for placing the order
        System.out.println("Placed an order for " + quantity + " of meal " + mealId);
    }
    public void simulateMealOrder(Customer customer, String mealId, int quantity,
                                  boolean approveReplacement, String originalIngredient, String substituteIngredient) {
        if (approveReplacement && !originalIngredient.isEmpty() && !substituteIngredient.isEmpty()) {

            customer.requestIngredientReplacement(mealId, originalIngredient, substituteIngredient);
        }

        customer.placeOrder(mealId, quantity);
    }

}