package io.github.skyshayde.features

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage

class NoMoreWaifuAlts {
    companion object {
        private const val MUDAE_ID: Long = 432610292342587392
        private const val MUDAMAID_ID: Long = 594159570688147466

        fun processMessage(m: IMessage) {
            if (m.author.longID == MUDAE_ID || m.author.longID == MUDAMAID_ID) {
                m.embeds.forEach { i ->
                    if (i.author?.name?.contains("Lord Shaxx") == true) {
                        GlobalScope.launch {
                            delay(500)
                            m.reactions.forEach { i ->
                                m.addReaction(i)
                            }
                        }
                    }
                }
            }
        }
    }

    @EventSubscriber
    fun onChatMessage(event: MessageReceivedEvent) {
        println(event.message)
        // Only work in Bat Cave
        if (event.guild.longID == 283862902920839169) {
            val m: IMessage = event.message
            processMessage(m)
        }
    }


}
