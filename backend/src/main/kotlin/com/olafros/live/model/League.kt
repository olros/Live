package com.olafros.live.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.olafros.live.security.authorize.SecurityService
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "leagues")
data class League(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: @NotBlank @Size(max = 128) String,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinTable(
        name = "league_admins",
        joinColumns = [JoinColumn(name = "league_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")]
    )
    @JsonBackReference
    @JsonIgnore
    var admins: MutableList<User> = mutableListOf(),

    @OneToMany(mappedBy = "league", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonManagedReference
    var teams: MutableList<Team> = mutableListOf(),

    @OneToMany(mappedBy = "league", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonManagedReference
    var seasons: MutableList<Season> = mutableListOf()
)

data class LeagueDto(
    val id: Long,
    val name: String,
    val teams: List<TeamDtoList>,
    val seasons: List<SeasonDtoList>,
    val isAdmin: Boolean
)

data class LeagueDtoList(val id: Long, val name: String)
data class CreateLeagueDto(val name: String)
data class UpdateLeagueDto(val name: String?)
data class AddLeagueAdminDto(val email: String)

fun League.toLeagueDto(): LeagueDto {
    val auth = SecurityContextHolder.getContext().authentication
    val isAdmin = if (auth != null && auth !is AnonymousAuthenticationToken) {
        this.admins.any { user -> user.email == auth.name }
    } else false
    return LeagueDto(
        this.id,
        this.name,
        this.teams.map { team -> team.toTeamDtoList() },
        this.seasons.map { season -> season.toSeasonDtoList() },
        isAdmin,
    )
}

fun League.toLeagueDtoList(): LeagueDtoList {
    return LeagueDtoList(this.id, this.name)
}