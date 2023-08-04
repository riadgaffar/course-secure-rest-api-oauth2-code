package example.cashcard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CashCardApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DirtiesContext
    @WithMockUser(username = "sarah1")
    void shouldCreateANewCashCard() throws Exception {
        mockMvc.perform(post("/cashcards")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                 {
                                     "amount" : 250.00,
                                     "owner" : "sarah1"
                                 }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getHeader("Location");
    }

    @Test
    @WithMockUser(username = "sarah1")
    void shouldReturnAllCashCardsWhenListIsRequested() throws Exception {
        mockMvc.perform(get("/cashcards")
                        .with(jwt().jwt((jwt) -> jwt.subject("sarah1"))))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$..id").value(containsInAnyOrder(99, 100, 101)))
                .andExpect(jsonPath("$..amount").value(containsInAnyOrder(123.45, 1.00, 150.00)));
    }
}