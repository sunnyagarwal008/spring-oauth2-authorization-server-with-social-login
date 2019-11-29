package com.besseggen.identity.security

import org.springframework.security.core.GrantedAuthority

data class Authority(private val role: String) : GrantedAuthority {

    override fun getAuthority(): String {
        return role
    }
}