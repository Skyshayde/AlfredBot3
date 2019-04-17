package io.github.skyshayde.structures


data class GuildData(
        val _id: Long,
        val roles: MutableMap<String, String> = mutableMapOf())

