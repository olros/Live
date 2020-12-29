package com.olafros.live.controller

import com.olafros.live.model.*
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.LeagueRepository
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/league")
class LeagueController(
    val leagueRepository: LeagueRepository,
    val securityService: SecurityService
) {

    @GetMapping
    fun getAllLeagues(): List<LeagueDtoList> = leagueRepository.findAll().map { league -> league.toLeagueDtoList() }

    @GetMapping("/{id}")
    fun getLeagueById(@PathVariable(value = "id") leagueId: Long): ResponseEntity<*> {
        val league = leagueRepository.findById(leagueId)
        return if (league.isPresent) {
            ResponseEntity.ok(league.get().toLeagueDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the league"))
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun createNewLeague(@Valid @RequestBody league: CreateLeagueDto, principal: Principal): ResponseEntity<*> {
        val user = securityService.getUser(principal.name)
        val admins: MutableList<User> = mutableListOf()
        admins.add(user)
        val newLeague = League(0, league.name, admins)
        return ResponseEntity.ok().body(leagueRepository.save(newLeague).toLeagueDto())
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(principal.username, #leagueId)")
    fun updateLeagueById(
        @PathVariable(value = "id") leagueId: Long,
        @Valid @RequestBody newLeague: UpdateLeagueDto
    ): ResponseEntity<*> {
        val league = leagueRepository.findById(leagueId)
        return if (league.isPresent) {
            val updatedLeague: League = league.get().copy(name = newLeague.name ?: league.get().name)
            ResponseEntity.ok().body(leagueRepository.save(updatedLeague).toLeagueDto())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(MessageResponse("Could not find the league to update"))
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(principal.username, #leagueId)")
    fun deleteLeagueById(@PathVariable(value = "id") leagueId: Long): ResponseEntity<*> {
        val league = leagueRepository.findById(leagueId)
        return if (league.isPresent) {
            leagueRepository.delete(league.get())
            ResponseEntity.ok<Any>(MessageResponse("League successfully deleted"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(MessageResponse("Could not find the league to delete"))
        }
    }
}
