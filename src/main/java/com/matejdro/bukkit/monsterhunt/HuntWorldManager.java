package com.matejdro.bukkit.monsterhunt;

import de.geistlande.monsterhunt.Localizer;

import java.util.Collection;
import java.util.HashMap;


public class HuntWorldManager {
    public static HashMap<String, MonsterHuntWorld> worlds = new HashMap<>();

    public static MonsterHuntWorld getWorld(String name) {
        return worlds.get(name);
    }

    public static Collection<MonsterHuntWorld> getWorlds() {
        return worlds.values();
    }

    public static void timer() {
        MonsterHunt.instance.getServer().getScheduler().scheduleSyncRepeatingTask(MonsterHunt.instance, () -> {
            for (MonsterHuntWorld world : getWorlds()) {
                if (world == null || world.getWorld() == null) return;
                long time = world.getWorld().getTime();

                if (world.state == 0 && time < world.worldSettings.getEndTime() && time > world.getSignUpPeriodTime() && world.getSignUpPeriodTime() > 0 && !world.manual && !world.waitday) {
                    if (world.canStart()) {
                        world.state = 1;
                        String message = Localizer.INSTANCE.getString("hunt.signup.start", world.name);
                        Util.Broadcast(message);

                    }
                    world.waitday = true;

                } else if (world.state < 2 && time > world.worldSettings.getStartTime() && time < world.worldSettings.getEndTime() && !world.manual) {
                    if (world.state == 1) {
                        if (world.Score.size() < world.worldSettings.getMinimumPlayers() && world.worldSettings.getEnableSignup()) {
                            Util.Broadcast(Localizer.INSTANCE.getString("hunt.start.notEnoughPlayers"));
                            world.state = 0;
                            world.Score.clear();
                            world.waitday = true;
                            world.skipNight();
                        } else {
                            world.start();
                        }


                    } else if (!world.waitday && world.worldSettings.getSignupPeriodTime() == 0) {
                        world.waitday = true;
                        if (world.canStart()) world.start();

                    }
                } else if (world.state == 2 && (time > world.worldSettings.getEndTime() || time < world.worldSettings.getStartTime()) && !world.manual) {
                    Util.Debug("[MonterHunt][DEBUG - NEVEREND]Stop Time");
                    world.stop();
                } else if (world.waitday && (time > world.worldSettings.getEndTime() || time < world.worldSettings.getStartTime() - world.getSignUpPeriodTime())) {
                    world.waitday = false;
                }
            }
        }, 200L, 40L);
    }
}
