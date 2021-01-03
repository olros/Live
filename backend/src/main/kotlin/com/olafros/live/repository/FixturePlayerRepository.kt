package com.olafros.live.repository

import com.olafros.live.model.FixturePlayer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FixturePlayerRepository : JpaRepository<FixturePlayer, Long> {
    fun findFixturePlayerById(id: Long?): FixturePlayer?
    fun findAllByFixture_Id(fixtureId: Long): List<FixturePlayer>
}