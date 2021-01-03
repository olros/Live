package com.olafros.live.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import java.time.LocalDateTime
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
    var time: @NotNull LocalDateTime,

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
    var players: MutableList<FixturePlayer> = mutableListOf()
)

data class FixtureDto(
    val id: Long,
    val location: String?,
    val referee: String?,
    val time: LocalDateTime,
    val homeTeam: TeamDtoList,
    val awayTeam: TeamDtoList,
    val season: SeasonDtoList,
    val players: List<FixturePlayerDtoList>
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
        this.season.toSeasonDtoList(),
        this.players.map { player -> player.toFixturePlayerDtoList() }
    )

fun Fixture.toFixtureDtoList() =
    FixtureDtoList(
        this.id,
        this.time,
        this.homeTeam.toTeamDtoList(),
        this.awayTeam.toTeamDtoList()
    )
