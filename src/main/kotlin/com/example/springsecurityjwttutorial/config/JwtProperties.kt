package com.example.springsecurityjwttutorial.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("custom.jwt")
data class JwtProperties(
    val secret: String,
    val expiration: Long,
)
