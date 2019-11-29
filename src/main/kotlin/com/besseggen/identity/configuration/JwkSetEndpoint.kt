package com.besseggen.identity.configuration

import com.nimbusds.jose.jwk.JWKSet
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@FrameworkEndpoint
class JwkSetEndpoint(private val jwkSet: JWKSet) {

    @GetMapping("/.well-known/jwks.json")
    @ResponseBody
    fun getKey(): Map<String, Any>? {
        return jwkSet.toJSONObject()
    }
}