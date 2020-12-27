package com.olafros.live

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LiveApplication

fun main(args: Array<String>) {
    runApplication<LiveApplication>(*args)
}
