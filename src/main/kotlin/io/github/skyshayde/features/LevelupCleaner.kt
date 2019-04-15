package io.github.skyshayde.features

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage

class LevelupCleaner {
    companion object {
        private val POKECORD_ID: Long = 365975655608745985
    }

    @EventSubscriber
    fun onChatMessage(event: MessageReceivedEvent) {
        val m: IMessage = event.message
        if (m.author.longID == POKECORD_ID && m.embeds.size > 0) {
            if (m.embeds.last().description != null && m.embeds.last().description.matches(Regex("Your (.*?) is now level (.*?)!"))) {
                GlobalScope.launch {
                    delay(30000)
                    m.delete()
                }
            }
        }
    }
}