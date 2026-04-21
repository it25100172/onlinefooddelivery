package com.fooddelivery.model;

/**
 * FoodItem — Subclass of MenuItem representing solid food (meals, snacks, etc.)
 *
 * OOP Concept: INHERITANCE
 *   Inherits all MenuItem fields. Adds food-specific fields: cuisineType and packagingFee.
 *
 * OOP Concept: POLYMORPHISM
 *   Overrides calculatePrice() to add a packaging fee on top of the base price.
 *   Overrides getDetails() to include cuisine type info.
 */
public class FoodItem extends MenuItem {

    private String cuisineType;   // e.g., "Sri Lankan", "Italian", "Chinese"
    private double packagingFee;  // extra charge for packaging this food item
    private boolean isSpicy;

    // --- Constructors ---

    public FoodItem() {
        super();
    }

    public FoodItem(int id, String name, double basePrice, String category,
                    boolean available, String imageUrl,
                    String cuisineType, double packagingFee, boolean isSpicy) {
        // super() initialises all MenuItem fields
        super(id, name, basePrice, category, available, imageUrl);
        this.cuisineType  = cuisineType;
        this.packagingFee = packagingFee;
        this.isSpicy      = isSpicy;
    }

    // --- Getters & Setters (Encapsulation) ---

    public String getCuisineType()                  { return cuisineType; }
    public void setCuisineType(String cuisineType)  { this.cuisineType = cuisineType; }

    public double getPackagingFee()                  { return packagingFee; }
    public void setPackagingFee(double packagingFee) { this.packagingFee = packagingFee; }

    public boolean isSpicy()              { return isSpicy; }
    public void setSpicy(boolean spicy)   { this.isSpicy = spicy; }

    // --- Overridden Methods (Polymorphism) ---

    /**
     * POLYMORPHISM — Overrides MenuItem.calculatePrice().
     * FoodItem price = basePrice + packagingFee.
     *
     * @return total price including packaging
     */
    @Override
    public double calculatePrice() {
        return getBasePrice() + packagingFee;
    }

    /**
     * POLYMORPHISM — Overrides MenuItem.getDetails() to include cuisine and spice info.
     */
    @Override
    public String getDetails() {
        return super.getDetails()
            + ", Cuisine=" + cuisineType
            + ", PackagingFee=Rs." + packagingFee
            + ", Spicy=" + isSpicy
            + ", FinalPrice=Rs." + String.format("%.2f", calculatePrice());
    }

    /** Saves to pipe-delimited string: parent fields + food-specific fields */
    @Override
    public String toFileString() {
        return "food|" + super.toFileString() + "|" + cuisineType + "|" + packagingFee + "|" + isSpicy;
    }

    /** Parses a food item line from menu_items.txt */
    public static FoodItem fromFileString(String line) {
        // Remove "food|" prefix first
        String data = line.startsWith("food|") ? line.substring(5) : line;
        String[] p = data.split("\\|");
        return new FoodItem(
            Integer.parseInt(p[0]), p[1],
            Double.parseDouble(p[2]), p[3],
            Boolean.parseBoolean(p[4]), p[5],
            p[6], Double.parseDouble(p[7]),
            Boolean.parseBoolean(p[8])
        );
    }
}
