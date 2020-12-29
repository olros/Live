package com.olafros.live.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
class TestController {
    @GetMapping("/all")
    fun allAccess(): String {
        return "Public Content."
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    fun userAccess(): String {
        return "User Content."
    }

    @GetMapping("/false")
    @PreAuthorize("authentication.name == 'olaf@gmail.com'")
    fun adminAccess(): String {
        return "Admin Board."
    }
}