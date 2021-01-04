package com.olafros.live.controller

import com.olafros.live.APIConstants
import com.olafros.live.model.CreateUserDto
import com.olafros.live.model.User
import com.olafros.live.payload.request.LoginRequest
import com.olafros.live.payload.response.JwtResponse
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.UserRepository
import com.olafros.live.security.jwt.JwtUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/${APIConstants.BASE}/${APIConstants.AUTH}")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder,
    private val jwtUtils: JwtUtils,
) {

    @PostMapping("/signin")
    fun authenticateUser(@RequestBody loginRequest: @Valid LoginRequest): ResponseEntity<*> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password)
        )
        SecurityContextHolder.getContext().authentication = authentication
        val jwt = jwtUtils.generateJwtToken(authentication)
        return ResponseEntity.ok<Any>(JwtResponse(jwt))
    }

    @PostMapping("/signup")
    fun registerUser(@RequestBody signUpRequest: @Valid CreateUserDto): ResponseEntity<*> {
        if (userRepository.existsByEmail(signUpRequest.email)) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body<Any>(MessageResponse("Email is already in use!"))
        }

        // Create new user's account
        val user = User(0, signUpRequest.name, signUpRequest.email, encoder.encode(signUpRequest.password))
        userRepository.save<User>(user)
        return ResponseEntity.ok<Any>(MessageResponse("User registered successfully!"))
    }
}