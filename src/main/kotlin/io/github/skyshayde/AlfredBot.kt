package io.github.skyshayde

import com.darichey.discord.CommandListener
import com.darichey.discord.CommandRegistry
import io.github.skyshayde.command.EmoteCommand
import io.github.skyshayde.command.RoleCommand
import io.github.skyshayde.command.ServerCommand
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.EventDispatcher
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.util.DiscordException


fun main(args: Array<String>) {
    AlfredBot.dispatcher?.registerListener(AlfredBot());
    AlfredBot.dispatcher?.registerListener(CommandListener(AlfredBot.registry));

}


fun createClient(token: String, login: Boolean): IDiscordClient? { // Returns a new instance of the Discord client
    val clientBuilder = ClientBuilder() // Creates the ClientBuilder instance
    clientBuilder.withToken(token) // Adds the login info to the builder
    try {
        return if (login) {
            clientBuilder.login() // Creates the client instance and logs the client in
        } else {
            clientBuilder.build() // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
        }
    } catch (e: DiscordException) { // This is thrown if there was a problem building the client
        e.printStackTrace()
        return null
    }
}

class AlfredBot() {

    init {
        var roleCommand: RoleCommand = RoleCommand();
        var serverCommand: ServerCommand = ServerCommand();
        var emoteCommand: EmoteCommand = EmoteCommand();
    }

    companion object {
        var DISCORD_TOKEN: String = System.getenv("DISCORD_TOKEN")
        var GCP_TOKEN: String = System.getenv("GCP_TOKEN")
        var GCP_APP_NAME: String = System.getenv("GCP_APP_NAME")
        var client: IDiscordClient? = createClient(DISCORD_TOKEN, true);
        var dispatcher: EventDispatcher? = client?.getDispatcher();
        public var registry = CommandRegistry("hey alfred, ")
    }

    @EventSubscriber
    fun onReadyEvent(event: ReadyEvent) {
        println("AlfredBot has started - " + event.client.applicationClientID)
    }

    fun writeConfig() {

    }

    fun readConfig() {

    }
}