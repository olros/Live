package com.olafros.live.controller

import com.olafros.live.model.*
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.LeagueRepository
import com.olafros.live.repository.SeasonRepository
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/seasons")
class SeasonController(
    val seasonRepository: SeasonRepository,
    val leagueRepository: LeagueRepository,
    val securityService: SecurityService,
) {

    @GetMapping("/{seasonId}")
    fun getSeasonById(@PathVariable seasonId: Long): ResponseEntity<*> {
        val season = seasonRepository.findSeasonById(seasonId)
        return if (season != null) {
            ResponseEntity.ok(season.toSeasonDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the season"))
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun createNewSeason(@Valid @RequestBody season: CreateSeasonDto): ResponseEntity<*> {
        if (!securityService.hasLeagueAccess(season.leagueId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body<Any>(MessageResponse("You're not allowed to create a season in this league"))
        }
        val league = leagueRepository.findLeagueById(season.leagueId)
        return if (league == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the league"))
        } else {
            val teams: MutableList<Team> = mutableListOf()
            val newSeason = Season(0, season.name, teams, league)
            ResponseEntity.ok().body(seasonRepository.save(newSeason).toSeasonDto())
        }
    }

    @PutMapping("/{seasonId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasSeasonAccess(#seasonId)")
    fun updateSeasonById(
        @PathVariable seasonId: Long,
        @Valid @RequestBody newSeason: UpdateSeasonDto
    ): ResponseEntity<*> {
        val season = seasonRepository.findSeasonById(seasonId)
        return if (season != null) {
            val updatedSeason = season.copy(
                name = newSeason.name ?: season.name,
            )
            ResponseEntity.ok().body(seasonRepository.save(updatedSeason).toSeasonDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(MessageResponse("Could not find the season to update"))
        }
    }

    @DeleteMapping("/{seasonId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasSeasonAccess(#seasonId)")
    fun deleteSeasonById(
        @PathVariable seasonId: Long
    ): ResponseEntity<*> {
        val season = seasonRepository.findSeasonById(seasonId)
        return if (season != null) {
            seasonRepository.delete(season)
            ResponseEntity.ok<Any>(MessageResponse("Season successfully deleted"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(MessageResponse("Could not find the season to delete"))
        }
    }
}
