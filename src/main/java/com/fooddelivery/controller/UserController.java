package com.fooddelivery.controller;

import com.fooddelivery.model.Customer;
import com.fooddelivery.model.User;
import com.fooddelivery.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * UserController — Handles user registration and login (Page 2).
 *
 * Member 1 is responsible for this controller.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // ============================================================
    //  SHOW FORMS
    // ============================================================

    /** GET /user/login — show login form */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    /** GET /user/register — show registration form */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "login"; // login.html has both forms
    }

    // ============================================================
    //  PROCESS REGISTRATION
    // ============================================================

    /**
     * POST /user/register — creates a new Customer account.
     * Redirects to login with a success message.
     */
    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String phone,
            @RequestParam String deliveryAddress,
            RedirectAttributes redirectAttributes) {

        // Check if email already exists
        if (userService.findByEmail(email) != null) {
            redirectAttributes.addFlashAttribute("error", "Email already registered. Please login.");
            return "redirect:/user/login";
        }

        // Create new Customer (OOP: using the Customer subclass)
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setPhone(phone);
        customer.setDeliveryAddress(deliveryAddress);
        customer.setTotalOrders(0);
        customer.setRole("customer");

        userService.add(customer);

        redirectAttributes.addFlashAttribute("success", "Account created! Please login.");
        return "redirect:/user/login";
    }

    // ============================================================
    //  PROCESS LOGIN
    // ============================================================

    /**
     * POST /user/login — authenticates user and stores in session.
     */
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (userService.authenticate(email, password)) {
            User user = userService.findByEmail(email);
            // Store user in session for subsequent pages
            session.setAttribute("loggedUser", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userRole", user.getRole());

            // Redirect based on role
            if ("admin".equals(user.getRole())) {
                return "redirect:/admin/orders";
            }
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
        return "redirect:/user/login";
    }

    // ============================================================
    //  LOGOUT
    // ============================================================

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ============================================================
    //  ADMIN: USER MANAGEMENT
    // ============================================================

    /** GET /user/admin/list — admin view of all users */
    @GetMapping("/admin/list")
    public String listUsers(Model model, HttpSession session) {
        if (!"admin".equals(session.getAttribute("userRole"))) {
            return "redirect:/user/login";
        }
        model.addAttribute("users", userService.findAll());
        model.addAttribute("deliveryPersons", userService.findAllDeliveryPersons());
        return "admin-users";
    }

    /** POST /user/admin/delete/{id} — deletes a user */
    @PostMapping("/admin/delete/{id}")
    public String deleteUser(@PathVariable int id, HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!"admin".equals(session.getAttribute("userRole"))) {
            return "redirect:/user/login";
        }
        userService.delete(id);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully.");
        return "redirect:/user/admin/list";
    }
}
