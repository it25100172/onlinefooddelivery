package com.fooddelivery.model;

/**
 * OrderItem — Represents one line item in an Order (one menu item + quantity).
 *
 * OOP Concept: ENCAPSULATION
 *   Private fields with getters/setters.
 *
 * OOP Concept: COMPOSITION
 *   An Order contains a List<OrderItem>. OrderItem holds a reference to a MenuItem.
 */
public class OrderItem {

    private int menuItemId;
    private String menuItemName;
    private int quantity;
    private double unitPrice;    // price at the time the order was placed

    // --- Constructors ---

    public OrderItem() {}

    public OrderItem(int menuItemId, String menuItemName, int quantity, double unitPrice) {
        this.menuItemId    = menuItemId;
        this.menuItemName  = menuItemName;
        this.quantity      = quantity;
        this.unitPrice     = unitPrice;
    }

    // --- Getters & Setters (Encapsulation) ---

    public int getMenuItemId()                  { return menuItemId; }
    public void setMenuItemId(int menuItemId)   { this.menuItemId = menuItemId; }

    public String getMenuItemName()                     { return menuItemName; }
    public void setMenuItemName(String menuItemName)    { this.menuItemName = menuItemName; }

    public int getQuantity()                { return quantity; }
    public void setQuantity(int quantity)   { this.quantity = quantity; }

    public double getUnitPrice()                { return unitPrice; }
    public void setUnitPrice(double unitPrice)  { this.unitPrice = unitPrice; }

    /** Calculates the subtotal for this line item */
    public double getSubtotal() {
        return unitPrice * quantity;
    }

    /** Converts to pipe-delimited string: menuItemId|menuItemName|quantity|unitPrice */
    public String toFileString() {
        return menuItemId + "~" + menuItemName + "~" + quantity + "~" + unitPrice;
    }

    /** Parses a tilde-delimited order item string */
    public static OrderItem fromFileString(String s) {
        String[] p = s.split("~");
        return new OrderItem(
            Integer.parseInt(p[0]), p[1],
            Integer.parseInt(p[2]), Double.parseDouble(p[3])
        );
    }
}
