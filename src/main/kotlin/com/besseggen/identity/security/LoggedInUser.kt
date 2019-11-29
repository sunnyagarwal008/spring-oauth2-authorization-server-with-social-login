package com.besseggen.identity.security

import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class LoggedInUser(private val username: String, private val password: String, private val enabled: Boolean, private val roles: List<String>) : UserDetails, CredentialsContainer {

    private val authorities: MutableSet<GrantedAuthority> = mutableSetOf()

    init {
        roles.forEach {
            val authority = Authority(it)
            authorities.add(authority)
        }
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    override fun getUsername(): String {
        return username
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        return password
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun eraseCredentials() {

    }
}