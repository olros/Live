package com.olafros.live.model

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "players")
data class Player(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: @NotBlank @Size(max = 128) String,
    var position: @NotBlank EPosition,
    var number: @Size(max = 128) Int?,
    var active: @NotBlank Boolean,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    @JsonBackReference
    var team: Team,
)

data class PlayerDto(
    val id: Long,
    val name: String,
    val position: EPosition,
    val number: Int?,
    val active: Boolean,
    val team: TeamDtoList,
)

data class PlayerDtoList(val id: Long, val name: String, val position: EPosition, val number: Int?, val active: Boolean)
data class CreatePlayerDto(val name: String, val position: EPosition, val number: Int?, val active: Boolean)
data class UpdatePlayerDto(val name: String?, val position: EPosition?, val number: Int?, val active: Boolean?)

fun Player.toPlayerDto() =
    PlayerDto(this.id, this.name, this.position, this.number, this.active, this.team.toTeamDtoList())

fun Player.toPlayerDtoList() = PlayerDtoList(this.id, this.name, this.position, this.number, this.active)
