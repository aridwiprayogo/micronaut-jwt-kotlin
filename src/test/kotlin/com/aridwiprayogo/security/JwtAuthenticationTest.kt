package com.aridwiprayogo.security

import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.SignedJWT
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken
import io.micronaut.test.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import javax.inject.Inject

@MicronautTest
class JwtAuthenticationTest {
    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `accessing a secured url without authenticating returns unauthorized`() {
        val executable = Executable {
            client.toBlocking().exchange<Any, Any>(HttpRequest.GET<Any>("/").accept(MediaType.TEXT_PLAIN))
        }
        val thrown = assertThrows(HttpClientResponseException::class.java, executable)
        assertEquals(thrown.status, HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun uponSuccessfulAuthenticationAJsonWebTokenIsIssuedToTheUser() {
        val credentials = UsernamePasswordCredentials("aridwiprayogo", "password")
        val request: HttpRequest<Any> = HttpRequest.POST("/login", credentials)
        val httpResponse: HttpResponse<BearerAccessRefreshToken> = client.toBlocking().exchange(request, BearerAccessRefreshToken::class.java)
        assertEquals(HttpStatus.OK, httpResponse.status)
        val bearerAccessRefreshToken: BearerAccessRefreshToken? = httpResponse.body()
        assertEquals("aridwiprayogo", bearerAccessRefreshToken?.username)
        assertNotNull(bearerAccessRefreshToken?.accessToken)

        assertTrue(JWTParser.parse(bearerAccessRefreshToken?.accessToken) is SignedJWT)
        val accessToken: String? = bearerAccessRefreshToken?.accessToken
        val response: HttpResponse<String> = client.toBlocking().exchange(HttpRequest.GET<Any>("/").accept(MediaType.TEXT_PLAIN).bearerAuth(accessToken), String::class.java)
        assertEquals(HttpStatus.OK, httpResponse.status)
        assertEquals("aridwiprayogo", response.body())
    }
}