package com.example.springsecurityjwttutorial

import com.example.springsecurityjwttutorial.domain.Role
import com.example.springsecurityjwttutorial.domain.User
import com.example.springsecurityjwttutorial.domain.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Component
class StartUpCommand(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : CommandLineRunner {
    @Transactional
    override fun run(vararg args: String?) {
        val users = listOf(
            User("user1", passwordEncoder.encode("password1"), Role.USER),
            User("user2", passwordEncoder.encode("password2"), Role.ADMIN),
        )
        userRepository.saveAll(users)
    }
}
