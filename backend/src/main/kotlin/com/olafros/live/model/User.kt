package com.olafros.live.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = ["email"])])
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0,
        var name: @NotBlank @Size(max = 128) String,
        var email: @NotBlank @Size(max = 64) @Email String,
        @JsonIgnore
        var password: @NotBlank @Size(max = 128) String,

        @ManyToMany(mappedBy = "admins", fetch = FetchType.LAZY)
        @JsonManagedReference
        var leagues: MutableList<League> = mutableListOf()
)

data class UserDto(val id: Long, val name: String, val email: String, val leagues: List<LeagueDtoList>)
data class UserDtoList(val id: Long, val name: String, val email: String)
data class CreateUserDto(val name: String, val email: String, val password: String)
data class UpdateUserDto(val name: String?)

fun User.toUserDto(): UserDto {
    return UserDto(this.id, this.name, this.email, this.leagues.map { league -> league.toLeagueDtoList() })
}

fun User.toUserDtoList(): UserDtoList {
    return UserDtoList(this.id, this.name, this.email)
}
