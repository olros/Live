package com.olafros.live.controller

import com.olafros.live.model.AddLeagueAdminDto
import com.olafros.live.model.Team
import com.olafros.live.model.UserDtoList
import com.olafros.live.model.toUserDtoList
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.TeamRepository
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/leagues/{leagueId}/teams/{teamId}/admins")
class TeamAdminController(
    val teamRepository: TeamRepository,
    val securityService: SecurityService
) {

    fun getTeamAdminsList(team: Team): List<UserDtoList> {
        return team.admins.map { user -> user.toUserDtoList() }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and @securityService.hasTeamAccess(#teamId)")
    fun getAllTeamAdmins(@PathVariable leagueId: Long, @PathVariable teamId: Long): ResponseEntity<*> {
        val team = teamRepository.findById(teamId)
        return if (!team.isPresent) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the team"))
        } else {
            ResponseEntity.ok().body(getTeamAdminsList(team.get()))
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() and @securityService.hasTeamAccess(#teamId)")
    fun addTeamAdmin(
        @PathVariable leagueId: Long,
        @PathVariable teamId: Long,
        @Valid @RequestBody newAdmin: AddLeagueAdminDto
    ): ResponseEntity<*> {
        val team = teamRepository.findById(teamId)
        val user = securityService.getUser(newAdmin.email)
        return if (!team.isPresent) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the team"))
        } else if (!user.isPresent) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body<Any>(MessageResponse("Could not find the user to add as admin"))
        } else if (team.get().admins.any { admin -> admin.id == user.get().id }) {
            ResponseEntity.status(HttpStatus.CONFLICT).body<Any>(MessageResponse("The user is already admin"))
        } else {
            val updatedTeam: Team = team.get()
            updatedTeam.admins.add(user.get())
            teamRepository.save(updatedTeam)
            ResponseEntity.ok().body(getTeamAdminsList(updatedTeam))
        }
    }

    @DeleteMapping("/{adminId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasTeamAccess(#teamId)")
    fun deleteTeamAdmin(
        @PathVariable leagueId: Long,
        @PathVariable teamId: Long,
        @PathVariable adminId: Long
    ): ResponseEntity<*> {
        val team = teamRepository.findById(teamId)
        return if (!team.isPresent) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the team"))
        } else {
            val user = team.get().admins.find { user -> user.id == adminId }
            if (user != null) {
                val updatedTeam = team.get()
                updatedTeam.admins.remove(user)
                teamRepository.save(updatedTeam)
                ResponseEntity.ok().body(getTeamAdminsList(updatedTeam))
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find the user to remove as admin"))
            }
        }
    }
}
