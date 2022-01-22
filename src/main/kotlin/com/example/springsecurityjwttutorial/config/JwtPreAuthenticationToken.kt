package com.example.springsecurityjwttutorial.config

import org.springframework.security.authentication.AbstractAuthenticationToken

class JwtPreAuthenticationToken(
    private val token: String,
) : AbstractAuthenticationToken(null) {
    override fun getPrincipal() = token
    override fun getCredentials() = null
}
