package org.example;

public class PurchaseOrder {
    private String componentName;
    private double quantity;
    private String status; // مفتوح - مؤكد - مرفوض

    public PurchaseOrder(String componentName, double quantity, String status) {
        this.componentName = componentName;
        this.quantity = quantity;
        this.status = status;
    }

    // Getters and Setters
}
