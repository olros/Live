package com.olafros.live.payload.response

class JwtResponse(val accessToken: String, val id: Long, val username: String, val email: String, val roles: List<String>) {
    var tokenType = "Bearer"
}