package com.fooddelivery.service;

import com.fooddelivery.interfaces.Manageable;
import com.fooddelivery.model.Customer;
import com.fooddelivery.model.DeliveryPerson;
import com.fooddelivery.model.User;
import com.fooddelivery.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserService — Handles all CRUD operations for User, Customer, and DeliveryPerson.
 *
 * OOP Concept: INTERFACE IMPLEMENTATION
 *   Implements Manageable<User, Integer>, meaning it must provide add(), update(),
 *   delete(), findById(), and findAll() methods.
 *
 * Member 1 is responsible for this service.
 */
@Service
public class UserService implements Manageable<User, Integer> {

    private static final String USERS_FILE           = "users.txt";
    private static final String DELIVERY_FILE        = "delivery_persons.txt";

    @Autowired
    private FileUtil fileUtil;

    // ============================================================
    //  CREATE
    // ============================================================

    /**
     * Saves a new User (Customer or DeliveryPerson) to the appropriate file.
     * Assigns an auto-incremented ID before saving.
     *
     * @param user the user object to save
     */
    @Override
    public void add(User user) {
        String file = user.getRole().equals("delivery") ? DELIVERY_FILE : USERS_FILE;
        // Auto-assign the next available ID
        user.setId(fileUtil.getNextId(file));
        // Append to the appropriate file
        fileUtil.appendLine(file, user.toFileString());
    }

    // ============================================================
    //  READ — Get all users
    // ============================================================

    /**
     * Reads all users from users.txt and returns them as User objects.
     * OOP Concept: POLYMORPHISM — returns both Customer and DeliveryPerson as User references.
     *
     * @return list of all users
     */
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        for (String line : fileUtil.readAllLines(USERS_FILE)) {
            // Check the role field (position 5) to determine which subclass to create
            String[] parts = line.split("\\|");
            if (parts.length >= 6) {
                if (parts[5].equals("customer")) {
                    users.add(Customer.fromFileString(line));
                } else {
                    users.add(User.fromFileString(line));
                }
            }
        }
        return users;
    }

    /** Returns all delivery persons from delivery_persons.txt */
    public List<DeliveryPerson> findAllDeliveryPersons() {
        List<DeliveryPerson> list = new ArrayList<>();
        for (String line : fileUtil.readAllLines(DELIVERY_FILE)) {
            list.add(DeliveryPerson.fromFileString(line));
        }
        return list;
    }

    /** Returns only available delivery persons */
    public List<DeliveryPerson> findAvailableDeliveryPersons() {
        return findAllDeliveryPersons().stream()
            .filter(DeliveryPerson::isAvailable)
            .collect(Collectors.toList());
    }

    // ============================================================
    //  READ — Find by ID
    // ============================================================

    /**
     * Finds a user by their ID by scanning the file.
     *
     * @param id the user's integer ID
     * @return the User object, or null if not found
     */
    @Override
    public User findById(Integer id) {
        for (User user : findAll()) {
            if (user.getId() == id) return user;
        }
        return null;
    }

    /** Finds a user by their email address (for login) */
    public User findByEmail(String email) {
        for (User user : findAll()) {
            if (user.getEmail().equalsIgnoreCase(email)) return user;
        }
        return null;
    }

    /** Returns true if email + password match a user record */
    public boolean authenticate(String email, String password) {
        User user = findByEmail(email);
        return user != null && user.getPassword().equals(password);
    }

    // ============================================================
    //  UPDATE
    // ============================================================

    /**
     * Updates a user's record in the file.
     * Reads all lines, replaces the matching line, then rewrites the file.
     *
     * @param updatedUser the user with updated fields (must have correct ID)
     */
    @Override
    public void update(User updatedUser) {
        String file = updatedUser.getRole().equals("delivery") ? DELIVERY_FILE : USERS_FILE;
        List<String> lines = fileUtil.readAllLines(file);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            // Check if this line belongs to the user we want to update
            String[] parts = line.split("\\|");
            if (parts.length > 0 && Integer.parseInt(parts[0]) == updatedUser.getId()) {
                // Replace with updated data
                updatedLines.add(updatedUser.toFileString());
            } else {
                // Keep unchanged
                updatedLines.add(line);
            }
        }

        fileUtil.writeAllLines(file, updatedLines);
    }

    // ============================================================
    //  DELETE
    // ============================================================

    /**
     * Deletes a user by ID — rewrites the file without that user's line.
     *
     * @param id the ID of the user to delete
     */
    @Override
    public void delete(Integer id) {
        for (String file : new String[]{USERS_FILE, DELIVERY_FILE}) {
            List<String> lines = fileUtil.readAllLines(file);
            List<String> filtered = lines.stream()
                .filter(line -> {
                    String[] parts = line.split("\\|");
                    return parts.length == 0 || Integer.parseInt(parts[0]) != id;
                })
                .collect(Collectors.toList());
            fileUtil.writeAllLines(file, filtered);
        }
    }
}
