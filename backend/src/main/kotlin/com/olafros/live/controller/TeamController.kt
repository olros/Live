package com.olafros.live.controller

import com.olafros.live.APIConstants
import com.olafros.live.model.CreateTeamDto
import com.olafros.live.model.Team
import com.olafros.live.model.UpdateTeamDto
import com.olafros.live.model.toTeamDto
import com.olafros.live.payload.response.ErrorResponse
import com.olafros.live.payload.response.SuccessResponse
import com.olafros.live.repository.LeagueRepository
import com.olafros.live.repository.TeamRepository
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/${APIConstants.BASE}/${APIConstants.TEAMS}")
class TeamController(
    val teamRepository: TeamRepository,
    val leagueRepository: LeagueRepository,
    val securityService: SecurityService,
) {

    @GetMapping("/{teamId}")
    fun getTeamById(@PathVariable teamId: Long): ResponseEntity<*> {
        val team = teamRepository.findTeamById(teamId)
        return if (team != null) {
            ResponseEntity.ok(team.toTeamDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the team"))
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun createNewTeam(@Valid @RequestBody newTeam: CreateTeamDto): ResponseEntity<*> {
        if (!securityService.hasLeagueAccess(newTeam.leagueId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body<Any>(ErrorResponse("You're not allowed to create a team in this league"))
        }
        val league = leagueRepository.findLeagueById(newTeam.leagueId)
        return if (league == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the league"))
        } else if (league.teams.any { team -> team.name == newTeam.name }) {
            ResponseEntity.status(HttpStatus.CONFLICT)
                .body<Any>(ErrorResponse("This league already contains a team with this name"))
        } else {
            val team = Team(0, newTeam.name, newTeam.logo, newTeam.description, mutableListOf(), league)
            ResponseEntity.ok().body(teamRepository.save(team).toTeamDto())
        }
    }

    @PutMapping("/{teamId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasTeamAccess(#teamId)")
    fun updateTeamById(@PathVariable teamId: Long, @Valid @RequestBody newTeam: UpdateTeamDto): ResponseEntity<*> {
        val team = teamRepository.findTeamById(teamId)
        return if (team != null) {
            val updatedTeam = team.copy(
                name = newTeam.name ?: team.name,
                logo = newTeam.logo ?: team.name,
                description = newTeam.description ?: team.description,
            )
            ResponseEntity.ok().body(teamRepository.save(updatedTeam).toTeamDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the team to update"))
        }
    }

    @DeleteMapping("/{teamId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasTeamAccess(#teamId)")
    fun deleteTeamById(@PathVariable teamId: Long): ResponseEntity<*> {
        val team = teamRepository.findTeamById(teamId)
        return if (team != null) {
            teamRepository.delete(team)
            ResponseEntity.ok<Any>(SuccessResponse("Team successfully deleted"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the team to delete"))
        }
    }
}
