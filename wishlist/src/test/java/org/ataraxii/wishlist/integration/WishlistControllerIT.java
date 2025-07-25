package org.ataraxii.wishlist.integration;

import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class WishlistControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class AuthorizedTests {

        private String sessionId;

        @BeforeEach
        void auth() throws Exception {
            String json = """
                            {
                                "username": "testuser",
                                "password": "testpassword"
                            }
                    """;

            mockMvc.perform(post("/api/v1/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated());

            MvcResult result = mockMvc.perform(post("/api/v1/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andReturn();

            sessionId = result.getResponse().getCookie("sessionId").getValue();
        }

        @Test
        void createWishlist_success() throws Exception {
            String wishlistJson = """
                            {
                                "name": "test-wishlist"
                            }
                    """;

            mockMvc.perform(post("/api/v1/wishlists")
                            .cookie(new Cookie("sessionId", sessionId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(wishlistJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("test-wishlist"));
        }
    }

    @Nested
    class UnauthorizedTests {

        @Test
        void createWishlist_success() throws Exception {
            String wishlistJson = """
                            {
                                "name": "test-wishlist"
                            }
                    """;

            mockMvc.perform(post("/api/v1/wishlists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(wishlistJson))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Сессия не найдена"));
        }
    }
}
