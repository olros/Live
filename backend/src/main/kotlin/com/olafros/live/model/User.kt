package com.olafros.live.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = ["email"])])
class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0,
        var name: @NotBlank @Size(max = 120) String,
        var email: @NotBlank @Size(max = 60) @Email String,
        @field:JsonIgnore
        var password: @NotBlank @Size(max = 120) String,

        @ManyToMany(mappedBy = "admins", fetch = FetchType.LAZY)
        @field:JsonIgnore
        var leagues: MutableSet<League> = mutableSetOf()
)
