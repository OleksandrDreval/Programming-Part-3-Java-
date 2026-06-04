package ua.nure.ice.bookcatalog.laboratorna3.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
                "application", "Book Catalog API",
                "description", "REST API for managing book catalog and orders",
                "authentication", "No authentication is required for this endpoint"
        );
    }
}
