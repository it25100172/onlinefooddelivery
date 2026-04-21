package com.fooddelivery.controller;

import com.fooddelivery.model.MenuItem;
import com.fooddelivery.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * HomeController — Handles the landing page (Page 1).
 * Shows the restaurant menu grouped by category.
 *
 * Member 5 is responsible for this controller and the home page UI.
 */
@Controller
public class HomeController {

    @Autowired
    private MenuService menuService;

    /**
     * GET /  — Shows the home/landing page with available menu items.
     * OOP Concept: POLYMORPHISM in action — menuItems list contains both
     *              FoodItem and Beverage objects referenced as MenuItem.
     *
     * @param category optional filter parameter (e.g., ?category=Beverage)
     * @param model    Spring MVC model to pass data to Thymeleaf template
     * @return the home.html template name
     */
    @GetMapping("/")
    public String home(@RequestParam(required = false) String category, Model model) {
        List<MenuItem> allItems = menuService.findAvailable();

        // Filter by category if provided
        List<MenuItem> displayItems = (category != null && !category.isEmpty())
            ? allItems.stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList())
            : allItems;

        // Get distinct categories for the filter buttons
        List<String> categories = allItems.stream()
            .map(MenuItem::getCategory)
            .distinct()
            .sorted()
            .collect(Collectors.toList());

        model.addAttribute("menuItems", displayItems);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        return "home";
    }
}
