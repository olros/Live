package com.olafros.live.controller

import com.olafros.live.model.FixtureEventDtoList
import com.olafros.live.model.toFixtureEventDto
import com.olafros.live.model.toFixtureEventDtoList
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.*
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/fixtures/{fixtureId}/events")
class FixtureEventController(
    val fixtureRepository: FixtureRepository,
    val playerRepository: PlayerRepository,
    val teamRepository: TeamRepository,
    val fixturePlayerRepository: FixturePlayerRepository,
    val fixtureEventRepository: FixtureEventRepository,
    val securityService: SecurityService,
) {

    @GetMapping
    fun getAllFixtureEvents(@PathVariable fixtureId: Long): List<FixtureEventDtoList> {
        return fixtureEventRepository.findAllByFixture_Id(fixtureId).map { event -> event.toFixtureEventDtoList() }
    }

    @GetMapping("/{eventId}")
    fun getFixtureEvent(@PathVariable fixtureId: Long, @PathVariable eventId: Long): ResponseEntity<*> {
        val event = fixtureEventRepository.findFixtureEventById(eventId)
        return if (event != null)
            ResponseEntity.ok(event.toFixtureEventDto())
        else
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the event"))
    }

//    @PostMapping
//    @PreAuthorize("isAuthenticated() and @securityService.hasFixtureAccess(#fixtureId)")
//    fun addFixtureEvent(@PathVariable fixtureId: Long, @Valid @RequestBody newEvent: CreateFixtureEventDto): ResponseEntity<*> {
//        val fixture = fixtureRepository.findById(fixtureId)
//        val player1 = if (newEvent.player1 != null) {
//            val player = playerRepository.findById(newEvent.player1)
//            if (player.isPresent) player.get() else null
//        } else null
//        val player2 = if (newEvent.player2 != null) {
//            val player = playerRepository.findById(newEvent.player2)
//            if (player.isPresent) player.get() else null
//        } else null
//        val team = if (newEvent.team != null){
//            val team = teamRepository.findTeamById(newEvent.team)
//            if (team != null) team else null
//        } else null
//
//        return if (newEvent.player1 != null && player1 != null)
//            ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body<Any>(MessageResponse("Could not find the player with id: ${newEvent.player1}"))
//        else if (newEvent.player2 != null && player2 != null)
//            ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body<Any>(MessageResponse("Could not find the player with id: ${newEvent.player2}"))
//        else if (newEvent.team != null && team != null)
//            ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body<Any>(MessageResponse("Could not find the team with id: ${newEvent.team}"))
//        else if (!fixture.isPresent)
//            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the fixture"))
//        else {
//            val fixturePlayer = FixturePlayer(
//                0,
//                player.get(),
//                fixture.get(),
//                newPlayer.number,
//                newPlayer.position ?: player.get().position
//            )
//            ResponseEntity.ok().body(fixturePlayerRepository.save(fixturePlayer).toFixturePlayerDto())
//        }
//    }
//
//    @PutMapping("/{eventId}")
//    @PreAuthorize("isAuthenticated() and @securityService.hasFixtureAccess(#fixtureId)")
//    fun updateTeamPlayer(
//        @PathVariable fixtureId: Long,
//        @PathVariable eventId: Long,
//        @Valid @RequestBody newPlayer: UpdateFixturePlayerDto
//    ): ResponseEntity<*> {
//        val player = fixturePlayerRepository.findById(eventId)
//        return when {
//            (!player.isPresent) ->
//                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the player"))
//            (!securityService.hasTeamAccess(player.get().player.team.id)) ->
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body<Any>(MessageResponse("You're not allowed to edit this team's fixture players"))
//            else -> {
//                val updatedFixturePlayer = player.get().copy(
//                    number = newPlayer.number ?: player.get().number,
//                    position = newPlayer.position ?: player.get().position,
//                )
//                ResponseEntity.ok().body(fixturePlayerRepository.save(updatedFixturePlayer).toFixturePlayerDto())
//            }
//        }
//    }
//
//    @DeleteMapping("/{eventId}")
//    @PreAuthorize("isAuthenticated() and @securityService.hasFixtureAccess(#fixtureId)")
//    fun deleteLeagueAdmin(@PathVariable fixtureId: Long, @PathVariable eventId: Long): ResponseEntity<*> {
//        val player = fixturePlayerRepository.findById(eventId)
//        val fixture = fixtureRepository.findById(fixtureId)
//        return when {
//            (!player.isPresent) ->
//                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the player"))
//            (!fixture.isPresent) ->
//                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the fixture"))
//            (!securityService.hasTeamAccess(player.get().player.team.id)) ->
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body<Any>(MessageResponse("You're not allowed to edit this team's fixture players"))
//            else -> {
//                val updatedFixture = fixture.get()
//                updatedFixture.players.remove(player.get())
//                fixtureRepository.save(updatedFixture)
//                ResponseEntity.ok<Any>(MessageResponse("Fixture player successfully deleted"))
//            }
//        }
//    }
}
