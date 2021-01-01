package com.olafros.live.repository

import com.olafros.live.model.Team
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamRepository : JpaRepository<Team, Long> {
    fun findAllByLeague_Id(id: Long): List<Team>
    fun findAllBySeasonsContains(id: Long): List<Team>
}