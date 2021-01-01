package com.olafros.live.controller

import com.olafros.live.model.*
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.SeasonRepository
import com.olafros.live.repository.TeamRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/leagues/{leagueId}/seasons/{seasonId}/teams")
class SeasonTeamController(
    val seasonRepository: SeasonRepository,
    val teamRepository: TeamRepository,
) {

    @GetMapping
    fun getAllTeams(@PathVariable leagueId: Long, @PathVariable seasonId: Long): List<TeamDtoList> {
        return teamRepository.findAllBySeasonsContains(seasonId).map { team -> team.toTeamDtoList() }
    }

    fun isValidTeam(team: Team, league: League): Boolean = team.league.id == league.id

    @PostMapping
    @PreAuthorize("isAuthenticated() and @securityService.hasSeasonAccess(#seasonId)")
    fun addNewTeam(
        @PathVariable leagueId: Long,
        @PathVariable seasonId: Long,
        @Valid @RequestBody team: AddSeasonTeamDto,
    ): ResponseEntity<*> {
        val season = seasonRepository.findById(seasonId)
        val team = teamRepository.findById(team.teamId)
        return when {
            (!season.isPresent) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the season"))
            (!team.isPresent) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the home-team"))
            (!isValidTeam(team.get(), team.get().league)) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find ${team.get().name} in this league"))
            else -> {
                val updatedSeason = season.get()
                updatedSeason.teams.add(team.get())
                ResponseEntity.ok().body(seasonRepository.save(updatedSeason).toSeasonDto())
            }
        }
    }

    @DeleteMapping("/{teamId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasSeasonAccess(#seasonId)")
    fun deleteTeamById(
        @PathVariable leagueId: Long,
        @PathVariable seasonId: Long,
        @PathVariable teamId: Long,
    ): ResponseEntity<*> {
        val season = seasonRepository.findById(seasonId)
        return if (!season.isPresent) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the season"))
        } else {
            val team = season.get().teams.find { team -> team.id == teamId }
            if (team != null) {
                val updatedSeason = season.get()
                updatedSeason.teams.remove(team)
                seasonRepository.save(updatedSeason)
                ResponseEntity.ok().body(seasonRepository.save(updatedSeason).toSeasonDto())
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find the team to remove from the season"))
            }
        }
    }
}
