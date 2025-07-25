package org.ataraxii.wishlist.integration;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class RegisterTests {
        @Test
        void registerUser_successfully() throws Exception {
            String json = """
                            {
                                "username": "testuser",
                                "password": "testpassword"
                            }
                    """;

            mockMvc.perform(post("/api/v1/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.id").isNotEmpty());
        }

        @Test
        void registerUser_failUsernameAlreadyExists() throws Exception {
            String json = """
                            {
                                "username": "duplicateduser",
                                "password": "testpassword"
                            }
                    """;
            // Первая регистрация
            mockMvc.perform(post("/api/v1/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated());

            // Повторная регистрация с таким же именем
            mockMvc.perform(post("/api/v1/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Пользователь с таким именем уже существует"));
        }
    }

    @Nested
    class LoginTests {

        @BeforeEach
        void createUser() throws Exception {

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
        }

        @Test
        void loginUser_successfully() throws Exception {

            String loginJson = """
                            {
                                "username": "testuser",
                                "password": "testpassword"
                            }
                    """;

            mockMvc.perform(post("/api/v1/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        void loginUser_failedUsernameNotFound() throws Exception {
            String loginJson = """
                            {
                                "username": "incorrectuser",
                                "password": "testpassword"
                            }
                    """;

            mockMvc.perform(post("/api/v1/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Пользователь с таким именем не найден"));
        }

        @Test
        void loginUser_failedWrongPassword() throws Exception {

            String loginJson = """
                            {
                                "username": "testuser",
                                "password": "wrongpassword"
                            }
                    """;

            mockMvc.perform(post("/api/v1/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Неверный пароль"));
        }
    }
}
