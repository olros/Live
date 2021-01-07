package com.olafros.live.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import java.time.OffsetDateTime
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "fixtures")
data class Fixture(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var location: @Size(max = 128) String?,
    var referee: @Size(max = 128) String?,
    var time: @NotNull OffsetDateTime,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "season_id", nullable = false)
    @JsonBackReference
    var season: @NotNull Season,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "home_team_id", nullable = false)
    var homeTeam: @NotNull Team,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "away_team_id", nullable = false)
    var awayTeam: @NotNull Team,

    @OneToMany(mappedBy = "fixture", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonManagedReference
    var players: MutableList<FixturePlayer> = mutableListOf(),

    @OneToMany(mappedBy = "fixture", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonManagedReference
    var events: MutableList<FixtureEvent> = mutableListOf(),
)

data class FixtureDto(
    val id: Long,
    val location: String?,
    val referee: String?,
    val time: OffsetDateTime,
    val homeTeam: TeamDtoList,
    val awayTeam: TeamDtoList,
    val season: SeasonDtoList,
    val players: List<FixturePlayerDtoList>,
    val result: FixtureResult
)

data class FixtureDtoList(
    val id: Long,
    val time: OffsetDateTime,
    val homeTeam: TeamDtoList,
    val awayTeam: TeamDtoList,
    val result: FixtureResult
)

data class CreateFixtureDto(
    val location: String?,
    val referee: String?,
    val time: OffsetDateTime,
    val homeTeam: Long,
    val awayTeam: Long,
    val seasonId: Long,
)

data class UpdateFixtureDto(
    val location: String?,
    val referee: String?,
    val time: OffsetDateTime?,
    val homeTeam: Long?,
    val awayTeam: Long?,
)

data class FixtureResult(val homeTeam: Int, val awayTeam: Int)

fun Fixture.getResult(): FixtureResult {
    var homeTeam = 0
    var awayTeam = 0
    this.events.filter { fixtureEvent -> fixtureEvent.type == EFixtureEvent.GOAL }.forEach { fixtureEvent ->
        run {
            if (fixtureEvent.team?.id == this.homeTeam.id) homeTeam++
            if (fixtureEvent.team?.id == this.awayTeam.id) awayTeam++
        }
    }
    return FixtureResult(homeTeam, awayTeam)
}

fun Fixture.toFixtureDto() =
    FixtureDto(
        this.id,
        this.location,
        this.referee,
        this.time,
        this.homeTeam.toTeamDtoList(),
        this.awayTeam.toTeamDtoList(),
        this.season.toSeasonDtoList(),
        this.players.map { player -> player.toFixturePlayerDtoList() },
        this.getResult()
    )

fun Fixture.toFixtureDtoList() =
    FixtureDtoList(
        this.id,
        this.time,
        this.homeTeam.toTeamDtoList(),
        this.awayTeam.toTeamDtoList(),
        this.getResult()
    )
