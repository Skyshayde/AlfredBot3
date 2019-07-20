package io.github.skyshayde.features

import discord4j.core.`object`.entity.Message
import discord4j.core.event.domain.message.MessageCreateEvent
import io.github.skyshayde.AlfredBot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NoMoreWaifuAlts {

    init {
        AlfredBot.dispatcher.on(MessageCreateEvent::class.java).map(MessageCreateEvent::getMessage).subscribe(this::processMessage)
    }

    companion object {
        private const val MUDAE_ID: Long = 432610292342587392
        private const val MUDAMAID_ID: Long = 594159570688147466
    }

    private fun processMessage(m: Message) {
        if (m.author.get().id.asLong() == MUDAE_ID || m.author.get().id.asLong() == MUDAMAID_ID) {
            m.embeds.filter { it.author.get().name.contains("Lord Shaxx") }.forEach {
                GlobalScope.launch {
                    delay(100)
                    m.reactions.forEach {
                        m.addReaction(it.emoji)
                    }
                }
            }
        }
    }

}
