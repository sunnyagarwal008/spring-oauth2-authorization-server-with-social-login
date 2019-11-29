package com.besseggen.identity.model

import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.UniqueConstraint

@Entity
@Table(name = "client", catalog = "identity", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("client_id"))])
data class Client constructor(
        @Id
        @Column(name = "id", unique = true, nullable = false)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column(name = "client_id", unique = true, nullable = false, length = 256)
        val clientId: String,

        @Column(name = "secret", nullable = false, length = 256)
        val secret: String,

        @Column(name = "authorization_grant_types", nullable = false, length = 256)
        val authorizationGrantTypes: String,

        @Column(name = "scopes", nullable = false, length = 256)
        val scopes: String,

        @Column(name = "auto_approved_scopes", nullable = true, length = 256)
        val autoApprovedScopes: String?,

        @Column(name = "redirect_uris", nullable = false, length = 256)
        val redirectUris: String,

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "created", nullable = false, length = 26)
        val created: Date,

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "updated", nullable = false, length = 26, updatable = false, insertable = false)
        val updated: Date
)