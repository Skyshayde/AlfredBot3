package io.github.skyshayde.features

import discord4j.core.`object`.entity.Message
import discord4j.core.event.domain.message.MessageCreateEvent
import io.github.skyshayde.AlfredBot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
class LevelupCleaner {

    init {
        AlfredBot.dispatcher.on(MessageCreateEvent::class.java).map(MessageCreateEvent::getMessage).subscribe(this::processMessage)
    }

    companion object {
        private const val POKECORD_ID: Long = 365975655608745985
    }

    private fun processMessage(m: Message) {
        // Only work in Bat Cave
        if (m.guild.block().id.asLong() == 283862902920839169) {
            if (m.author.get().id.asLong() == POKECORD_ID && m.embeds.size > 0) {
                if (m.embeds.last().description.isPresent && m.embeds.last().description.get().matches(Regex("Your (.*?) is now level (.*?)!"))) {
                    GlobalScope.launch {
                        delay(30000)
                        m.delete()
                    }
                }
            }
        }
    }
}