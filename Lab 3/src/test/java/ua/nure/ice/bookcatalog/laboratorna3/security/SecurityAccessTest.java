package ua.nure.ice.bookcatalog.laboratorna3.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ua.nure.ice.bookcatalog.laboratorna3.app.App;
import ua.nure.ice.bookcatalog.laboratorna3.model.Book;
import ua.nure.ice.bookcatalog.laboratorna3.model.BookGenre;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = App.class)
@AutoConfigureMockMvc
class SecurityAccessTest {

    private static final String USER_LOGIN = "user";
    private static final String USER_PASSWORD = "user123";
    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void publicInfoWithoutAuthentication_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/public/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application").value("Book Catalog API"));
    }

    @Test
    void booksWithoutAuthentication_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void booksWithUserCredentials_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/books")
                        .with(httpBasic(USER_LOGIN, USER_PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    void createBookWithUserCredentials_ReturnsCreated() throws Exception {
        Book bookRequest = new Book(0L, "Security Essentials", "Jane Doe", 2024, BookGenre.EDUCATION);

        mockMvc.perform(post("/api/books")
                        .with(httpBasic(USER_LOGIN, USER_PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void adminUsersWithoutAuthentication_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminUsersWithUserCredentials_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .with(httpBasic(USER_LOGIN, USER_PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUsersWithAdminCredentials_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .with(httpBasic(ADMIN_LOGIN, ADMIN_PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    void currentUserInfoWithUserCredentials_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/me")
                        .with(httpBasic(USER_LOGIN, USER_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USER_LOGIN))
                .andExpect(jsonPath("$.authenticated").value(true));
    }

    @Test
    void adminStatsWithUserCredentials_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/stats")
                        .with(httpBasic(USER_LOGIN, USER_PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminStatsWithAdminCredentials_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/admin/stats")
                        .with(httpBasic(ADMIN_LOGIN, ADMIN_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(2))
                .andExpect(jsonPath("$.securityMode").value("HTTP Basic with role-based authorization"));
    }
}
