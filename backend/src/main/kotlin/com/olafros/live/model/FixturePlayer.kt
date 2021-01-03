package com.olafros.live.model

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "fixture_players")
data class FixturePlayer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    @JsonBackReference
    var player: @NotNull Player,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fixture_id", nullable = false)
    @JsonBackReference
    var fixture: @NotNull Fixture,

    var number: @Size(max = 128) Int?,
    var position: @NotNull EPosition,
)

data class FixturePlayerDto(
    val id: Long,
    val player: PlayerDto,
    val fixture: FixtureDtoList,
    val number: Int?,
    val position: EPosition
)

data class FixturePlayerDtoList(val id: Long, val player: PlayerDtoList, val number: Int?, val position: EPosition)
data class CreateFixturePlayerDto(val playerId: Long, val number: Int?, val position: EPosition?)
data class UpdateFixturePlayerDto(val number: Int?, val position: EPosition?)

fun FixturePlayer.toFixturePlayerDto() =
    FixturePlayerDto(this.id, this.player.toPlayerDto(), this.fixture.toFixtureDtoList(), this.number, this.position)

fun FixturePlayer.toFixturePlayerDtoList() =
    FixturePlayerDtoList(this.id, this.player.toPlayerDtoList(), this.number, this.position)
