package com.olafros.live.repository

import com.olafros.live.model.EFixtureEvent
import com.olafros.live.model.FixtureEvent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FixtureEventRepository : JpaRepository<FixtureEvent, Long> {
    fun findAllByFixture_Id(fixtureId: Long): List<FixtureEvent>
    fun findAllByType(type: EFixtureEvent): List<FixtureEvent>
}