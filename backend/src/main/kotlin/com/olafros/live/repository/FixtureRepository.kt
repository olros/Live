package com.olafros.live.repository

import com.olafros.live.model.Fixture
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface FixtureRepository : JpaRepository<Fixture, Long> {
    fun findFixtureById(id: Long?): Fixture?

    @Query(nativeQuery = true, value = "select * from fixtures f where f.time >= :time ORDER BY f.time ASC LIMIT 10")
    fun findAllWithTimeAfter(
        @Param("time") time: OffsetDateTime?
    ): List<Fixture>
}