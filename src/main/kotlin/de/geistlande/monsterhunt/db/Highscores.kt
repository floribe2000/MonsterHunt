package de.geistlande.monsterhunt.db

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object Highscores : UUIDTable("mh_highscores") {
    val score = integer("highscore")
    val player = uuid("player").uniqueIndex()
    val playerName = varchar("playername", 32)
}

class Highscore(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Highscore>(Highscores) {
        fun findByPlayerUuid(player: UUID): Highscore? = find { Highscores.player eq player }.firstOrNull()
    }
    var score by Highscores.score
    var player by Highscores.player
    var playerName by Highscores.playerName
}
