package com.example.springsecurityjwttutorial.domain

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
class User(
    @Column(unique = true)
    val name: String,

    @Column
    val password: String,

    @Column
    @Enumerated(EnumType.STRING)
    val role: Role,
) {
    @Id
    val id: UUID = UUID.randomUUID()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

enum class Role {
    ADMIN,
    USER
}
