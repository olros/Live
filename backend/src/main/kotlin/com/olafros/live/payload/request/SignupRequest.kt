package com.olafros.live.payload.request

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


class SignupRequest (
    var email: @NotBlank @Size(max = 50) @Email String,
    var role: Set<String>? = null,
    var password: @NotBlank @Size(min = 6, max = 40) String,
)