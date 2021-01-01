package com.olafros.live.controller

import com.olafros.live.model.UpdateUserDto
import com.olafros.live.model.User
import com.olafros.live.model.toUserDto
import com.olafros.live.payload.response.MessageResponse
import com.olafros.live.repository.UserRepository
import com.olafros.live.security.authorize.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/users")
class UserController(val userRepository: UserRepository, val securityService: SecurityService) {

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun getUser(): ResponseEntity<*> {
        val user = securityService.getUser()
        return if (user.isPresent)
            ResponseEntity.ok(user.get().toUserDto())
        else
            ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find user"))
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    fun registerUser(@RequestBody updatedUser: @Valid UpdateUserDto): ResponseEntity<*> {
        val existingUser = securityService.getUser()
        return if (existingUser.isPresent) {
            val user: User = existingUser.get().copy(name = updatedUser.name ?: existingUser.get().name)
            userRepository.save<User>(user)
            ResponseEntity.ok(user.toUserDto())
        } else ResponseEntity.status(HttpStatus.NOT_FOUND).body<Any>(MessageResponse("Could not find user"))
    }
}