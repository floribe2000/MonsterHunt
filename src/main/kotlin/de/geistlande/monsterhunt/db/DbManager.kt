package de.geistlande.monsterhunt.db

import de.geistlande.monsterhunt.Settings
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionManager
import java.sql.Connection
import java.util.UUID

object DbManager {
    val database: Database by lazy { connectToDatabase() }

    fun initialize() {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Highscores)
        }
    }

    fun getHighScores(count: Int) = transaction(database) {
        Highscore.all().orderBy(Highscores.score to SortOrder.DESC).limit(count).map { it.playerName to it.score }
    }

    fun getPlayerHighscore(player: UUID) = transaction(database) {
        Highscore.find { Highscores.player eq player }.firstOrNull()?.score ?: -1
    }

    fun getPlayerHighscore(player: String) = transaction(database) {
        Highscore.find { Highscores.playerName eq player }.firstOrNull()?.score ?: -1
    }

    fun updateHighscore(player: OfflinePlayer, newScore: Int) = transaction(database) {
        Highscore.findByPlayerUuid(player.uniqueId)?.let {
            it.score = newScore
        } ?: Highscore.new {
            this.player = player.uniqueId
            playerName = player.name ?: "unknown"
            score = newScore
        }
    }

    private fun connectToDatabase(): Database {
        val dbSettings = Settings.config.dbSettings
        return if (dbSettings.useMySql) {
            val driverClassName = "com.mysql.cj.jdbc.Driver"

            Database.connect(
                "jdbc:mysql://${dbSettings.sqlHostName}:${dbSettings.sqlPort}/${dbSettings.databaseName}",
                driver = driverClassName,
                user = dbSettings.dbUser,
                password = dbSettings.dbPassword
            )
        } else {
            val driverClassName = "org.sqlite.JDBC"

            Database.connect(
                "jdbc:sqlite://${dbSettings.fileName}",
                driver = driverClassName,
                user = dbSettings.dbUser,
                password = dbSettings.dbPassword
            ).apply {
                transactionManager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
            }
        }
    }
}
