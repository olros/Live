package com.olafros.live.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "seasons")
data class Season(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: @NotNull @Size(max = 64) String,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "season_teams",
        joinColumns = [JoinColumn(name = "season_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "team_id", referencedColumnName = "id")],
        uniqueConstraints = [UniqueConstraint(columnNames = ["season_id", "team_id"])]
    )
    @JsonBackReference
    @JsonIgnore
    var teams: MutableList<Team> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "league_id", nullable = false)
    @JsonBackReference
    var league: @NotNull League,

    @OneToMany(mappedBy = "season", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonManagedReference
    var fixtures: MutableList<Fixture> = mutableListOf()
)

data class SeasonDto(val id: Long, val name: String, val teams: List<TeamDtoList>, val isAdmin: Boolean)
data class SeasonDtoList(val id: Long, val name: String)
data class CreateSeasonDto(val name: String, val leagueId: Long)
data class UpdateSeasonDto(val name: String?)
data class AddSeasonTeamDto(val teamId: Long)

fun Season.toSeasonDto(): SeasonDto {
    val auth = SecurityContextHolder.getContext().authentication
    val isAdmin = if (auth != null && auth !is AnonymousAuthenticationToken) {
        this.league.admins.any { user -> user.email == auth.name }
    } else false
    return SeasonDto(this.id, this.name, this.teams.map { team -> team.toTeamDtoList() }, isAdmin)
}

fun Season.toSeasonDtoList(): SeasonDtoList {
    return SeasonDtoList(this.id, this.name)
}

data class TableEntryDto(
    val team: TeamDtoList,
    var goalsFor: Int = 0,
    var goalsAgainst: Int = 0,
    var wins: Int = 0,
    var draws: Int = 0,
    var losses: Int = 0,
    var played: Int = 0,
    var points: Int = 0,
    var rank: Int = 0,
)