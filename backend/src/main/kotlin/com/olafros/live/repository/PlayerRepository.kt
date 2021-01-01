package com.olafros.live.repository

import com.olafros.live.model.Player
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : JpaRepository<Player, Long> {
    fun findAllByTeam_Id(id: Long): List<Player>
}