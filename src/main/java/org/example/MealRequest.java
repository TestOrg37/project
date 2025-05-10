package org.example;

import java.util.*;

public class MealRequest {
    private String customerEmail;
    private String mealName;
    private Map<String, String> substitutions; // original -> substitute
    private String status; // pending, approved, rejected

    public MealRequest(String customerEmail, String mealName) {
        this.customerEmail = customerEmail;
        this.mealName = mealName;
        this.substitutions = new HashMap<>();
        this.status = "pending";
    }

    public MealRequest(String customerEmail, String mealName, String originalIngredient, String substitute) {
        this.customerEmail = customerEmail;
        this.mealName = mealName;
        this.substitutions = new HashMap<>();
        this.substitutions.put(originalIngredient, substitute);
        this.status = "pending";
    }

    public void addSubstitution(String original, String substitute) {
        substitutions.put(original, substitute);
    }

    public String toFileFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append(customerEmail).append(":").append(mealName).append(":");
        for (Map.Entry<String, String> entry : substitutions.entrySet()) {
            sb.append(entry.getKey()).append("->").append(entry.getValue()).append(";");
        }
        sb.append(":").append(status);  // Added status to file format
        return sb.toString();
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getMealName() {
        return mealName;
    }

    public Map<String, String> getSubstitutions() {
        return substitutions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Customization request from: " + customerEmail +
                "\nMeal: " + mealName +
                "\nSubstitutions: " + substitutions.toString() +
                "\nStatus: " + status;
    }
}