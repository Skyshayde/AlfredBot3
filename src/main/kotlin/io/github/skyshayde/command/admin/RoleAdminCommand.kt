package io.github.skyshayde.command.admin

import com.darichey.discord.Command
import com.darichey.discord.CommandContext
import io.github.skyshayde.AlfredBot
import sx.blah.discord.handle.obj.Permissions

class RoleAdminCommand(name: String) : io.github.skyshayde.command.Command(name) {
    override val desc: String
        get() = "If you have permission to manage roles, you can add toggleable roles.  "

    override val help: String
        get() = "" +
                "Name: addrole\n" +
                "Usage: hey alfred, addrole <key> <role>\n" +
                "Example: hey alfred, addrole destiny2 Destineers\n" +
                "Notes: Role is caps sensitive. Destineers will work.  destineers will not"

    override fun execute(ctx: CommandContext) {
        if (ctx.author.getPermissionsForGuild(ctx.guild).contains(Permissions.MANAGE_ROLES)) {
            AlfredBot.db.addRole(ctx.guild.longID, Pair(ctx.args[0], ctx.args[1]))
            ctx.channel.sendMessage("Role mapping added for ${ctx.args[0]} ⟺ ${ctx.args[1]}")
        } else {
            ctx.channel.sendMessage("I'm sorry, you don't have the permission to use this command.  ")
        }
    }
}
