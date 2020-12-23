package com.olafros.live.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
data class Game (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @get: NotBlank
    val title: String = "",

    @get: NotBlank
    val referee: String = "",

    @get: NotBlank
    val time: String = ""
)