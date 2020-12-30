package com.olafros.live.repository

import com.olafros.live.model.Season
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SeasonRepository : JpaRepository<Season, Long> {
    fun findByLeague_Id(id: Long): List<Season>
}