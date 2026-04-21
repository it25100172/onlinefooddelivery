package com.fooddelivery.service;

import com.fooddelivery.interfaces.Manageable;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;
import com.fooddelivery.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderService — CRUD operations for Order and OrderItem.
 *
 * Orders are stored in orders.txt (one line per order).
 * Order items are stored in order_items.txt (format: orderId|item1~item2~item3).
 *
 * Member 3 and 4 are responsible for this service.
 */
@Service
public class OrderService implements Manageable<Order, Integer> {

    private static final String ORDERS_FILE      = "orders.txt";
    private static final String ORDER_ITEMS_FILE = "order_items.txt";

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private FileUtil fileUtil;

    // ============================================================
    //  CREATE
    // ============================================================

    /**
     * Saves a new order to orders.txt and its items to order_items.txt.
     * Auto-assigns ID and sets order date to current time.
     *
     * @param order the order to save
     */
    @Override
    public void add(Order order) {
        // Auto-assign ID and timestamp
        order.setId(fileUtil.getNextId(ORDERS_FILE));
        order.setOrderDate(LocalDateTime.now().format(FORMATTER));
        order.setStatus(Order.STATUS_PENDING);

        // Save the order header to orders.txt
        fileUtil.appendLine(ORDERS_FILE, order.toFileString());

        // Save order items to order_items.txt
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            String itemsLine = order.getId() + "|"
                + order.getItems().stream()
                    .map(OrderItem::toFileString)
                    .collect(Collectors.joining(","));
            fileUtil.appendLine(ORDER_ITEMS_FILE, itemsLine);
        }
    }

    // ============================================================
    //  READ
    // ============================================================

    @Override
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        for (String line : fileUtil.readAllLines(ORDERS_FILE)) {
            Order order = Order.fromFileString(line);
            order.setItems(findItemsForOrder(order.getId()));
            orders.add(order);
        }
        return orders;
    }

    /** Returns orders for a specific customer */
    public List<Order> findByCustomerId(int customerId) {
        return findAll().stream()
            .filter(o -> o.getCustomerId() == customerId)
            .collect(Collectors.toList());
    }

    /** Returns orders by status */
    public List<Order> findByStatus(String status) {
        return findAll().stream()
            .filter(o -> o.getStatus().equalsIgnoreCase(status))
            .collect(Collectors.toList());
    }

    @Override
    public Order findById(Integer id) {
        return findAll().stream()
            .filter(o -> o.getId() == id)
            .findFirst()
            .orElse(null);
    }

    /** Reads order items for a given order ID from order_items.txt */
    private List<OrderItem> findItemsForOrder(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        for (String line : fileUtil.readAllLines(ORDER_ITEMS_FILE)) {
            String[] parts = line.split("\\|", 2);
            if (parts.length == 2 && Integer.parseInt(parts[0]) == orderId) {
                // Items are comma-separated
                for (String itemStr : parts[1].split(",")) {
                    items.add(OrderItem.fromFileString(itemStr));
                }
            }
        }
        return items;
    }

    // ============================================================
    //  UPDATE
    // ============================================================

    /** Updates an order's status (most common update operation) */
    public void updateStatus(int orderId, String newStatus) {
        Order order = findById(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            update(order);
        }
    }

    /** Assigns a delivery person to an order */
    public void assignDeliveryPerson(int orderId, int deliveryPersonId) {
        Order order = findById(orderId);
        if (order != null) {
            order.setDeliveryPersonId(deliveryPersonId);
            update(order);
        }
    }

    @Override
    public void update(Order updated) {
        List<String> lines = fileUtil.readAllLines(ORDERS_FILE);
        List<String> updatedLines = lines.stream()
            .map(line -> {
                String[] parts = line.split("\\|");
                return Integer.parseInt(parts[0]) == updated.getId()
                    ? updated.toFileString()
                    : line;
            })
            .collect(Collectors.toList());
        fileUtil.writeAllLines(ORDERS_FILE, updatedLines);
    }

    // ============================================================
    //  DELETE
    // ============================================================

    @Override
    public void delete(Integer id) {
        // Delete from orders.txt
        List<String> orders = fileUtil.readAllLines(ORDERS_FILE).stream()
            .filter(line -> Integer.parseInt(line.split("\\|")[0]) != id)
            .collect(Collectors.toList());
        fileUtil.writeAllLines(ORDERS_FILE, orders);

        // Delete from order_items.txt
        List<String> items = fileUtil.readAllLines(ORDER_ITEMS_FILE).stream()
            .filter(line -> Integer.parseInt(line.split("\\|")[0]) != id)
            .collect(Collectors.toList());
        fileUtil.writeAllLines(ORDER_ITEMS_FILE, items);
    }

    /** Returns the count of orders by status — used for the dashboard */
    public long countByStatus(String status) {
        return findAll().stream()
            .filter(o -> o.getStatus().equalsIgnoreCase(status))
            .count();
    }
}
