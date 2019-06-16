package io.github.skyshayde.command

import com.darichey.discord.CommandContext
import com.google.api.client.util.Base64
import io.github.skyshayde.AlfredBot
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.Image
import java.io.InputStream
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class EmoteCommand(name: String) : io.github.skyshayde.command.Command(name) {
    override val desc: String
        get() = "Lists statistics for emote usage"

    override val help: String
        get() = "" +
                "Name: emote\n" +
                "Usage: hey alfred, emote stats\n" +
                "Notes: I don't really have anything else to do with this.  Ping me if you have ideas.  "

    override fun execute(ctx: CommandContext) {
        val cmdArgs = ctx.args
        if (cmdArgs.size > 0) {
            if (cmdArgs[0] == "stats") {
                if (ctx.author.getPermissionsForGuild(ctx.guild).contains(Permissions.MANAGE_EMOJIS)) {
                    ctx.channel.sendMessage("Calculating emote usage")
                    sendLongMessage(ctx.channel, stats(ctx))
                }
            }
            if(cmdArgs[0] == "add") {
                ctx.channel.sendMessage(add(ctx))
            }
        }
    }

    private fun stats(ctx: CommandContext): String {
        val emoteId = mutableMapOf<Long, Int>()
        val lastMonthTime = LocalDateTime.now().plusMonths(-1).toInstant(ZoneOffset.UTC)
        val emoteRegex = Regex("<:(.*?):(.*?)>")
        ctx.guild.channels.forEach { i ->
            run {
                for (message in i.getMessageHistoryTo(lastMonthTime)) {
                    for (matchResult in emoteRegex.findAll(message.content)) {
                        val id: Long = matchResult.groupValues.last().toLong()
                        emoteId[id] = emoteId.getOrDefault(id, 0) + 1
                    }
                    for (reaction in message.reactions) {
                        emoteId[reaction.emoji.longID] = emoteId.getOrDefault(reaction.emoji.longID, 0) + reaction.count
                    }
                }
            }
        }
        val sum = emoteId.entries.sumBy { (k, v) -> if (ctx.guild.getEmojiByID(k) != null) v else 0 }
        val data: MutableList<Triple<String, String, String>> = mutableListOf()
        for ((key, value) in emoteId.entries.sortedByDescending { it.value }) {
            val emote = ctx.guild.getEmojiByID(key) ?: continue
            data.add(Triple(":${emote.name}:", value.toString(), "%.2f".format((value.toFloat() / sum) * 100) + "%"))
        }
        val t: List<Map<String, String>> = data.map {
            mapOf("Name" to it.first, "#" to it.second, "%" to it.third)
        }
        return AlfredBot.tablify(t)
    }

    private fun add(ctx: CommandContext): String {
        if (ctx.author.hasRole(ctx.guild.getRoleByID(294203561095725057L))) {
            if (ctx.message.attachments.size > 0) {
                val image = Image.forUrl(ctx.message.attachments.first().url.split(".").last(), ctx.message.attachments.first().url)
                ctx.guild.createEmoji(ctx.args[1], image, listOf(ctx.guild.everyoneRole).toTypedArray())
                return "Success! :${ctx.args[1]}:"
            } else {
                return "Please attach a photo with this message.  "
            }
        } else {
            return "Sorry, you don't have permission to use this command.  "
        }
    }

    private fun sendLongMessage(channel: IChannel, message: String) {
        val messages: MutableList<String> = mutableListOf()
        val split = message.split("\n").toMutableList()
        var temp = ""
        while (split.size > 0) {
            if (temp.length + split.first().length > 1994) {
                messages.add(temp)
                temp = ""
            }
            temp += split.first() + "\n"
            split.removeAt(0)
        }
        messages.add(temp)
        messages.forEach { channel.sendMessage("```$it```") }

    }

}

