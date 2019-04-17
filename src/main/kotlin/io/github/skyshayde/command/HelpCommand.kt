package io.github.skyshayde.command

import com.darichey.discord.CommandContext
import io.github.skyshayde.AlfredBot

class HelpCommand(name: String) : io.github.skyshayde.command.Command(name) {
    override val desc: String
        get() = "List commands or usage of specific command.  "

    override val help: String
        get() = "" +
                "Name: help\n" +
                "Usage: hey alfred, help <command>\n" +
                "Notes: If you've got this far you should already know how to use this command.  "

    override fun execute(ctx: CommandContext) {
        if(ctx.args.first() == "") {
            val msg: String = AlfredBot.tablify(AlfredBot.commands.map { mapOf("Name" to it.key, "Description" to it.value.desc)})
            ctx.channel.sendMessage("```$msg```")
        } else {
            val cmd = AlfredBot.commands[ctx.args.first().toLowerCase()]
            if(cmd != null) {
                ctx.channel.sendMessage("```${cmd.help}```")
            } else {
                ctx.channel.sendMessage("Sorry, that command can't be found.  Maybe check spelling?  ")
            }

        }
    }
}
