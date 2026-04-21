package com.fooddelivery.controller;

import com.fooddelivery.model.MenuItem;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;
import com.fooddelivery.service.MenuService;
import com.fooddelivery.service.OrderService;
import com.fooddelivery.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * OrderController — Handles Place Order (Page 3) and Order Management (Page 4).
 *
 * Member 3 handles Place Order page.
 * Member 4 handles Order Management (admin) page.
 */
@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    // ============================================================
    //  PAGE 3 — Place Order
    // ============================================================

    /**
     * GET /order/place — shows the place order form with the full menu.
     * Requires the user to be logged in.
     */
    @GetMapping("/order/place")
    public String showPlaceOrder(Model model, HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/user/login";
        }
        // Pass available menu items to the order page
        // OOP: menuItems list is polymorphic — contains FoodItem and Beverage objects as MenuItem
        List<MenuItem> menuItems = menuService.findAvailable();
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("loggedUser", session.getAttribute("loggedUser"));
        return "place-order";
    }

    /**
     * POST /order/place — creates a new order from the submitted form.
     *
     * @param itemIds        array of selected menu item IDs
     * @param quantities     array of quantities matching itemIds
     * @param deliveryAddress the address to deliver to
     * @param specialNote    optional note for the kitchen
     */
    @PostMapping("/order/place")
    public String placeOrder(
            @RequestParam(value = "itemIds", required = false) int[] itemIds,
            @RequestParam(value = "quantities", required = false) int[] quantities,
            @RequestParam String deliveryAddress,
            @RequestParam(required = false) String specialNote,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/user/login";
        }

        if (itemIds == null || itemIds.length == 0) {
            redirectAttributes.addFlashAttribute("error", "Please select at least one item.");
            return "redirect:/order/place";
        }

        // Build the Order object
        com.fooddelivery.model.User loggedUser =
            (com.fooddelivery.model.User) session.getAttribute("loggedUser");

        Order order = new Order();
        order.setCustomerId(loggedUser.getId());
        order.setCustomerName(loggedUser.getName());
        order.setDeliveryAddress(deliveryAddress);
        order.setSpecialNote(specialNote);
        order.setDeliveryPersonId(0); // not yet assigned

        // Build order items list
        List<OrderItem> items = new ArrayList<>();
        double total = 0;

        for (int i = 0; i < itemIds.length; i++) {
            MenuItem menuItem = menuService.findById(itemIds[i]);
            if (menuItem != null && quantities[i] > 0) {
                // calculatePrice() uses polymorphism — calls correct subclass version
                double price = menuItem.calculatePrice();
                OrderItem orderItem = new OrderItem(
                    menuItem.getId(),
                    menuItem.getName(),
                    quantities[i],
                    price
                );
                items.add(orderItem);
                total += price * quantities[i];
            }
        }

        order.setItems(items);
        order.setTotalAmount(total);

        // Save via OrderService (CREATE operation)
        orderService.add(order);

        redirectAttributes.addFlashAttribute("success",
            "Order #" + order.getId() + " placed successfully! Total: Rs." +
            String.format("%.2f", total));
        return "redirect:/order/my-orders";
    }

    // ============================================================
    //  Customer: My Orders
    // ============================================================

    /**
     * GET /order/my-orders — shows the logged-in customer's orders.
     */
    @GetMapping("/order/my-orders")
    public String myOrders(Model model, HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/user/login";
        }
        com.fooddelivery.model.User user =
            (com.fooddelivery.model.User) session.getAttribute("loggedUser");
        List<Order> myOrders = orderService.findByCustomerId(user.getId());
        model.addAttribute("orders", myOrders);
        model.addAttribute("loggedUser", user);
        return "my-orders";
    }

    /**
     * POST /order/cancel/{id} — customer cancels their own order.
     */
    @PostMapping("/order/cancel/{id}")
    public String cancelOrder(@PathVariable int id, HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/user/login";
        }
        orderService.updateStatus(id, Order.STATUS_CANCELLED);
        redirectAttributes.addFlashAttribute("success", "Order #" + id + " cancelled.");
        return "redirect:/order/my-orders";
    }

    // ============================================================
    //  PAGE 4 — Admin: Order Management
    // ============================================================

    /**
     * GET /admin/orders — shows all orders for admin management.
     */
    @GetMapping("/admin/orders")
    public String adminOrders(
            @RequestParam(required = false) String status,
            Model model, HttpSession session) {

        if (!"admin".equals(session.getAttribute("userRole"))) {
            return "redirect:/user/login";
        }

        List<Order> orders = (status != null && !status.isEmpty())
            ? orderService.findByStatus(status)
            : orderService.findAll();

        // Dashboard counts for summary cards
        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("countPending",   orderService.countByStatus(Order.STATUS_PENDING));
        model.addAttribute("countConfirmed", orderService.countByStatus(Order.STATUS_CONFIRMED));
        model.addAttribute("countOut",       orderService.countByStatus(Order.STATUS_OUT));
        model.addAttribute("countDelivered", orderService.countByStatus(Order.STATUS_DELIVERED));
        model.addAttribute("deliveryPersons", userService.findAvailableDeliveryPersons());
        model.addAttribute("allStatuses", new String[]{
            Order.STATUS_PENDING, Order.STATUS_CONFIRMED,
            Order.STATUS_PREPARING, Order.STATUS_OUT,
            Order.STATUS_DELIVERED, Order.STATUS_CANCELLED
        });
        return "order-management";
    }

    /**
     * POST /admin/orders/update-status — admin updates an order's status.
     */
    @PostMapping("/admin/orders/update-status")
    public String updateOrderStatus(
            @RequestParam int orderId,
            @RequestParam String newStatus,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!"admin".equals(session.getAttribute("userRole"))) {
            return "redirect:/user/login";
        }
        orderService.updateStatus(orderId, newStatus);
        redirectAttributes.addFlashAttribute("success",
            "Order #" + orderId + " status updated to: " + newStatus);
        return "redirect:/admin/orders";
    }

    /**
     * POST /admin/orders/assign-delivery — assigns a delivery person to an order.
     */
    @PostMapping("/admin/orders/assign-delivery")
    public String assignDelivery(
            @RequestParam int orderId,
            @RequestParam int deliveryPersonId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!"admin".equals(session.getAttribute("userRole"))) {
            return "redirect:/user/login";
        }
        orderService.assignDeliveryPerson(orderId, deliveryPersonId);
        redirectAttributes.addFlashAttribute("success",
            "Delivery person assigned to Order #" + orderId);
        return "redirect:/admin/orders";
    }

    /**
     * POST /admin/orders/delete/{id} — admin deletes (cancels) an order.
     */
    @PostMapping("/admin/orders/delete/{id}")
    public String deleteOrder(@PathVariable int id, HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!"admin".equals(session.getAttribute("userRole"))) {
            return "redirect:/user/login";
        }
        orderService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Order #" + id + " deleted.");
        return "redirect:/admin/orders";
    }
}
