package com.olafros.live.controller

import com.olafros.live.APIConstants
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
@RequestMapping("/${APIConstants.BASE}/${APIConstants.LEAGUES}/{leagueId}/${APIConstants.ADMINS}")
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
        val league = leagueRepository.findLeagueById(leagueId)
        return if (league == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the league"))
        } else {
            ResponseEntity.ok().body(getLeagueAdminsList(league))
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(#leagueId)")
    fun addLeagueAdmin(
        @PathVariable leagueId: Long,
        @Valid @RequestBody newAdmin: AddLeagueAdminDto
    ): ResponseEntity<*> {
        val league = leagueRepository.findLeagueById(leagueId)
        val user = securityService.getUser()
        return when {
            (league == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the league"))
            (user == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find the user to add as admin"))
            (league.admins.any { admin -> admin.id == user.id }) ->
                ResponseEntity.status(HttpStatus.CONFLICT).body<Any>(MessageResponse("The user is already admin"))
            else -> {
                league.admins.add(user)
                leagueRepository.save(league)
                ResponseEntity.ok().body(getLeagueAdminsList(league))
            }
        }
    }

    @DeleteMapping("/{adminId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasLeagueAccess(#leagueId)")
    fun deleteLeagueAdmin(@PathVariable leagueId: Long, @PathVariable adminId: Long): ResponseEntity<*> {
        val league = leagueRepository.findLeagueById(leagueId)
        return if (league == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the league"))
        } else if (league.admins.size <= 1) {
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body<Any>(MessageResponse("There must be at least 1 admin left in the league"))
        } else {
            val user = league.admins.find { user -> user.id == adminId }
            if (user != null) {
                league.admins.remove(user)
                leagueRepository.save(league)
                ResponseEntity.ok().body(getLeagueAdminsList(league))
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find the user to remove as admin"))
            }
        }
    }
}
