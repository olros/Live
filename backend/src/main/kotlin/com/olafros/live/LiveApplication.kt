package com.olafros.live

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import java.util.*

@SpringBootApplication
class LiveApplication

fun main(args: Array<String>) {
    SpringApplicationBuilder(LiveApplication::class.java)
        .properties(props())
        .build()
        .run(*args)
}

private fun props(): Properties {
    val properties = Properties()
    print("\n Get Properties: \n")
    try {
        val connectString = System.getenv("MYSQLCONNSTR_localdb")
        print("\n" + connectString + "\n")
        var database = ""
        var port = ""

        val strArray = connectString.split(";".toRegex()).toTypedArray()
        for (i in strArray.indices) {
            val paramArray = strArray[i].split("=".toRegex()).toTypedArray()
            when (i) {
                0 -> database = paramArray[1]
                1 -> port = paramArray[1]
                2 -> properties.setProperty("spring.datasource.username", paramArray[1])
                3 -> properties.setProperty("spring.datasource.password", paramArray[1])
            }
        }

        val url =
            ("jdbc:mysql://$port/$database?autoReconnect=true&failOverReadOnly=false&maxReconnects=10&allowPublicKeyRetrieval=true&serverTimezone=UTC")
        properties.setProperty("spring.datasource.url", url)
    } catch (e: Exception) {
        print("\n Oh no \n")
    }
    return properties
}