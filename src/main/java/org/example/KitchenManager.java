package org.example;

import java.util.*;

public class KitchenManager extends User {
    private static Scanner scanner = new Scanner(System.in);

    public KitchenManager(String name, String email, String password) {
        super(name, email, password, "KitchenManager");
    }

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n🔧 Kitchen Manager Menu:");
            System.out.println("1. Team Management");
            System.out.println("2. Inventory Management");
            System.out.println("3. Review Purchase Orders");
            System.out.println("4. View Alerts");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    manageTeam();
                    break;
                case "2":
                    manageInventory();
                    break;
                case "3":
                    reviewPurchaseOrders();
                    break;
                case "4":
                    showAlertsForLowStock();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("❌ Invalid option.");
            }
        }
    }

    public void reviewPurchaseOrders() {
        Map<String, Integer> purchasedQuantities = new HashMap<>();

        List<String> supplierData = FileUtils.readFile("suppliers.txt");
        if (supplierData.isEmpty()) {
            System.out.println("⚠️ No supplier data available.");
            return;
        }

        System.out.println("📦 Supplier and Product List:");
        for (String line : supplierData) {
            System.out.println(line);
        }

        Scanner scanner = new Scanner(System.in);
        List<String> invoice = new ArrayList<>();
        double totalInvoiceAmount = 0.0;

        // Read inventory from inventory file
        List<String> inventoryData = FileUtils.readFile("inventory.txt");

        List<String> updatedProducts = new ArrayList<>(); // To track products updated in inventory

        while (true) {
            System.out.print("\n🔤 Enter supplier name: ");
            String supplierName = scanner.nextLine().trim();

            System.out.print("📦 Enter product name: ");
            String productName = scanner.nextLine().trim();

            System.out.print("🔢 Enter required quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Clear the line

            boolean found = false;
            for (int i = 0; i < supplierData.size(); i++) {
                String line = supplierData.get(i);
                if (line.startsWith("Supplier:" + supplierName)) {
                    String email = "";
                    for (int j = i + 1; j < supplierData.size(); j++) {
                        String nextLine = supplierData.get(j).trim();

                        if (nextLine.startsWith("Email:")) {
                            email = nextLine.substring(6).trim();
                        } else if (nextLine.startsWith("Item:")) {
                            String[] parts = nextLine.substring(5).split(":");
                            if (parts.length >= 5 && parts[0].equalsIgnoreCase(productName)) {
                                double normalPrice = Double.parseDouble(parts[1]);
                                double bulkPrice = Double.parseDouble(parts[2]);
                                int available = Integer.parseInt(parts[3]);
                                String status = parts[4];

                                if (available < quantity) {
                                    System.out.println("❌ Required quantity not available.");
                                } else {
                                    double selectedPrice = (quantity > 150) ? bulkPrice : normalPrice;
                                    double total = selectedPrice * quantity;
                                    totalInvoiceAmount += total; // Add price to total
                                    String entry = "Supplier: " + supplierName + "\nEmail: " + email +
                                            "\nItem: " + productName + "\nQuantity: " + quantity +
                                            "\nTotal Price: " + total + "\n---";
                                    invoice.add(entry);
                                    purchasedQuantities.put(productName, purchasedQuantities.getOrDefault(productName, 0) + quantity);

                                    updatedProducts.add(productName); // Add the updated product
                                    System.out.println("✅ Added to invoice:\n" + entry);
                                    found = true;
                                    break;
                                }
                            }
                        } else if (nextLine.startsWith("Supplier:")) {
                            break; // New supplier, exit
                        }
                    }
                    break;
                }
            }

            if (!found) {
                System.out.println("⚠️ Supplier or product not found.");
            }

            System.out.print("➕ Do you want to make another purchase? (yes/no): ");
            String again = scanner.nextLine().trim().toLowerCase();
            if (!again.equals("yes")) {
                break;
            }
        }

        // Display total invoice amount
        if (!invoice.isEmpty()) {
            System.out.println("\n💰 Total Invoice Amount: " + totalInvoiceAmount);

            // Save invoice to file
            StringBuilder builder = new StringBuilder();
            for (String line : invoice) {
                builder.append(line).append("\n");
            }
            FileUtils.appendToFile("purchase_invoices.txt", builder.toString());
            System.out.println("🧾 Invoice saved to purchase_invoices.txt file.");
        }

        // Update inventory only for purchased products
        updateInventory(inventoryData, purchasedQuantities);

        // Save updated inventory to the same file
        FileUtils.writeToFile("inventory.txt", inventoryData);

        System.out.println("🔙 Returning to Kitchen Manager Menu...");
    }

    // Function to update inventory based on purchased products
    public void updateInventory(List<String> inventoryData, Map<String, Integer> purchasedQuantities) {
        for (int i = 0; i < inventoryData.size(); i++) {
            String line = inventoryData.get(i);
            String[] parts = line.split(":");
            if (purchasedQuantities.containsKey(parts[0])) {
                int currentQuantity = Integer.parseInt(parts[1]);
                int purchasedQuantity = purchasedQuantities.get(parts[0]);
                int newQuantity = currentQuantity + purchasedQuantity;
                inventoryData.set(i, parts[0] + ":" + newQuantity);
                System.out.println("✅ Inventory quantity updated: " + parts[0] + " - New quantity: " + newQuantity);
            }
        }
    }

    // Function to get purchased quantity from invoice
    public int getProductQuantityFromInvoice(String productName) {
        int totalQuantity = 0;

        // Read invoice from file (or from list if in memory)
        List<String> invoiceData = FileUtils.readFile("purchase_invoices.txt");

        // Search for product in invoice
        for (String line : invoiceData) {
            if (line.contains("Item: " + productName)) {
                // Extract quantity from invoice
                String[] parts = line.split("\n");
                for (String part : parts) {
                    if (part.startsWith("Quantity:")) {
                        String quantityString = part.substring("Quantity:".length()).trim();
                        totalQuantity += Integer.parseInt(quantityString); // Add quantity to total
                    }
                }
            }
        }

        // Return total product quantity from invoice
        return totalQuantity;
    }

    public void manageInventory() {
        List<String> inventoryData = FileUtils.readFile("inventory.txt");

        if (inventoryData.isEmpty()) {
            System.out.println("❌ No inventory data available.");
            return;
        }

        System.out.println("\n📦 Inventory Status:\n");

        for (String line : inventoryData) {
            String[] parts = line.split(":");
            if (parts.length == 2) {
                String itemName = parts[0].trim();
                int quantity = Integer.parseInt(parts[1].trim());

                System.out.print("🔹 " + itemName + ": " + quantity);

                if (quantity < 50) {
                    System.out.print(" ⚠️ Low - Consider restocking");
                }

                System.out.println(); // New line
            }
        }
    }

    public void manageTeam() {
        List<String> chefs = FileUtils.readFile("chefs.txt");

        if (chefs.isEmpty()) {
            System.out.println("📭 No chefs found.");
            return;
        }

        System.out.println("\n👨‍🍳 Available Chefs:");
        for (int i = 0; i < chefs.size(); i++) {
            String[] parts = chefs.get(i).split(":");
            if (parts.length >= 4) {
                System.out.println((i + 1) + ". Name: " + parts[0] +
                        " | Email: " + parts[1] +
                        " | Specialty: " + parts[2] +
                        " | Experience: " + parts[3] + " years");
            }
        }

        System.out.print("\n🔢 Select a chef by number to assign a task: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;

        if (index < 0 || index >= chefs.size()) {
            System.out.println("❌ Invalid selection.");
            return;
        }

        String selectedChef = chefs.get(index);
        String[] selectedParts = selectedChef.split(":");
        String chefName = selectedParts[0];
        String chefEmail = selectedParts[1];

        System.out.print("📝 Enter the task to assign to " + chefName + ": ");
        String task = scanner.nextLine();

        // Save the task to a file (e.g., chef_tasks.txt)
        String taskEntry = chefEmail + ":" + task;
        FileUtils.appendToFile("chef_tasks.txt", taskEntry);

        System.out.println("✅ Task assigned to " + chefName + " successfully.");
    }

    // KitchenManager.java
    public void assignTaskToChef(String task, String chefId) {
        System.out.println("Assigning task: " + task + " to Chef " + chefId);
        // Store assigned task in a file or database
    }

    public void showAlertsForLowStock() {
        List<String> stockData = FileUtils.readFile("inventory.txt"); // Assume this is the inventory file
        if (stockData.isEmpty()) {
            System.out.println("⚠️ No inventory data available.");
            return;
        }

        System.out.println("🔔 Low Stock Alerts:");

        boolean lowStockFound = false;
        for (String line : stockData) {
            String[] parts = line.split(":");
            if (parts.length == 2) {
                String ingredientName = parts[0].trim();
                int quantity = Integer.parseInt(parts[1].trim());

                if (quantity < 50) {
                    System.out.println("🔴 " + ingredientName + " - Quantity: " + quantity + " (Low level)");
                    lowStockFound = true;
                }
            }
        }

        if (!lowStockFound) {
            System.out.println("✅ No low stock items found.");
        }
    }

    public void viewAlerts() {
        System.out.println("Viewing Alerts:");
        List<String> alerts = FileUtils.readFile("alerts.txt");
        for (String alert : alerts) {
            System.out.println(alert);
        }
    }
}