package com.olafros.live.payload.request

import javax.validation.constraints.NotBlank

data class LoginRequest (val username: @NotBlank String, val password: @NotBlank String)