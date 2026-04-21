package com.fooddelivery.model;

/**
 * Beverage — Subclass of MenuItem representing drinks.
 *
 * OOP Concept: INHERITANCE
 *   Inherits all MenuItem fields. Adds beverage-specific fields: size and isCold.
 *
 * OOP Concept: POLYMORPHISM
 *   Overrides calculatePrice() to apply a size multiplier.
 *   Small=base, Medium=base*1.3, Large=base*1.6.
 */
public class Beverage extends MenuItem {

    private String size;        // "Small", "Medium", "Large"
    private boolean isCold;     // true = cold drink, false = hot drink
    private boolean hasCaffeine;

    // --- Constructors ---

    public Beverage() {
        super();
    }

    public Beverage(int id, String name, double basePrice, String category,
                    boolean available, String imageUrl,
                    String size, boolean isCold, boolean hasCaffeine) {
        super(id, name, basePrice, category, available, imageUrl);
        this.size         = size;
        this.isCold       = isCold;
        this.hasCaffeine  = hasCaffeine;
    }

    // --- Getters & Setters (Encapsulation) ---

    public String getSize()           { return size; }
    public void setSize(String size)  { this.size = size; }

    public boolean isCold()               { return isCold; }
    public void setCold(boolean cold)     { this.isCold = cold; }

    public boolean isHasCaffeine()                  { return hasCaffeine; }
    public void setHasCaffeine(boolean hasCaffeine) { this.hasCaffeine = hasCaffeine; }

    // --- Overridden Methods (Polymorphism) ---

    /**
     * POLYMORPHISM — Overrides MenuItem.calculatePrice() with size-based pricing.
     * Small = basePrice, Medium = basePrice * 1.3, Large = basePrice * 1.6
     *
     * @return price adjusted for size
     */
    @Override
    public double calculatePrice() {
        double base = getBasePrice();
        switch (size.toLowerCase()) {
            case "medium": return base * 1.3;
            case "large":  return base * 1.6;
            default:       return base;  // Small
        }
    }

    /**
     * POLYMORPHISM — Overrides MenuItem.getDetails() to include drink-specific info.
     */
    @Override
    public String getDetails() {
        return super.getDetails()
            + ", Size=" + size
            + ", Cold=" + isCold
            + ", Caffeine=" + hasCaffeine
            + ", FinalPrice=Rs." + String.format("%.2f", calculatePrice());
    }

    /** Saves to pipe-delimited string */
    @Override
    public String toFileString() {
        return "beverage|" + super.toFileString() + "|" + size + "|" + isCold + "|" + hasCaffeine;
    }

    /** Parses a beverage line from menu_items.txt */
    public static Beverage fromFileString(String line) {
        String data = line.startsWith("beverage|") ? line.substring(9) : line;
        String[] p = data.split("\\|");
        return new Beverage(
            Integer.parseInt(p[0]), p[1],
            Double.parseDouble(p[2]), p[3],
            Boolean.parseBoolean(p[4]), p[5],
            p[6], Boolean.parseBoolean(p[7]),
            Boolean.parseBoolean(p[8])
        );
    }
}
