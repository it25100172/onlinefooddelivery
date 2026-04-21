package com.fooddelivery.model;

/**
 * DeliveryPerson — Subclass of User representing a delivery driver.
 *
 * OOP Concept: INHERITANCE
 *   Inherits all User fields and methods. Only adds delivery-specific fields.
 *
 * OOP Concept: POLYMORPHISM
 *   Overrides getDetails() to include vehicle and availability info.
 */
public class DeliveryPerson extends User {

    private String vehicleType;    // e.g., "Bike", "Scooter", "Car"
    private boolean isAvailable;   // true = can take new orders
    private int deliveriesCompleted;

    // --- Constructors ---

    public DeliveryPerson() {
        super();
    }

    public DeliveryPerson(int id, String name, String email, String password,
                          String phone, String vehicleType, boolean isAvailable, int deliveriesCompleted) {
        super(id, name, email, password, phone, "delivery");
        this.vehicleType          = vehicleType;
        this.isAvailable          = isAvailable;
        this.deliveriesCompleted  = deliveriesCompleted;
    }

    // --- Getters & Setters (Encapsulation) ---

    public String getVehicleType()                { return vehicleType; }
    public void setVehicleType(String vehicleType){ this.vehicleType = vehicleType; }

    public boolean isAvailable()                  { return isAvailable; }
    public void setAvailable(boolean available)   { this.isAvailable = available; }

    public int getDeliveriesCompleted()                      { return deliveriesCompleted; }
    public void setDeliveriesCompleted(int deliveriesCompleted){ this.deliveriesCompleted = deliveriesCompleted; }

    // --- Overridden Methods (Polymorphism) ---

    /**
     * POLYMORPHISM — Overrides User.getDetails() to show delivery-specific info.
     */
    @Override
    public String getDetails() {
        return super.getDetails()
            + ", Vehicle=" + vehicleType
            + ", Available=" + isAvailable
            + ", Deliveries=" + deliveriesCompleted;
    }

    /** Converts to pipe-delimited string for file storage */
    @Override
    public String toFileString() {
        return super.toFileString() + "|" + vehicleType + "|" + isAvailable + "|" + deliveriesCompleted;
    }

    /** Parses a line from delivery_assignments.txt or users.txt */
    public static DeliveryPerson fromFileString(String line) {
        String[] p = line.split("\\|");
        return new DeliveryPerson(
            Integer.parseInt(p[0]),
            p[1], p[2], p[3], p[4],
            p[6],
            Boolean.parseBoolean(p[7]),
            Integer.parseInt(p[8])
        );
    }
}
