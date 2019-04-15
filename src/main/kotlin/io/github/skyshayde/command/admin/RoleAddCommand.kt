package io.github.skyshayde.command.admin

import com.darichey.discord.Command
import io.github.skyshayde.AlfredBot

class RoleAddCommand {

    init {
        val role = Command.builder()
                .onCalled { ctx ->
                    println(ctx)
                }
                .build()
        AlfredBot.registry.register(role, "addrole")
    }
}
