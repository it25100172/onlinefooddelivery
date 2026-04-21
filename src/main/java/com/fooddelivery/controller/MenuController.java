package com.fooddelivery.controller;

import com.fooddelivery.model.Beverage;
import com.fooddelivery.model.FoodItem;
import com.fooddelivery.model.MenuItem;
import com.fooddelivery.service.MenuService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MenuController — Admin CRUD for menu items (Page 5).
 *
 * OOP Concept: POLYMORPHISM
 *   The addItem() method creates either a FoodItem or Beverage based on the
 *   "itemType" form parameter — the same form handles both types.
 *
 * Member 2 is responsible for this controller.
 */
@Controller
@RequestMapping("/admin/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    // ============================================================
    //  PAGE 5 — Menu Management (READ)
    // ============================================================

    /**
     * GET /admin/menu — displays all menu items for admin management.
     */
    @GetMapping
    public String showMenuManagement(Model model, HttpSession session) {
        if (!"admin".equals(session.getAttribute("userRole"))) {
            return "redirect:/user/login";
        }
        model.addAttribute("menuItems", menuService.findAll());
        return "menu-management";
    }

    // ============================================================
    //  CREATE — Add new menu item
    // ============================================================

    /**
     * POST /admin/menu/add — adds a new FoodItem or Beverage.
     *
     * OOP Concept: POLYMORPHISM
     *   Depending on itemType ("food" or "beverage"), we instantiate a different subclass.
     *   Both are then passed to menuService.add() which accepts the parent type MenuItem.
     */
    @PostMapping("/add")
    public String addItem(
            @RequestParam String itemType,
            @RequestParam String name,
            @RequestParam double basePrice,
            @RequestParam String category,
            @RequestParam(defaultValue = "true") boolean available,
            @RequestParam(defaultValue = "") String imageUrl,
            // FoodItem-specific
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false, defaultValue = "0") double packagingFee,
            @RequestParam(required = false, defaultValue = "false") boolean isSpicy,
            // Beverage-specific
            @RequestParam(required = false, defaultValue = "Medium") String size,
            @RequestParam(required = false, defaultValue = "true") boolean isCold,
            @RequestParam(required = false, defaultValue = "false") boolean hasCaffeine,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!"admin".equals(session.getAttribute("userRole"))) {
            return "redirect:/user/login";
        }

        MenuItem item;
        if ("beverage".equals(itemType)) {
            // Create a Beverage subclass object
            item = new Beverage(0, name, basePrice, category, available, imageUrl,
                                size, isCold, hasCaffeine);
        } else {
            // Create a FoodItem subclass object (default)
            item = new FoodItem(0, name, basePrice, category, available, imageUrl,
                                cuisineType != null ? cuisineType : "Local",
                                packagingFee, isSpicy);
        }

        menuService.add(item);
        redirectAttributes.addFlashAttribute("success", "Menu item '" + name + "' added.");
        return "redirect:/admin/menu";
    }

    // ============================================================
    //  READ — Edit form
    // ============================================================

    /**
     * GET /admin/menu/edit/{id} — shows the edit form pre-filled with item data.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model, HttpSession session) {
        if (!"admin".equals(session.getAttribute("userRole"))) {
            return "redirect:/user/login";
        }
        MenuItem item = menuService.findById(id);
        if (item == null) {
            return "redirect:/admin/menu";
        }
        model.addAttribute("item", item);
        model.addAttribute("isFoodItem", item instanceof FoodItem);
        model.addAttribute("isBeverage", item instanceof Beverage);
        return "menu-management";
    }

    // ============================================================
    //  UPDATE — Save edited item
    // ============================================================

    /**
     * POST /admin/menu/update — saves changes to an existing menu item.
     */
    @PostMapping("/update")
    public String updateItem(
            @RequestParam int id,
            @RequestParam String itemType,
            @RequestParam String name,
            @RequestParam double basePrice,
            @RequestParam String category,
            @RequestParam(defaultValue = "false") boolean available,
            @RequestParam(defaultValue = "") String imageUrl,
            @RequestParam(required = false) String cuisineType,
            @RequestParam(required = false, defaultValue = "0") double packagingFee,
            @RequestParam(required = false, defaultValue = "false") boolean isSpicy,
            @RequestParam(required = false, defaultValue = "Medium") String size,
            @RequestParam(required = false, defaultValue = "true") boolean isCold,
            @RequestParam(required = false, defaultValue = "false") boolean hasCaffeine,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!"admin".equals(session.getAttribute("userRole"))) {
            return "redirect:/user/login";
        }

        MenuItem item;
        if ("beverage".equals(itemType)) {
            item = new Beverage(id, name, basePrice, category, available, imageUrl,
                                size, isCold, hasCaffeine);
        } else {
            item = new FoodItem(id, name, basePrice, category, available, imageUrl,
                                cuisineType != null ? cuisineType : "Local",
                                packagingFee, isSpicy);
        }

        menuService.update(item);
        redirectAttributes.addFlashAttribute("success", "Menu item updated successfully.");
        return "redirect:/admin/menu";
    }

    // ============================================================
    //  DELETE — Remove item
    // ============================================================

    /**
     * POST /admin/menu/delete/{id} — removes a menu item permanently.
     */
    @PostMapping("/delete/{id}")
    public String deleteItem(@PathVariable int id, HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!"admin".equals(session.getAttribute("userRole"))) {
            return "redirect:/user/login";
        }
        menuService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Menu item deleted.");
        return "redirect:/admin/menu";
    }

    /**
     * POST /admin/menu/toggle/{id} — toggles item availability on/off.
     */
    @PostMapping("/toggle/{id}")
    public String toggleAvailability(@PathVariable int id, HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        if (!"admin".equals(session.getAttribute("userRole"))) {
            return "redirect:/user/login";
        }
        MenuItem item = menuService.findById(id);
        if (item != null) {
            item.setAvailable(!item.isAvailable());
            menuService.update(item);
        }
        redirectAttributes.addFlashAttribute("success", "Item availability updated.");
        return "redirect:/admin/menu";
    }
}
