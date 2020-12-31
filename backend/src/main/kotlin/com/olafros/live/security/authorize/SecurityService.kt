package com.olafros.live.security.authorize

import com.olafros.live.model.User
import com.olafros.live.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class SecurityService(val userRepository: UserRepository) {

    fun getUser(email: String): Optional<User> = userRepository.findByEmail(email)

    fun hasLeagueAccess(email: String, leagueId: Long): Boolean {
        val user = getUser(email)
        return if (user.isPresent) user.get().leagues.any { league -> league.id == leagueId } else false
    }

    fun hasTeamAccess(email: String, teamId: Long, leagueId: Long): Boolean {
        val user = getUser(email)
        return if (!user.isPresent) false
        else user.get().teams.any { team -> team.id == teamId } || hasLeagueAccess(
            email,
            leagueId
        )
    }
}