package com.olafros.live.model

import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Entity
@Table(name = "fixture_events")
data class FixtureEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var minute: @Min(0) @Max(90) Int,
    var type: @NotNull EFixtureEvent,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fixture_id", nullable = false)
    @JsonBackReference
    var fixture: @NotNull Fixture,

    var text: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id")
    @JsonBackReference
    var player1: Player? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id")
    @JsonBackReference
    var player2: Player? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @JsonBackReference
    var team: Team? = null,
)

data class Participants(
    var team: TeamDtoList? = null,
    var goalScorer: PlayerDtoList? = null,
    var assistant: PlayerDtoList? = null,
    var playerIn: PlayerDtoList? = null,
    var playerOut: PlayerDtoList? = null,
    var player: PlayerDtoList? = null,
)

data class FixtureEventDto(
    val id: Long,
    val minute: Int,
    val type: EFixtureEvent,
    val fixture: FixtureDtoList,
    val text: String? = null,
    val participants: Participants
)

data class FixtureEventDtoList(
    val id: Long,
    val minute: Int,
    val type: EFixtureEvent,
    val text: String? = null,
    val participants: Participants
)

data class CreateFixtureEventDto(
    val minute: Int,
    val type: EFixtureEvent,
    val text: String?,
    val player1: Long?,
    val player2: Long?,
    val team: Long?
)

data class UpdateFixtureEventDto(
    val minute: Int?,
    val type: EFixtureEvent?,
    val text: String?,
    val player1: Long?,
    val player2: Long?,
    val team: Long?
)

fun FixtureEvent.getParticipants(): Participants {
    val participants = Participants(this.team?.toTeamDtoList())
    if (this.type == EFixtureEvent.GOAL) {
        participants.goalScorer = this.player1?.toPlayerDtoList()
        participants.assistant = this.player2?.toPlayerDtoList()
    } else if (this.type == EFixtureEvent.SUBSTITUTION) {
        participants.playerIn = this.player1?.toPlayerDtoList()
        participants.playerOut = this.player2?.toPlayerDtoList()
    } else if (
        this.type == EFixtureEvent.RED_CARD ||
        this.type == EFixtureEvent.YELLOW_CARD ||
        this.type == EFixtureEvent.FOUL
    ) {
        participants.player = this.player1?.toPlayerDtoList()
    }
    return participants
}

fun FixtureEvent.toFixtureEventDto() = FixtureEventDto(
    this.id,
    this.minute,
    this.type,
    this.fixture.toFixtureDtoList(),
    this.text, this.getParticipants()
)

fun FixtureEvent.toFixtureEventDtoList() = FixtureEventDtoList(
    this.id,
    this.minute,
    this.type,
    this.text,
    this.getParticipants()
)
