package com.olafros.live.repository

import com.olafros.live.model.ERole
import com.olafros.live.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: ERole): Role
}