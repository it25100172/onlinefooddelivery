package com.fooddelivery.model;

/**
 * MenuItem — Parent class for all items on the restaurant menu.
 *
 * OOP Concept: ENCAPSULATION
 *   Private fields with public getters/setters.
 *
 * OOP Concept: INHERITANCE
 *   FoodItem and Beverage both extend this class.
 *
 * OOP Concept: POLYMORPHISM
 *   calculatePrice() and getDetails() are overridden in subclasses.
 */
public class MenuItem {

    private int id;
    private String name;
    private double basePrice;
    private String category;   // e.g., "Main", "Beverage", "Dessert"
    private boolean available; // whether item is currently on the menu
    private String imageUrl;   // path to food image for UI display

    // --- Constructors ---

    public MenuItem() {}

    public MenuItem(int id, String name, double basePrice, String category,
                    boolean available, String imageUrl) {
        this.id        = id;
        this.name      = name;
        this.basePrice = basePrice;
        this.category  = category;
        this.available = available;
        this.imageUrl  = imageUrl;
    }

    // --- Getters & Setters (Encapsulation) ---

    public int getId()              { return id; }
    public void setId(int id)       { this.id = id; }

    public String getName()               { return name; }
    public void setName(String name)      { this.name = name; }

    public double getBasePrice()                  { return basePrice; }
    public void setBasePrice(double basePrice)    { this.basePrice = basePrice; }

    public String getCategory()                 { return category; }
    public void setCategory(String category)    { this.category = category; }

    public boolean isAvailable()                  { return available; }
    public void setAvailable(boolean available)   { this.available = available; }

    public String getImageUrl()                 { return imageUrl; }
    public void setImageUrl(String imageUrl)    { this.imageUrl = imageUrl; }

    // --- Methods ---

    /**
     * Calculates the final price of this menu item.
     * OOP Concept: POLYMORPHISM — subclasses override this to apply their own pricing logic.
     * (e.g., FoodItem adds a packaging fee, Beverage adds size-based pricing)
     *
     * @return the base price as the default calculation
     */
    public double calculatePrice() {
        return basePrice;
    }

    /**
     * Returns a formatted description of this menu item.
     * OOP Concept: POLYMORPHISM — subclasses override this to include extra info.
     */
    public String getDetails() {
        return "MenuItem [ID=" + id + ", Name=" + name
            + ", Price=Rs." + String.format("%.2f", basePrice)
            + ", Category=" + category + ", Available=" + available + "]";
    }

    /** Converts to pipe-delimited string for menu_items.txt */
    public String toFileString() {
        return id + "|" + name + "|" + basePrice + "|" + category + "|" + available + "|" + imageUrl;
    }

    /** Parses a line from menu_items.txt into a MenuItem */
    public static MenuItem fromFileString(String line) {
        String[] p = line.split("\\|");
        return new MenuItem(
            Integer.parseInt(p[0]),
            p[1],
            Double.parseDouble(p[2]),
            p[3],
            Boolean.parseBoolean(p[4]),
            p.length > 5 ? p[5] : ""
        );
    }

    @Override
    public String toString() {
        return getDetails();
    }
}
