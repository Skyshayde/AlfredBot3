package io.github.skyshayde

import ch.qos.logback.classic.Level
import io.github.skyshayde.command.*
import io.github.skyshayde.command.admin.RoleAdminCommand
import io.github.skyshayde.features.ServerNameRotation
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.LoggerContext
import discord4j.core.DiscordClient
import discord4j.core.DiscordClientBuilder
import discord4j.core.event.EventDispatcher
import discord4j.core.event.domain.lifecycle.ReadyEvent
import io.github.skyshayde.features.NoMoreWaifuAlts


fun main() {}


class AlfredBot {

    init {
        RoleCommand("role")
        RoleListCommand("roles")
        RoleAdminCommand("addrole")
        EmoteCommand("emote")
        HelpCommand("help")

        val nameRotation = ServerNameRotation()
        val waifuAlts = NoMoreWaifuAlts()


        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        val rootLogger = loggerContext.getLogger("org.mongodb.driver")
        rootLogger.level = Level.OFF

        dispatcher.on(ReadyEvent::class.java).subscribe(this::onReadyEvent)
    }

    companion object {
        private var DISCORD_TOKEN: String = System.getenv("DISCORD_TOKEN")
        var MONGODB_URI: String = System.getenv("MONGODB_URI")
        val client: DiscordClient = DiscordClientBuilder(DISCORD_TOKEN).build();
        var dispatcher: EventDispatcher = client.eventDispatcher
        val db = DBManager()
        val commands: MutableMap<String, Command> = mutableMapOf()
        val instance: AlfredBot = AlfredBot()

        fun tablify(list: List<Map<String, String>>): String {
            if (list.isEmpty()) return "Empty"
            val columnLengths = mutableListOf<Int>()
            for (key in list[0].keys) {
                columnLengths.add(list.maxBy { it.getValue(key).length }!!.getValue(key).length)
            }

            val output: MutableList<String> = mutableListOf()
            val spacer = "\n╠═${columnLengths.joinToString("═╬═") { "".padEnd(it, '═') }}═╣\n"
            var prefix = "╔${columnLengths.joinToString("╦") { "".padEnd(it + 2, '═') }}╗"
            prefix += "\n║ ${columnLengths.mapIndexed { index, it -> list[0].keys.elementAt(index).padEnd(it) }.joinToString(" ║ ")} ║ $spacer"
            val postfix = "\n╚${columnLengths.joinToString("╩") { "".padEnd(it + 2, '═') }}╝"
            for (triple in list) {
                output.add("║ ${columnLengths.mapIndexed { index, it -> triple.values.elementAt(index).padEnd(it) }.joinToString(" ║ ")} ║")
            }
            return prefix + output.joinToString(spacer) + postfix
        }

    }

    fun onReadyEvent(event: ReadyEvent) {
        println("AlfredBot has started - " + event.client.applicationInfo.block().id)
    }

}