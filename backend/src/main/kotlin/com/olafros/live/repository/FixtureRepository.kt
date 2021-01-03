package com.olafros.live.repository

import com.olafros.live.model.Fixture
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FixtureRepository : JpaRepository<Fixture, Long> {
    fun findFixtureById(id: Long?): Fixture?
    fun findAllBySeason_Id(id: Long): List<Fixture>
    fun findAllByHomeTeam_IdOrAwayTeam_Id(homeTeamId: Long, awayTeamId: Long): List<Fixture>
}