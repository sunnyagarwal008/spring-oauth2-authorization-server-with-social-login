package com.besseggen.identity.model

import java.io.Serializable
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
@Table(name = "role", catalog = "identity", uniqueConstraints = [UniqueConstraint(columnNames = arrayOf("name"))])
data class Role constructor(
        @Id
        @Column(name = "id", unique = true, nullable = false)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column(name = "name", unique = true, nullable = false, length = 45)
        val name: String,

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "created", nullable = false, length = 26)
        val created: Date,

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "updated", nullable = false, length = 26, updatable = false, insertable = false)
        val updated: Date
)