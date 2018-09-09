package io.github.skyshayde

import io.github.skyshayde.structures.GuildData
import io.github.skyshayde.structures.ServerData
import org.dizitart.kno2.nitrite
import org.dizitart.no2.objects.ObjectRepository
import org.dizitart.no2.objects.filters.ObjectFilters
import sx.blah.discord.handle.obj.IGuild
import java.io.File


class ConfigManager {

    val db = nitrite {
        file = File("AlfredBot.db")       // or, path = fileName
        autoCommitBufferSize = 2048
        compress = true
        autoCompact = false
    }
    val servers: ObjectRepository<ServerData> = db.getRepository(ServerData::class.java)
    val guilds: ObjectRepository<GuildData> = db.getRepository(GuildData::class.java)

    constructor() {

    }


    fun getRoles(guild : IGuild) : Map<String, String> {
        var guild : GuildData? = guilds.find(ObjectFilters.eq("ID", guild.longID)).firstOrNull()
        if(guild != null) {
            return guild.roles;
        }
        return emptyMap()
    }
}