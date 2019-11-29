package com.besseggen.identity.configuration

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationDetailsSource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetailsSource
import org.springframework.security.oauth2.provider.authentication.TokenExtractor
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class OAuth2PreAuthenticationFilter(private val authenticationManager: AuthenticationManager) : OncePerRequestFilter() {

    private val authenticationEntryPoint: AuthenticationEntryPoint = OAuth2AuthenticationEntryPoint()
    private val authenticationDetailsSource: AuthenticationDetailsSource<HttpServletRequest, *> = OAuth2AuthenticationDetailsSource()
    private val tokenExtractor: TokenExtractor = BearerTokenExtractor()
    private val stateless = true

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val debug = logger.isDebugEnabled
        try {
            val authentication = tokenExtractor.extract(request)
            if (authentication == null) {
                if (stateless && isAuthenticated()) {
                    if (debug) {
                        logger.debug("Clearing security context.")
                    }
                }
                if (debug) {
                    logger.debug("No token in request, will continue chain.")
                }
            } else {
                request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE, authentication.principal)
                if (authentication is AbstractAuthenticationToken) {
                    authentication.details = authenticationDetailsSource.buildDetails(request)
                }
                val authResult = authenticationManager.authenticate(authentication)
                if (debug) {
                    logger.debug("Authentication success: $authResult")
                }
                SecurityContextHolder.getContext().authentication = authResult
            }
        } catch (failed: OAuth2Exception) {
            SecurityContextHolder.clearContext()
            if (debug) {
                logger.debug("Authentication request failed: $failed")
            }
            authenticationEntryPoint.commence(request, response, InsufficientAuthenticationException(failed.message, failed))
            return
        }
        filterChain.doFilter(request, response)
    }

    private fun isAuthenticated(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        return !(authentication == null || authentication is AnonymousAuthenticationToken)
    }

    override fun destroy() {}
}