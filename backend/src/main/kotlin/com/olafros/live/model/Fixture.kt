package com.olafros.live.model

import com.fasterxml.jackson.annotation.JsonBackReference
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "fixtures")
data class Fixture(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var location: @Size(max = 128) String?,
    var referee: @Size(max = 128) String?,
    var time: LocalDateTime,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "season_id", nullable = false)
    @JsonBackReference
    var season: Season,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "home_team_id", nullable = false)
    var homeTeam: Team,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "away_team_id", nullable = false)
    var awayTeam: Team,
)

data class FixtureDto(
    val id: Long,
    val location: String?,
    val referee: String?,
    val time: LocalDateTime,
    val homeTeam: TeamDtoList,
    val awayTeam: TeamDtoList,
    val season: SeasonDtoList
)

data class FixtureDtoList(
    val id: Long,
    val time: LocalDateTime,
    val homeTeam: TeamDtoList,
    val awayTeam: TeamDtoList
)

data class CreateFixtureDto(
    val location: String?,
    val referee: String?,
    val time: LocalDateTime,
    val homeTeam: Long,
    val awayTeam: Long,
    val seasonId: Long,
)

data class UpdateFixtureDto(
    val location: String?,
    val referee: String?,
    val time: LocalDateTime?,
    val homeTeam: Long?,
    val awayTeam: Long?,
)

fun Fixture.toFixtureDto() =
    FixtureDto(
        this.id,
        this.location,
        this.referee,
        this.time,
        this.homeTeam.toTeamDtoList(),
        this.awayTeam.toTeamDtoList(),
        this.season.toSeasonDtoList()
    )

fun Fixture.toFixtureDtoList() =
    FixtureDtoList(
        this.id,
        this.time,
        this.homeTeam.toTeamDtoList(),
        this.awayTeam.toTeamDtoList()
    )
