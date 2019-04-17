package io.github.skyshayde.command

import com.darichey.discord.Command
import com.darichey.discord.CommandContext
import io.github.skyshayde.AlfredBot
import sx.blah.discord.handle.obj.Permissions
import java.time.LocalDateTime
import java.time.ZoneOffset

class EmoteCommand(name: String) : io.github.skyshayde.command.Command(name) {
    override val help: String
        get() = ""

    override fun execute(ctx: CommandContext) {
        val cmdArgs = ctx.args
        if (cmdArgs.size > 0) {
            if (cmdArgs[0] == "stats") {
                if (ctx.author.getPermissionsForGuild(ctx.guild).contains(Permissions.MANAGE_EMOJIS)) {
                    ctx.channel.sendMessage("Calculating emote usage")
                    val messages: MutableList<String> = mutableListOf()
                    val stats: MutableList<String> = stats(ctx).split("\n").toMutableList()
                    var temp = ""
                    while (stats.size > 0) {
                        if (temp.length + stats.first().length > 1994) {
                            messages.add(temp)
                            temp = ""
                        }
                        temp += stats.first() + "\n"
                        stats.removeAt(0)
                    }
                    messages.add(temp)
                    messages.forEach { ctx.channel.sendMessage("```$it```") }

                }
            }
        }
    }

    private fun stats(ctx: CommandContext): String {
        val emoteId = mutableMapOf<Long, Int>()
        val lastMonthTime = LocalDateTime.now().plusMonths(-1).toInstant(ZoneOffset.UTC)
        val emoteRegex: Regex = Regex("<:(.*?):(.*?)>")
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

}

