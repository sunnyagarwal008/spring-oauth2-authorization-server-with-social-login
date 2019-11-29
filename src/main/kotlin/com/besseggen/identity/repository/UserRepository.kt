package com.besseggen.identity.repository

import com.besseggen.identity.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Int> {

    fun findByEmail(email: String?): User?
}