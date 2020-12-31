package com.olafros.live.controller

import com.olafros.live.model.UpdateUserDto
import com.olafros.live.model.User
import com.olafros.live.model.UserDto
import com.olafros.live.model.toUserDto
import com.olafros.live.repository.UserRepository
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.validation.Valid

@RestController
@RequestMapping("/api/users")
class UserController(val userRepository: UserRepository, val securityService: SecurityService) {

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun getUser(principal: Principal): ResponseEntity<UserDto> {
        val user = securityService.getUser(principal.name)
        return ResponseEntity.ok(user.toUserDto())
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    fun registerUser(@RequestBody updatedUser: @Valid UpdateUserDto, principal: Principal): ResponseEntity<UserDto> {
        val existingUser = securityService.getUser(principal.name)
        val user: User = existingUser.copy(name = updatedUser.name ?: existingUser.name)
        userRepository.save<User>(user)
        return ResponseEntity.ok(user.toUserDto())
    }
}