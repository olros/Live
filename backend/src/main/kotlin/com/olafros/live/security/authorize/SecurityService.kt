package com.olafros.live.security.authorize

import com.olafros.live.model.User
import com.olafros.live.repository.UserRepository
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class SecurityService(val userRepository: UserRepository) {

    fun getUser(): Optional<User> {
        val auth = SecurityContextHolder.getContext().authentication
        return if (auth != null && auth !is AnonymousAuthenticationToken) {
            getUser(auth.name)
        } else Optional.empty()
    }

    fun getUser(email: String): Optional<User> {
        return userRepository.findByEmail(email)
    }

    fun hasLeagueAccess(leagueId: Long): Boolean {
        val user = getUser()
        return if (user.isPresent) user.get().leagues.any { league -> league.id == leagueId } else false
    }

    fun hasTeamAccess(teamId: Long, leagueId: Long): Boolean {
        val user = getUser()
        return if (!user.isPresent) false
        else user.get().teams.any { team -> team.id == teamId } || hasLeagueAccess(leagueId)
    }
}