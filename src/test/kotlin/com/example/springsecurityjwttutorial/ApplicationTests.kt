package com.example.springsecurityjwttutorial

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.eq

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTests {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun test_login_api_returns_authorization_token() {
        val requestBody = """
            {
                "username": "user1",
                "password": "password1"
            }
        """.trimIndent()

        mvc
            .post("/login") {
                header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                content = requestBody
            }
            .andExpect { status { isOk() } }
            .andExpect { header { this.exists(HttpHeaders.AUTHORIZATION) } }
    }

    @Test
    fun test_login_api_when_user_not_exist() {
        val requestBody = """
            {
                "username": "user3",
                "password": "password1"
            }
        """.trimIndent()

        mvc
            .post("/login") {
                header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                content = requestBody
            }
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun test_login_api_when_password_not_matched() {
        val requestBody = """
            {
                "username": "user1",
                "password": "1"
            }
        """.trimIndent()

        mvc
            .post("/login") {
                header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                content = requestBody
            }
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun test_users_api_require_authorization() {
        mvc.get("/api/users")
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun test_users_api_return_users_when_authorization_is_exists() {
        val token = generateToken()
        mvc
            .get("/api/users") {
                header(HttpHeaders.AUTHORIZATION, token)
            }
            .andExpect { status { isOk() } }
            .andExpect {
                content { jsonPath("$.items") { isNotEmpty() } }
            }
    }

    @Test
    fun test_users_api_return_unauthorized_if_token_expired() {
        val fixBeforeExpiration = Clock.fixed(Instant.ofEpochMilli(915116400000), ZoneId.systemDefault())
        val token: String

        mockStatic(Clock::class.java).use {
            `when`(Clock.systemDefaultZone()).thenReturn(fixBeforeExpiration)
            token = generateToken()
        }

        mvc
            .get("/api/users") {
                header(HttpHeaders.AUTHORIZATION, token)
            }
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun test_me_api_return_login_user() {
        val token = generateToken()
        mvc
            .get("/api/me") {
                header(HttpHeaders.AUTHORIZATION, token)
            }
            .andExpect { status { isOk() } }
            .andExpect {
                content {
                    jsonPath("$.name") { eq("user1") }
                    jsonPath("$.role") { eq("USER") }
                }
            }
    }

    private fun generateToken(): String {
        return mvc
            .post("/login") {
                header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                content = """
                        {
                            "username": "user1",
                            "password": "password1"
                        }
                """.trimIndent()
            }
            .andReturn()
            .response
            .getHeader(HttpHeaders.AUTHORIZATION)!!
    }
}
