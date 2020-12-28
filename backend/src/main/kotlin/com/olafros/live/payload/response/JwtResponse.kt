package com.olafros.live.payload.response

class JwtResponse(val accessToken: String, val id: Long, val name: String, val email: String) {
    var tokenType = "Bearer"
}