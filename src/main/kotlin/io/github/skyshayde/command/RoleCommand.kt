package io.github.skyshayde.command

import com.darichey.discord.CommandContext
import io.github.skyshayde.AlfredBot
import sx.blah.discord.handle.obj.IRole
import sx.blah.discord.handle.obj.IUser

class RoleCommand(name: String) : io.github.skyshayde.command.Command(name) {
    override val desc: String
        get() = "Toggle game roles.  "

    override val help: String
        get() = "" +
                "Name: role\n" +
                "Usage: hey alfred, role <role>\n" +
                "Example: hey alfred, role overwatch\n"

    override fun execute(ctx: CommandContext) {
        val cmdArgs = ctx.args
        if (cmdArgs.size > 0) {
            val roles = AlfredBot.db.getRolesByGuildId(ctx.guild.longID)
            val role = ctx.guild.getRolesByName(roles[cmdArgs[0].toLowerCase()]).first()
            val onoff = roleToggle(role, ctx.author)
            ctx.channel.sendMessage("Toggling role " + role.name + (if (onoff) " on" else " off") + " for " + ctx.author.name)
        }
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
