package com.besseggen.identity.model

import java.io.Serializable
import java.util.Date
import java.util.HashSet
import javax.persistence.*

@Entity
@Table(name = "user", catalog = "identity", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("email"))])
data class User constructor(
        @Id
        @Column(name = "id", unique = true, nullable = false)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column(name = "email", unique = true, nullable = false, length = 256)
        val email: String,

        @Column(name = "password", nullable = false, length = 256)
        val password: String,

        @Column(name = "enabled", nullable = false)
        val enabled: Boolean,

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "last_login_time", length = 26)
        val lastLoginTime: Date? = null,

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "password_change_date", length = 26)
        val passwordChangeDate: Date? = null,

        @Column(name = "failed_login_attempts", nullable = false)
        val failedLoginAttempts: Int = 0,

        @Column(name = "last_accessed_from", length = 256)
        val lastAccessedFrom: String? = null,

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "created", nullable = false, length = 26)
        val created: Date,

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "updated", nullable = false, length = 26, updatable = false, insertable = false)
        val updated: Date
) {
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = [CascadeType.ALL])
    val userRoles: Set<UserRole> = HashSet(0)
}