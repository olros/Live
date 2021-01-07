package com.olafros.live.controller

import com.olafros.live.APIConstants
import com.olafros.live.model.*
import com.olafros.live.payload.response.ErrorResponse
import com.olafros.live.payload.response.SuccessResponse
import com.olafros.live.repository.FixtureEventRepository
import com.olafros.live.repository.FixtureRepository
import com.olafros.live.repository.PlayerRepository
import com.olafros.live.repository.TeamRepository
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/${APIConstants.BASE}/${APIConstants.FIXTURES}/{fixtureId}/${APIConstants.EVENTS}")
class FixtureEventController(
    val fixtureRepository: FixtureRepository,
    val playerRepository: PlayerRepository,
    val teamRepository: TeamRepository,
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
        return if (event != null && event.fixture.id == fixtureId) ResponseEntity.ok(event.toFixtureEventDto())
        else ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the event"))
    }

    fun isValidFixtureEvent(event: FixtureEvent): Boolean {
        if (event.minute < 0 || event.minute > 90) return false
        if (event.player1?.id == event.player2?.id) return false
        if (event.team != null) {
            if (event.player1 != null && event.player1?.team?.id != event.team?.id) return false
            if (event.player2 != null && event.player2?.team?.id != event.team?.id) return false
        }
        return when (event.type) {
            EFixtureEvent.GOAL -> event.team != null && event.player1 != null
            EFixtureEvent.SUBSTITUTION -> event.team != null && event.player1 != null && event.player2 != null
            else -> true
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() and @securityService.hasFixtureAccess(#fixtureId)")
    fun addFixtureEvent(
        @PathVariable fixtureId: Long,
        @Valid @RequestBody newEvent: CreateFixtureEventDto
    ): ResponseEntity<*> {
        val fixture = fixtureRepository.findFixtureById(fixtureId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(ErrorResponse("Could not find the fixture"))
        val player1 = if (newEvent.player1 != null) {
            playerRepository.findPlayerById(newEvent.player1)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(ErrorResponse("Could not find the player with id: ${newEvent.player1}"))
        } else null
        val player2 = if (newEvent.player2 != null) {
            playerRepository.findPlayerById(newEvent.player2)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(ErrorResponse("Could not find the player with id: ${newEvent.player2}"))
        } else null
        val team = if (newEvent.team != null) {
            teamRepository.findTeamById(newEvent.team)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(ErrorResponse("Could not find the team with id: ${newEvent.team}"))
        } else null

        val fixtureEvent = FixtureEvent(
            0,
            newEvent.minute,
            newEvent.type,
            fixture,
            newEvent.text,
            player1,
            player2,
            team,
        )
        return if (isValidFixtureEvent(fixtureEvent))
            ResponseEntity.ok().body(fixtureEventRepository.save(fixtureEvent).toFixtureEventDto())
        else ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body<Any>(ErrorResponse("The event is not valid"))
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasFixtureAccess(#fixtureId)")
    fun updateTeamPlayer(
        @PathVariable fixtureId: Long,
        @PathVariable eventId: Long,
        @Valid @RequestBody newEvent: UpdateFixtureEventDto
    ): ResponseEntity<*> {
        val fixtureEvent = fixtureEventRepository.findFixtureEventById(eventId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the event"))
        val player1 = if (newEvent.player1 != null) {
            playerRepository.findPlayerById(newEvent.player1)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(ErrorResponse("Could not find the player with id: ${newEvent.player1}"))
        } else fixtureEvent.player1
        val player2 = if (newEvent.player2 != null) {
            playerRepository.findPlayerById(newEvent.player2)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(ErrorResponse("Could not find the player with id: ${newEvent.player2}"))
        } else fixtureEvent.player2
        val team = if (newEvent.team != null) {
            teamRepository.findTeamById(newEvent.team)
                ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(ErrorResponse("Could not find the team with id: ${newEvent.team}"))
        } else fixtureEvent.team

        val updatedFixtureEvent = fixtureEvent.copy(
            minute = newEvent.minute ?: fixtureEvent.minute,
            type = newEvent.type ?: fixtureEvent.type,
            text = newEvent.text ?: fixtureEvent.text,
            player1 = player1,
            player2 = player2,
            team = team,
        )
        return if (isValidFixtureEvent(updatedFixtureEvent))
            ResponseEntity.ok().body(fixtureEventRepository.save(updatedFixtureEvent).toFixtureEventDto())
        else ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body<Any>(ErrorResponse("The event is not valid"))
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasFixtureAccess(#fixtureId)")
    fun deleteLeagueAdmin(@PathVariable fixtureId: Long, @PathVariable eventId: Long): ResponseEntity<*> {
        val event = fixtureEventRepository.findFixtureEventById(eventId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(ErrorResponse("Could not find the event"))

        fixtureEventRepository.delete(event)
        return ResponseEntity.ok<Any>(SuccessResponse("Event successfully deleted"))
    }
}
