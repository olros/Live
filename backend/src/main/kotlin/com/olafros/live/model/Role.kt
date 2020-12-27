package com.olafros.live.model

import javax.persistence.*


@Entity
data class Role (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int,

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    var name: ERole
)