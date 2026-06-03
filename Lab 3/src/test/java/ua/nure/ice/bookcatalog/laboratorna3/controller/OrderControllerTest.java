package ua.nure.ice.bookcatalog.laboratorna3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ua.nure.ice.bookcatalog.laboratorna3.model.Book;
import ua.nure.ice.bookcatalog.laboratorna3.model.BookGenre;
import ua.nure.ice.bookcatalog.laboratorna3.model.order.BookOrder;
import ua.nure.ice.bookcatalog.laboratorna3.model.order.OrderStatus;
import ua.nure.ice.bookcatalog.laboratorna3.service.OrderService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ua.nure.ice.bookcatalog.laboratorna3.app.App;

@WebMvcTest(controllers = OrderController.class)
@ContextConfiguration(classes = App.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllOrders_ShouldReturnOrders() throws Exception {
        BookOrder order = new BookOrder.Builder("ORD-1", "Alice").build();
        when(orderService.findAllOrders()).thenReturn(Collections.singletonList(order));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value("ORD-1"));
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenFound() throws Exception {
        BookOrder order = new BookOrder.Builder("ORD-1", "Alice").build();
        when(orderService.findOrderById("ORD-1")).thenReturn(order);

        mockMvc.perform(get("/api/orders/ORD-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Alice"));
    }

    @Test
    void getOrderById_ShouldReturn404_WhenNotFound() throws Exception {
        when(orderService.findOrderById("UNKNOWN")).thenThrow(new IllegalArgumentException("Not found"));

        mockMvc.perform(get("/api/orders/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrder_ShouldReturn201() throws Exception {
        BookOrder order = new BookOrder.Builder("ORD-1", "Alice").build();
        when(orderService.createOrder(any(BookOrder.class))).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("ORD-1"));
    }

    @Test
    void createOrder_ShouldReturn409_OnConflict() throws Exception {
        when(orderService.createOrder(any(BookOrder.class))).thenThrow(new IllegalStateException("Conflict"));

        BookOrder order = new BookOrder.Builder("ORD-1", "Alice").build();

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isConflict());
    }

    @Test
    void createOrder_ShouldReturn400_OnBadRequest() throws Exception {
        when(orderService.createOrder(any(BookOrder.class))).thenThrow(new IllegalArgumentException("Bad Request"));

        BookOrder order = new BookOrder.Builder("ORD-1", "Alice").build();

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrderStatus_ShouldReturn200() throws Exception {
        BookOrder updated = new BookOrder.Builder("ORD-1", "Alice").status(OrderStatus.APPROVED).build();
        when(orderService.updateOrderStatus("ORD-1", OrderStatus.APPROVED)).thenReturn(updated);

        mockMvc.perform(put("/api/orders/ORD-1/status")
                .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void updateOrderStatus_ShouldReturn404_WhenNotFound() throws Exception {
        when(orderService.updateOrderStatus("UNKNOWN", OrderStatus.APPROVED))
                .thenThrow(new IllegalArgumentException("Not found"));

        mockMvc.perform(put("/api/orders/UNKNOWN/status")
                .param("status", "APPROVED"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrderStatus_ShouldReturn409_WhenStateConflict() throws Exception {
        when(orderService.updateOrderStatus("ORD-1", OrderStatus.APPROVED))
                .thenThrow(new IllegalStateException("Conflict"));

        mockMvc.perform(put("/api/orders/ORD-1/status")
                .param("status", "APPROVED"))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteOrder_ShouldReturn204() throws Exception {
        doNothing().when(orderService).deleteOrder("ORD-1");

        mockMvc.perform(delete("/api/orders/ORD-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteOrder_ShouldReturn404_WhenNotFound() throws Exception {
        doThrow(new IllegalArgumentException("Not found")).when(orderService).deleteOrder("UNKNOWN");

        mockMvc.perform(delete("/api/orders/UNKNOWN"))
                .andExpect(status().isNotFound());
    }
}
