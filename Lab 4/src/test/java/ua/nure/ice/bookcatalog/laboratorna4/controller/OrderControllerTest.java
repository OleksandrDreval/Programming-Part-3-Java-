package ua.nure.ice.bookcatalog.laboratorna4.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import ua.nure.ice.bookcatalog.laboratorna4.model.Book;
import ua.nure.ice.bookcatalog.laboratorna4.model.BookGenre;
import ua.nure.ice.bookcatalog.laboratorna4.model.order.BookOrder;
import ua.nure.ice.bookcatalog.laboratorna4.model.order.DeliveryAddress;
import ua.nure.ice.bookcatalog.laboratorna4.model.order.OrderStatus;
import ua.nure.ice.bookcatalog.laboratorna4.service.OrderService;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookOrder validOrder;

    @BeforeEach
    void setUp() {
        Book book = new Book(1L, "Test Title", "Author", 2023, BookGenre.FICTION);

        DeliveryAddress address = new DeliveryAddress.Builder("Country", "City", "Street", "1")
                .postalCode("00000")
                .build();

        validOrder = new BookOrder.Builder("John Doe")
                .books(List.of(book))
                .deliveryAddress(address)
                .status(OrderStatus.NEW)
                .build();
    }

    @Test
    void getAllOrders_returnsOk() throws Exception {
        when(orderService.findAllOrders()).thenReturn(List.of(validOrder));
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("John Doe"));
    }

    @Test
    void getOrderById_found_returnsOk() throws Exception {
        when(orderService.findOrderById(1L)).thenReturn(validOrder);
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("John Doe"));
    }

    @Test
    void getOrderById_notFound_returns404() throws Exception {
        when(orderService.findOrderById(2L)).thenThrow(new IllegalArgumentException("Not found"));
        mockMvc.perform(get("/api/orders/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrder_valid_returnsCreated() throws Exception {
        when(orderService.createOrder(any(BookOrder.class))).thenReturn(validOrder);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validOrder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("John Doe"));
    }

    @Test
    void createOrder_conflict_returnsConflict() throws Exception {
        when(orderService.createOrder(any(BookOrder.class))).thenThrow(new IllegalStateException("Conflict"));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validOrder)))
                .andExpect(status().isConflict());
    }

    @Test
    void createOrder_invalid_returnsBadRequest() throws Exception {
        when(orderService.createOrder(any(BookOrder.class))).thenThrow(new IllegalArgumentException("Invalid"));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validOrder)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrderStatus_success_returnsOk() throws Exception {
        when(orderService.updateOrderStatus(anyLong(), any(OrderStatus.class))).thenReturn(validOrder);

        mockMvc.perform(put("/api/orders/1/status")
                .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("John Doe"));
    }

    @Test
    void updateOrderStatus_notFound_returns404() throws Exception {
        when(orderService.updateOrderStatus(anyLong(), any(OrderStatus.class)))
                .thenThrow(new IllegalArgumentException("Not found"));

        mockMvc.perform(put("/api/orders/1/status")
                .param("status", "APPROVED"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrderStatus_conflict_returnsConflict() throws Exception {
        when(orderService.updateOrderStatus(anyLong(), any(OrderStatus.class)))
                .thenThrow(new IllegalStateException("Conflict"));

        mockMvc.perform(put("/api/orders/1/status")
                .param("status", "APPROVED"))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteOrder_success_returnsNoContent() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteOrder_exception_returnsNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Not found")).when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isNotFound());
    }
}
