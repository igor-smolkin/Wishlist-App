package org.ataraxii.wishlist.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import org.ataraxii.wishlist.dto.wishlist.WishlistResponseDto;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ItemControllerIT {

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

        UUID createWishlist() throws Exception {
            String wishlistJson = """
                            {
                                "name": "test-wishlist"
                            }
                    """;

            MvcResult result = mockMvc.perform(post("/api/v1/wishlists")
                            .cookie(new Cookie("sessionId", sessionId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(wishlistJson))
                    .andReturn();

            String json = result.getResponse().getContentAsString();
            WishlistResponseDto responseDto = new ObjectMapper().readValue(json, WishlistResponseDto.class);
            return responseDto.getId();
        }

        @Test
        void createItem_withoutWishlist_success() throws Exception {
            String jsonItem = """
                            {
                                "name": "testname",
                                "url": "testurl"
                            }
                    """;

            mockMvc.perform(post("/api/v1/items")
                            .cookie(new Cookie("sessionId", sessionId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonItem))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("testname"));
        }

        @Test
        void createItem_withWishlist_success() throws Exception {

            UUID wishlistId = createWishlist();

            String jsonItem = """
                            {
                                "name": "testname",
                                "url": "testurl",
                                "wishlistId": "%s"
                            }
                    """.formatted(wishlistId.toString());

            mockMvc.perform(post("/api/v1/items")
                            .cookie(new Cookie("sessionId", sessionId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonItem))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("testname"))
                    .andExpect(jsonPath("$.wishlistId").value(wishlistId.toString()));
        }

        @Test
        void createItem_withWrongWishlist_wishlistNotFound() throws Exception {

            UUID wrongWishlistId = UUID.randomUUID();

            String jsonItem = """
                            {
                                "name": "testname",
                                "url": "testurl",
                                "wishlistId": "%s"
                            }
                    """.formatted(wrongWishlistId.toString());

            mockMvc.perform(post("/api/v1/items")
                            .cookie(new Cookie("sessionId", sessionId))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonItem))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Вишлист не найден"));
        }
    }

    @Nested
    class UnauthorizedTests {

        @Test
        void createItem_withoutWishlist_returnUnauthorized() throws Exception {
            String jsonItem = """
                            {
                                "name": "testname",
                                "url": "testurl"
                            }
                    """;

            mockMvc.perform(post("/api/v1/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonItem))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Сессия не найдена"));
        }

        @Test
        void createItem_withWrongWishlist_returnUnauthorized() throws Exception {

            UUID wrongWishlistId = UUID.randomUUID();

            String jsonItem = """
                            {
                                "name": "testname",
                                "url": "testurl",
                                "wishlistId": "%s"
                            }
                    """.formatted(wrongWishlistId.toString());

            mockMvc.perform(post("/api/v1/items")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonItem))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Сессия не найдена"));
        }
    }
}
