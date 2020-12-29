package com.olafros.live.controller

import com.olafros.live.model.League
import com.olafros.live.model.User
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.LeagueRepository
import com.olafros.live.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/league")
class LeagueController(val leagueRepository: LeagueRepository, val userRepository: UserRepository) {

    fun getUser(@Autowired principal: Principal): User = userRepository.findByEmail(principal.name)

    @GetMapping
    fun getAllLeagues(): List<League> = leagueRepository.findAll()

    @GetMapping("/{id}")
    fun getLeagueById(@PathVariable(value = "id") leagueId: Long): ResponseEntity<*> {
        return leagueRepository.findById(leagueId).map { league ->
            ResponseEntity.ok(league)
        }.orElse(ResponseEntity.notFound().build())
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun createNewLeague(@Valid @RequestBody league: League, principal: Principal): ResponseEntity<*> {
        val user = getUser(principal)
        val admins: MutableSet<User> = HashSet()
        admins.add(user)
        val newLeague = League(0, league.name, admins)
        return ResponseEntity.ok().body(leagueRepository.save(newLeague))
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun updateLeagueById(@PathVariable(value = "id") leagueId: Long,
                         @Valid @RequestBody newLeague: League,
                         principal: Principal): ResponseEntity<*> {

        return leagueRepository.findById(leagueId).map { league ->
            if (league.admins.contains(getUser(principal))) {
                val updatedArticle: League = league.copy(name = newLeague.name)
                ResponseEntity.ok().body(leagueRepository.save(updatedArticle))
            } else {
                ResponseEntity.status(HttpStatus.FORBIDDEN).body<Any>(MessageResponse("You're not allowed to update this league"))
            }
        }.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the league to update")))

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun deleteLeagueById(@PathVariable(value = "id") leagueId: Long, principal: Principal): ResponseEntity<*> {
        return leagueRepository.findById(leagueId).map { league ->
            if (league.admins.contains(getUser(principal))) {
                leagueRepository.delete(league)
                ResponseEntity.ok<Any>(MessageResponse("League successfully deleted"))
            } else {
                ResponseEntity.status(HttpStatus.FORBIDDEN).body<Any>(MessageResponse("You're not allowed to update this league"))
            }
        }.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find the league to delete")))
    }
}
