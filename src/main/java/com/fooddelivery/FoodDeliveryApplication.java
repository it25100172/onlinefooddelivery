package com.fooddelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Online Food Delivery Management System.
 * SE1020 OOP Project - 6 Member Team
 *
 * OOP Concepts demonstrated across the project:
 *   - Encapsulation : All model classes use private fields + getters/setters
 *   - Inheritance   : User -> Customer, DeliveryPerson | MenuItem -> FoodItem, Beverage
 *   - Polymorphism  : getDetails() and calculatePrice() overridden in subclasses
 */
@SpringBootApplication
public class FoodDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryApplication.class, args);
    }
}
