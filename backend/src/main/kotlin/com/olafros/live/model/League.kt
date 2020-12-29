package com.olafros.live.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "leagues")
data class League(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0,
        var name: @NotBlank @Size(max = 120) String,

        @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
        @JoinTable(
                name = "league_admins",
                joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "league_id", referencedColumnName = "id")]
        )
//        @field:JsonIgnore
        var admins: MutableSet<User> = mutableSetOf()
)