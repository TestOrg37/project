package org.example;

import java.util.*;

public class Supplier extends User {
    public Supplier(String name, String email, String password) {
        super(name, email, password, "Supplier");
    }

    @Override
    public void showMenu() {
        System.out.println("1. View invoices");
        System.out.println("2. Update prices and quantities");
    }
    // In Supplier.java
    public void viewOpenPurchaseOrders() {
        System.out.println("Open purchase orders:");
        List<String> purchaseOrders = FileUtils.readFile("purchase_orders.txt");
        for (String order : purchaseOrders) {
            System.out.println(order);
        }
    }
}