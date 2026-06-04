package ua.nure.ice.bookcatalog.laboratorna3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ua.nure.ice.bookcatalog.laboratorna3.model.Book;
import ua.nure.ice.bookcatalog.laboratorna3.model.BookGenre;
import ua.nure.ice.bookcatalog.laboratorna3.service.CatalogService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ua.nure.ice.bookcatalog.laboratorna3.app.App;
import ua.nure.ice.bookcatalog.laboratorna3.config.SecurityConfig;

@WebMvcTest(controllers = BookController.class)
@ContextConfiguration(classes = App.class)
@Import(SecurityConfig.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogService catalogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void getAllBooks_ShouldReturnBooks() throws Exception {
        Book book = new Book(1L, "1984", "George Orwell", 1949, BookGenre.SCIENCE_FICTION);
        when(catalogService.findAllBooks()).thenReturn(Collections.singletonList(book));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("1984"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getBookById_ShouldReturnBook_WhenFound() throws Exception {
        Book book = new Book(1L, "1984", "George Orwell", 1949, BookGenre.SCIENCE_FICTION);
        when(catalogService.findAllBooks()).thenReturn(Collections.singletonList(book));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("1984"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getBookById_ShouldReturn404_WhenIdDoesNotMatch() throws Exception {
        Book book = new Book(2L, "1984", "George Orwell", 1949, BookGenre.SCIENCE_FICTION);
        when(catalogService.findAllBooks()).thenReturn(Collections.singletonList(book));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getBookById_ShouldReturn404_WhenNotFound() throws Exception {
        when(catalogService.findAllBooks()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createBook_ShouldReturn201() throws Exception {
        Book newBook = new Book(1L, "1984", "George Orwell", 1949, BookGenre.SCIENCE_FICTION);
        when(catalogService.addBook(anyString(), anyString(), anyInt(), any(BookGenre.class)))
                .thenReturn(newBook);

        Book requestBook = new Book(0L, "1984", "George Orwell", 1949, BookGenre.SCIENCE_FICTION);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createBook_ShouldReturn400_OnError() throws Exception {
        when(catalogService.addBook(anyString(), anyString(), anyInt(), any(BookGenre.class)))
                .thenThrow(new IllegalArgumentException("Invalid"));

        Book requestBook = new Book(0L, "Valid Title", "Valid Author", 1949, BookGenre.SCIENCE_FICTION);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBook)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateBook_ShouldReturn200() throws Exception {
        Book updatedBook = new Book(2L, "1984", "George Orwell", 1949, BookGenre.SCIENCE_FICTION);
        doNothing().when(catalogService).removeBook(1L);
        when(catalogService.addBook(anyString(), anyString(), anyInt(), any(BookGenre.class)))
                .thenReturn(updatedBook);

        Book requestBook = new Book(0L, "1984", "George Orwell", 1949, BookGenre.SCIENCE_FICTION);

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateBook_ShouldReturn404_WhenRemoveFails() throws Exception {
        doThrow(new IllegalArgumentException("Not found")).when(catalogService).removeBook(1L);

        Book requestBook = new Book(0L, "1984", "George Orwell", 1949, BookGenre.SCIENCE_FICTION);

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateBook_ShouldReturn404_WhenAddFails() throws Exception {
        doNothing().when(catalogService).removeBook(1L);
        when(catalogService.addBook(anyString(), anyString(), anyInt(), any(BookGenre.class)))
                .thenThrow(new IllegalArgumentException("Invalid"));

        Book requestBook = new Book(0L, "1984", "George Orwell", 1949, BookGenre.SCIENCE_FICTION);

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteBook_ShouldReturn204() throws Exception {
        doNothing().when(catalogService).removeBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteBook_ShouldReturn404_WhenFails() throws Exception {
        doThrow(new IllegalArgumentException("Not found")).when(catalogService).removeBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNotFound());
    }
}
