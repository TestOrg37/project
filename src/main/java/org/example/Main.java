package org.example;

import java.io.*;
import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static List<User> users = new ArrayList<>();

    public static void main(String[] args) {
        loadUsers();

        while (true) {
            System.out.println("\n🎯 Private Chef Management System");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            System.out.print("Choose: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> login();
                case 2 -> register();
                case 3 -> {
                    System.out.println("👋 Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice!");
            }
        }
    }

    private static void loadUsers() {
        List<String> lines = FileUtils.readFile("users.txt");
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 4) continue;
            String name = parts[0], email = parts[1], password = parts[2], type = parts[3];
            users.add(createUserFromType(name, email, password, type));
        }
    }

    private static void register() {
        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.println("User type: (Customer, Chef, KitchenManager, SystemAdmin, Supplier)");
        String type = scanner.nextLine();

        User user = createUserFromType(name, email, password, type);
        if (user != null) {
            users.add(user);
            FileUtils.appendToFile("users.txt", String.join(",", name, email, password, type));
            System.out.println("✅ Registration successful!\n");
            user.showMenu();
            handleMenu(user);
        } else {
            System.out.println("❌ Unsupported user type.");
        }
    }

    private static void login() {
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        for (User user : users) {
            if (user.email.equals(email) && user.password.equals(password)) {
                System.out.println("✅ Welcome " + user.name);
                user.showMenu();
                handleMenu(user);
                return;
            }
        }
        System.out.println("❌ Invalid credentials.");
    }

    private static User createUserFromType(String name, String email, String password, String type) {
        return switch (type) {
            case "Customer" -> new Customer(name, email, password);
            case "Chef" -> new Chef(name, email, password);
            case "KitchenManager" -> new KitchenManager(name, email, password);
            case "SystemAdmin" -> new SystemAdmin(name, email, password);
            case "Supplier" -> new Supplier(name, email, password);
            default -> null;
        };
    }

    private static void handleMenu(User user) {
        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            customer.showMenu();
        } else if (user instanceof Chef) {
            Chef chef = (Chef) user;
            chef.showMenu();
        }
    }
}