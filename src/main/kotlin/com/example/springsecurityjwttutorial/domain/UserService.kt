package com.example.springsecurityjwttutorial.domain

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(private val userRepository: UserRepository) {
    fun getByName(name: String): User = userRepository.findByName(name).orElseThrow()
    fun getUsers(): List<User> = userRepository.findAll()
    fun getById(id: UUID): User = userRepository.findById(id).orElseThrow()
}
