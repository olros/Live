package com.olafros.live.controller

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
@RequestMapping("/api/fixtures")
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
        val fixture = fixtureRepository.findById(fixtureId)
        return if (fixture.isPresent) {
            ResponseEntity.ok(fixture.get().toFixtureDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the fixture"))
        }
    }

    fun isValidTeam(team: Team, season: Season): Boolean = team.seasons.any { s -> s.id == season.id }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun createNewFixture(@Valid @RequestBody fixture: CreateFixtureDto): ResponseEntity<*> {
        if (securityService.hasSeasonAccess(fixture.seasonId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body<Any>(MessageResponse("You're not allowed to create a fixture in this season"))
        }
        val season = seasonRepository.findById(fixture.seasonId)
        val homeTeam = teamRepository.findById(fixture.homeTeam)
        val awayTeam = teamRepository.findById(fixture.awayTeam)
        return when {
            (!season.isPresent) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the season"))
            (!homeTeam.isPresent) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the home-team"))
            (!awayTeam.isPresent) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the away-team"))
            (!isValidTeam(homeTeam.get(), season.get())) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find ${homeTeam.get().name} in this season"))
            (!isValidTeam(awayTeam.get(), season.get())) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find ${awayTeam.get().name} in this season"))
            else -> {
                val newFixture = Fixture(
                    0,
                    fixture.location,
                    fixture.referee,
                    fixture.time,
                    season.get(),
                    homeTeam.get(),
                    awayTeam.get()
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
        val fixture = fixtureRepository.findById(fixtureId)
        if (!fixture.isPresent) return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body<Any>(MessageResponse("Could not find the fixture to update"))
        val season = fixture.get().season
        val homeTeam = if (newFixture.homeTeam != null) {
            val team = teamRepository.findById(newFixture.homeTeam)
            if (team.isPresent && isValidTeam(team.get(), season)) team.get() else null
        } else fixture.get().homeTeam
        val awayTeam = if (newFixture.awayTeam != null) {
            val team = teamRepository.findById(newFixture.awayTeam)
            if (team.isPresent && isValidTeam(team.get(), season)) team.get() else null
        } else fixture.get().awayTeam
        return when {
            (homeTeam == null) -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(MessageResponse("Could not find home-team in this season"))
            (awayTeam == null) -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(MessageResponse("Could not find away-team in this season"))
            else -> {
                val updatedFixture: Fixture = fixture.get().copy(
                    location = newFixture.location ?: fixture.get().location,
                    referee = newFixture.referee ?: fixture.get().referee,
                    time = newFixture.time ?: fixture.get().time,
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
        val fixture = fixtureRepository.findById(fixtureId)
        return when {
            (!fixture.isPresent) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find the fixture to delete"))
            else -> {
                fixtureRepository.delete(fixture.get())
                ResponseEntity.ok<Any>(MessageResponse("Fixture successfully deleted"))
            }
        }
    }
}
