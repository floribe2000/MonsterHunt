package com.matejdro.bukkit.monsterhunt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;

import de.geistlande.monsterhunt.Localizer;
import de.geistlande.monsterhunt.WorldSettings;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class MonsterHuntWorld {
    public String name;

    public boolean manual;

    public int state;

    public Boolean waitday;

    public int curday;

    //public SettingsOld settings;

    public WorldSettings worldSettings;

    public HashMap<String, Integer> Score = new HashMap<>();

    public HashMap<String, Integer> lastScore = new HashMap<>();

    public ArrayList<Integer> properlySpawned = new ArrayList<>();

    public ArrayList<Integer> blacklist = new ArrayList<>();

    public HashMap<Player, Location> tpLocations = new HashMap<>();

    public MonsterHuntWorld(String worldName, WorldSettings worldSettings) {
        state = 0;
        waitday = false;
        manual = false;
        curday = 0;
        name = worldName;
        this.worldSettings = worldSettings;
    }

    public World getWorld() {
        return MonsterHunt.instance.getServer().getWorld(name);
    }

    public int getSignUpPeriodTime() {
        int time = worldSettings.getSignupPeriodTime();
        if (time != 0) {
            time = worldSettings.getStartTime() - worldSettings.getSignupPeriodTime() * 1200;
            if (time < 0) {
                MonsterHunt.log.log(Level.WARNING, "[MonterHunt] Wrong SignUpPeriodTime Configuration! Sign Up period will be disabled!");
                time = 0;
            }

        }

        return time;

    }

    /**
     * its starts something //TODO find out what it does
     */
    public void start() {
        String message = Localizer.INSTANCE.getString("hunt.start");
        message = message.replace("<World>", name);
        Util.Broadcast(message);
        state = 2;
        waitday = true;
    }
    /**
     * its stops something //TODO find out what it does
     */
    public void stop() {
        if (state < 2) return;
        if (Score.size() < worldSettings.getMinimumPlayers()) {
            String message = Localizer.INSTANCE.getString("hunt.finish.notEnoughPlayers", name);
            Util.Broadcast(message);
        } else {
            RewardManager.rewardWinners(this);
        }
        for (Entry<Player, Location> e : tpLocations.entrySet()) {
            Player player = e.getKey();
            if (player == null || !player.isOnline()) continue;
            player.teleport(e.getValue());
        }
        state = 0;
        for (String i : Score.keySet()) {
            Integer hs = InputOutput.getHighScore(i);
            if (hs == null) hs = 0;
            int score = Score.get(i);
            if (score > hs) {
                InputOutput.UpdateHighScore(i, score);
                Player player = MonsterHunt.instance.getServer().getPlayer(i);
                if (player != null) {
                    String message = Localizer.INSTANCE.getString("personal.newHighScore", score);
                    Util.Message(message, player);
                }
            }
        }

        lastScore.putAll(Score);
        Score.clear();
        properlySpawned.clear();
    }

    public void skipNight() {
        if (worldSettings.getSkipToIfFailsToStart() >= 0) {
            getWorld().setTime(worldSettings.getSkipToIfFailsToStart());
        }
    }

    public Boolean canStart() {
        if (curday == 0) {
            curday = worldSettings.getSkipDays();
            return (new Random().nextInt(100)) < worldSettings.getStartChance();
        } else {
            curday--;
        }
        return false;
    }

}
