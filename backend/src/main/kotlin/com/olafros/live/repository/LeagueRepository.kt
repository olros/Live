package com.olafros.live.repository

import com.olafros.live.model.League
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LeagueRepository : JpaRepository<League, Long> {
    fun findByNameContaining(name: String): League
}