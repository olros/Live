package com.olafros.live.controller

import com.olafros.live.model.CreateTeamDto
import com.olafros.live.model.Team
import com.olafros.live.model.UpdateTeamDto
import com.olafros.live.model.toTeamDto
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.LeagueRepository
import com.olafros.live.repository.TeamRepository
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/teams")
class TeamController(
    val teamRepository: TeamRepository,
    val leagueRepository: LeagueRepository,
    val securityService: SecurityService,
) {

    @GetMapping("/{teamId}")
    fun getTeamById(@PathVariable teamId: Long): ResponseEntity<*> {
        val team = teamRepository.findById(teamId)
        return if (team.isPresent) {
            ResponseEntity.ok(team.get().toTeamDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the team"))
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(#leagueId)")
    fun createNewTeam(@Valid @RequestBody newTeam: CreateTeamDto): ResponseEntity<*> {
        if (securityService.hasLeagueAccess(newTeam.leagueId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body<Any>(MessageResponse("You're not allowed to create a team in this league"))
        }
        val league = leagueRepository.findById(newTeam.leagueId)
        return if (!league.isPresent) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the league"))
        } else if (league.get().teams.any { team -> team.name == newTeam.name }) {
            ResponseEntity.status(HttpStatus.CONFLICT)
                .body<Any>(MessageResponse("This league already contains a team with this name"))
        } else {
            val newTeam = Team(0, newTeam.name, newTeam.logo, newTeam.description, mutableListOf(), league.get())
            ResponseEntity.ok().body(teamRepository.save(newTeam).toTeamDto())
        }
    }

    @PutMapping("/{teamId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasTeamAccess(#teamId)")
    fun updateTeamById(@PathVariable teamId: Long, @Valid @RequestBody newTeam: UpdateTeamDto): ResponseEntity<*> {
        val team = teamRepository.findById(teamId)
        return if (team.isPresent) {
            val updatedTeam: Team = team.get().copy(
                name = newTeam.name ?: team.get().name,
                logo = newTeam.logo ?: team.get().name,
                description = newTeam.description ?: team.get().description,
            )
            ResponseEntity.ok().body(teamRepository.save(updatedTeam).toTeamDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the team to update"))
        }
    }

    @DeleteMapping("/{teamId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasTeamAccess(#teamId)")
    fun deleteTeamById(@PathVariable teamId: Long): ResponseEntity<*> {
        val team = teamRepository.findById(teamId)
        return if (team.isPresent) {
            teamRepository.delete(team.get())
            ResponseEntity.ok<Any>(MessageResponse("Team successfully deleted"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the team to delete"))
        }
    }
}
