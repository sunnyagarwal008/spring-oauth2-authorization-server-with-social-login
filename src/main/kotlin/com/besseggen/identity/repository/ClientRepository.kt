package com.besseggen.identity.repository

import com.besseggen.identity.model.Client
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClientRepository : JpaRepository<Client, Int> {

    fun findByClientId(clientId: String): Client?
}