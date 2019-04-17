package io.github.skyshayde.command

import com.darichey.discord.Command
import io.github.skyshayde.AlfredBot
import sx.blah.discord.handle.obj.IRole
import sx.blah.discord.handle.obj.IUser
import java.util.*

class RoleCommand {

    init {
        val role = Command.builder()
                .onCalled { ctx ->
                    val cmdArgs = ctx.args
                    if (cmdArgs.size > 0) {
                        val roles = AlfredBot.db.getRolesByGuildId(ctx.guild.longID)
                        val role = ctx.guild.getRolesByName(roles[cmdArgs[0].toLowerCase()]).first()
                        val onoff = roleToggle(role, ctx.author)
                        ctx.channel.sendMessage("Toggling role " + role.name + (if (onoff) " on" else " off") + " for " + ctx.author.name)
                    }
                }
                .build()
        val roles = Command.builder()
                .onCalled { ctx ->
                    val table = AlfredBot.db.getRolesByGuildId(ctx.guild.longID).map { mapOf("Key" to it.key, "Role" to it.value) }
                    ctx.channel.sendMessage("```${EmoteCommand.tablify(table)}```")
                }
                .build()
        AlfredBot.registry.register(role, "role")
        AlfredBot.registry.register(roles, "roles")


    }

    private fun roleToggle(role: IRole, user: IUser): Boolean {
        return if (user.hasRole(role)) {
            user.removeRole(role)
            false
        } else {
            user.addRole(role)
            true
        }
    }
}
