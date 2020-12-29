package com.olafros.live.security.authorize

import com.olafros.live.model.User
import com.olafros.live.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class SecurityService(val userRepository: UserRepository) {

    fun getUser(email : String): User = userRepository.findByEmail(email)

    fun hasLeagueAccess(email: String, leagueId: Long): Boolean {
        return getUser(email).leagues.any { league -> league.id == leagueId }
    }
}