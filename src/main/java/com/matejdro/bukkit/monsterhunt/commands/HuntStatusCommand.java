package com.matejdro.bukkit.monsterhunt.commands;

import de.geistlande.monsterhunt.Localizer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.monsterhunt.HuntWorldManager;
import com.matejdro.bukkit.monsterhunt.MonsterHuntWorld;
import com.matejdro.bukkit.monsterhunt.Util;

public class HuntStatusCommand extends BaseCommand {

    public HuntStatusCommand() {
        needPlayer = true;
        permission = "monsterhunt.usercmd.huntstatus";
        adminCommand = false;
    }


    public Boolean run(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        boolean anyactive = false;
        StringBuilder actives = new StringBuilder();
        for (MonsterHuntWorld world : HuntWorldManager.getWorlds()) {
            if (world.state > 0) {
                anyactive = true;
                actives.append(world.name).append(",");
            }
        }
        if (!anyactive) {
            Util.Message(Localizer.INSTANCE.getString("personal.hunt.notActive"), player);
        } else {
            Util.Message(Localizer.INSTANCE.getString("personal.hunt.activeWorlds", actives.substring(0, actives.length() - 1)), player);
        }
        MonsterHuntWorld world = HuntWorldManager.getWorld(player.getWorld().getName());
        if (world == null || world.getWorld() == null) return true;

        if (world.state == 0) {

            if (world.lastScore.containsKey(player.getName()))
                Util.Message(Localizer.INSTANCE.getString("personal.status.lastScore", world.lastScore.get(player.getName())), player);
            else
                Util.Message(Localizer.INSTANCE.getString("personal.status.notInvolvedLastHunt"), player);
        } else if (world.state == 2) {
            if (world.Score.containsKey(player.getName())) {
                if (world.Score.get(player.getName()) == 0)
                    Util.Message(Localizer.INSTANCE.getString("personal.status.noKills"), player);
                else
                    Util.Message(Localizer.INSTANCE.getString("personal.status.score", world.Score.get(player.getName())), player);
            }
            if (world.worldSettings.getTellTime() && !world.manual) {
                int timediff = world.worldSettings.getEndTime() - world.worldSettings.getStartTime();
                long time = player.getWorld().getTime();
                long curdiff = (time - world.worldSettings.getStartTime()) * 100;
                double calc = (double) curdiff / timediff;
                int curpercent = (int) (100 - Math.round(calc));
                curpercent += 100;
                Util.Message(Localizer.INSTANCE.getString("personal.hunt.timeRemaining", curpercent), player);
            }


        }
        return true;
    }

}
