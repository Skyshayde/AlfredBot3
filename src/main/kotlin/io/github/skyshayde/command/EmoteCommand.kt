package io.github.skyshayde.command

import com.darichey.discord.Command
import com.darichey.discord.CommandContext
import io.github.skyshayde.AlfredBot
import sx.blah.discord.handle.obj.Permissions
import java.time.LocalDateTime
import java.time.ZoneOffset

class EmoteCommand {

    init {
        val role = Command.builder()
                .onCalled { ctx ->
                    val cmdArgs = ctx.args
                    if (cmdArgs.size > 0) {
                        if (cmdArgs[0] == "stats") {
                            if(ctx.author.getPermissionsForGuild(ctx.guild).contains(Permissions.MANAGE_EMOJIS)) {
                                ctx.channel.sendMessage("Calculating emote usage")
                                ctx.channel.sendMessage("```${stats(ctx)}```")
                            }
                        }
                    }
                }
                .build()
        AlfredBot.registry.register(role, "emote")
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
        val sum = emoteId.entries.sumBy { (k,v) -> if(ctx.guild.getEmojiByID(k) != null) v else 0  }
        val data: MutableList<Triple<String, String, String>> = mutableListOf()
        for((key, value) in emoteId.entries.sortedByDescending { it.value }){
            val emote = ctx.guild.getEmojiByID(key) ?: continue
            data.add(Triple(":${emote.name}:", value.toString(), "%.2f".format((value.toFloat()/sum) * 100)+"%"))
        }
        val col1Length = data.maxBy { it.first.length }!!.first.length
        val col2Length = data.maxBy { it.second.length }!!.second.length
        val col3Length = data.maxBy { it.third.length }!!.third.length
        val output: MutableList<String> = mutableListOf()
        val spacer: String = "\n╠═${"".padEnd(col1Length,'═')}═╬═${"".padEnd(col2Length,'═')}═╬═${"".padEnd(col3Length,'═')}═╣\n"
        val prefix: String = "╔${"".padEnd(col1Length+2,'═')}╦${"".padEnd(col2Length+2,'═')}╦${"".padEnd(col3Length+2,'═')}╗\n║ ${"Name".padEnd(col1Length)} ║ ${"#".padEnd(col2Length)} ║ ${"%".padEnd(col3Length)} ║ $spacer"
        val postfix: String = "\n╚${"".padEnd(col1Length+2,'═')}╩${"".padEnd(col2Length+2,'═')}╩${"".padEnd(col3Length+2,'═')}╝"
        for (triple in data) {
            val paddedName: String = triple.first.padEnd(col1Length)
            val paddedValue: String = triple.second.padEnd(col2Length)
            val paddedPercent: String = triple.third.padEnd(col3Length)
            output.add("║ $paddedName ║ $paddedValue ║ $paddedPercent ║")
        }
        return prefix + output.joinToString(spacer) + postfix
    }

}

