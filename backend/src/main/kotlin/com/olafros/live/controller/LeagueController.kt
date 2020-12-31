package com.olafros.live.controller

import com.olafros.live.model.*
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.LeagueRepository
import com.olafros.live.repository.UserRepository
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/leagues")
class LeagueController(
    val leagueRepository: LeagueRepository,
    val userRepository: UserRepository,
    val securityService: SecurityService
) {

    @GetMapping
    fun getAllLeagues(): List<LeagueDtoList> = leagueRepository.findAll().map { league -> league.toLeagueDtoList() }

    @GetMapping("/{leagueId}")
    fun getLeagueById(@PathVariable leagueId: Long): ResponseEntity<*> {
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
        return if (user.isPresent) {
            val admins: MutableList<User> = mutableListOf()
            admins.add(user.get())
            val newLeague = League(0, league.name, admins)
            ResponseEntity.ok().body(leagueRepository.save(newLeague).toLeagueDto())
        } else ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find user"))
    }

    @PutMapping("/{leagueId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(principal.username, #leagueId)")
    fun updateLeagueById(
        @PathVariable leagueId: Long,
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

    @DeleteMapping("/{leagueId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(principal.username, #leagueId)")
    fun deleteLeagueById(@PathVariable leagueId: Long): ResponseEntity<*> {
        val league = leagueRepository.findById(leagueId)
        return if (league.isPresent) {
            leagueRepository.delete(league.get())
            ResponseEntity.ok<Any>(MessageResponse("League successfully deleted"))
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(MessageResponse("Could not find the league to delete"))
        }
    }

    fun getLeagueAdminsList(league: League): List<UserDtoList> {
        return league.admins.map { user -> user.toUserDtoList() }
    }

    @GetMapping("/{leagueId}/admins")
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(principal.username, #leagueId)")
    fun getAllLeagueAdmins(@PathVariable leagueId: Long): ResponseEntity<*> {
        val league = leagueRepository.findById(leagueId)
        return if (!league.isPresent) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the league"))
        } else {
            ResponseEntity.ok().body(getLeagueAdminsList(league.get()))
        }
    }

    @PostMapping("/{leagueId}/admins")
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(principal.username, #leagueId)")
    fun addLeagueAdmin(
        @PathVariable leagueId: Long,
        @Valid @RequestBody newAdmin: AddLeagueAdminDto
    ): ResponseEntity<*> {
        val league = leagueRepository.findById(leagueId)
        val user = securityService.getUser(newAdmin.email)
        return if (!league.isPresent) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the league"))
        } else if (!user.isPresent) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(MessageResponse("Could not find the user to add as admin"))
        } else if (league.get().admins.any { admin -> admin.id == user.get().id }) {
            ResponseEntity.status(HttpStatus.CONFLICT).body<Any>(MessageResponse("The user is already admin"))
        } else {
            val updatedLeague: League = league.get()
            updatedLeague.admins.add(user.get())
            leagueRepository.save(updatedLeague)
            ResponseEntity.ok().body(getLeagueAdminsList(updatedLeague))
        }
    }

    @DeleteMapping("/{leagueId}/admins/{adminId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(principal.username, #leagueId)")
    fun deleteLeagueAdmin(@PathVariable leagueId: Long, @PathVariable adminId: Long): ResponseEntity<*> {
        val league = leagueRepository.findById(leagueId)
        return if (!league.isPresent) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the league"))
        } else if (league.get().admins.size <= 1) {
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body<Any>(MessageResponse("There must be at least 1 admin left in the league"))
        } else {
            val user = league.get().admins.find { user -> user.id == adminId }
            if (user != null) {
                val updatedLeague: League = league.get()
                updatedLeague.admins.remove(user)
                leagueRepository.save(updatedLeague)
                ResponseEntity.ok().body(getLeagueAdminsList(updatedLeague))
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find the user to remove as admin"))
            }
        }
    }
}
