package com.olafros.live.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.olafros.live.security.authorize.SecurityService
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "teams", uniqueConstraints = [UniqueConstraint(columnNames = ["name", "league_id"])])
data class Team(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: @NotBlank @Size(max = 128) String,
    var logo: @Size(max = 256) String?,
    var description: @NotBlank @Size(max = 512) String,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinTable(
        name = "team_admins",
        joinColumns = [JoinColumn(name = "team_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")]
    )
    @JsonBackReference
    @JsonIgnore
    var admins: MutableList<User> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "league_id", nullable = false)
    @JsonBackReference
    var league: League
)

data class TeamDto(val id: Long, val name: String, val logo: String, val description: String, val isAdmin: Boolean)
data class TeamDtoList(val id: Long, val name: String, val logo: String, val description: String)
data class CreateTeamDto(val name: String, val logo: String?, val description: String)
data class UpdateTeamDto(val name: String?, val logo: String?, val description: String?)

fun Team.toTeamDto(): TeamDto {
    val securityService = SecurityService()
    return TeamDto(
        this.id,
        this.name,
        this.logo.orEmpty(),
        this.description,
        securityService.hasTeamAccess(this.id, this.league.id)
    )
}

fun Team.toTeamDtoList(): TeamDtoList {
    return TeamDtoList(this.id, this.name, this.logo.orEmpty(), this.description)
}