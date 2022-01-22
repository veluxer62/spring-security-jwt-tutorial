package com.example.springsecurityjwttutorial.config

import org.springframework.security.authentication.AbstractAuthenticationToken

class JwtUserAuthenticationToken(
    private val principal: UserPrincipal,
) : AbstractAuthenticationToken(principal.authorities) {
    init {
        super.setAuthenticated(true)
    }

    override fun getPrincipal() = principal
    override fun getCredentials() = null
}
