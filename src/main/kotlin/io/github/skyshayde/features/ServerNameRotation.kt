package io.github.skyshayde.features

import com.darichey.discord.Command
import io.github.skyshayde.AlfredBot
import io.github.skyshayde.structures.GuildName
import org.litote.kmongo.*
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.Permissions

class ServerNameRotation {

    init {
        val cmd = Command.builder()
                .onCalled { ctx ->
                    val hasPermission = ctx.author.getPermissionsForGuild(ctx.guild).contains(Permissions.ADMINISTRATOR)
                    val isMe = ctx.author.longID == 174667467509989376
                    if (hasPermission or isMe) {
                        if (ctx.args.size > 1) {
                            ctx.channel.sendMessage(when (ctx.args.first()) {
                                "add" -> add(ctx.args.drop(1).joinToString(" "))
                                "remove" -> remove(ctx.args.drop(1).joinToString(" "))
                                else -> "Sorry, ${ctx.args.first()} is not a valid subcommand.  "
                            })
                        } else {
                            ctx.channel.sendMessage(when (ctx.args.first()) {
                                "" -> rotate(ctx.guild)
                                "list" -> list()
                                else -> "Sorry, ${ctx.args.first()} is not a valid subcommand.  "
                            })
                        }
                    } else {
                        ctx.channel.sendMessage("Sorry, you don't have permission to execute these commands.  ")
                    }
                }
                .build()
        AlfredBot.registry.register(cmd, "namerotate")
    }

    private fun list(): String {
        val names = AlfredBot.db.nameRotateData.find().toMutableList().map { mapOf("Name" to it._id) }
        return if (names.isEmpty()) "No names are stored" else "```${AlfredBot.tablify(names)}```"
    }

    private fun add(name: String): String {
        AlfredBot.db.nameRotateData.insertOne(GuildName(name))
        return "Added name: $name"
    }

    private fun remove(name: String): String {
        AlfredBot.db.nameRotateData.deleteOne(GuildName::_id eq name)
        return "Removed name: $name"
    }

    private fun rotate(guild: IGuild): String {
        val name = AlfredBot.db.nameRotateData.find().shuffled().first()._id
        guild.changeName(name)
        return "Set name to $name"
    }
}