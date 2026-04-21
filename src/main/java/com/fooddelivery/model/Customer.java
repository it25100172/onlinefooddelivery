package com.fooddelivery.model;

/**
 * Customer — Subclass of User representing a food ordering customer.
 *
 * OOP Concept: INHERITANCE
 *   Customer extends User, meaning it INHERITS all fields (id, name, email, password, phone, role)
 *   and methods from User. We only add fields specific to a Customer (deliveryAddress, totalOrders).
 *   This avoids code duplication — we reuse User's code through inheritance.
 *
 * OOP Concept: POLYMORPHISM (Method Overriding)
 *   getDetails() is overridden here to include Customer-specific info.
 *   When a Customer object calls getDetails(), THIS version runs, not the User version.
 *   This is runtime polymorphism (dynamic dispatch).
 */
public class Customer extends User {

    // Additional fields specific to Customer only
    private String deliveryAddress;
    private int totalOrders;

    // --- Constructors ---

    public Customer() {
        super(); // calls User's default constructor
    }

    public Customer(int id, String name, String email, String password,
                    String phone, String deliveryAddress, int totalOrders) {
        // super() calls the parent (User) constructor to set shared fields
        super(id, name, email, password, phone, "customer");
        this.deliveryAddress = deliveryAddress;
        this.totalOrders     = totalOrders;
    }

    // --- Getters & Setters (Encapsulation) ---

    public String getDeliveryAddress()                    { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress){ this.deliveryAddress = deliveryAddress; }

    public int getTotalOrders()                { return totalOrders; }
    public void setTotalOrders(int totalOrders){ this.totalOrders = totalOrders; }

    // --- Overridden Methods (Polymorphism) ---

    /**
     * POLYMORPHISM — Overrides User.getDetails() to include Customer-specific info.
     * The @Override annotation confirms this is intentionally overriding the parent method.
     *
     * @return formatted string with both User and Customer details
     */
    @Override
    public String getDetails() {
        // super.getDetails() calls the User version first, then we append Customer info
        return super.getDetails() + ", Address=" + deliveryAddress + ", Orders=" + totalOrders;
    }

    /**
     * Converts this Customer to a pipe-delimited string for file storage.
     * Format: id|name|email|password|phone|customer|deliveryAddress|totalOrders
     */
    @Override
    public String toFileString() {
        return super.toFileString() + "|" + deliveryAddress + "|" + totalOrders;
    }

    /**
     * Parses a pipe-delimited line from users.txt and creates a Customer object.
     */
    public static Customer fromFileString(String line) {
        String[] p = line.split("\\|");
        return new Customer(
            Integer.parseInt(p[0]),  // id
            p[1],                    // name
            p[2],                    // email
            p[3],                    // password
            p[4],                    // phone
            p[6],                    // deliveryAddress
            Integer.parseInt(p[7])   // totalOrders
        );
    }
}
