package com.example.springsecurityjwttutorial.presentation

import com.example.springsecurityjwttutorial.domain.Role
import com.example.springsecurityjwttutorial.domain.User
import java.util.UUID

data class UserDto(
    val id: UUID,
    val name: String,
    val role: Role,
) {
    constructor(user: User) : this(
        id = user.id,
        name = user.name,
        role = user.role
    )
}
