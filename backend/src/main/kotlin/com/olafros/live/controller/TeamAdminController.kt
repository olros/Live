package com.olafros.live.controller

import com.olafros.live.APIConstants
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
@RequestMapping("/${APIConstants.BASE}/${APIConstants.TEAMS}/{teamId}/${APIConstants.ADMINS}")
class TeamAdminController(
    val teamRepository: TeamRepository,
    val securityService: SecurityService
) {

    fun getTeamAdminsList(team: Team): List<UserDtoList> {
        return team.admins.map { user -> user.toUserDtoList() }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and @securityService.hasTeamAccess(#teamId)")
    fun getAllTeamAdmins(@PathVariable teamId: Long): ResponseEntity<*> {
        val team = teamRepository.findTeamById(teamId)
        return if (team == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the team"))
        } else {
            ResponseEntity.ok().body(getTeamAdminsList(team))
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() and @securityService.hasTeamAccess(#teamId)")
    fun addTeamAdmin(@PathVariable teamId: Long, @Valid @RequestBody newAdmin: AddLeagueAdminDto): ResponseEntity<*> {
        val team = teamRepository.findTeamById(teamId)
        val user = securityService.getUser(newAdmin.email)
        return when {
            (team == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the team"))
            (user == null) ->
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find the user to add as admin"))
            (team.admins.any { admin -> admin.id == user.id }) ->
                ResponseEntity.status(HttpStatus.CONFLICT).body<Any>(MessageResponse("The user is already admin"))
            else -> {
                team.admins.add(user)
                teamRepository.save(team)
                ResponseEntity.ok().body(getTeamAdminsList(team))
            }
        }
    }

    @DeleteMapping("/{adminId}")
    @PreAuthorize("isAuthenticated() and @securityService.hasTeamAccess(#teamId)")
    fun deleteTeamAdmin(@PathVariable teamId: Long, @PathVariable adminId: Long): ResponseEntity<*> {
        val team = teamRepository.findTeamById(teamId)
        return if (team == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the team"))
        } else {
            val user = team.admins.find { user -> user.id == adminId }
            if (user != null) {
                team.admins.remove(user)
                teamRepository.save(team)
                ResponseEntity.ok().body(getTeamAdminsList(team))
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body<Any>(MessageResponse("Could not find the user to remove as admin"))
            }
        }
    }
}
