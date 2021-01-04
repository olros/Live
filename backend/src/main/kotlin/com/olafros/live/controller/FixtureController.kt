package com.olafros.live.controller

import com.olafros.live.APIConstants
import com.olafros.live.model.*
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.FixtureRepository
import com.olafros.live.repository.SeasonRepository
import com.olafros.live.repository.TeamRepository
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/${APIConstants.BASE}/${APIConstants.FIXTURES}")
class FixtureController(
    val seasonRepository: SeasonRepository,
    val fixtureRepository: FixtureRepository,
    val teamRepository: TeamRepository,
    val securityService: SecurityService,
) {

    @GetMapping
    fun getAllFixtures(): List<FixtureDtoList> {
        return fixtureRepository.findAll().map { fixture -> fixture.toFixtureDtoList() }
    }

    @GetMapping("/{fixtureId}")
    fun getFixtureById(@PathVariable fixtureId: Long): ResponseEntity<*> {
        val fixture = fixtureRepository.findFixtureById(fixtureId)
        return if (fixture != null) {
            ResponseEntity.ok(fixture.toFixtureDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the fixture"))
        }
    }

    fun isValidTeam(team: Team, season: Season): Boolean = team.seasons.any { s -> s.id == season.id }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun createNewFixture(@Valid @RequestBody fixture: CreateFixtureDto): ResponseEntity<*> {
        if (!securityService.hasSeasonAccess(fixture.seasonId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body<Any>(MessageResponse("You're not allowed to create a fixture in this season"))
        }
        val season = seasonRepository.findSeasonById(fixture.seasonId)
        val homeTeam = teamRepository.findTeamById(fixture.homeTeam)
        val awayTeam = teamRepository.findTeamById(fixture.awayTeam)
        return when {
            (season == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the season"))
            (homeTeam == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the home-team"))
            (awayTeam == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the away-team"))
            (!isValidTeam(homeTeam, season)) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find ${homeTeam.name} in this season"))
            (!isValidTeam(awayTeam, season)) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find ${awayTeam.name} in this season"))
            else -> {
                val newFixture = Fixture(
                    0,
                    fixture.location,
                    fixture.referee,
                    fixture.time,
                    season,
                    homeTeam,
                    awayTeam
                )
                ResponseEntity.ok().body(fixtureRepository.save(newFixture).toFixtureDto())
            }
        }
    }

    @PutMapping("/{fixtureId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasFixtureAccess(#fixtureId)")
    fun updateSeasonById(
        @PathVariable fixtureId: Long,
        @Valid @RequestBody newFixture: UpdateFixtureDto
    ): ResponseEntity<*> {
        val fixture = fixtureRepository.findFixtureById(fixtureId)
        if (fixture == null) return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body<Any>(MessageResponse("Could not find the fixture to update"))
        val season = fixture.season
        val homeTeam = if (newFixture.homeTeam != null) {
            val team = teamRepository.findTeamById(newFixture.homeTeam)
            if (team != null && isValidTeam(team, season)) team else null
        } else fixture.homeTeam
        val awayTeam = if (newFixture.awayTeam != null) {
            val team = teamRepository.findTeamById(newFixture.awayTeam)
            if (team != null && isValidTeam(team, season)) team else null
        } else fixture.awayTeam
        return when {
            (homeTeam == null) -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(MessageResponse("Could not find home-team in this season"))
            (awayTeam == null) -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(MessageResponse("Could not find away-team in this season"))
            else -> {
                val updatedFixture = fixture.copy(
                    location = newFixture.location ?: fixture.location,
                    referee = newFixture.referee ?: fixture.referee,
                    time = newFixture.time ?: fixture.time,
                    homeTeam = homeTeam,
                    awayTeam = awayTeam,
                )
                ResponseEntity.ok().body(fixtureRepository.save(updatedFixture).toFixtureDto())
            }
        }
    }

    @DeleteMapping("/{fixtureId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasFixtureAccess(#fixtureId)")
    fun deleteFixtureById(@PathVariable fixtureId: Long): ResponseEntity<*> {
        val fixture = fixtureRepository.findFixtureById(fixtureId)
        return when {
            (fixture == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find the fixture to delete"))
            else -> {
                fixtureRepository.delete(fixture)
                ResponseEntity.ok<Any>(MessageResponse("Fixture successfully deleted"))
            }
        }
    }
}
