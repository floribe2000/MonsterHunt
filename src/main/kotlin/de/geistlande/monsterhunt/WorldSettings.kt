package de.geistlande.monsterhunt

import org.bukkit.Material

data class WorldSettings(
    val enabled: Boolean = true,
    val startTime: Int = 13000,
    val endTime: Int = 23600,
    val tellTime: Boolean = true,
    val deathPenalty: Int = 30,
    val enableSignup: Boolean = true,
    val announceSignup: Boolean = true,
    val minimumPlayers: Int = 2,
    val startChance: Int = 100,
    val skipDays: Int = 0,
    val skipToIfFailsToStart: Int = -1,
    val signupPeriodTime: Int = 5,
    val allowSignupAfterStart: Boolean = false,
    val huntZoneMode: Boolean = false,
    val mobSettings: MobSettings = MobSettings(),
    val rewardSettings: RewardSettings = RewardSettings(),
)

data class MobSettings(
    val onlyCountMobsSpawnedOutside: Boolean = false,
    val mobSpawnHeightLimit: Int = 0,
    val onlyCountMobsSpawnedOutsideBlackList: Boolean = true,
)

data class RewardSettings(
    val enabled: Boolean = true,
    val enableRewardEveryonePermission: Boolean = false,
    val rewardEveryone: Boolean = false,
    val numberOfWinners: Int = 3,
    val rewardParametersEveryone: String = "3 1-1",
    val minimumPointsEveryone: Int = 1,
    val minimumPointsPlace: Int = 1,
    val rewardParametersPlace: String = "",
    val availableRewards: List<RewardGroup> = listOf(),
)

data class RewardGroup(
    val name: String,
    val items: List<RewardElement>,
)

sealed interface RewardElement {
    val stochasticWeight: Int
}

data class MoneyReward(
    override val stochasticWeight: Int = 1,
    val amount: Double = 10.0,
): RewardElement

data class MaterialReward(
    override val stochasticWeight: Int = 1,
    val material: Material,
    val amount: Int,
) : RewardElement
