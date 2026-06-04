package ua.nure.ice.bookcatalog.laboratorna3.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.nure.ice.bookcatalog.laboratorna3.service.CatalogService;
import ua.nure.ice.bookcatalog.laboratorna3.service.OrderService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final int TOTAL_DEMO_USERS = 2;

    private final CatalogService catalogService;
    private final OrderService orderService;

    public AdminController(CatalogService catalogService, OrderService orderService) {
        this.catalogService = catalogService;
        this.orderService = orderService;
    }

    @GetMapping("/users")
    public List<Map<String, String>> getUsers() {
        return List.of(
                Map.of("username", "user", "role", "ROLE_USER"),
                Map.of("username", "admin", "role", "ROLE_ADMIN")
        );
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getStats() {
        return Map.of(
                "totalUsers", TOTAL_DEMO_USERS,
                "totalBooks", catalogService.findAllBooks().size(),
                "totalOrders", orderService.findAllOrders().size(),
                "securityMode", "HTTP Basic with role-based authorization"
        );
    }
}
