package ua.nure.ice.bookcatalog.laboratorna3.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoController {

    @GetMapping("/api/me")
    public Map<String, Object> currentUserInfo() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();

        return Map.of(
                "username", authentication.getName(),
                "authenticated", authentication.isAuthenticated(),
                "authorities", authorities
        );
    }
}
