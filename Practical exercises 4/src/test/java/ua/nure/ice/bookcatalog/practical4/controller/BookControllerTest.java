package ua.nure.ice.bookcatalog.practical4.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.nure.ice.bookcatalog.practical4.exception.BookNotFoundException;
import ua.nure.ice.bookcatalog.practical4.exception.InvalidBookDataException;
import ua.nure.ice.bookcatalog.practical4.model.Book;
import ua.nure.ice.bookcatalog.practical4.model.BookGenre;
import ua.nure.ice.bookcatalog.practical4.service.CatalogService;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogService catalogService;

    @Autowired
    private ObjectMapper objectMapper;

    private Book validBook;

    @BeforeEach
    void setUp() {
        validBook = new Book(1L, "Test Title", "Test Author", 2023, BookGenre.FICTION);
    }

    @Test
    void getAllBooks_returnsOk() throws Exception {
        when(catalogService.findAllBooks()).thenReturn(List.of(validBook));
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"));
    }

    @Test
    void getBookById_found_returnsOk() throws Exception {
        when(catalogService.findAllBooks()).thenReturn(List.of(validBook));
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    void getBookById_notFound_returns404() throws Exception {
        when(catalogService.findAllBooks()).thenReturn(List.of(validBook));
        mockMvc.perform(get("/api/books/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBook_valid_returnsCreated() throws Exception {
        when(catalogService.addBook(anyString(), anyString(), anyInt(), any(BookGenre.class))).thenReturn(validBook);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    void createBook_invalid_returnsBadRequest() throws Exception {
        when(catalogService.addBook(anyString(), anyString(), anyInt(), any(BookGenre.class)))
                .thenThrow(new InvalidBookDataException("Invalid"));

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validBook)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBook_success_returnsOk() throws Exception {
        when(catalogService.updateBook(anyLong(), anyString(), anyString(), anyInt(), any(BookGenre.class)))
                .thenReturn(validBook);

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validBook)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    void updateBook_notFound_returns404() throws Exception {
        when(catalogService.updateBook(anyLong(), anyString(), anyString(), anyInt(), any(BookGenre.class)))
                .thenThrow(new BookNotFoundException("Not found"));

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validBook)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBook_invalid_returnsBadRequest() throws Exception {
        when(catalogService.updateBook(anyLong(), anyString(), anyString(), anyInt(), any(BookGenre.class)))
                .thenThrow(new InvalidBookDataException("Invalid"));

        mockMvc.perform(put("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validBook)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBook_success_returnsNoContent() throws Exception {
        doNothing().when(catalogService).removeBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_exception_returnsNotFound() throws Exception {
        doThrow(new BookNotFoundException("Not found")).when(catalogService).removeBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNotFound());
    }
}
