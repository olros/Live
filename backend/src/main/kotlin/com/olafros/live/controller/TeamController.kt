package com.olafros.live.controller

import com.olafros.live.model.*
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.LeagueRepository
import com.olafros.live.repository.TeamRepository
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/leagues/{leagueId}/teams")
class TeamController(
    val teamRepository: TeamRepository,
    val leagueRepository: LeagueRepository,
    val securityService: SecurityService
) {

    @GetMapping
    fun getAllTeams(@PathVariable leagueId: Long): List<TeamDtoList> {
        return teamRepository.findByLeague_Id(leagueId).map { team -> team.toTeamDtoList() }
    }

    @GetMapping("/{teamId}")
    fun getTeamById(
        @PathVariable leagueId: Long,
        @PathVariable teamId: Long
    ): ResponseEntity<*> {
        val team = teamRepository.findById(teamId)
        return if (team.isPresent) {
            ResponseEntity.ok(team.get().toTeamDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the team"))
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(principal.username, #leagueId)")
    fun createNewTeam(
        @PathVariable leagueId: Long,
        @Valid @RequestBody team: CreateTeamDto,
        principal: Principal
    ): ResponseEntity<*> {
        val league = leagueRepository.findById(leagueId)
        return if (league.isPresent) {
            val user = securityService.getUser(principal.name)
            val admins: MutableList<User> = mutableListOf()
            admins.add(user)
            val newTeam = Team(0, team.name, team.logo, team.description, admins, league.get())
            ResponseEntity.ok().body(teamRepository.save(newTeam).toTeamDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the league"))
        }
    }

    @PutMapping("/{teamId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasTeamAccess(principal.username, #teamId, #leagueId)")
    fun updateTeamById(
        @PathVariable leagueId: Long,
        @PathVariable teamId: Long,
        @Valid @RequestBody newTeam: UpdateTeamDto
    ): ResponseEntity<*> {
        val team = teamRepository.findById(teamId)
        return if (team.isPresent) {
            val updatedTeam: Team = team.get().copy(
                name = newTeam.name ?: team.get().name,
                logo = newTeam.logo,
                description = newTeam.description ?: team.get().description,
            )
            ResponseEntity.ok().body(teamRepository.save(updatedTeam).toTeamDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the team to update"))
        }
    }

    @DeleteMapping("/{teamId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasTeamAccess(principal.username, #teamId, #leagueId)")
    fun deleteTeamById(
        @PathVariable leagueId: Long,
        @PathVariable teamId: Long
    ): ResponseEntity<*> {
        val team = teamRepository.findById(teamId)
        return if (team.isPresent) {
            teamRepository.delete(team.get())
            ResponseEntity.ok<Any>(MessageResponse("Team successfully deleted"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the team to delete"))
        }
    }
}
