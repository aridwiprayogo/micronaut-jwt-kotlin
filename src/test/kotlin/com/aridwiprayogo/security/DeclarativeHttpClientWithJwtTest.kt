package com.aridwiprayogo.security

import com.aridwiprayogo.client.AppClient
import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.SignedJWT
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Test
import javax.inject.Inject

import org.junit.jupiter.api.Assertions.*

@MicronautTest
class DeclarativeHttpClientWithJwtTest {
    @Inject
    lateinit var appClient: AppClient

    @Test
    fun `verify JWT authentication works with declarative client`(){
        val credentials =
                UsernamePasswordCredentials("aridwiprayogo", "password")
        val loginResponse: BearerAccessRefreshToken = appClient.login(credentials)

        assertNotNull(loginResponse)
        assertNotNull(loginResponse.accessToken)
        assertTrue(JWTParser.parse(loginResponse.accessToken) is SignedJWT)
        val message = appClient.home("Bearer ${loginResponse.accessToken}")

        assertEquals("aridwiprayogo", message)
    }

}