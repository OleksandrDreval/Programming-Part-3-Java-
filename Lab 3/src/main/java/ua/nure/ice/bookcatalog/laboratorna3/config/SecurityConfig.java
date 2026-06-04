package ua.nure.ice.bookcatalog.laboratorna3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ROLE_USER = "USER";
    private static final String ROLE_ADMIN = "ADMIN";

    private static final String DEFAULT_USER_LOGIN = "user";
    private static final String DEFAULT_USER_PASSWORD = "user123";
    private static final String DEFAULT_ADMIN_LOGIN = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/books/**").hasAnyRole(ROLE_USER, ROLE_ADMIN)
                        .requestMatchers("/api/orders/**").hasAnyRole(ROLE_USER, ROLE_ADMIN)
                        .requestMatchers("/api/admin/stats").authenticated()
                        .requestMatchers("/api/admin/**").hasRole(ROLE_ADMIN)
                        .requestMatchers("/api/me").authenticated()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails regularUser = User.withUsername(DEFAULT_USER_LOGIN)
                .password(passwordEncoder.encode(DEFAULT_USER_PASSWORD))
                .roles(ROLE_USER)
                .build();

        UserDetails adminUser = User.withUsername(DEFAULT_ADMIN_LOGIN)
                .password(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD))
                .roles(ROLE_ADMIN)
                .build();

        return new InMemoryUserDetailsManager(regularUser, adminUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
