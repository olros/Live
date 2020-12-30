package com.olafros.live.controller

import com.olafros.live.model.*
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.LeagueRepository
import com.olafros.live.repository.SeasonRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/league/{leagueId}/season")
class SeasonController(
    val seasonRepository: SeasonRepository,
    val leagueRepository: LeagueRepository
) {

    @GetMapping
    fun getAllSeasons(@PathVariable leagueId: Long): List<SeasonDtoList> {
        return seasonRepository.findByLeague_Id(leagueId).map { season -> season.toSeasonDtoList() }
    }

    @GetMapping("/{seasonId}")
    fun getSeasonById(
        @PathVariable leagueId: Long,
        @PathVariable seasonId: Long
    ): ResponseEntity<*> {
        val season = seasonRepository.findById(seasonId)
        return if (season.isPresent) {
            ResponseEntity.ok(season.get().toSeasonDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the season"))
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(principal.username, #leagueId)")
    fun createNewSeason(
        @PathVariable leagueId: Long,
        @Valid @RequestBody season: CreateSeasonDto,
        principal: Principal
    ): ResponseEntity<*> {
        val league = leagueRepository.findById(leagueId)
        return if (league.isPresent) {
            val teams: MutableList<Team> = mutableListOf()
            val newSeason = Season(0, season.name, teams, league.get())
            ResponseEntity.ok().body(seasonRepository.save(newSeason).toSeasonDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the league"))
        }
    }

    @PutMapping("/{seasonId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(principal.username, #leagueId)")
    fun updateSeasonById(
        @PathVariable leagueId: Long,
        @PathVariable seasonId: Long,
        @Valid @RequestBody newSeason: UpdateSeasonDto
    ): ResponseEntity<*> {
        val season = seasonRepository.findById(seasonId)
        return if (season.isPresent) {
            val updatedSeason: Season = season.get().copy(
                name = newSeason.name ?: season.get().name,
            )
            ResponseEntity.ok().body(seasonRepository.save(updatedSeason).toSeasonDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(MessageResponse("Could not find the season to update"))
        }
    }

    @DeleteMapping("/{seasonId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(principal.username, #leagueId)")
    fun deleteSeasonById(
        @PathVariable leagueId: Long,
        @PathVariable seasonId: Long
    ): ResponseEntity<*> {
        val season = seasonRepository.findById(seasonId)
        return if (season.isPresent) {
            seasonRepository.delete(season.get())
            ResponseEntity.ok<Any>(MessageResponse("Season successfully deleted"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(MessageResponse("Could not find the season to delete"))
        }
    }
}
