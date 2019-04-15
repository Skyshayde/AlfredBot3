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
                            if (ctx.author.getPermissionsForGuild(ctx.guild).contains(Permissions.MANAGE_EMOJIS)) {
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
        val sum = emoteId.entries.sumBy { (k, v) -> if (ctx.guild.getEmojiByID(k) != null) v else 0 }
        val data: MutableList<Triple<String, String, String>> = mutableListOf()
        for ((key, value) in emoteId.entries.sortedByDescending { it.value }) {
            val emote = ctx.guild.getEmojiByID(key) ?: continue
            data.add(Triple(":${emote.name}:", value.toString(), "%.2f".format((value.toFloat() / sum) * 100) + "%"))
        }
        val t: MutableList<Map<String, String>> = mutableListOf()
        for(i in data) {
            val a: MutableMap<String, String> = mutableMapOf()
            a["Name"] = i.first
            a["#"] = i.second
            a["%"] = i.third
            t.add(a)
        }
        return tablify(t)
    }

    fun tablify(list: List<Map<String, String>>): String {
        val columnLengths = mutableListOf<Int>()
        for(key in list[0].keys) {
            columnLengths.add(list.maxBy { it.get(key)!!.length }!!.get(key)!!.length)
        }

        val output: MutableList<String> = mutableListOf()
        val spacer = "\n╠═${columnLengths.map { "".padEnd(it,'═') }.joinToString("═╬═")}═╣\n"
        var prefix = "╔${columnLengths.map { "".padEnd(it + 2,'═') }.joinToString("╦")}╗"
        prefix += "\n║ ${columnLengths.mapIndexed {index, it -> list[0].keys.elementAt(index).padEnd(it)}.joinToString(" ║ ")} ║ $spacer"
        val postfix = "╚${columnLengths.map { "".padEnd(it + 2,'═') }.joinToString("╩")}╝"
        for (triple in list) {
            output.add("║ ${columnLengths.mapIndexed {index, it -> list[0].values.elementAt(index).padEnd(it)}.joinToString(" ║ ") } ║\n")
        }
        return prefix + output.joinToString(spacer) + postfix
    }

}

