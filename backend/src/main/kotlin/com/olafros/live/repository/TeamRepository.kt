package com.olafros.live.repository

import com.olafros.live.model.Team
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TeamRepository : JpaRepository<Team, Long> {
    fun findByName(name: String): Optional<Team>
    fun findByLeague_Id(id: Long): List<Team>
    fun findByLeague_IdAndId(leagueId: Long, id: Long): Optional<Team>
}