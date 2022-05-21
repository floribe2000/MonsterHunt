package de.geistlande.monsterhunt

import org.bukkit.Material
import java.util.Locale

data class PluginConfig(
    val worldSettings: Map<String, WorldSettings> = mapOf("default" to WorldSettings()),
    val announceLead: Boolean = true,
    val selectionTool: Material = Material.WOODEN_SWORD,
    val defaultRewards: RewardSettings? = null,
    val dbSettings: DatabaseSettings = DatabaseSettings(),
    val locale: Locale = Locale.ENGLISH,
    val debug: Boolean = false,
)

data class DatabaseSettings(
    val useDb: Boolean = false,
    val connectionString: String = "",
    val dbUser: String = "",
    val dbPassword: String = "",
)
