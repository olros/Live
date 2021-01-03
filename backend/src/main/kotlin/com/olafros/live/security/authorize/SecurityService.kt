package com.olafros.live.security.authorize

import com.olafros.live.model.Season
import com.olafros.live.model.Team
import com.olafros.live.model.User
import com.olafros.live.repository.FixtureRepository
import com.olafros.live.repository.SeasonRepository
import com.olafros.live.repository.TeamRepository
import com.olafros.live.repository.UserRepository
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityService(
    val userRepository: UserRepository,
    val seasonRepository: SeasonRepository,
    val teamRepository: TeamRepository,
    val fixtureRepository: FixtureRepository,
) {

    fun getUser(): User? {
        val auth = SecurityContextHolder.getContext().authentication
        return if (auth != null && auth !is AnonymousAuthenticationToken) {
            getUser(auth.name)
        } else null
    }

    fun getUser(email: String): User? = userRepository.findByEmail(email)

    fun hasUserAccess() = getUser() != null

    fun hasLeagueAccess(leagueId: Long): Boolean {
        val user = getUser()
        return if (user != null) checkLeagueAccess(leagueId, user) else false
    }

    fun hasTeamAccess(teamId: Long): Boolean {
        val user = getUser()
        val team = teamRepository.findTeamById(teamId)
        return if (user != null && team != null) checkTeamAccess(team, user) else false
    }

    fun hasSeasonAccess(seasonId: Long): Boolean {
        val user = getUser()
        val season = seasonRepository.findSeasonById(seasonId)
        return if (season != null && user != null) checkSeasonAccess(season, user) else false
    }

    fun hasFixtureAccess(fixtureId: Long): Boolean {
        val user = getUser()
        val fixture = fixtureRepository.findFixtureById(fixtureId)
        return if (fixture != null && user != null)
            return (checkSeasonAccess(fixture.season, user) ||
                    checkTeamAccess(fixture.homeTeam, user) ||
                    checkTeamAccess(fixture.awayTeam, user))
        else false
    }

    companion object {
        fun checkLeagueAccess(leagueId: Long, user: User): Boolean {
            return user.leagues.any { league -> league.id == leagueId }
        }

        fun checkTeamAccess(team: Team, user: User): Boolean {
            return user.teams.any { t -> t.id == team.id } || checkLeagueAccess(team.league.id, user)
        }

        fun checkSeasonAccess(season: Season, user: User): Boolean {
            return checkLeagueAccess(season.league.id, user)
        }
    }
}

