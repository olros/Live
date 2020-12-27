package com.olafros.live.controller

import com.olafros.live.model.ERole
import com.olafros.live.model.Role
import com.olafros.live.model.User
import com.olafros.live.payload.request.LoginRequest
import com.olafros.live.payload.request.SignupRequest
import com.olafros.live.payload.response.JwtResponse
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.RoleRepository
import com.olafros.live.repository.UserRepository
import com.olafros.live.security.jwt.JwtUtils
import com.olafros.live.security.services.UserDetailsImpl
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
class AuthController(
        private val authenticationManager: AuthenticationManager,
        private val userRepository: UserRepository,
        private val roleRepository: RoleRepository,
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
        val roles = userDetails.authorities.stream()
                .map { item: GrantedAuthority -> item.authority }
                .collect(Collectors.toList())
        return ResponseEntity.ok<Any>(JwtResponse(jwt,
                userDetails.id,
                userDetails.name,
                userDetails.email,
                roles))
    }

    @PostMapping("/signup")
    fun registerUser(@RequestBody signUpRequest: @Valid SignupRequest): ResponseEntity<*> {
        if (userRepository.existsByEmail(signUpRequest.email)) {
            return ResponseEntity
                    .badRequest()
                    .body<Any>(MessageResponse("Error: Email is already in use!"))
        }

        // Create new user's account
        val user = User(0, signUpRequest.name, signUpRequest.email,
                encoder.encode(signUpRequest.password))
        val strRoles: Set<String>? = signUpRequest.role
        val roles: MutableSet<Role> = HashSet<Role>()
        if (strRoles == null) {
            val userRole: Role = roleRepository.findByName(ERole.ROLE_USER)
            roles.add(userRole)
        } else {
            strRoles.forEach(Consumer { role: String? ->
                when (role) {
                    "admin" -> {
                        val adminRole: Role = roleRepository.findByName(ERole.ROLE_ADMIN)
                        roles.add(adminRole)
                    }
                    "mod" -> {
                        val modRole: Role = roleRepository.findByName(ERole.ROLE_MODERATOR)
                        roles.add(modRole)
                    }
                    else -> {
                        val userRole: Role = roleRepository.findByName(ERole.ROLE_USER)
                        roles.add(userRole)
                    }
                }
            })
        }
        user.roles = roles
        userRepository.save<User>(user)
        return ResponseEntity.ok<Any>(MessageResponse("User registered successfully!"))
    }
}