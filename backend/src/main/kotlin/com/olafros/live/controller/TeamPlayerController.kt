package com.olafros.live.controller

import com.olafros.live.model.*
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.PlayerRepository
import com.olafros.live.repository.TeamRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/teams/{teamId}/players")
class TeamPlayerController(
    val teamRepository: TeamRepository,
    val playerRepository: PlayerRepository,
) {

    @GetMapping
    fun getAllTeamPlayers(@PathVariable teamId: Long): List<PlayerDtoList> {
        return playerRepository.findAllByTeam_Id(teamId).map { player -> player.toPlayerDtoList() }
    }

    @GetMapping("/{playerId}")
    fun getTeamPlayer(@PathVariable teamId: Long, @PathVariable playerId: Long): ResponseEntity<*> {
        val player = playerRepository.findPlayerById(playerId)
        return if (player != null)
            ResponseEntity.ok(player.toPlayerDto())
        else
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the player"))
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() and @securityService.hasTeamAccess(#teamId)")
    fun addTeamPlayer(@PathVariable teamId: Long, @Valid @RequestBody newPlayer: CreatePlayerDto): ResponseEntity<*> {
        val team = teamRepository.findTeamById(teamId)
        return if (team == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the team"))
        } else {
            val player = Player(0, newPlayer.name, newPlayer.position, newPlayer.number, newPlayer.active, team)
            ResponseEntity.ok().body(playerRepository.save(player).toPlayerDto())
        }
    }

    @PutMapping("/{playerId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasTeamAccess(#teamId)")
    fun updateTeamPlayer(
        @PathVariable teamId: Long,
        @PathVariable playerId: Long,
        @Valid @RequestBody newPlayer: UpdatePlayerDto
    ): ResponseEntity<*> {
        val player = playerRepository.findPlayerById(playerId)
        return if (player != null) {
            val updatedPlayer = player.copy(
                name = newPlayer.name ?: player.name,
                position = newPlayer.position ?: player.position,
                number = newPlayer.number ?: player.number,
                active = newPlayer.active ?: player.active,
            )
            ResponseEntity.ok().body(playerRepository.save(updatedPlayer).toPlayerDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(MessageResponse("Could not find the player to update"))
        }
    }
}
