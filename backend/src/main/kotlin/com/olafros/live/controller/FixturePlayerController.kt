package com.olafros.live.controller

import com.olafros.live.model.*
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.FixturePlayerRepository
import com.olafros.live.repository.FixtureRepository
import com.olafros.live.repository.PlayerRepository
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/fixtures/{fixtureId}/players")
class FixturePlayerController(
    val fixtureRepository: FixtureRepository,
    val playerRepository: PlayerRepository,
    val fixturePlayerRepository: FixturePlayerRepository,
    val securityService: SecurityService,
) {

    @GetMapping
    fun getAllFixturePlayers(@PathVariable fixtureId: Long): List<FixturePlayerDtoList> {
        return fixturePlayerRepository.findAllByFixture_Id(fixtureId).map { player -> player.toFixturePlayerDtoList() }
    }

    @GetMapping("/{playerId}")
    fun getFixturePlayer(@PathVariable fixtureId: Long, @PathVariable playerId: Long): ResponseEntity<*> {
        val player = fixturePlayerRepository.findFixturePlayerById(playerId)
        return if (player != null && player.fixture.id == fixtureId)
            ResponseEntity.ok(player.toFixturePlayerDto())
        else
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the player"))
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun addFixturePlayer(
        @PathVariable fixtureId: Long,
        @Valid @RequestBody newPlayer: CreateFixturePlayerDto
    ): ResponseEntity<*> {
        val player = playerRepository.findPlayerById(newPlayer.playerId)
        val fixture = fixtureRepository.findFixtureById(fixtureId)
        return when {
            (player == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the player"))
            (fixture == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the fixture"))
            (!securityService.hasTeamAccess(player.team.id)) ->
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body<Any>(MessageResponse("You're not allowed to edit this team's fixture players"))
            (fixture.players.any { p -> p.player.id == newPlayer.playerId }) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("The player is already playing in this fixture"))
            else -> {
                val fixturePlayer = FixturePlayer(
                    0,
                    player,
                    fixture,
                    newPlayer.number ?: player.number,
                    newPlayer.position ?: player.position
                )
                ResponseEntity.ok().body(fixturePlayerRepository.save(fixturePlayer).toFixturePlayerDto())
            }
        }
    }

    @PutMapping("/{playerId}")
    @PreAuthorize("isAuthenticated()")
    fun updateTeamPlayer(
        @PathVariable fixtureId: Long,
        @PathVariable playerId: Long,
        @Valid @RequestBody newPlayer: UpdateFixturePlayerDto
    ): ResponseEntity<*> {
        val player = fixturePlayerRepository.findFixturePlayerById(playerId)
        return when {
            (player == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the player"))
            (!securityService.hasTeamAccess(player.player.team.id)) ->
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body<Any>(MessageResponse("You're not allowed to edit this team's fixture players"))
            else -> {
                val updatedFixturePlayer = player.copy(
                    number = newPlayer.number ?: player.number,
                    position = newPlayer.position ?: player.position,
                )
                ResponseEntity.ok().body(fixturePlayerRepository.save(updatedFixturePlayer).toFixturePlayerDto())
            }
        }
    }

    @DeleteMapping("/{playerId}")
    @PreAuthorize("isAuthenticated()")
    fun deleteLeagueAdmin(@PathVariable fixtureId: Long, @PathVariable playerId: Long): ResponseEntity<*> {
        val player = fixturePlayerRepository.findFixturePlayerById(playerId)
        val fixture = fixtureRepository.findFixtureById(fixtureId)
        return when {
            (player == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the player"))
            (fixture == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the fixture"))
            (!securityService.hasTeamAccess(player.player.team.id)) ->
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body<Any>(MessageResponse("You're not allowed to edit this team's fixture players"))
            else -> {
                fixture.players.remove(player)
                fixtureRepository.save(fixture)
                ResponseEntity.ok<Any>(MessageResponse("Fixture player successfully deleted"))
            }
        }
    }
}
