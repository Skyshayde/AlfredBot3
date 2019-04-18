package io.github.skyshayde

import com.mongodb.ConnectionString
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import io.github.skyshayde.structures.GuildData
import org.litote.kmongo.*


class DBManager {
    private val client: MongoClient = KMongo.createClient(ConnectionString(AlfredBot.MONGODB_URI))
    private val database: MongoDatabase = client.getDatabase("heroku_05vvg0pg")
    private val collection = database.getCollection<GuildData>("guilddata")

    fun getGuildById(id: Long): GuildData {
        return collection.findOne(GuildData::_id eq id) ?: GuildData(id)
    }

    fun setGuildById(id: Long, guildData: GuildData) {
        collection.save(guildData)
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