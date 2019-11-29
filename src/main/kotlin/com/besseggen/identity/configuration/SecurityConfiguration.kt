package com.besseggen.identity.configuration

import com.besseggen.identity.repository.UserRepository
import com.besseggen.identity.security.LoggedInUser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider

@EnableWebSecurity
@Configuration
class SecurityConfiguration(private val userRepository: UserRepository,
                            private val clientDetailsService: ClientDetailsService,
                            private val tokenStore: TokenStore,
                            private val accessTokenConverter: JwtAccessTokenConverter) : WebSecurityConfigurerAdapter() {

    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity
                .rememberMe().key("besseggenRem")
                .userDetailsService(userDetailsService()).and()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated()
                .and().cors().and()
                .formLogin()
                .and()
                .oauth2Login()

        httpSecurity
                .addFilterBefore(oauth2AuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }

    fun oauth2AuthenticationFilter(): OAuth2PreAuthenticationFilter {
        return OAuth2PreAuthenticationFilter(oauthAuthenticationManager())
    }

    @Bean
    override fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            val user = userRepository.findByEmail(username)
            if (user != null) {
                LoggedInUser(username = user.email, password = user.password, enabled = user.enabled, roles = user.userRoles.map { it.role.name })
            } else {
                throw UsernameNotFoundException("invalid username/email: $username")
            }
        }
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    private fun oauthAuthenticationManager(): AuthenticationManager {
        val oauthAuthenticationManager = OAuth2AuthenticationManager()
        oauthAuthenticationManager.setResourceId("oauth2-resource")
        oauthAuthenticationManager.setTokenServices(defaultTokenServices())
        oauthAuthenticationManager.setClientDetailsService(clientDetailsService)
        return oauthAuthenticationManager
    }

    private fun defaultTokenServices(): DefaultTokenServices {
        val tokenServices = DefaultTokenServices()
        tokenServices.setTokenStore(tokenStore)
        tokenServices.setSupportRefreshToken(true)
        tokenServices.setReuseRefreshToken(true)
        tokenServices.setClientDetailsService(clientDetailsService)
        tokenServices.setTokenEnhancer(accessTokenConverter)
        addUserDetailsService(tokenServices, userDetailsService())
        return tokenServices
    }

    private fun addUserDetailsService(tokenServices: DefaultTokenServices, userDetailsService: UserDetailsService?) {
        val provider = PreAuthenticatedAuthenticationProvider()
        provider.setPreAuthenticatedUserDetailsService(UserDetailsByNameServiceWrapper(userDetailsService))
        tokenServices.setAuthenticationManager(ProviderManager(listOf<AuthenticationProvider>(provider)))
    }
}

fun main() {
    print(BCryptPasswordEncoder().encode("noonewilleverguess"))
}