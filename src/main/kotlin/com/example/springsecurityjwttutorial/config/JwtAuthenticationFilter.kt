package com.example.springsecurityjwttutorial.config

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtAuthenticationFilter(
    private val http: HttpSecurity,
    processUrl: String,
) : AbstractAuthenticationProcessingFilter(processUrl) {
    init {
        super.setAuthenticationSuccessHandler { _, response, _ ->
            response.status = HttpServletResponse.SC_OK
        }
    }

    override fun getAuthenticationManager(): AuthenticationManager =
        http.getSharedObject(AuthenticationManager::class.java)

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val token = extractToken(request)
        val authentication = JwtPreAuthenticationToken(token)
        return this.authenticationManager.authenticate(authentication)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        super.successfulAuthentication(request, response, chain, authResult)
        chain.doFilter(request, response)
    }

    private fun extractToken(request: HttpServletRequest): String {
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
        val tokenParseable = !authorization.isNullOrEmpty() && authorization.startsWith("Bearer ")

        return if (tokenParseable)
            authorization.substring(7)
        else
            throw AuthenticationCredentialsNotFoundException("authorization invalid")
    }
}
