package io.github.skyshayde.command

import com.darichey.discord.CommandContext
import io.github.skyshayde.AlfredBot

class RoleListCommand(name: String) : io.github.skyshayde.command.Command(name) {
    override val help: String
        get() = ""

    override fun execute(ctx: CommandContext) {
        val table = AlfredBot.db.getRolesByGuildId(ctx.guild.longID).map { mapOf("Key" to it.key, "Role" to it.value) }
        ctx.channel.sendMessage("```${AlfredBot.tablify(table)}```")
    }

}