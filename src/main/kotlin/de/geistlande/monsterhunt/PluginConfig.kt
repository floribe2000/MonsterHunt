package de.geistlande.monsterhunt

import org.bukkit.Material
import java.util.Locale

data class PluginConfig(
    val worldSettings: Map<String, WorldSettings> = mapOf("default" to WorldSettings()),
    val tellTime: Boolean = true,
    val announceLead: Boolean = true,
    val selectionTool: Material = Material.WOODEN_SWORD,
    val announceSignup: Boolean = true,
    val defaultRewards: RewardSettings? = null,
    val dbSettings: DatabaseSettings = DatabaseSettings(),
    val locale: Locale = Locale.ENGLISH,
)

data class DatabaseSettings(
    val useDb: Boolean = false,
    val connectionString: String = "",
    val dbUser: String = "",
    val dbPassword: String = "",
)
