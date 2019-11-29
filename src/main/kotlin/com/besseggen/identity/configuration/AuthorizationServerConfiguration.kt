package com.besseggen.identity.configuration

import com.besseggen.identity.repository.ClientRepository
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.Order
import org.springframework.core.io.ClassPathResource
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.ClientRegistrationException
import org.springframework.security.oauth2.provider.client.BaseClientDetails
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory
import java.security.KeyPair
import java.security.interfaces.RSAPublicKey
import javax.sql.DataSource

@EnableAuthorizationServer
@Import(AuthorizationServerEndpointsConfiguration::class)
@Configuration
@Order(3)
class AuthorizationServerConfiguration(
        private val dataSource: DataSource,
        private val clientRepository: ClientRepository) : AuthorizationServerConfigurerAdapter() {

    companion object {
        private val KEY_STORE_FILE = "besseggen-jwt.jks"
        private val KEY_STORE_PASSWORD = "besseggen-pass"
        private val KEY_ALIAS = "besseggen-oauth-jwt"
        private val JWK_KID = "besseggen-key-id"
    }

    @Bean
    fun keyPair(): KeyPair {
        val ksFile = ClassPathResource(KEY_STORE_FILE)
        val ksFactory = KeyStoreKeyFactory(ksFile, KEY_STORE_PASSWORD.toCharArray())
        return ksFactory.getKeyPair(KEY_ALIAS)
    }

    @Bean
    fun jwkSet(): JWKSet {
        val builder =
                RSAKey.Builder(keyPair().public as RSAPublicKey)
                        .keyUse(KeyUse.SIGNATURE)
                        .algorithm(JWSAlgorithm.RS256)
                        .keyID(JWK_KID)
        return JWKSet(builder.build())
    }

    @Bean
    fun tokenStore(): TokenStore {
        return JwtTokenStore(accessTokenConverter())
    }

    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter {
        val converter = JwtAccessTokenConverter()
        converter.setKeyPair(keyPair())
        return converter
    }

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.withClientDetails(clientDetailsService())
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
    }

    override fun configure(security: AuthorizationServerSecurityConfigurer) {
        security
                .checkTokenAccess("isAuthenticated()")
    }

    private fun clientDetailsService(): ClientDetailsService {
        return ClientDetailsService { clientId ->
            val client = clientRepository.findByClientId(clientId)
            if (client != null) {
                val clientDetails = BaseClientDetails()
                clientDetails.clientId = client.clientId
                clientDetails.clientSecret = client.secret
                clientDetails.setAuthorizedGrantTypes(client.authorizationGrantTypes.split(','))
                clientDetails.setScope(client.scopes.split(','))
                clientDetails.registeredRedirectUri = client.redirectUris.split(',').toMutableSet()
                if (client.autoApprovedScopes != null) {
                    clientDetails.setAutoApproveScopes(client.autoApprovedScopes.split(','))
                }
                clientDetails
            } else {
                throw ClientRegistrationException("$clientId not found")
            }
        }
    }
}