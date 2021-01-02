package com.olafros.live.repository

import com.olafros.live.model.FixturePlayer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FixturePlayerRepository : JpaRepository<FixturePlayer, Long> {
    fun findAllByFixture_Id(fixtureId: Long): List<FixturePlayer>
}