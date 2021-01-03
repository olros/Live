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
@Table(name = "teams", uniqueConstraints = [UniqueConstraint(columnNames = ["name", "league_id"])])
data class Team(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: @NotNull @Size(max = 128) String,
    var logo: @Size(max = 256) String?,
    var description: @NotNull @Size(max = 512) String,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "team_admins",
        joinColumns = [JoinColumn(name = "team_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        uniqueConstraints = [UniqueConstraint(columnNames = ["team_id", "user_id"])]
    )
    @JsonBackReference
    @JsonIgnore
    var admins: MutableList<User> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "league_id", nullable = false)
    @JsonBackReference
    var league: @NotNull League,

    @ManyToMany(mappedBy = "teams", fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnore
    var seasons: MutableList<Season> = mutableListOf(),

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonManagedReference
    var players: MutableList<Player> = mutableListOf()
)

data class TeamDto(
    val id: Long,
    val name: String,
    val logo: String,
    val description: String,
    val isAdmin: Boolean,
    val players: List<PlayerDtoList>,
)

data class TeamDtoList(val id: Long, val name: String, val logo: String, val description: String)
data class CreateTeamDto(val name: String, val logo: String?, val description: String, val leagueId: Long)
data class UpdateTeamDto(val name: String?, val logo: String?, val description: String?)

fun Team.toTeamDto(): TeamDto {
    val auth = SecurityContextHolder.getContext().authentication
    val isAdmin = if (auth != null && auth !is AnonymousAuthenticationToken) {
        this.admins.any { user -> user.email == auth.name } || this.league.admins.any { user -> user.email == auth.name }
    } else false
    val players = this.players.map { player -> player.toPlayerDtoList() }
    return TeamDto(
        this.id,
        this.name,
        this.logo.orEmpty(),
        this.description,
        isAdmin,
        if (isAdmin) players else players.filter { player -> player.active }
    )
}

fun Team.toTeamDtoList(): TeamDtoList {
    return TeamDtoList(this.id, this.name, this.logo.orEmpty(), this.description)
}