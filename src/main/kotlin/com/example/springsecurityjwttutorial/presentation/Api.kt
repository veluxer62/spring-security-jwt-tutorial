package com.example.springsecurityjwttutorial.presentation

import com.example.springsecurityjwttutorial.config.JwtProcessor
import com.example.springsecurityjwttutorial.config.JwtUserAuthenticationToken
import com.example.springsecurityjwttutorial.config.UserPrincipal
import com.example.springsecurityjwttutorial.domain.UserService
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class Api(
    private val passwordEncoder: PasswordEncoder,
    private val userService: UserService,
    private val jwtProcessor: JwtProcessor,
) {
    @PostMapping("/login")
    fun login(@RequestBody data: LoginCommand): ResponseEntity<Unit> {
        val user = try {
            userService.getByName(data.username)
        } catch (e: NoSuchElementException) {
            throw UsernameNotFoundException("user not found")
        }

        if (!passwordEncoder.matches(data.password, user.password))
            throw BadCredentialsException("password not matched")

        val token = jwtProcessor.generateToken(UserPrincipal(user))

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .build()
    }

    @GetMapping("/api/users")
    fun users(principal: Principal): ResponseEntity<ItemsDto<UserDto>> {
        return userService.getUsers()
            .map { UserDto(it) }
            .let { ItemsDto(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/api/me")
    fun user(principal: Principal): ResponseEntity<UserDto> {
        val userPrincipal = (principal as JwtUserAuthenticationToken).principal
        val user = userService.getById(userPrincipal.id)
        return UserDto(user)
            .let { ResponseEntity.ok(it) }
    }
}
