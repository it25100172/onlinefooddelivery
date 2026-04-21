package com.fooddelivery.model;

/**
 * User — Parent/Base class for all user types in the system.
 *
 * OOP Concept: ENCAPSULATION
 *   All fields are declared private — they cannot be accessed directly from outside this class.
 *   Public getters and setters control how these fields are read and modified.
 *   This protects the integrity of the data (e.g., we can validate an email before setting it).
 *
 * OOP Concept: INHERITANCE
 *   This class is the parent. Customer and DeliveryPerson extend this class,
 *   inheriting all these fields and methods so we don't duplicate code.
 */
public class User {

    // Private fields — only accessible through getters/setters (Encapsulation)
    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String role; // "customer", "delivery", "admin"

    // --- Constructors ---

    /** Default constructor required for object creation from file data */
    public User() {}

    /** Parameterized constructor for creating a full User object at once */
    public User(int id, String name, String email, String password, String phone, String role) {
        this.id       = id;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.phone    = phone;
        this.role     = role;
    }

    // --- Getters & Setters (Encapsulation) ---

    public int getId()                { return id; }
    public void setId(int id)         { this.id = id; }

    public String getName()           { return name; }
    public void setName(String name)  { this.name = name; }

    public String getEmail()              { return email; }
    public void setEmail(String email)    { this.email = email; }

    public String getPassword()               { return password; }
    public void setPassword(String password)  { this.password = password; }

    public String getPhone()              { return phone; }
    public void setPhone(String phone)    { this.phone = phone; }

    public String getRole()             { return role; }
    public void setRole(String role)    { this.role = role; }

    // --- Methods ---

    /**
     * Returns user details as a formatted string.
     * OOP Concept: POLYMORPHISM — subclasses (Customer, DeliveryPerson) OVERRIDE this method
     *              to add their own extra details on top of the base user info.
     *
     * @return formatted string describing this user
     */
    public String getDetails() {
        return "User [ID=" + id + ", Name=" + name + ", Email=" + email + ", Role=" + role + "]";
    }

    /**
     * Converts this user object into a pipe-delimited string for file storage.
     * Format: id|name|email|password|phone|role
     *
     * @return pipe-delimited string representation
     */
    public String toFileString() {
        return id + "|" + name + "|" + email + "|" + password + "|" + phone + "|" + role;
    }

    /**
     * Creates a User object by parsing a pipe-delimited line from the data file.
     *
     * @param line pipe-delimited string from users.txt
     * @return User object populated with parsed data
     */
    public static User fromFileString(String line) {
        String[] parts = line.split("\\|");
        return new User(
            Integer.parseInt(parts[0]),  // id
            parts[1],                    // name
            parts[2],                    // email
            parts[3],                    // password
            parts[4],                    // phone
            parts[5]                     // role
        );
    }

    @Override
    public String toString() {
        return getDetails();
    }
}
