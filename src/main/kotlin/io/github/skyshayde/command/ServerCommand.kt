package io.github.skyshayde.command

import com.darichey.discord.Command
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.compute.Compute
import com.google.api.services.compute.ComputeScopes
import io.github.skyshayde.AlfredBot


class ServerCommand {
    companion object {
        private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
    }

    private var httpTransport: HttpTransport? = null

    init {
        val start = Command.builder()
                .onCalled { ctx ->
                    val cmdArgs = ctx.args
                    if (cmdArgs.size > 0) {
                        val compute: Compute = getCompute(AlfredBot.GCP_APP_NAME)
                        compute.instances().start(AlfredBot.GCP_APP_NAME, getZoneFromName(cmdArgs[0]), cmdArgs[0]).execute()
                        ctx.channel.sendMessage("Server is starting")
                    }
                }
                .build()
        AlfredBot.registry.register(start, "start")

        val stop = Command.builder()
                .onCalled { ctx ->
                    val cmdArgs = ctx.args
                    if (cmdArgs.size > 0) {
                        val compute: Compute = getCompute(AlfredBot.GCP_APP_NAME)
                        compute.instances().stop(AlfredBot.GCP_APP_NAME, getZoneFromName(cmdArgs[0]), cmdArgs[0]).execute()
                        ctx.channel.sendMessage("Server is stopping")

                    }
                }
                .build()
        AlfredBot.registry.register(stop, "stop")

        val status = Command.builder()
                .onCalled { ctx ->
                    val cmdArgs = ctx.args
                    if (cmdArgs.size > 0) {
                        val compute: Compute = getCompute(AlfredBot.GCP_APP_NAME)
                        val instance = compute.instances().get(AlfredBot.GCP_APP_NAME, getZoneFromName(cmdArgs[0]), cmdArgs[0]).execute()
                        when (instance.status) {
                            "RUNNING" -> ctx.channel.sendMessage("Server: " + instance.name + " is currently running with IP: " + instance.networkInterfaces[0].accessConfigs[0].natIP)
                            "TERMINATED" -> ctx.channel.sendMessage("Server: " + instance.name + " is currently not running")
                            else -> ctx.channel.sendMessage("Server: " + instance.name + " is not running or stopped.  It is likely starting up or stopping right now")
                        }
                    }
                }
                .build()
        AlfredBot.registry.register(status, "status")
    }

    private fun getCompute(appName: String): Compute {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val stream = this.javaClass.classLoader.getResourceAsStream("client_secret.json")
        val credential: GoogleCredential = GoogleCredential.fromStream(stream).createScoped(listOf(ComputeScopes.COMPUTE))
        return Compute.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(appName).build()
    }

    private fun getZoneFromName(instance: String): String {
        // TODO make this dynamic
        return when (instance) {
            "survival" -> "us-east1-b"
            "terraria" -> "us-east1-b"
            else -> ""
        }
    }
}



