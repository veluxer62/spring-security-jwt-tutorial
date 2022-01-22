package com.example.springsecurityjwttutorial.config

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationProvider(private val jwtProcessor: JwtProcessor) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication? {
        if (!supports(authentication::class.java)) return null

        val principal = jwtProcessor.getPrincipal(authentication.principal.toString())
        return JwtUserAuthenticationToken(principal)
    }

    override fun supports(authentication: Class<*>): Boolean =
        authentication == JwtPreAuthenticationToken::class.java
}
