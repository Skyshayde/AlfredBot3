package io.github.skyshayde

import ch.qos.logback.classic.Level
import com.darichey.discord.CommandListener
import com.darichey.discord.CommandRegistry
import io.github.skyshayde.command.*
import io.github.skyshayde.command.admin.RoleAdminCommand
import io.github.skyshayde.features.LevelupCleaner
import io.github.skyshayde.features.ServerNameRotation
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.util.DiscordException
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.LoggerContext




fun main() {
    AlfredBot.dispatcher?.registerListener(AlfredBot())
    AlfredBot.dispatcher?.registerListener(CommandListener(AlfredBot.registry))
    AlfredBot.dispatcher?.registerListener(LevelupCleaner())
    AlfredBot.dispatcher?.registerListener(ServerNameRotation())

}


fun createClient(token: String, login: Boolean): IDiscordClient? { // Returns a new instance of the Discord client
    val clientBuilder = ClientBuilder() // Creates the ClientBuilder instance
    clientBuilder.withToken(token) // Adds the login info to the builder
    return try {
        if (login) {
            clientBuilder.login() // Creates the client instance and logs the client in
        } else {
            clientBuilder.build() // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
        }
    } catch (e: DiscordException) { // This is thrown if there was a problem building the client
        e.printStackTrace()
        null
    }
}

class AlfredBot {

    init {
        RoleCommand("role")
        RoleListCommand("roles")
        RoleAdminCommand("addrole")
        EmoteCommand("emote")
        HelpCommand("help")

        ServerCommand()
        ServerNameRotation()



        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        val rootLogger = loggerContext.getLogger("org.mongodb.driver")
        rootLogger.level = Level.OFF
    }

    companion object {
        private var DISCORD_TOKEN: String = System.getenv("DISCORD_TOKEN")
        var GCP_APP_NAME: String = System.getenv("GCP_APP_NAME")
        var MONGODB_URI: String = System.getenv("MONGODB_URI")
        private var client: IDiscordClient? = createClient(DISCORD_TOKEN, true)
        var dispatcher: EventDispatcher? = client?.dispatcher
        var registry = CommandRegistry("hey alfred, ")
        val db = DBManager()
        val commands: MutableMap<String, Command> = mutableMapOf()

        fun tablify(list: List<Map<String, String>>): String {
            if(list.isEmpty()) return "Empty"
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

    @EventSubscriber
    fun onReadyEvent(event: ReadyEvent) {
        println("AlfredBot has started - " + event.client.applicationClientID)
    }

}