package com.olafros.live.payload.request

import javax.validation.constraints.NotBlank

class LoginRequest (val email: @NotBlank String, val password: @NotBlank String)