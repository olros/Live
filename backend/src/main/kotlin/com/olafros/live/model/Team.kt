package com.olafros.live.model

import com.fasterxml.jackson.annotation.JsonBackReference
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

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "league_id", nullable = false)
        @JsonBackReference
        var league: League
)

data class TeamDto(val id: Long, val name: String, val logo: String, val description: String, val league: Long)
data class CreateTeamDto(val name: String, val logo: String?, val description: String, val league: Long)
data class UpdateTeamDto(val name: String?, val logo: String?, val description: String?)

fun Team.toTeamDto(): TeamDto {
    return TeamDto(this.id, this.name, this.logo.orEmpty(), this.description, this.league.id)
}