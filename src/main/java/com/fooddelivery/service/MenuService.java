package com.fooddelivery.service;

import com.fooddelivery.interfaces.Manageable;
import com.fooddelivery.model.Beverage;
import com.fooddelivery.model.FoodItem;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MenuService — CRUD operations for MenuItem (FoodItem and Beverage).
 *
 * OOP Concept: POLYMORPHISM
 *   findAll() returns a List<MenuItem> that can contain both FoodItem and Beverage objects.
 *   When getDetails() or calculatePrice() is called on any element,
 *   the correct subclass version runs automatically (runtime polymorphism).
 *
 * Member 2 is responsible for this service.
 */
@Service
public class MenuService implements Manageable<MenuItem, Integer> {

    private static final String MENU_FILE = "menu_items.txt";

    @Autowired
    private FileUtil fileUtil;

    // ============================================================
    //  CREATE
    // ============================================================

    /** Adds a new MenuItem (FoodItem or Beverage) to menu_items.txt */
    @Override
    public void add(MenuItem item) {
        item.setId(fileUtil.getNextId(MENU_FILE));
        fileUtil.appendLine(MENU_FILE, item.toFileString());
    }

    // ============================================================
    //  READ
    // ============================================================

    /**
     * Reads all menu items from menu_items.txt.
     * OOP Concept: POLYMORPHISM — returns a mixed list of FoodItem and Beverage
     *              objects, all referenced as MenuItem (their parent type).
     */
    @Override
    public List<MenuItem> findAll() {
        List<MenuItem> items = new ArrayList<>();
        for (String line : fileUtil.readAllLines(MENU_FILE)) {
            if (line.startsWith("food|")) {
                items.add(FoodItem.fromFileString(line));
            } else if (line.startsWith("beverage|")) {
                items.add(Beverage.fromFileString(line));
            } else {
                items.add(MenuItem.fromFileString(line));
            }
        }
        return items;
    }

    /** Returns only available menu items (for customer-facing pages) */
    public List<MenuItem> findAvailable() {
        return findAll().stream()
            .filter(MenuItem::isAvailable)
            .collect(Collectors.toList());
    }

    /** Returns items filtered by category */
    public List<MenuItem> findByCategory(String category) {
        return findAll().stream()
            .filter(item -> item.getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }

    @Override
    public MenuItem findById(Integer id) {
        return findAll().stream()
            .filter(item -> item.getId() == id)
            .findFirst()
            .orElse(null);
    }

    // ============================================================
    //  UPDATE
    // ============================================================

    @Override
    public void update(MenuItem updated) {
        List<String> lines = fileUtil.readAllLines(MENU_FILE);
        List<String> updatedLines = lines.stream()
            .map(line -> {
                String idStr = line.startsWith("food|") || line.startsWith("beverage|")
                    ? line.split("\\|")[1]   // skip "food" or "beverage" prefix
                    : line.split("\\|")[0];
                return Integer.parseInt(idStr) == updated.getId()
                    ? updated.toFileString()
                    : line;
            })
            .collect(Collectors.toList());
        fileUtil.writeAllLines(MENU_FILE, updatedLines);
    }

    // ============================================================
    //  DELETE
    // ============================================================

    @Override
    public void delete(Integer id) {
        List<String> lines = fileUtil.readAllLines(MENU_FILE);
        List<String> filtered = lines.stream()
            .filter(line -> {
                String idStr = line.startsWith("food|") || line.startsWith("beverage|")
                    ? line.split("\\|")[1]
                    : line.split("\\|")[0];
                return Integer.parseInt(idStr) != id;
            })
            .collect(Collectors.toList());
        fileUtil.writeAllLines(MENU_FILE, filtered);
    }
}
