package com.example.springsecurityjwttutorial.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.time.Clock
import java.util.Date
import java.util.UUID

@Component
class JwtProcessor(private val jwtProperties: JwtProperties) {
    fun generateToken(principal: UserPrincipal): String {
        val nowMillis = Clock.systemDefaultZone().millis()
        val issuedAt = Date(nowMillis)
        val expiration = Date(nowMillis + jwtProperties.expiration)

        return Jwts.builder()
            .setClaims(principal.claims)
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS256, jwtProperties.secret)
            .compact()
    }

    fun getPrincipal(token: String): UserPrincipal {
        assert(token)
        return generatePrincipal(token)
    }

    private fun assert(token: String) {
        val claims = getClaims(token)

        if (claims.isExpired())
            throw CredentialsExpiredException("token is expired")

        val isValid = claims.containsKey("user_id") &&
            claims.containsKey("user_name") &&
            claims.containsKey("authorities")

        if (!isValid)
            throw BadCredentialsException("token invalid")
    }

    private fun generatePrincipal(token: String): UserPrincipal {
        val claims = getClaims(token)
        val id = claims.parse<String>("user_id").let { UUID.fromString(it) }
        val name = claims.parse<String>("user_name")
        val authorities = claims.get("authorities", List::class.java)
            .map { SimpleGrantedAuthority(it.toString()) }

        return UserPrincipal(
            id = id,
            name = name,
            authorities = authorities,
        )
    }

    private fun Claims.isExpired(): Boolean {
        val expiration = this.get(Claims.EXPIRATION, Date::class.java)
        val now = Date(Clock.systemDefaultZone().millis())
        return expiration != null && expiration.before(now)
    }

    private inline fun <reified T> Claims.parse(key: String): T =
        this.get(key, T::class.java)
            ?: throw BadCredentialsException("\"$key\"does not exist")

    private fun getClaims(token: String): Claims =
        try {
            Jwts.parser().setSigningKey(jwtProperties.secret).parseClaimsJws(token).body
        } catch (e: JwtException) {
            throw BadCredentialsException(e.message)
        }
}
