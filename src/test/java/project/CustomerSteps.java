package project;



import io.cucumber.java.en.*;
import org.example.*;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

import static org.example.FileUtils.readFile;
import static org.example.FileUtils.*;


public class CustomerSteps {
    Customer customer;
    String testEmail = "laila@example.com";

    @Given("the customer {string} is logged in with password {string}")
    public void the_customer_logs_in(String email, String password) {
        customer = new Customer("Laila", email, password);
    }

    @When("the customer updates preferences to {string} and allergy to {string}")
    public void customer_updates_preferences(String preference, String allergy) {
        customer.inputPreferences(preference, allergy);
    }

    @Then("the preferences file should contain {string}")
    public void preferences_file_should_contain(String expectedEntry) {
        List<String> lines = readFile("customer_preferences.txt");
        Assert.assertTrue(lines.stream().anyMatch(line -> line.contains(expectedEntry)));
    }

    @Given("orders file contains {string}")
    public void orders_file_contains(String entry) {
        writeFile("orders.txt", Arrays.asList(entry));

    }

    @When("the customer views order history")
    public void customer_views_order_history() {
        customer.viewOrderHistory();
    }

    @Then("the system should show orders for {string}")
    public void system_should_show_orders(String email) {
        List<String> orders = readFile("orders.txt");
        Assert.assertTrue(orders.stream().anyMatch(line -> line.contains(email)));
    }

    @Given("invoices file contains {string}")
    public void invoices_file_contains(String entry) {
        writeFile("invoices.txt", Arrays.asList(entry));

    }

    @When("the customer views invoices")
    public void customer_views_invoices() {
        customer.viewInvoices();
    }

    @Then("the system should display invoice for {string}")
    public void system_should_display_invoice(String mealName) {
        List<String> lines = readFile("invoices.txt");
        Assert.assertTrue(lines.stream().anyMatch(line -> line.contains(mealName)));
    }

    @Given("preferences file contains {string}")
    public void preferences_file_contains(String entry) {

        writeFile("icustomer_preferences.txt", Arrays.asList(entry));

    }

    @When("the customer views preferences")
    public void customer_views_preferences() {
        customer.viewPreferences();
    }

    @Then("the system should display {string} and {string}")
    public void system_should_display_preferences(String pref, String allergy) {
        Assert.assertEquals(pref, customer.getPreferences());
        Assert.assertEquals(allergy, customer.getAllergies());
    }

    @Given("approved orders file contains {string}")
    public void approved_orders_file_contains(String entry) {

        writeFile("approved_orders.txt", Arrays.asList(entry));

    }

    @When("the customer checks approved orders")
    public void customer_checks_approved_orders() {
        customer.checkApprovedOrders();
    }

    @Then("the system should display approved order with id {string}")
    public void system_should_display_approved_order(String orderId) {
        List<String> lines = readFile("approved_orders.txt");
        Assert.assertTrue(lines.stream().anyMatch(line -> line.contains(orderId)));
    }

    @Given("meals file contains:")
    public void meals_file_contains(String content) {

        writeFile("meals.txt", Arrays.asList(content));
    }

}
