package com.fooddelivery.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Order — Represents a customer's food order.
 *
 * OOP Concept: ENCAPSULATION
 *   All fields private, accessed only through getters/setters.
 *
 * OOP Concept: COMPOSITION
 *   An Order "has-a" list of OrderItems — this is object composition.
 */
public class Order {

    // Order status constants
    public static final String STATUS_PENDING    = "Pending";
    public static final String STATUS_CONFIRMED  = "Confirmed";
    public static final String STATUS_PREPARING  = "Preparing";
    public static final String STATUS_OUT        = "Out for Delivery";
    public static final String STATUS_DELIVERED  = "Delivered";
    public static final String STATUS_CANCELLED  = "Cancelled";

    private int id;
    private int customerId;
    private String customerName;
    private String deliveryAddress;
    private String status;
    private String orderDate;       // stored as "yyyy-MM-dd HH:mm"
    private double totalAmount;
    private int deliveryPersonId;   // 0 = not yet assigned
    private String specialNote;

    // Composition: an Order contains multiple OrderItems
    private List<OrderItem> items;

    // --- Constructors ---

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(int id, int customerId, String customerName, String deliveryAddress,
                 String status, String orderDate, double totalAmount,
                 int deliveryPersonId, String specialNote) {
        this.id               = id;
        this.customerId       = customerId;
        this.customerName     = customerName;
        this.deliveryAddress  = deliveryAddress;
        this.status           = status;
        this.orderDate        = orderDate;
        this.totalAmount      = totalAmount;
        this.deliveryPersonId = deliveryPersonId;
        this.specialNote      = specialNote;
        this.items            = new ArrayList<>();
    }

    // --- Getters & Setters (Encapsulation) ---

    public int getId()              { return id; }
    public void setId(int id)       { this.id = id; }

    public int getCustomerId()                  { return customerId; }
    public void setCustomerId(int customerId)   { this.customerId = customerId; }

    public String getCustomerName()                   { return customerName; }
    public void setCustomerName(String customerName)  { this.customerName = customerName; }

    public String getDeliveryAddress()                      { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress)  { this.deliveryAddress = deliveryAddress; }

    public String getStatus()               { return status; }
    public void setStatus(String status)    { this.status = status; }

    public String getOrderDate()                  { return orderDate; }
    public void setOrderDate(String orderDate)    { this.orderDate = orderDate; }

    public double getTotalAmount()                   { return totalAmount; }
    public void setTotalAmount(double totalAmount)   { this.totalAmount = totalAmount; }

    public int getDeliveryPersonId()                      { return deliveryPersonId; }
    public void setDeliveryPersonId(int deliveryPersonId) { this.deliveryPersonId = deliveryPersonId; }

    public String getSpecialNote()                  { return specialNote; }
    public void setSpecialNote(String specialNote)  { this.specialNote = specialNote; }

    public List<OrderItem> getItems()             { return items; }
    public void setItems(List<OrderItem> items)   { this.items = items; }

    /** Adds a single item to this order's item list */
    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    /**
     * Recalculates the total amount from all OrderItems.
     * Called after adding or removing items.
     */
    public void recalculateTotal() {
        this.totalAmount = items.stream()
            .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
            .sum();
    }

    /** Converts to pipe-delimited string for orders.txt */
    public String toFileString() {
        return id + "|" + customerId + "|" + customerName + "|" + deliveryAddress
            + "|" + status + "|" + orderDate + "|" + String.format("%.2f", totalAmount)
            + "|" + deliveryPersonId + "|" + (specialNote == null ? "" : specialNote);
    }

    /** Parses an order line from orders.txt */
    public static Order fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        return new Order(
            Integer.parseInt(p[0]), Integer.parseInt(p[1]),
            p[2], p[3], p[4], p[5],
            Double.parseDouble(p[6]),
            Integer.parseInt(p[7]),
            p.length > 8 ? p[8] : ""
        );
    }

    @Override
    public String toString() {
        return "Order [ID=" + id + ", Customer=" + customerName
            + ", Status=" + status + ", Total=Rs." + String.format("%.2f", totalAmount) + "]";
    }
}
