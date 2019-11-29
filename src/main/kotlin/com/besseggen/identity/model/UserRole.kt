package com.besseggen.identity.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "user_role", catalog = "identity")
data class UserRole constructor(
        @Id
        @Column(name = "id", unique = true, nullable = false)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int,

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "role_id", nullable = false)
        val role: Role,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        val user: User
)