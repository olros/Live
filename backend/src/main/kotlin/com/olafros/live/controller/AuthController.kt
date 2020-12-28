package com.olafros.live.controller

import com.olafros.live.model.User
import com.olafros.live.payload.request.LoginRequest
import com.olafros.live.payload.request.SignupRequest
import com.olafros.live.payload.response.JwtResponse
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.UserRepository
import com.olafros.live.security.jwt.JwtUtils
import com.olafros.live.security.services.UserDetailsImpl
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
class AuthController(
        private val authenticationManager: AuthenticationManager,
        private val userRepository: UserRepository,
        private val encoder: PasswordEncoder,
        private val jwtUtils: JwtUtils,
) {

    @PostMapping("/signin")
    fun authenticateUser(@RequestBody loginRequest: @Valid LoginRequest): ResponseEntity<*> {
        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password))
        SecurityContextHolder.getContext().authentication = authentication
        val jwt = jwtUtils.generateJwtToken(authentication)
        val userDetails = authentication.principal as UserDetailsImpl
        return ResponseEntity.ok<Any>(JwtResponse(jwt,
                userDetails.id,
                userDetails.name,
                userDetails.email))
    }

    @PostMapping("/signup")
    fun registerUser(@RequestBody signUpRequest: @Valid SignupRequest): ResponseEntity<*> {
        if (userRepository.existsByEmail(signUpRequest.email)) {
            return ResponseEntity
                    .status(409)
                    .body<Any>(MessageResponse("Email is already in use!"))
        }

        // Create new user's account
        val user = User(0, signUpRequest.name, signUpRequest.email, encoder.encode(signUpRequest.password))
        userRepository.save<User>(user)
        return ResponseEntity.ok<Any>(MessageResponse("User registered successfully!"))
    }
}