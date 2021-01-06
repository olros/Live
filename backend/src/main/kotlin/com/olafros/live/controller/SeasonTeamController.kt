package com.olafros.live.controller

import com.olafros.live.APIConstants
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
@RequestMapping("/${APIConstants.BASE}/${APIConstants.SEASONS}/{seasonId}/${APIConstants.TEAMS}")
class SeasonTeamController(
    val seasonRepository: SeasonRepository,
    val teamRepository: TeamRepository,
) {

    @GetMapping
    fun getAllTeams(@PathVariable seasonId: Long): List<TeamDtoList> {
        return seasonRepository.findSeasonById(seasonId)?.teams?.map { team -> team.toTeamDtoList() } ?: emptyList()
    }

    fun isValidTeam(team: Team, league: League): Boolean = team.league.id == league.id

    @PostMapping
    @PreAuthorize("isAuthenticated() and @securityService.hasSeasonAccess(#seasonId)")
    fun addNewTeam(@PathVariable seasonId: Long, @Valid @RequestBody newTeam: AddSeasonTeamDto): ResponseEntity<*> {
        val season = seasonRepository.findSeasonById(seasonId)
        val team = teamRepository.findTeamById(newTeam.teamId)
        return when {
            (season == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the season"))
            (team == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the home-team"))
            (!isValidTeam(team, team.league)) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find ${team.name} in this league"))
            else -> {
                season.teams.add(team)
                ResponseEntity.ok().body(seasonRepository.save(season).teams.map { t -> t.toTeamDtoList() })
            }
        }
    }

    @DeleteMapping("/{teamId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasSeasonAccess(#seasonId)")
    fun deleteTeamById(@PathVariable seasonId: Long, @PathVariable teamId: Long): ResponseEntity<*> {
        val season = seasonRepository.findSeasonById(seasonId)
        return if (season == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the season"))
        } else {
            val team = season.teams.find { team -> team.id == teamId }
            if (team != null) {
                season.teams.remove(team)
                seasonRepository.save(season)
                ResponseEntity.ok().body(seasonRepository.save(season).teams.map { t -> t.toTeamDtoList() })
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find the team to remove from the season"))
            }
        }
    }
}
