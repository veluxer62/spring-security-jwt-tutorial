package com.example.springsecurityjwttutorial.config

import com.example.springsecurityjwttutorial.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.UUID

class UserPrincipal(
    val id: UUID,
    val name: String,
    val authorities: Collection<GrantedAuthority>,
) {
    constructor(user: User) : this(
        id = user.id,
        name = user.name,
        authorities = setOf(SimpleGrantedAuthority(user.role.name))
    )

    val claims: Map<String, Any> get() = mapOf(
        "user_id" to id,
        "user_name" to name,
        "authorities" to authorities.map { it.authority }
    )
}
