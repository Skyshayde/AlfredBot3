package io.github.skyshayde.command.admin

import com.darichey.discord.Command
import io.github.skyshayde.AlfredBot
import sx.blah.discord.handle.obj.Permissions

class RoleAdminCommand {

    init {
        val addrole = Command.builder()
                .onCalled { ctx ->
                    if(ctx.author.getPermissionsForGuild(ctx.guild).contains(Permissions.MANAGE_ROLES)) {
                        AlfredBot.db.addRole(ctx.guild.longID, Pair(ctx.args[0], ctx.args[1]))
                        ctx.channel.sendMessage("Role mapping added for ${ctx.args[0]} ⟺ ${ctx.args[1]}")
                    } else {
                        ctx.channel.sendMessage("I'm sorry, you don't have the permission to use this command.  ")
                    }
                }
                .build()
        AlfredBot.registry.register(addrole, "addrole")
    }
}
