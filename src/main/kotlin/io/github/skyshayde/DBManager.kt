package io.github.skyshayde

import com.mongodb.ConnectionString
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import io.github.skyshayde.structures.GuildData
import io.github.skyshayde.structures.GuildName
import org.litote.kmongo.*


class DBManager {
    val client: MongoClient = KMongo.createClient(ConnectionString(AlfredBot.MONGODB_URI))
    val database: MongoDatabase = client.getDatabase("heroku_05vvg0pg")
    val guildData = database.getCollection<GuildData>("guilddata")
    val nameRotateData = database.getCollection<GuildName>("tnsnamerotation")

    fun getGuildById(id: Long): GuildData {
        return guildData.findOne(GuildData::_id eq id) ?: GuildData(id)
    }

    fun setGuildById(id: Long, guildData: GuildData) {
        this.guildData.save(guildData)
    }

    fun getRolesByGuildId(id: Long): Map<String, String> {
        return getGuildById(id).roles
    }

    fun addRole(id: Long, role: Pair<String, String>) {
        val gd = getGuildById(id)
        gd.roles[role.first] = role.second
        setGuildById(id, gd)
    }
}