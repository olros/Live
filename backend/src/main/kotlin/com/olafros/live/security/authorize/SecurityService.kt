package com.olafros.live.security.authorize

import com.olafros.live.model.Season
import com.olafros.live.model.Team
import com.olafros.live.model.User
import com.olafros.live.repository.SeasonRepository
import com.olafros.live.repository.TeamRepository
import com.olafros.live.repository.UserRepository
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class SecurityService(
    val userRepository: UserRepository,
    val seasonRepository: SeasonRepository,
    val teamRepository: TeamRepository,
) {

    fun getUser(): Optional<User> {
        val auth = SecurityContextHolder.getContext().authentication
        return if (auth != null && auth !is AnonymousAuthenticationToken) {
            getUser(auth.name)
        } else Optional.empty()
    }

    fun getUser(email: String): Optional<User> {
        return userRepository.findByEmail(email)
    }

    fun hasUserAccess() = getUser().isPresent

    fun hasLeagueAccess(leagueId: Long): Boolean {
        val user = getUser()
        return if (user.isPresent) checkLeagueAccess(leagueId, user.get()) else false
    }

    fun hasTeamAccess(teamId: Long): Boolean {
        val user = getUser()
        val team = teamRepository.findById(teamId)
        return if (user.isPresent && team.isPresent) checkTeamAccess(team.get(), user.get()) else false
    }

    fun hasSeasonAccess(seasonId: Long): Boolean {
        val user = getUser()
        val season = seasonRepository.findById(seasonId)
        return if (season.isPresent && user.isPresent) checkSeasonAccess(season.get(), user.get()) else false
    }
}

fun SecurityService.checkLeagueAccess(leagueId: Long, user: User): Boolean {
    return user.leagues.any { league -> league.id == leagueId }
}

fun SecurityService.checkTeamAccess(team: Team, user: User): Boolean {
    return user.teams.any { t -> t.id == team.id } || hasLeagueAccess(team.league.id)
}

fun SecurityService.checkSeasonAccess(season: Season, user: User): Boolean {
    return checkLeagueAccess(season.league.id, user)
}