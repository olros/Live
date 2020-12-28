package com.olafros.live.model

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
data class Game(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        @get: NotBlank
        val title: String = "",

        @get: NotBlank
        val referee: String = "",

        @NotBlank
        val extra: String = "",

        @Column(columnDefinition = "varchar(255) default 'John Snow'")
        val extra2: @NotBlank @Size(max = 255) String,

        @get: NotBlank
        val time: String = ""
)