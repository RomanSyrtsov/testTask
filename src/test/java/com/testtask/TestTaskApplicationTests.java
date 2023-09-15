package com.testtask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testtask.entity.Order;
import com.testtask.entity.Product;
import com.testtask.repo.OrderRepository;
import com.testtask.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class TestTaskApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private OrderRepository orderRepository;

    @Test
    @WithMockUser(username = "user", password = "1234", roles = "CLIENT")
    public void testMarkOrderAsPaidSuccess() throws Exception {
        Long orderId = 1L;
        Order mockOrder = new Order();
        mockOrder.setId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/orders/" + orderId + "/pay")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Order has been successfully paid."));
    }

    @Test
    @WithMockUser(username = "user", password = "1234", roles = "CLIENT")
    public void testMarkOrderAsPaidFailure() throws Exception {
        Long orderId = 2L;
        OrderService orderService = mock(OrderService.class);
        when(orderService.markOrderAsPaid(orderId)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/orders/" + orderId + "/pay")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Payment failed. Please try again."));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "MANAGER")
    public void testManagerEndpoint() throws Exception {
        String json = objectMapper.writeValueAsString(new Product("Iphone 5", 6.99, 24));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "user", password = "1234", roles = "CLIENT")
    public void testClientEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
