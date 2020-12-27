package com.olafros.live.payload.response

class JwtResponse(val accessToken: String, val id: Long, val email: String, val roles: List<String>) {
    var tokenType = "Bearer"
}