package com.olafros.live.controller

import com.olafros.live.model.AddLeagueAdminDto
import com.olafros.live.model.League
import com.olafros.live.model.UserDtoList
import com.olafros.live.model.toUserDtoList
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.LeagueRepository
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/leagues/{leagueId}/admins")
class LeagueAdminController(
    val leagueRepository: LeagueRepository,
    val securityService: SecurityService
) {

    fun getLeagueAdminsList(league: League): List<UserDtoList> {
        return league.admins.map { user -> user.toUserDtoList() }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(#leagueId)")
    fun getAllLeagueAdmins(@PathVariable leagueId: Long): ResponseEntity<*> {
        val league = leagueRepository.findById(leagueId)
        return if (!league.isPresent) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the league"))
        } else {
            ResponseEntity.ok().body(getLeagueAdminsList(league.get()))
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(#leagueId)")
    fun addLeagueAdmin(
        @PathVariable leagueId: Long,
        @Valid @RequestBody newAdmin: AddLeagueAdminDto
    ): ResponseEntity<*> {
        val league = leagueRepository.findById(leagueId)
        val user = securityService.getUser()
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

    @DeleteMapping("/{adminId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(#leagueId)")
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
