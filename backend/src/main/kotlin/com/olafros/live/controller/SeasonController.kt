package com.olafros.live.controller

import com.olafros.live.APIConstants
import com.olafros.live.model.*
import com.olafros.live.payload.response.ErrorResponse
import com.olafros.live.payload.response.SuccessResponse
import com.olafros.live.repository.LeagueRepository
import com.olafros.live.repository.SeasonRepository
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import javax.validation.Valid

@RestController
@RequestMapping("/${APIConstants.BASE}/${APIConstants.SEASONS}")
class SeasonController(
    val seasonRepository: SeasonRepository,
    val leagueRepository: LeagueRepository,
    val securityService: SecurityService,
) {

    @GetMapping("/{seasonId}")
    fun getSeasonById(@PathVariable seasonId: Long): ResponseEntity<*> {
        val season = seasonRepository.findSeasonById(seasonId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(ErrorResponse("Could not find the season"))
        return ResponseEntity.ok(season.toSeasonDto())
    }

    @GetMapping("/{seasonId}/${APIConstants.FIXTURES}")
    fun getSeasonFixturesById(@PathVariable seasonId: Long): ResponseEntity<*> {
        val season = seasonRepository.findSeasonById(seasonId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(ErrorResponse("Could not find the season"))
        return ResponseEntity.ok(season.fixtures.map { fixture -> fixture.toFixtureDtoList() })
    }

    @GetMapping("/{seasonId}/${APIConstants.TABLE}")
    fun getSeasonTableById(@PathVariable seasonId: Long): ResponseEntity<*> {
        val season = seasonRepository.findSeasonById(seasonId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(ErrorResponse("Could not find the season"))
        val table: MutableList<TableEntryDto> = mutableListOf()
        season.teams.forEach { team -> table.add(TableEntryDto(team.toTeamDtoList())) }
        season.fixtures.filter { fixture -> fixture.time.isBefore(OffsetDateTime.now()) }.forEach { fixture ->
            run {
                val result = fixture.getResult()
                table.find { entry -> entry.team.id == fixture.homeTeam.id }?.apply {
                    goalsFor += result.homeTeam
                    goalsAgainst += result.awayTeam
                    wins += if (result.homeTeam > result.awayTeam) 1 else 0
                    draws += if (result.homeTeam == result.awayTeam) 1 else 0
                    losses += if (result.homeTeam < result.awayTeam) 1 else 0
                    points += if (result.homeTeam > result.awayTeam) 3 else if (result.homeTeam == result.awayTeam) 1 else 0
                    played++
                }
                table.find { entry -> entry.team.id == fixture.awayTeam.id }?.apply {
                    goalsFor += result.awayTeam
                    goalsAgainst += result.homeTeam
                    wins += if (result.homeTeam < result.awayTeam) 1 else 0
                    draws += if (result.homeTeam == result.awayTeam) 1 else 0
                    losses += if (result.homeTeam > result.awayTeam) 1 else 0
                    points += if (result.homeTeam < result.awayTeam) 3 else if (result.homeTeam == result.awayTeam) 1 else 0
                    played++
                }
            }
        }
        val sortedTable =
            table.sortedWith(compareByDescending<TableEntryDto> { it.points }.thenByDescending { it.goalsFor - it.goalsAgainst })
        var rank = 1
        val tableWithRank = sortedTable.map { entry -> entry.copy(rank = rank++) }
        return ResponseEntity.ok(tableWithRank)
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun createNewSeason(@Valid @RequestBody season: CreateSeasonDto): ResponseEntity<*> {
        if (!securityService.hasLeagueAccess(season.leagueId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body<Any>(ErrorResponse("You're not allowed to create a season in this league"))
        }
        val league = leagueRepository.findLeagueById(season.leagueId)
        return if (league == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the league"))
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
                .body<Any>(ErrorResponse("Could not find the season to update"))
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
            ResponseEntity.ok<Any>(SuccessResponse("Season successfully deleted"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(ErrorResponse("Could not find the season to delete"))
        }
    }
}
